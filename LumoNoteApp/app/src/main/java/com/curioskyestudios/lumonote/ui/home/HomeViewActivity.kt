package com.curioskyestudios.lumonote.ui.home

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.curioskyestudios.lumonote.R
import com.curioskyestudios.lumonote.databinding.ActivityHomeViewBinding
import com.curioskyestudios.lumonote.ui.home.calendar.CalendarViewFragment
import com.curioskyestudios.lumonote.ui.home.notepreview.view.NotePreviewViewFragment
import com.curioskyestudios.lumonote.ui.home.settings.SettingsViewFragment
import com.curioskyestudios.lumonote.utils.general.GeneralButtonIVHelper

class HomeViewActivity : AppCompatActivity() {

    private lateinit var homeViewBinding: ActivityHomeViewBinding
    private lateinit var homeViewModel: HomeViewModel
    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()

    private val notePreviewViewFragment = NotePreviewViewFragment()
    private val calendarViewFragment = CalendarViewFragment()
    private val settingsViewFragment = SettingsViewFragment()
    private lateinit var navigationFragments: MutableList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        homeViewBinding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(homeViewBinding.root)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        navigationFragments = mutableListOf(notePreviewViewFragment, calendarViewFragment,
            settingsViewFragment)


        supportFragmentManager.beginTransaction().apply {

            add(homeViewBinding.currentHomeFragmentFL.id, notePreviewViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, calendarViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, settingsViewFragment)

            //reduce loading times by keeping all fragments in memory
            hide(calendarViewFragment)
            hide(settingsViewFragment)

            commit()
        }

        switchToFragment(notePreviewViewFragment)


        homeViewBinding.notesViewIV.setOnClickListener {

            switchToFragment(notePreviewViewFragment)
        }

        homeViewBinding.calendarViewIV.setOnClickListener {

            switchToFragment(calendarViewFragment)
        }

        homeViewBinding.settingsViewIV.setOnClickListener {

            switchToFragment(settingsViewFragment)
        }


        homeViewModel.notePreviewActive.observe(this) { active ->

            updateIVButtonHighlight(homeViewBinding.notesViewIV, active)
        }

        homeViewModel.calendarActive.observe(this) { active ->

            updateIVButtonHighlight(homeViewBinding.calendarViewIV, active)
        }

        homeViewModel.settingsActive.observe(this) { active ->

            updateIVButtonHighlight(homeViewBinding.settingsViewIV, active)
        }
    }


    private fun switchToFragment(targetFragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {

            for (fragment in navigationFragments) {

                if (fragment == targetFragment) {

                    //show it
                    show(fragment)

                    //depending on the fragment, update its view model counterpart to active
                    updateFragmentActiveStatus(fragment, true)
                } else {

                    // hide the rest
                    hide(fragment)

                    //make as inactive all the rest in their view model counterpart
                    updateFragmentActiveStatus(fragment, false)
                }
            }

            commit()
        }
    }


    private fun updateFragmentActiveStatus(fragment: Fragment, status: Boolean)  {

        when (fragment) {

            notePreviewViewFragment -> homeViewModel.setNotePreviewActive(status)
            calendarViewFragment -> homeViewModel.setCalendarActive(status)
            settingsViewFragment -> homeViewModel.setSettingsActive(status)
        }
    }

    private fun updateIVButtonHighlight(buttonImageView: ImageView, isActive: Boolean) {

        if (isActive) {

            generalButtonIVHelper.changeButtonIVCustomColor(this, buttonImageView, R.color.gold)
        } else {

            generalButtonIVHelper.changeButtonIVCustomColor(this, buttonImageView, R.color.light_grey_2)
        }
    }

}