package com.ckestudios.lumonote.ui.tagview.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.databinding.ActivityTagViewBinding
import com.ckestudios.lumonote.ui.other.ConfirmationDialogFragment
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper

class TagViewActivity : AppCompatActivity() {

    private lateinit var tagViewBinding: ActivityTagViewBinding

    private lateinit var tagEditDisplayAdapter: TagEditDisplayAdapter

    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel

    private var tagIDToDelete: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_view)

        tagViewBinding = ActivityTagViewBinding.inflate(layoutInflater)
        setContentView(tagViewBinding.root)


        val tagRepository = TagRepository(this) // DB

        tagAppSharedViewModel = ViewModelProvider(this, AppSharedViewFactory(tagRepository))
            .get(TagAppSharedViewModel::class.java)


        setupAdapterDisplay()

        setOnClickListeners()

        observeTagAppVMValues()
    }

    private fun setupAdapterDisplay() {

        tagEditDisplayAdapter = TagEditDisplayAdapter (

            onClickDeleteFunction = {
                tagID ->

                tagIDToDelete = tagID

                ConfirmationDialogFragment(tagAppSharedViewModel,
                    "Are you sure you want to delete this tag? It cannot be undone.",
                    "Yes, Delete")
                    .show(supportFragmentManager, "confirmNoteDialog")
            },

            onClickSaveFunction = {
                tagID, tagName ->

                val oldTag = tagAppSharedViewModel.getTag(tagID)

                if (tagName == "") {
                    GeneralUIHelper.displayFeedbackToast( this,
                        "Empty tag will be deleted", false)
                }

                if (tagNameIsTaken(tagName)) {
                    GeneralUIHelper.displayFeedbackToast( this,
                        "Tag name already exists", false)
                    return@TagEditDisplayAdapter
                }

                if (oldTag.tagName != tagName) {
                    tagAppSharedViewModel.updateTag(Tag(tagID, tagName))
                } else {
                    GeneralUIHelper.displayFeedbackToast( this, "Up to date",
                        false)
                }
            }
        )

        tagViewBinding.tagsHolderRV.layoutManager = LinearLayoutManager(this)
        tagViewBinding.tagsHolderRV.adapter = tagEditDisplayAdapter
    }

    private fun tagNameIsTaken(newTagName: String): Boolean {

        val tags = tagAppSharedViewModel.tags.value!!
        val existingTagNames = tags.map { it.tagName }

        return newTagName in existingTagNames
    }

    private fun newTagAlreadyCreated(): Boolean {

        val tags = tagAppSharedViewModel.tags.value!!
        val existingTagNames = tags.map { it.tagName }

        return "New Tag" in existingTagNames
    }

    private fun setOnClickListeners() {

        tagViewBinding.apply {

            addButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@TagViewActivity,
                    addButtonIV)
                if (newTagAlreadyCreated()) {
                    GeneralUIHelper.displayFeedbackToast( this@TagViewActivity,
                        "New tag already created. Please rename", false)
                } else {
                    tagAppSharedViewModel.createTag(Tag(0, "New Tag"))
                }
            }

            backButtonIV.setOnClickListener {

                tagAppSharedViewModel.deleteEmptyTags()

                GeneralButtonIVHelper.playSelectionIndication(this@TagViewActivity,
                    backButtonIV)

                finish()
            }

            val backButtonPressedCallback =
                object : OnBackPressedCallback(true) {
                    // Custom logic for back button press
                    override fun handleOnBackPressed() {

                        tagAppSharedViewModel.deleteEmptyTags()

                        finish()
                    }
                }
            onBackPressedDispatcher.addCallback(this@TagViewActivity,
                backButtonPressedCallback)
        }

    }

    private fun observeTagAppVMValues() {

        tagAppSharedViewModel.apply {

            tags.observe(this@TagViewActivity) { tags ->

                val tagsExcludingAllNotes = tags.filter { it.tagName != "All Notes" }

                tagEditDisplayAdapter.refreshData(tagsExcludingAllNotes)
            }

            notifyRefresh.observe(this@TagViewActivity) { shouldRefresh ->

                if (shouldRefresh == true) {
                    loadAllTags()
                }
            }

            noTagsCreated.observe(this@TagViewActivity) { hasTags ->

                GeneralUIHelper.changeViewVisibility(tagViewBinding.noTagsMessageTV,
                    hasTags)
            }


            tagWasCreated.observe(this@TagViewActivity){ isTrue ->

                if (isTrue) {

                    GeneralUIHelper.displayFeedbackToast( this@TagViewActivity,
                        "Tag Created", false)
                }
            }

            tagWasUpdated.observe(this@TagViewActivity){ isTrue ->

                if (isTrue) {

                    GeneralUIHelper.displayFeedbackToast( this@TagViewActivity,
                        "Changes Saved", false)
                }
            }

            tagWasDeleted.observe(this@TagViewActivity){ isTrue ->

                val emptyTagDeleted = emptyTagDeleted.value as Boolean

                if (isTrue && !emptyTagDeleted) {

                    GeneralUIHelper.displayFeedbackToast( this@TagViewActivity,
                        "Tag Deleted", false)
                }
            }

            emptyTagDeleted.observe(this@TagViewActivity){ isTrue ->

                if (isTrue) {

                    GeneralUIHelper.displayFeedbackToast(this@TagViewActivity,
                        "Empty tag(s) deleted", false)
                }
            }

            deleteTagConfirmed.observe(this@TagViewActivity){ shouldDelete ->

                if (shouldDelete) {
                    tagAppSharedViewModel.deleteTag(tagIDToDelete)
                    setDeleteTagConfirmed(false)
                }
            }

        }
    }


}