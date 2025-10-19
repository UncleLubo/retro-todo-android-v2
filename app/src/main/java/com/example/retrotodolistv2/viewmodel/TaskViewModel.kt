package com.example.retrotodolistv2.viewmodel

import androidx.lifecycle.*
import com.example.retrotodolistv2.data.TaskEntity
import com.example.retrotodolistv2.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<TaskEntity>> = repository.allTasks.asLiveData()

    // Edit state
    private val _editingTaskId = MutableLiveData<Int?>(null)
    val editingTaskId: LiveData<Int?> = _editingTaskId

    private val _editingText = MutableLiveData<String>("")
    val editingText: LiveData<String> = _editingText

    private val _originalTaskTitle = MutableLiveData<String>("")
    val originalTaskTitle: LiveData<String> = _originalTaskTitle

    val isEditing: Boolean get() = _editingTaskId.value != null

    fun insert(title: String, isHighPriority: Boolean) = viewModelScope.launch {
        val task = TaskEntity(title = title, isHighPriority = isHighPriority)
        repository.insert(task)
    }

    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
    }

    fun update(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
    }

    fun cycleTaskState(task: TaskEntity) = viewModelScope.launch {
        when {
            // Stav: [ ] (isDone=false, isHighPriority=false) -> Prepnúť na [!]
            !task.isDone && !task.isHighPriority -> {
                repository.update(task.copy(isDone = false, isHighPriority = true))
            }
            // Stav: [!] (isDone=false, isHighPriority=true) -> Prepnúť na [x]
            !task.isDone && task.isHighPriority -> {
                repository.update(task.copy(isDone = true, isHighPriority = false))
            }
            // Stav: [x] (isDone=true) -> Prepnúť na [ ]
            task.isDone -> {
                repository.update(task.copy(isDone = false, isHighPriority = false))
            }
            // Záchranný prípad, ak by isDone=true a isHighPriority=true
            else -> {
                repository.update(task.copy(isDone = false, isHighPriority = false))
            }
        }
    }

    // Edit events
    fun startEdit(taskId: Int, taskTitle: String) {
        _editingTaskId.value = taskId
        _editingText.value = taskTitle
        _originalTaskTitle.value = taskTitle
    }

    fun updateEditingText(text: String) {
        _editingText.value = text
    }

    fun confirmEdit() = viewModelScope.launch {
        val taskId = _editingTaskId.value
        val newText = _editingText.value
        if (taskId != null && !newText.isNullOrBlank()) {
            // Find the task and update it
            val tasks = allTasks.value
            val taskToUpdate = tasks?.find { it.id == taskId }
            if (taskToUpdate != null) {
                repository.update(taskToUpdate.copy(title = newText))
            }
        }
        cancelEdit()
    }

    fun cancelEdit() {
        _editingTaskId.value = null
        _editingText.value = ""
        _originalTaskTitle.value = ""
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