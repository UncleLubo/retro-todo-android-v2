package com.example.retrotodolistv2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.retrotodolistv2.data.AppDatabase
import com.example.retrotodolistv2.data.TaskRepository
import com.example.retrotodolistv2.ui.AddTaskScreen
import com.example.retrotodolistv2.ui.TaskListScreen
import com.example.retrotodolistv2.ui.theme.RetroTodoListV2Theme
import com.example.retrotodolistv2.viewmodel.TaskViewModel
import com.example.retrotodolistv2.viewmodel.TaskViewModelFactory

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
                        val tasks by viewModel.allTasks.observeAsState(initial = emptyList())
                        val editingTaskId by viewModel.editingTaskId.observeAsState()
                        val editingText by viewModel.editingText.observeAsState()
                        val originalTaskTitle by viewModel.originalTaskTitle.observeAsState()

                        TaskListScreen(
                            tasks = tasks,
                            onCycleTaskState = { task -> viewModel.cycleTaskState(task) },
                            onDeleteTask = { task -> viewModel.delete(task) },
                            onNavigateToAdd = { navController.navigate("add") },
                            onUpdateTask = { task -> viewModel.update(task) },
                            editingTaskId = editingTaskId,
                            editingText = editingText ?: "",
                            originalTaskTitle = originalTaskTitle ?: "",
                            isEditing = viewModel.isEditing,
                            onStartEdit = { id, title -> viewModel.startEdit(id, title) },
                            onUpdateEditingText = { text -> viewModel.updateEditingText(text) },
                            onConfirmEdit = { viewModel.confirmEdit() },
                            onCancelEdit = { viewModel.cancelEdit() }
                        )
                    }
                    composable("add") {
                        AddTaskScreen(
                            onSave = { title ->
                                viewModel.insert(title, false) // Dočasné riešenie: vždy nízka priorita
                                navController.popBackStack()
                            },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
