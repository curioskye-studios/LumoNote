package com.ckestudios.lumonote.ui.home.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.databinding.FragmentSettingsViewBinding
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel


class SettingsViewFragment : Fragment() {

    private var _settingsViewBinding: FragmentSettingsViewBinding? = null
    private val settingsViewBinding get() = _settingsViewBinding!!


    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel

    private lateinit var userSharedPreferences: SharedPreferences
    private lateinit var preferencesEditor: Editor

    private var removeEmptyNotes = false



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val app: Application = requireActivity().application
        val noteRepository = NoteRepository(requireContext()) // DB
        noteAppSharedViewModel = ViewModelProvider(requireActivity(),
            AppSharedViewFactory(app, noteRepository)).get(NoteAppSharedViewModel::class.java)

        userSharedPreferences =
            requireContext().getSharedPreferences("userPreferences", Context.MODE_PRIVATE)
        preferencesEditor = userSharedPreferences.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _settingsViewBinding = FragmentSettingsViewBinding.inflate(inflater, container,
            false)
        return settingsViewBinding.root // return the root view for the fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        loadPreferences()

        populateUIWithPreferences()

        setupListeners()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _settingsViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun loadPreferences() {

        removeEmptyNotes = userSharedPreferences.getBoolean("removeEmptyNotes", false)

//        Log.d("SettingsDeBug", "removeEmptyNotes: $removeEmptyNotes")

        noteAppSharedViewModel.setShouldDiscardEmptyNotes(removeEmptyNotes)
    }

    private fun populateUIWithPreferences() {

        settingsViewBinding.apply {

            removeEmptyNoteSwitchSM.isChecked = removeEmptyNotes
        }
    }

    private fun updatePreferences() {

        preferencesEditor.apply {

            putBoolean("removeEmptyNotes", removeEmptyNotes)
            apply()
        }

//        Log.d("SettingsDeBug", "removeEmptyNotes: $removeEmptyNotes")

        noteAppSharedViewModel.setShouldDiscardEmptyNotes(removeEmptyNotes)
    }

    private fun setupListeners() {

        settingsViewBinding.apply {

            removeEmptyNoteSwitchSM.setOnCheckedChangeListener { _, isChecked ->

                removeEmptyNotes = isChecked
                updatePreferences()
            }

        }

    }



}