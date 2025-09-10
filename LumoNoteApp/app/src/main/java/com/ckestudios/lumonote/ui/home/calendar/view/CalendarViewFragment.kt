package com.ckestudios.lumonote.ui.home.calendar.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.databinding.FragmentCalendarViewBinding
import com.ckestudios.lumonote.ui.home.calendar.viewmodel.CalendarViewModel
import com.ckestudios.lumonote.ui.noteview.view.NoteViewActivity
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.general.GeneralTextHelper
import com.ckestudios.lumonote.utils.general.GeneralUIHelper
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date


class CalendarViewFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_calendarViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _calendarViewBinding: FragmentCalendarViewBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _calendarViewBinding!! everywhere
    // The "!!" means it assumes _calendarViewBinding is not null between onCreateView & onDestroyView
    private val calendarViewBinding get() = _calendarViewBinding!!

    private lateinit var calendarNotePreviewAdapter: CalendarNotePreviewAdapter

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()
    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val dbConnection = DatabaseHelper(requireContext()) // DB
        val appSharedVMConstructor = AppSharedViewFactory(dbConnection) // Factory

        // Custom ViewModelProviders know how to build viewmodels w/ dbconnection dependency
        noteAppSharedViewModel = ViewModelProvider(this, appSharedVMConstructor)
            .get(NoteAppSharedViewModel::class.java)

        calendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for requireContext() fragment
        _calendarViewBinding = FragmentCalendarViewBinding.inflate(inflater, container, false)
        return calendarViewBinding.root // return the root view for the fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Initialize calendar selected date
        val todayCurrentDate = Date()

        calendarViewBinding.calendarDateSelectorKV.setInitialSelectedDate(todayCurrentDate)
        calendarViewModel.setSelectedDate(todayCurrentDate)


        initializeAdapters()

        setupAdapterDisplays()

        setupListeners()

        observeNoteAppVMValues()

        observeCalendarVMValues()
    }

    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _calendarViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun initializeAdapters() {

        calendarNotePreviewAdapter = CalendarNotePreviewAdapter (
            setNoteIDToOpen =
            { noteID ->

                openNoteViewActivity(noteID)
            }
        )
    }

    private fun setupAdapterDisplays() {

        // Define layout and adapter to use for tag display
        calendarViewBinding.calendarNotesPreviewRV.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)

        calendarViewBinding.calendarNotesPreviewRV.adapter = calendarNotePreviewAdapter
    }

    private fun openNoteViewActivity(noteID: Int) {

        // Open Note View of note by clicking on one
        val intent = Intent(requireContext(), NoteViewActivity::class.java).apply {
            // Also pass in the id of the note interacted w/ for later retrieval in note view
            putExtra("note_id", noteID)
        }

        // Starts the update note activity
        requireContext().startActivity(intent)
    }


    private fun setupListeners() {

        calendarViewModel.selectedDate.observe(viewLifecycleOwner) { date ->

            val dateAsLocalDate= date.toInstant()
                .atZone(ZoneId.systemDefault()) // or specify a zone
                .toLocalDate()

            if (dateAsLocalDate == LocalDate.now()) {

                calendarViewBinding.selectedDateTV.text = "Today"
            } else {

                val dateWithWeekDay = generalTextHelper.formatDate(dateAsLocalDate)

                val dateNoWeekday =  dateWithWeekDay.substring(5)

                calendarViewBinding.selectedDateTV.text = "$dateNoWeekday"
            }


        }
    }

    private fun observeNoteAppVMValues() {

        // Observe changes
        noteAppSharedViewModel.notes.observe(viewLifecycleOwner) { notes ->

            calendarNotePreviewAdapter.refreshData(notes)
        }
    }


    private fun observeCalendarVMValues() {

        calendarViewBinding.calendarDateSelectorKV.setDateSelector { selectedDate ->

            calendarViewModel.setSelectedDate(selectedDate)
        }
    }



}