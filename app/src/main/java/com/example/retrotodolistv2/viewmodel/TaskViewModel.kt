package com.example.retrotodolistv2.viewmodel

import androidx.lifecycle.*
import com.example.retrotodolistv2.data.TaskEntity
import com.example.retrotodolistv2.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<TaskEntity>> = repository.allTasks.asLiveData()

    fun insert(task: TaskEntity) = viewModelScope.launch {
        repository.insert(task)
    }

    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
    }

    fun update(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 