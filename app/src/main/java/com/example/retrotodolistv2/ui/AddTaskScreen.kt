package com.example.retrotodolistv2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background // Import for background

@OptIn(ExperimentalMaterial3Api::class) // Added for Scaffold
@Composable
fun AddTaskScreen(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    Scaffold { innerPadding -> // Added Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Added innerPadding from Scaffold
                .background(MaterialTheme.colorScheme.background) // Added background color
                .padding(16.dp), // Kept existing padding
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add Task",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Task title") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.isNotBlank()) onSave(title)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) onSave(title)
                    },
                    enabled = title.isNotBlank()
                ) { Text("Save") }

                OutlinedButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}
