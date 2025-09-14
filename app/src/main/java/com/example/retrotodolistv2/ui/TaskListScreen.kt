package com.example.retrotodolistv2.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.retrotodolistv2.data.TaskEntity

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onToggleDone: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onTogglePriority: (TaskEntity) -> Unit,
    onNavigateToAdd: () -> Unit,
    onUpdateTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var taskToAnimate by remember { mutableStateOf<TaskEntity?>(null) }

    var editingTaskId by remember { mutableStateOf<Int?>(null) }
    var editingTaskValue by remember { mutableStateOf(TextFieldValue("")) } // Zmenené na TextFieldValue
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = editingTaskId != null) {
        editingTaskId = null
    }

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
                    Text("Add Task")
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
                        }
                    }

                    AnimatedVisibility(
                        visibleState = visibleState,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        if (task.id == editingTaskId) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                            .clickable(enabled = false) {}
                                    )
                                    BasicTextField(
                                        value = editingTaskValue,
                                        onValueChange = { newValue ->
                                            if (newValue.text.length <= 25) {
                                                editingTaskValue = newValue.copy(text = newValue.text.filter { it != '\n' })
                                            } else {
                                                // Ak je text dlhší, orežeme ho a zachováme pozíciu kurzora (ak je to možné)
                                                val newText = newValue.text.substring(0, 25)
                                                editingTaskValue = TextFieldValue(
                                                    text = newText,
                                                    selection = TextRange(minOf(newValue.selection.start, 25), minOf(newValue.selection.end, 25))
                                                )
                                            }
                                        },
                                        textStyle = TextStyle(
                                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                                            letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                                            color = MaterialTheme.colorScheme.onBackground
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (editingTaskValue.text.isNotBlank()) {
                                                    onUpdateTask(task.copy(title = editingTaskValue.text))
                                                }
                                                editingTaskId = null
                                            }
                                        ),
                                        singleLine = true,
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .weight(1f)
                                            .focusRequester(focusRequester)
                                            .onKeyEvent {
                                                if (it.key == Key.Escape) {
                                                    editingTaskId = null
                                                    true
                                                } else false
                                            }
                                    )
                                }
                                Text(
                                    text = if (task.isDone) "[x]" else "[ ]",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable(enabled = false) {}
                                )
                            }
                            LaunchedEffect(editingTaskId) { // Zmenené z Unit na editingTaskId
                                if (editingTaskId == task.id) { // Požiadaj o focus len ak je to aktuálne editovaná úloha
                                    focusRequester.requestFocus()
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onToggleDone(task) },
                                        onDoubleClick = {
                                            taskToDelete = task
                                            showDialog = true
                                        },
                                        onLongClick = {
                                            editingTaskId = task.id
                                            editingTaskValue = TextFieldValue(
                                                text = task.title,
                                                selection = TextRange(task.title.length) // Kurzor na koniec
                                            )
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
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
                                    }
                                    val deco =
                                        if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = titleColor,
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
                    }

                    LaunchedEffect(visibleState.currentState) {
                        if (!visibleState.currentState && visibleState.isIdle && taskToAnimate?.id == task.id) {
                            onDeleteTask(task)
                            taskToAnimate = null
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
                            taskToDelete?.let { taskToConfirm ->
                                taskToAnimate = taskToConfirm
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
