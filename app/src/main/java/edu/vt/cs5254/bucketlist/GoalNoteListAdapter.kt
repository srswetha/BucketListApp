package edu.vt.cs5254.bucketlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.bucketlist.GoalNote
import edu.vt.cs5254.bucketlist.GoalNoteHolder  // Import from main package
import edu.vt.cs5254.bucketlist.GoalNoteType
import edu.vt.cs5254.bucketlist.databinding.ListItemGoalNoteBinding

class GoalNoteListAdapter : RecyclerView.Adapter<GoalNoteHolder>() {
    private var notes: List<GoalNote> = emptyList()

    fun getNotes(): List<GoalNote> = notes

    fun submitList(newNotes: List<GoalNote>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalNoteHolder {
        val binding = ListItemGoalNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalNoteHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalNoteHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size
}