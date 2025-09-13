package com.example.retrotodolistv2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.retrotodolistv2.data.TaskEntity

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
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var taskToAnimate by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentPadding = PaddingValues(0.dp)
            ) {
                Button(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RectangleShape,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Add Task") // Zmenený text tlačidla
                }
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
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    val visibleState = remember {
                        MutableTransitionState(false).apply { targetState = true }
                    }

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
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (task.isHighPriority) "⭐" else "☆",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable { onTogglePriority(task) }
                                )

                                val alpha by animateFloatAsState(
                                    targetValue = if (task.isDone) 0.4f else 1f,
                                    label = "doneAlpha"
                                )
                                val titleColor = if (task.isHighPriority) {
                                    MaterialTheme.colorScheme.secondary // Farba pre vysokú prioritu
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = alpha) // Pôvodná farba
                                }
                                val deco =
                                    if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = titleColor, // Aplikovaná dynamická farba
                                    textDecoration = deco
                                )
                            }

                            Text(
                                text = if (task.isDone) "[x]" else "[ ]",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    LaunchedEffect(visibleState.currentState) {
                        if (!visibleState.currentState && visibleState.isIdle) {
                            onDeleteTask(task)
                        }
                    }
                }
            }
        }

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
