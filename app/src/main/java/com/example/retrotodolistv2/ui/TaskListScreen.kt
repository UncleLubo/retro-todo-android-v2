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
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.retrotodolistv2.data.TaskEntity

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onCycleTaskState: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onNavigateToAdd: () -> Unit,
    onUpdateTask: (TaskEntity) -> Unit,
    editingTaskId: Int?,
    editingText: String,
    originalTaskTitle: String,
    isEditing: Boolean,
    onStartEdit: (Int, String) -> Unit,
    onUpdateEditingText: (String) -> Unit,
    onConfirmEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var taskToAnimate by remember { mutableStateOf<TaskEntity?>(null) }

    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = isEditing) {
        onCancelEdit()
    }

    Scaffold(
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(
                    onClick = onNavigateToAdd,
                    shape = RectangleShape,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), RectangleShape)
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            if (isEditing) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // Show Confirm/Cancel buttons when editing
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCancelEdit,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { contentDescription = "Cancel edit" },
                            shape = RectangleShape,
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = onConfirmEdit,
                            enabled = !editingText.isNullOrBlank() && editingText != originalTaskTitle,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { contentDescription = "Confirm edit" },
                            shape = RectangleShape,
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
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
                                    BasicTextField(
                                        value = TextFieldValue(
                                            text = editingText,
                                            selection = TextRange(editingText.length)
                                        ),
                                        onValueChange = { newValue ->
                                            val filteredText = newValue.text.filter { it != '\n' }
                                            if (filteredText.length <= 25) {
                                                onUpdateEditingText(filteredText)
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
                                                if (!editingText.isNullOrBlank() && editingText != originalTaskTitle) {
                                                    onConfirmEdit()
                                                }
                                            }
                                        ),
                                        singleLine = true,
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .weight(1f)
                                            .focusRequester(focusRequester)
                                            .onKeyEvent {
                                                if (it.key == Key.Escape) {
                                                    onCancelEdit()
                                                    true
                                                } else false
                                            }
                                    )
                                }
                                Text(
                                    text = when {
                                        task.isDone -> "[x]"
                                        task.isHighPriority -> "[!]"
                                        else -> "[ ]"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable(enabled = false) {}
                                )
                            }
                            LaunchedEffect(editingTaskId) {
                                if (editingTaskId == task.id) {
                                    focusRequester.requestFocus()
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { onCycleTaskState(task) },
                                        onDoubleClick = {
                                            taskToDelete = task
                                            showDialog = true
                                        },
                                        onLongClick = {
                                            onStartEdit(task.id, task.title)
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
                                    val alpha by animateFloatAsState(
                                        targetValue = if (task.isDone) 0.4f else 1f,
                                        label = "doneAlpha"
                                    )
                                    val textStyle = if (task.isHighPriority && !task.isDone) {
                                        MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic
                                        )
                                    } else {
                                        MaterialTheme.typography.bodyLarge
                                    }
                                    val deco =
                                        if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                                    Text(
                                        text = task.title,
                                        style = textStyle,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                                        textDecoration = deco
                                    )
                                }
                                Text(
                                    text = when {
                                        task.isDone -> "[x]"
                                        task.isHighPriority -> "[!]"
                                        else -> "[ ]"
                                    },
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
