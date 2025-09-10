package com.ckestudios.lumonote.ui.home.notepreview.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class NotePreviewAdapter(private val setNoteIDToOpen: (Int) -> Unit,
                         private val whenCurrentNotePinClicked: (Boolean, Int) -> Unit)
    : RecyclerView.Adapter<NotePreviewAdapter.NotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()
    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()

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
        holder.pinPreview.tag = note.notePinned
        updatePinHighlight(holder)

        //Log.d("NoteFrag", "pinnedPreview: ${holder.pinPreview.tag}")


        holder.noteCardPreview.setOnClickListener {

            setNoteIDToOpen(note.noteID)
        }

        // Pin preview of note (toggle gold)
        holder.pinPreview.setOnClickListener {

            val pinnedFlag = holder.pinPreview.tag as Boolean

            Log.d("NoteFrag", "pinnedFlag: $pinnedFlag")

            holder.pinPreview.tag = !pinnedFlag

            val currentNoteID = note.noteID

            whenCurrentNotePinClicked(!pinnedFlag, currentNoteID)

            updatePinHighlight(holder)
        }
    }


    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {

        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }

    private fun updatePinHighlight(holder: NotePreviewViewHolder){

        if (holder.pinPreview.tag == true) {

            generalButtonIVHelper.changeButtonIVResTint(holder.itemView.context, holder.pinPreview,
                R.color.gold)
        } else {

            generalButtonIVHelper.changeButtonIVResTint(holder.itemView.context, holder.pinPreview,
                R.color.light_grey_3)
        }
    }
}