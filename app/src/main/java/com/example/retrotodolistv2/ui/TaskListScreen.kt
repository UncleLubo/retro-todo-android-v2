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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onAddTask: (String, Boolean) -> Unit,
    onToggleDone: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onTogglePriority: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTaskTitle by remember { mutableStateOf("") }
    var isHighPriority by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Input field + Add button
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                placeholder = { Text("Enter task") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newTaskTitle.isNotBlank()) {
                            onAddTask(newTaskTitle, isHighPriority)
                            newTaskTitle = ""
                            isHighPriority = false
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newTaskTitle.isNotBlank()) {
                    onAddTask(newTaskTitle, isHighPriority)
                    newTaskTitle = ""
                    isHighPriority = false
                }
            }) {
                Text("Add")
            }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(
                checked = isHighPriority,
                onCheckedChange = { isHighPriority = it }
            )
            Text("High Priority")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Task list
        LazyColumn {
            items(tasks, key = { it.id }) { task ->

                val visibleState = remember {
                    MutableTransitionState(false).apply { targetState = true }
                }

                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit  = fadeOut(animationSpec = tween(300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick     = { onToggleDone(task) },
                                onLongClick = { visibleState.targetState = false }
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
                        val deco = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                        Text(
                            text           = task.title,
                            style          = MaterialTheme.typography.bodyLarge,
                            color          = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                            textDecoration = deco
                        )

                        // checkbox
                        Checkbox(
                            checked          = task.isDone,
                            onCheckedChange  = { onToggleDone(task) }
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
} 