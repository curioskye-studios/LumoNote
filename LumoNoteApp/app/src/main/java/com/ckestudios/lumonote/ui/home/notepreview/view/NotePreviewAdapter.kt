package com.ckestudios.lumonote.ui.home.notepreview.view

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.SpanProcessor


// Inherits from RecyclerView.Adapter to allow definition of recycler view behaviour
class NotePreviewAdapter(private val setNoteIDToOpen: (Int) -> Unit,
                         private val whenCurrentNotePinClicked: (Boolean, Int) -> Unit)
    : RecyclerView.Adapter<NotePreviewAdapter.NotePreviewViewHolder>() {

    private val notesList = mutableListOf<Note>()


    // The layout from which this view data is accessed is passed into this later
    class NotePreviewViewHolder (notePreviewView: View) : RecyclerView.ViewHolder(notePreviewView) {

        val noteCardPreview: CardView = notePreviewView.findViewById(R.id.notePreviewCV)
        val noteLinearLayout: LinearLayout = notePreviewView.findViewById(R.id.notePreviewLL)
        val titlePreview: TextView = notePreviewView.findViewById(R.id.titlePreviewTV)
        val contentPreview: TextView = notePreviewView.findViewById(R.id.contentPreviewTV)
        val pinPreview: ImageView = notePreviewView.findViewById(R.id.previewPinIV)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotePreviewViewHolder {

        val notePreviewView = LayoutInflater.from(parent.context).inflate(R.layout.item_note_preview,
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

        val showTitleView = note.noteTitle.isNotEmpty()
        GeneralUIHelper.changeViewVisibility(holder.titlePreview, showTitleView)

        val showContentView = note.noteContent.isNotEmpty()
        GeneralUIHelper.changeViewVisibility(holder.contentPreview, showContentView)

        SpanProcessor.reapplySpansTV(note.noteSpans, holder.contentPreview)
        GeneralUIHelper.replaceTextViewObjectChars(holder.contentPreview)

        holder.pinPreview.tag = note.notePinned
        GeneralButtonIVHelper.updatePinHighlight(holder.pinPreview, holder.pinPreview.context,
            R.drawable.selected_background)


        holder.noteCardPreview.setOnClickListener {

            GeneralUIHelper.playViewSelectionIndication(holder.itemView.context,
                holder.noteLinearLayout, R.color.dark_grey, R.color.dark_grey_selected)

            Handler(Looper.getMainLooper()).postDelayed({

                setNoteIDToOpen(note.noteID)
            }, 600) // Delay in milliseconds (500ms = 0.5 seconds)
        }

        // Pin preview of note (toggle gold)
        holder.pinPreview.setOnClickListener {

            val pinnedFlag = holder.pinPreview.tag as Boolean

//            Log.d("NoteFrag", "pinnedFlag: $pinnedFlag")

            holder.pinPreview.tag = !pinnedFlag

            val currentNoteID = note.noteID

            GeneralButtonIVHelper.updatePinHighlight(holder.pinPreview, holder.pinPreview.context,
                R.drawable.selected_background)

            Handler(Looper.getMainLooper()).postDelayed({

                whenCurrentNotePinClicked(!pinnedFlag, currentNoteID)
            }, 300)

        }
    }


    // Ensure UI stays up-to-date with notes list
    fun refreshData(newNotes: List<Note>) {

        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }

}