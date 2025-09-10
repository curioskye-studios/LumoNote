package com.ckestudios.lumonote.ui.home.calendar.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class CalendarNotePreviewAdapter(private val setNoteIDToOpen: (Int) -> Unit)
    : RecyclerView.Adapter<CalendarNotePreviewAdapter.CalendarNotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()


    // The layout from which this view data is accessed is passed into this later
    class CalendarNotePreviewViewHolder (calendarNotePreviewView: View) : RecyclerView.ViewHolder(calendarNotePreviewView) {

        val noteCardPreview: CardView = calendarNotePreviewView.findViewById(R.id.notePreviewCV)
        val titlePreview: TextView = calendarNotePreviewView.findViewById(R.id.titlePreviewTV)
        val contentPreview: TextView = calendarNotePreviewView.findViewById(R.id.contentPreviewTV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarNotePreviewViewHolder {

        val calendarNotePreviewView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_note,
            parent, false)

        return CalendarNotePreviewViewHolder(calendarNotePreviewView)
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