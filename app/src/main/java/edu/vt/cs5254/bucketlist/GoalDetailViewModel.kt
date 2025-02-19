package edu.vt.cs5254.bucketlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GoalDetailViewModel(private val goalId: UUID) : ViewModel(){

    private val repository = GoalRepository.get()
    private val _goal: MutableStateFlow<Goal?> = MutableStateFlow(null)
    val goal
        get() = _goal.asStateFlow()

    init {
        viewModelScope.launch {
            _goal.value = repository.getGoal(goalId)
        }

    }
    fun updateGoal(onUpdate: (Goal) -> Goal) {
        _goal.update { oldGoal ->
            val newGoal = oldGoal?.let { onUpdate(it) } ?: return
            if (newGoal == oldGoal && newGoal.notes == oldGoal.notes) return
            val updatedGoal = newGoal.copy(lastUpdated = System.currentTimeMillis())
                .apply { notes = newGoal.notes }


            viewModelScope.launch {
                repository.updateGoal(updatedGoal)
            }

            updatedGoal
        }
    }


    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {

        }
    }
}

class GoalDetailViewModelFactory(private  val goalId: UUID): ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoalDetailViewModel(goalId) as T
    }
}