package com.ckestudios.lumonote.ui.noteview.view.taginput

import android.annotation.SuppressLint
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
class TagInputDisplayAdapter()
    : RecyclerView.Adapter<TagInputDisplayAdapter.TagDisplayViewHolder>(){

    private var tagsList = mutableListOf<Tag>()


    // The layout from which this view data is accessed is passed into this later
    class TagDisplayViewHolder (tagDisplayView: View) : RecyclerView.ViewHolder(tagDisplayView) {

        val tagCardView: CardView = tagDisplayView.findViewById(R.id.tagItemCV)
        val tagLayoutView: LinearLayout = tagDisplayView.findViewById(R.id.tagItemLayoutLL)
        val tagName: TextView = tagDisplayView.findViewById(R.id.tagNameTV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagDisplayViewHolder {

        val tagDisplayView = LayoutInflater.from(parent.context).inflate(R.layout.item_tag,
            parent, false)

        return TagDisplayViewHolder(tagDisplayView)
    }


    override fun getItemCount(): Int {

        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagDisplayViewHolder,
                                  @SuppressLint("RecyclerView") position: Int) {

        // Find and store the equivalent tag object in the list meant to be same as in UI
        val tag = tagsList[position]
        // Populate the UI tag at that position
        holder.tagName.text = tag.tagName
        // Note: position of the tags can change dynamically at runtime, state should be tracked

        highlightTag(holder)
    }


    // Ensure UI stays up-to-date with tagss list
    fun refreshData(newTags: List<Tag>) {

        tagsList.clear()
        tagsList.addAll(newTags)
        notifyDataSetChanged()
    }


    private fun highlightTag(holder: TagDisplayViewHolder) {

        val context = holder.itemView.context

        holder.tagLayoutView.setBackgroundColor(ContextCompat.getColor(context,
            R.color.black_3))
        holder.tagName.setTextColor(ContextCompat.getColor(context, R.color.light_grey_1))
        holder.tagName.setTypeface(null, Typeface.NORMAL)

        holder.tagCardView.apply {

            val params = layoutParams as ViewGroup.MarginLayoutParams

            params.bottomMargin = GeneralUIHelper.intToPx(12, context)
            params.rightMargin = GeneralUIHelper.intToPx(8, context)

            layoutParams = params
        }
    }

}