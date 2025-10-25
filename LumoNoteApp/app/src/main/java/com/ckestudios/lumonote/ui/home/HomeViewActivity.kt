package com.ckestudios.lumonote.ui.home

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.databinding.ActivityHomeViewBinding
import com.ckestudios.lumonote.ui.home.calendar.view.CalendarViewFragment
import com.ckestudios.lumonote.ui.home.notepreview.view.NotePreviewViewFragment
import com.ckestudios.lumonote.ui.home.settings.SettingsViewFragment
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper

class HomeViewActivity : AppCompatActivity() {

    private lateinit var homeViewBinding: ActivityHomeViewBinding
    private lateinit var homeViewModel: HomeViewModel

    private val notePreviewViewFragment = NotePreviewViewFragment()
    private val calendarViewFragment = CalendarViewFragment()
    private val settingsViewFragment = SettingsViewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        homeViewBinding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(homeViewBinding.root)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        // Setup fragments
        supportFragmentManager.beginTransaction().apply {

            add(homeViewBinding.currentHomeFragmentFL.id, calendarViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, notePreviewViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, settingsViewFragment)
            commit()
        }

        homeViewModel.setNotePreviewActive(true)
        switchToFragment(notePreviewViewFragment, calendarViewFragment, settingsViewFragment)


        // Prevent navigation to main activity on back button press
        consumeBackButtonPress()

        // Other setup
        setOnClickListeners()

        observeHomeVMValues()
    }

    private fun setOnClickListeners() {

        homeViewBinding.notesViewIV.setOnClickListener {

            homeViewModel.setNotePreviewActive(true)
            switchToFragment(notePreviewViewFragment, calendarViewFragment, settingsViewFragment)
        }

        homeViewBinding.calendarViewIV.setOnClickListener {

            homeViewModel.setCalendarActive(true)
            switchToFragment(calendarViewFragment, notePreviewViewFragment, settingsViewFragment)
        }

        homeViewBinding.settingsViewIV.setOnClickListener {

            homeViewModel.setSettingsActive(true)
            switchToFragment(settingsViewFragment, notePreviewViewFragment, calendarViewFragment)
        }
    }


    private fun observeHomeVMValues() {

        homeViewModel.notePreviewActive.observe(this) { active ->

            GeneralButtonIVHelper.updateButtonIVHighlight(homeViewBinding.notesViewIV, active,
                this, R.color.light_grey_3, R.drawable.selected_background)

//            if (active) {
//
//                GeneralButtonIVHelper.changeBtnBackgroundColor(this,
//                    homeViewBinding.notesViewIV, R.color.light_grey_3_selected2)
//            } else {
//
//                GeneralButtonIVHelper.removeButtonBackground(homeViewBinding.notesViewIV)
//            }
        }

        homeViewModel.calendarActive.observe(this) { active ->

            GeneralButtonIVHelper.updateButtonIVHighlight(homeViewBinding.calendarViewIV, active,
                this, R.color.light_grey_3, R.drawable.selected_background)

//            if (active) {
//
//                GeneralButtonIVHelper.changeBtnBackgroundColor(this,
//                    homeViewBinding.calendarViewIV, R.color.light_grey_3_selected2)
//            } else {
//
//                GeneralButtonIVHelper.removeButtonBackground(homeViewBinding.calendarViewIV)
//            }
        }

        homeViewModel.settingsActive.observe(this) { active ->

            GeneralButtonIVHelper.updateButtonIVHighlight(homeViewBinding.settingsViewIV, active,
                this, R.color.light_grey_3, R.drawable.selected_background)

//            if (active) {
//
//                GeneralButtonIVHelper.changeBtnBackgroundColor(this,
//                    homeViewBinding.settingsViewIV, R.color.light_grey_3_selected2)
//            } else {
//
//                GeneralButtonIVHelper.removeButtonBackground(homeViewBinding.settingsViewIV)
//            }
        }
    }


    private fun switchToFragment(targetFragment: Fragment, otherFragment1: Fragment,
                                 otherFragment2: Fragment) {

        supportFragmentManager.beginTransaction().apply {

            show(targetFragment)
            hide(otherFragment1)
            hide(otherFragment2)

            commit()
        }
    }


    private fun updateIVButtonHighlight(buttonImageView: ImageView, isActive: Boolean) {

        if (isActive) {

            GeneralButtonIVHelper.changeButtonIVResTint(this, buttonImageView, R.color.gold)
        } else {

            GeneralButtonIVHelper.changeButtonIVResTint(this, buttonImageView, R.color.light_grey_2)
        }
    }


    private fun consumeBackButtonPress() {

        val backButtonPressedCallback =
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                }
            }

        onBackPressedDispatcher.addCallback(this, backButtonPressedCallback)
    }

}