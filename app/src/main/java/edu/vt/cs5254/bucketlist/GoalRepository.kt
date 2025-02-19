package edu.vt.cs5254.bucketlist

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import edu.vt.cs5254.bucketlist.database.GoalDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "goal-database"
class GoalRepository private constructor(context: Context){

    private val database = databaseBuilder(
        context,
       GoalDatabase:: class.java,
        DATABASE_NAME
    ).createFromAsset(DATABASE_NAME)
        .build()

    fun getGoals(): Flow<List<Goal>>{

        val flowMultiMap = database.goalDao().getGoals()
        Log.d("GoalRepository", "Fetching goals: $flowMultiMap")
        return flowMultiMap.map { multiMap ->
            multiMap.keys.map {
                it.apply { notes = multiMap.getValue(it) }
            }
        }

    }

    suspend fun getGoal(id: UUID): Goal = database.goalDao().getGoalAndNotes(id)

    suspend fun updateGoal(goal: Goal) {
        database.goalDao().updateGoalAndNotes(goal)
    }
    suspend fun updateGoalAsync(goal: Goal) {
        coroutineScope {
            launch {
                database.goalDao().updateGoalAndNotes(goal)
            }
        }
    }
    suspend fun deleteGoal(goal: Goal){
        database.goalDao().deleteGoalAndNotes(goal)
    }


    suspend fun  addGoal(goal:Goal) = database.goalDao().addGoal(goal)

    companion object{
        private var INSTANCE: GoalRepository? = null

        fun initialize(context: Context){
            check(INSTANCE == null) { "Goal Repository is ALREADY initialize!!!" }
            INSTANCE = GoalRepository(context)
        }

        fun get(): GoalRepository{
            return checkNotNull(INSTANCE){"Goal Repository MUST BE initialized!!"}
        }
    }
}