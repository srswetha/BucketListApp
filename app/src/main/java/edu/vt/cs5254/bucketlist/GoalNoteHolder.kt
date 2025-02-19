package edu.vt.cs5254.bucketlist

import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.bucketlist.databinding.ListItemGoalNoteBinding

class GoalNoteHolder(private val binding: ListItemGoalNoteBinding) :
    RecyclerView.ViewHolder(binding.root) {
    lateinit var boundNote: GoalNote
        private set

    fun bind(note: GoalNote) {
        boundNote = note
        binding.goalNoteButton.isEnabled = false
        when (note.type) {
            GoalNoteType.PAUSED -> {
                binding.goalNoteButton.text = "PAUSED"
            }
            GoalNoteType.COMPLETED -> {
                binding.goalNoteButton.text = "COMPLETED"
            }
            else -> {
                binding.goalNoteButton.text = note.text
            }
        }
    }
}