package com.example.lumonote.ui.home.notepreview.view

import android.content.Intent
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
import com.example.lumonote.ui.noteview.view.NoteViewActivity
import com.example.lumonote.utils.general.GeneralTextHelper
import com.example.lumonote.utils.general.GeneralUIHelper


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class NotePreviewAdapter() : RecyclerView.Adapter<NotePreviewAdapter.NotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()
    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()


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

        // Open Note View of note by clicking on one
        holder.noteCardPreview.setOnClickListener {
            val intent = Intent(holder.itemView.context, NoteViewActivity::class.java).apply {
                // Also pass in the id of the note interacted w/ for later retrieval in update note
                putExtra("note_id", note.noteID)
            }

            // Starts the update note activity
            holder.itemView.context.startActivity(intent)
        }

        // Pin preview of note (toggle gold)
        holder.pinPreview.setOnClickListener {
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


    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {
        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }

}