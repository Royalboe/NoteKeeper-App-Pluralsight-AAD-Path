package com.example.notekeeperapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteRecyclerAdapter(private val context: Context, private val notes: List<NoteInfo>):
    RecyclerView.Adapter<NoteRecyclerAdapter.NoteRecyclerViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    private var onNoteSelectedListener: OnNoteSelectedListener? = null

    inner class NoteRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCourse: TextView? = itemView.findViewById<TextView?>(R.id.textCourse)
        val textTitle: TextView? = itemView.findViewById<TextView>(R.id.textTitle)
        var notePosition = 0
        var color: View = itemView.findViewById(R.id.noteColor)
        init {
            itemView.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra(NOTE_POSITION, notePosition)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteRecyclerViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_view, parent, false)
        return NoteRecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteRecyclerViewHolder, position: Int) {
        val note = notes[position]
        holder.textCourse?.text = note.course?.title
        holder.textTitle?.text = note.title
        holder.notePosition = position
        holder.color.setBackgroundColor(note.color)
    }

    override fun getItemCount() = notes.size

    fun setOnSelectedListener(listener: OnNoteSelectedListener) {
        onNoteSelectedListener = listener
    }

    interface OnNoteSelectedListener {
        fun onNoteSelected(note: NoteInfo)
    }

}
