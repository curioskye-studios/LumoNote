package com.ckestudios.lumonote.ui.noteview.view.taginput

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper

// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class TagInputSelectorAdapter(private val onTagClickedFunction: (MutableList<Int>) -> Unit)
    : RecyclerView.Adapter<TagInputSelectorAdapter.TagDisplayViewHolder>(){

    private var tagsList = mutableListOf<Tag>()
    private var selectedTagIDs = mutableListOf<Int>()


    // The layout from which this view data is accessed is passed into this later
    class TagDisplayViewHolder (tagDisplayView: View) : RecyclerView.ViewHolder(tagDisplayView) {

        val tagCardView: CardView = tagDisplayView.findViewById(R.id.tagItemCV)
        val tagLayoutView: LinearLayout = tagDisplayView.findViewById(R.id.tagItemLayoutLL)
        val tagID: TextView = tagDisplayView.findViewById(R.id.tagIdTV)
        val tagName: TextView = tagDisplayView.findViewById(R.id.tagNameTV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagDisplayViewHolder {

        val tagDisplayView = LayoutInflater.from(parent.context).inflate(R.layout.item_tag,
            parent, false)

        return TagDisplayViewHolder(tagDisplayView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {

        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagDisplayViewHolder,
                                  @SuppressLint("RecyclerView") position: Int) {

        // Find and store the equivalent tag object in the list meant to be same as in UI
        val tag = tagsList[position]
        val tagID = tag.tagID

        // Populate the UI tag at that position
        holder.tagName.text = tag.tagName
        holder.tagID.text = tagID.toString()
        holder.tagCardView.tag = tagID in selectedTagIDs

//        Log.d("TagDebug", "tagID: $tagID in selectedTagIDs - ${tagID in selectedTagIDs}.")
        highlightActiveTags(holder)

        setTagMargin(holder)
        // Note: position of the tags can change dynamically at runtime, state should be tracked

        holder.tagCardView.setOnClickListener {

            holder.tagCardView.tag = !(holder.tagCardView.tag as Boolean)

            updateSelectedTagsList(holder, tagID)
            onTagClickedFunction(selectedTagIDs)

            highlightActiveTags(holder)
        }
    }


    // Ensure UI stays up-to-date with tagss list
    fun refreshData(newTags: List<Tag>) {

        tagsList.clear()
        tagsList.addAll(newTags)
        notifyDataSetChanged()
    }


    fun setSelectedTagsList(newSelectedTagIDs: List<Int>){

        selectedTagIDs = newSelectedTagIDs.toMutableList()
    }

    private fun updateSelectedTagsList(holder: TagDisplayViewHolder, tagID: Int) {

        val tagIDInList = selectedTagIDs.contains(tagID)

        if (holder.tagCardView.tag == true && !tagIDInList) {
            selectedTagIDs.add(tagID)
        } else if (holder.tagCardView.tag == false && tagIDInList) {
            selectedTagIDs.remove(tagID)
        }
    }


    private fun highlightActiveTags(holder: TagDisplayViewHolder) {

        val context = holder.itemView.context

        if (holder.tagCardView.tag == true) {
            setHighlightTagStyle(holder, context)
        } else {
            setDefaultTagStyle(holder, context)
        }
    }

    private fun setHighlightTagStyle(holder: TagDisplayViewHolder, context: Context) {

        holder.tagLayoutView.setBackgroundColor(ContextCompat.getColor(context,
            R.color.gold))
        holder.tagName.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.tagName.setTypeface(null, Typeface.BOLD)
    }

    private fun setDefaultTagStyle(holder: TagDisplayViewHolder, context: Context) {

        holder.tagLayoutView.setBackgroundColor(ContextCompat.getColor(context,
            R.color.dark_grey_variant))
        holder.tagName.setTextColor(ContextCompat.getColor(context, R.color.light_grey_1))
        holder.tagName.setTypeface(null, Typeface.NORMAL)
    }

    private fun setTagMargin(holder: TagDisplayViewHolder) {

        holder.tagCardView.apply {

            val params = layoutParams as ViewGroup.MarginLayoutParams

            params.bottomMargin = GeneralUIHelper.intToPx(0, context)
            params.rightMargin = GeneralUIHelper.intToPx(6, context)

            layoutParams = params
        }
    }

}