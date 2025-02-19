package edu.vt.cs5254.bucketlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "goal")
data class Goal(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var lastUpdated: Long = System.currentTimeMillis()

) {
    @Ignore
    var notes: List<GoalNote> = listOf()

    val isPaused: Boolean
        get() = notes.any { it.type == GoalNoteType.PAUSED }

    val isCompleted: Boolean
        get() = notes.any { it.type == GoalNoteType.COMPLETED }

    val photoFileName get() = "IMG_$id.JPG"

    fun pause() {
        if (!isPaused) {
            notes = notes + GoalNote(type = GoalNoteType.PAUSED, goalId = id)
            lastUpdated = System.currentTimeMillis()
        }
    }

    fun unpause() {
        notes = notes.filter { it.type != GoalNoteType.PAUSED }
        lastUpdated = System.currentTimeMillis()
    }

    fun complete() {
        if (!isCompleted) {
            notes = notes + GoalNote(type = GoalNoteType.COMPLETED, goalId = id)
            lastUpdated = System.currentTimeMillis()
        }
    }

    fun uncomplete() {
        notes = notes.filter { it.type != GoalNoteType.COMPLETED }
        lastUpdated = System.currentTimeMillis()
    }
}


@Entity(tableName = "goal_note")
data class GoalNote(
    @PrimaryKey @ColumnInfo(name = "noteId") val id: UUID = UUID.randomUUID(),
    val text: String = "",
    val type: GoalNoteType,
    val goalId: UUID
)

enum class GoalNoteType {
    PROGRESS,
    PAUSED,
    COMPLETED,
}