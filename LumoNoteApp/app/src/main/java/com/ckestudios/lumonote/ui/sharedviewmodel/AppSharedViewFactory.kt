package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.data.repository.Repository
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.data.repository.TaggedRepository


// A custom ViewModelFactory is needed because our ViewModel has a constructor parameter
// (a Repository), and ViewModelProvider by default only knows how to create ViewModels
// with empty constructors.
class AppSharedViewFactory(
    private val repository: Repository  // Factory holds onto the dependency
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // First we check if the caller is asking for a TagViewModel specifically.
        // "isAssignableFrom" means: is modelClass the same type as, or a superclass of, TagViewModel?
        // If true, then we know it's safe to construct and return a TagViewModel.
        if (modelClass.isAssignableFrom(NoteAppSharedViewModel::class.java)) {

            // The compiler warning:
            // Because of type erasure, Kotlin/Java generics lose type info at runtime.
            // That means the compiler *cannot prove* that TagViewModel really matches T.
            // Even though we know it's correct after the check above,
            // the compiler issues an "Unchecked cast" warning.
            //
            // Why it's safe:
            // We already checked isAssignableFrom, so at runtime this cast cannot fail.
            // This is the standard pattern recommended in Android's docs.
            @Suppress("UNCHECKED_CAST")
            return NoteAppSharedViewModel(Application(), repository as NoteRepository) as T
        }

        if (modelClass.isAssignableFrom(TagAppSharedViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return TagAppSharedViewModel(Application(), repository as TagRepository) as T
        }

        if (modelClass.isAssignableFrom(TaggedAppSharedViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return TaggedAppSharedViewModel(Application(), repository as TaggedRepository) as T
        }

        // If some other ViewModel type is requested, we throw an error.
        // This prevents accidentally returning the wrong type.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

