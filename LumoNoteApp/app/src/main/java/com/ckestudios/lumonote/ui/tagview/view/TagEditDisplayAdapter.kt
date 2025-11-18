package com.ckestudios.lumonote.ui.tagview.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper

// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class TagEditDisplayAdapter(private val onClickDeleteFunction: (Int) -> Unit,
                            private val onClickSaveFunction: (Int, String) -> Unit)
    : RecyclerView.Adapter<TagEditDisplayAdapter.TagEditDisplayViewHolder>(){

    private var tagsList = mutableListOf<Tag>()


    // The layout from which this view data is accessed is passed into this later
    class TagEditDisplayViewHolder (tagEditDisplayView: View)
        : RecyclerView.ViewHolder(tagEditDisplayView) {

        val tagEditCardView: CardView = tagEditDisplayView.findViewById(R.id.tagEditItemCV)
        val tagEditLayoutView: RelativeLayout =
            tagEditDisplayView.findViewById(R.id.tagEditItemLayoutRL)

        val tagID: TextView = tagEditDisplayView.findViewById(R.id.tagIdTV)
        val tagEditName: CustomSelectionET = tagEditDisplayView.findViewById(R.id.tagEditNameETV)
        val tagDeleteButton: ImageView = tagEditDisplayView.findViewById(R.id.deleteTagButtonIV)
        val tagSaveButton: ImageView = tagEditDisplayView.findViewById(R.id.saveTagButtonIV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagEditDisplayViewHolder {

        val tagEditDisplayView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tag_edit, parent,
                false)

        return TagEditDisplayViewHolder(tagEditDisplayView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {

        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagEditDisplayViewHolder,
                                  @SuppressLint("RecyclerView") position: Int) {

        // Find and store the equivalent tag object in the list meant to be same as in UI
        // Note: position of the tags can change dynamically at runtime, state should be tracked
        val tag = tagsList[position]
        val tagID = tag.tagID
        holder.tagID.text = tagID.toString()

        if (holder.tagEditName.text != null) {
            holder.tagEditName.text!!.replace(0, holder.tagEditName.text!!.length, tag.tagName)
        }

        holder.tagSaveButton.visibility = View.INVISIBLE


        holder.apply {

            tagDeleteButton.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(tagDeleteButton.context,
                    tagDeleteButton)

                onClickDeleteFunction(tagID)
            }

            tagSaveButton.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(tagSaveButton.context,
                    tagSaveButton)

                holder.tagSaveButton.visibility = View.INVISIBLE

                GeneralUIHelper.clearETViewFocusOnHideKeyboard(tagEditName, itemView)
                tagEditName.clearFocus()

                onClickSaveFunction(tagID, tagEditName.text.toString())
            }

            tagEditName.addTextChangedListener {

                holder.tagSaveButton.visibility = View.VISIBLE
            }
        }
    }

    // Ensure UI stays up-to-date with tagss list
    fun refreshData(newTags: List<Tag>) {

        tagsList.clear()
        tagsList.addAll(newTags)
        notifyDataSetChanged()
    }

}