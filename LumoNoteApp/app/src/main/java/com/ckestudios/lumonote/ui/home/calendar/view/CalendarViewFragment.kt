package com.ckestudios.lumonote.ui.home.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ckestudios.lumonote.databinding.FragmentCalendarViewBinding
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.general.GeneralUIHelper
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

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
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

        calendarViewBinding.calendarDateSelectorCV.setInitialSelectedDate(Date())
    }

    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _calendarViewBinding = null // prevent memory leaks by clearing reference
    }



}