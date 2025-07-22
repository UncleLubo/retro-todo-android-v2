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
import com.example.retrotodolistv2.ui.theme.RetroTodoListV2Theme
import com.example.retrotodolistv2.viewmodel.TaskViewModel
import com.example.retrotodolistv2.viewmodel.TaskViewModelFactory
import androidx.core.view.WindowCompat

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 🔁 Sledovanie zoznamu úloh cez LiveData
                    val tasks by viewModel.allTasks.observeAsState(emptyList())

                    // 📋 Compose UI – hlavná obrazovka s úlohami
                    TaskListScreen(
                        tasks = tasks,
                        onAddTask = { title, isHighPriority ->
                            viewModel.insert(title, isHighPriority)
                        },
                        onToggleDone = { task ->
                            viewModel.update(task.copy(isDone = !task.isDone))
                        },
                        onDeleteTask = { task ->
                            viewModel.delete(task)
                        },
                        onTogglePriority = { task ->
                            viewModel.togglePriority(task)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}
