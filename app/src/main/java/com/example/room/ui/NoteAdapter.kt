package com.example.room.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.room.R
import com.example.room.database.Note

class NoteAdapter (private val notesList: List<Note>,
                   private val onItemClick: (Note) -> Unit,
                   private val onItemLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_items, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.title.text = currentNote.title
        holder.date.text = currentNote.date
        holder.description.text = currentNote.description
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.itemTitle)
        val date: TextView = itemView.findViewById(R.id.itemDate)
        val description: TextView = itemView.findViewById(R.id.itemDescription)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(notesList[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(notesList[position])
                    true
                } else {
                    false
                }
            }
        }
    }
}