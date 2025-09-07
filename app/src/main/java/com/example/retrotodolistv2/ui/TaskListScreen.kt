package com.example.retrotodolistv2.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.retrotodolistv2.data.TaskEntity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onToggleDone: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onTogglePriority: (TaskEntity) -> Unit,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stavové premenné pre dialógové okno
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var taskToAnimate by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tasks heading
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Task list
            LazyColumn {
                items(tasks, key = { it.id }) { task ->

                    val visibleState = remember {
                        MutableTransitionState(false).apply { targetState = true }
                    }

                    // Spustenie animácie zmazania
                    LaunchedEffect(taskToAnimate) {
                        if (taskToAnimate?.id == task.id) {
                            visibleState.targetState = false
                            taskToAnimate = null
                        }
                    }

                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onToggleDone(task) },
                                    onLongClick = {
                                        taskToDelete = task
                                        showDialog = true
                                    }
                                )
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // priority star
                            Text(
                                text = if (task.isHighPriority) "⭐" else "☆",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { onTogglePriority(task) }
                            )

                            // title with fade / strikethrough (keeps existing animation)
                            val alpha by animateFloatAsState(
                                targetValue = if (task.isDone) 0.4f else 1f,
                                label = "doneAlpha"
                            )
                            val deco =
                                if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                                textDecoration = deco
                            )

                            // checkbox
                            Checkbox(
                                checked = task.isDone,
                                onCheckedChange = { onToggleDone(task) }
                            )
                        }
                    }

                    // after fade-out finishes, delete from DB
                    LaunchedEffect(visibleState.currentState) {
                        if (!visibleState.currentState && visibleState.isIdle) {
                            onDeleteTask(task)
                        }
                    }
                }
            }
        }

        // Dialógové okno na potvrdenie zmazania
        if (showDialog && taskToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    taskToDelete = null
                },
                title = { Text("Potvrdenie zmazania") },
                text = { Text("Naozaj chcete zmazať túto úlohu?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            taskToDelete?.let { task ->
                                // Spustíme animáciu zmazania
                                taskToAnimate = task
                            }
                            showDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text("Áno")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            taskToDelete = null
                        }
                    ) {
                        Text("Nie")
                    }
                }
            )
        }
    }
}
