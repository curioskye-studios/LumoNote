package com.example.lumonote.ui.home.notepreview.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lumonote.R
import com.example.lumonote.data.models.Note
import com.example.lumonote.utils.general.GeneralUIHelper


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class NotePreviewAdapter(private val setNoteIDToOpen: (Int) -> Unit,
                         private val shouldHighlightNotePin: (Boolean) -> Unit)
    : RecyclerView.Adapter<NotePreviewAdapter.NotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    // cache of highlighted/selected item, updated by notepreviewfragment
    private val areNotesPinned = mutableListOf<Boolean>()


    // The layout from which this view data is accessed is passed into this later
    class NotePreviewViewHolder (notePreviewView: View) : RecyclerView.ViewHolder(notePreviewView) {

        val noteCardPreview: CardView = notePreviewView.findViewById(R.id.notePreviewCV)
        val titlePreview: TextView = notePreviewView.findViewById(R.id.titlePreviewTV)
        val contentPreview: TextView = notePreviewView.findViewById(R.id.contentPreviewTV)
        val pinPreview: ImageView = notePreviewView.findViewById(R.id.previewPinIV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewViewHolder {

        val notePreviewView = LayoutInflater.from(parent.context).inflate(R.layout.item_note,
            parent, false)

        return NotePreviewViewHolder(notePreviewView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {

        return notesList.size
    }

    override fun onBindViewHolder(holder: NotePreviewViewHolder, position: Int) {

        // Find and store the equivalent note object in the list meant to be same as in UI
        val note = notesList[position]

        // Populate the UI note at that position with the data from the note obj at same
        // index in the list
        holder.titlePreview.text = note.noteTitle
        holder.contentPreview.text = note.noteContent


        holder.noteCardPreview.setOnClickListener {

            setNoteIDToOpen(note.noteID)
        }

        // Pin preview of note (toggle gold)
        holder.pinPreview.setOnClickListener {
            //TODO("Pull pin status from note and use it to update highlight display")
            //TODO("Move Taasts to fragment instead based on chnage of pin status")
            updatePinHightlight(holder)
        }
    }


    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {

        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }

    fun updateAreNotesPinned() {

    }

    private fun updatePinHightlight(holder: NotePreviewViewHolder){

        val tintColor = holder.pinPreview.imageTintList
            ?.getColorForState(holder.pinPreview.drawableState, 0)

        val lightGrey3Tint = ContextCompat.getColor(holder.itemView.context, R.color.light_grey_3)

        if (tintColor == lightGrey3Tint) {
            generalUIHelper.changeButtonIVColor(holder.itemView.context, holder.pinPreview,
                R.color.gold)

            // Put small notification popup at bottom of screen
            Toast.makeText(holder.itemView.context, "Note Pinned", Toast.LENGTH_SHORT).show()
        } else {
            generalUIHelper.changeButtonIVColor(holder.itemView.context, holder.pinPreview,
                R.color.light_grey_3)

            // Put small notification popup at bottom of screen
            Toast.makeText(holder.itemView.context, "Note Unpinned", Toast.LENGTH_SHORT).show()
        }
    }

}