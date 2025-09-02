package com.curioskyestudios.lumonote.ui.home.notepreview.view

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
import com.curioskyestudios.lumonote.R
import com.curioskyestudios.lumonote.data.models.Tag

// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class TagDisplayAdapter(private val onTagClickedFunction: (Int) -> Unit)
    : RecyclerView.Adapter<TagDisplayAdapter.TagDisplayViewHolder>(){

    private var tagsList = mutableListOf<Tag>()

    // cache of highlighted/selected item, updated by notepreviewfragment
    private var selectedPosition: Int? = null


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

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {

        return tagsList.size
    }

    override fun onBindViewHolder(holder: TagDisplayViewHolder, @SuppressLint("RecyclerView") position: Int) {

        // Find and store the equivalent tag object in the list meant to be same as in UI
        val tag = tagsList[position]
        // Populate the UI tag at that position
        holder.tagName.text = tag.tagName
        // Note: position of the tags can change dynamically at runtime, state should be tracked

        // set styles based on selection
        highlightOnlyActiveTag(holder, position)

        // Handle click to change selection
        holder.tagCardView.setOnClickListener {
            onTagClickedFunction(position)
        }
    }

    // Ensure UI stays up-to-date with tagss list
    fun refreshData(newTags: List<Tag>) {

        tagsList.clear()
        tagsList.addAll(newTags)
        notifyDataSetChanged()
    }

    fun setSelectedPosition(position: Int) {

        val previousPosition = selectedPosition
        selectedPosition = position
        if (previousPosition != null) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(position)
    }

    private fun highlightOnlyActiveTag(holder: TagDisplayViewHolder, position: Int) {

        val context = holder.itemView.context

        // Apply selected/highlight style
        if (position == selectedPosition) {

            holder.tagLayoutView.setBackgroundColor(ContextCompat.getColor(context, R.color.gold))
            holder.tagName.setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
            holder.tagName.setTypeface(null, Typeface.BOLD);
        } else {

            // Reset other items to default
            holder.tagLayoutView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_grey
                )
            )
            holder.tagName.setTextColor(ContextCompat.getColor(context, R.color.light_grey_2))
            holder.tagName.setTypeface(null, Typeface.NORMAL);
        }

    }
}