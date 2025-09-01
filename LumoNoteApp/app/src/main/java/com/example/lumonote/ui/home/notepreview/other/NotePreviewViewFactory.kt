package com.example.lumonote.ui.home.notepreview.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.ui.home.notepreview.viewmodel.NotePreviewViewModel


// A custom ViewModelFactory is needed because our ViewModel has a constructor parameter
// (a Repository), and ViewModelProvider by default only knows how to create ViewModels
// with empty constructors.
class NotePreviewViewFactory(
    private val dbConnection: DatabaseHelper   // Factory holds onto the dependency
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // First we check if the caller is asking for a TagViewModel specifically.
        // "isAssignableFrom" means: is modelClass the same type as, or a superclass of, TagViewModel?
        // If true, then we know it's safe to construct and return a TagViewModel.
        if (modelClass.isAssignableFrom(NotePreviewViewModel::class.java)) {

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
            return NotePreviewViewModel(dbConnection) as T
        }

        // If some other ViewModel type is requested, we throw an error.
        // This prevents accidentally returning the wrong type.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

