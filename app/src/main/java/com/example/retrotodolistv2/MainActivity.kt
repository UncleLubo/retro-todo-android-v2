package com.example.retrotodolistv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.retrotodolistv2.data.AppDatabase
import com.example.retrotodolistv2.data.TaskEntity
import com.example.retrotodolistv2.data.TaskRepository
import com.example.retrotodolistv2.ui.TaskListScreen
import com.example.retrotodolistv2.ui.AddTaskScreen
import com.example.retrotodolistv2.ui.theme.RetroTodoListV2Theme
import com.example.retrotodolistv2.viewmodel.TaskViewModel
import com.example.retrotodolistv2.viewmodel.TaskViewModelFactory
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    // 🔹 Inicializácia ViewModel
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(
                AppDatabase.getDatabase(this).taskDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RetroTodoListV2Theme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "list"
                ) {
                    composable("list") {
                        val tasks by viewModel.allTasks.observeAsState(emptyList())
                        val editingTaskId by viewModel.editingTaskId.observeAsState(null)
                        val editingText by viewModel.editingText.observeAsState("")
                        val originalTaskTitle by viewModel.originalTaskTitle.observeAsState("")
                        val isEditing = viewModel.isEditing
                        
                        TaskListScreen(
                            tasks = tasks,
                            onToggleDone = { task -> viewModel.update(task.copy(isDone = !task.isDone)) },
                            onDeleteTask = { task -> viewModel.delete(task) },
                            onTogglePriority = { task -> viewModel.togglePriority(task) },
                            onNavigateToAdd = { navController.navigate("add") },
                            onUpdateTask = { task -> viewModel.update(task) },
                            editingTaskId = editingTaskId,
                            editingText = editingText,
                            originalTaskTitle = originalTaskTitle,
                            isEditing = isEditing,
                            onStartEdit = { taskId, taskTitle -> viewModel.startEdit(taskId, taskTitle) },
                            onUpdateEditingText = { text -> viewModel.updateEditingText(text) },
                            onConfirmEdit = { viewModel.confirmEdit() },
                            onCancelEdit = { viewModel.cancelEdit() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable("add") {
                        AddTaskScreen(
                            onSave = { title ->
                                viewModel.insert(title, false) // false for isHighPriority
                                navController.popBackStack() // go back to list
                            },
                            onCancel = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
