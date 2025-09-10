package com.ckestudios.lumonote.ui.home.calendar.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class CalendarNotePreviewAdapter(private val setNoteIDToOpen: (Int) -> Unit)
    : RecyclerView.Adapter<CalendarNotePreviewAdapter.CalendarNotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()
    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()


    // The layout from which this view data is accessed is passed into this later
    class CalendarNotePreviewViewHolder (notePreviewView: View) : RecyclerView.ViewHolder(notePreviewView) {

        val noteCardPreview: CardView = notePreviewView.findViewById(R.id.notePreviewCV)
        val titlePreview: TextView = notePreviewView.findViewById(R.id.titlePreviewTV)
        val contentPreview: TextView = notePreviewView.findViewById(R.id.contentPreviewTV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarNotePreviewViewHolder {

        val notePreviewView = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_note,
            parent, false)

        return CalendarNotePreviewViewHolder(notePreviewView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {

        return notesList.size
    }

    override fun onBindViewHolder(holder: CalendarNotePreviewViewHolder, position: Int) {

        // Find and store the equivalent note object in the list meant to be same as in UI
        val note = notesList[position]

        // Populate the UI note at that position with the data from the note obj at same
        // index in the list
        holder.titlePreview.text = note.noteTitle
        holder.contentPreview.text = note.noteContent

        holder.noteCardPreview.setOnClickListener {

            setNoteIDToOpen(note.noteID)
        }
    }


    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {

        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }

}