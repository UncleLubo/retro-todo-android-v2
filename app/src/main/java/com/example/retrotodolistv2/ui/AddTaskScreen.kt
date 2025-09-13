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
import androidx.compose.ui.graphics.Color // Pridaný import
import androidx.compose.ui.graphics.RectangleShape // Pridaný import
import androidx.compose.foundation.border // Pridaný import
import androidx.compose.foundation.BorderStroke // Pridaný import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Mierne zväčšené pre lepší vzhľad
        ) {
            Text(
                text = "Add Task", // Môžeš zvážiť zmenu na "[ Add Task ]" alebo podobne pre retro konzistenciu
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Task title") },
                singleLine = true,
                shape = RectangleShape, // Pridaný parameter
                colors = TextFieldDefaults.colors( // Pridaný parameter
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background // Explicitne pre disabled stav
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (title.isNotBlank()) onSave(title)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.onBackground) // Pridaný border
            )

            Row(
                modifier = Modifier.fillMaxWidth(), // Aby sa tlačidlá roztiahli
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) onSave(title)
                    },
                    enabled = title.isNotBlank(),
                    shape = RectangleShape, // Pridaný parameter
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), // Pridaný parameter
                    colors = ButtonDefaults.buttonColors( // Pridaný parameter
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f), // Vizuálne odlíšenie disabled
                        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.weight(1f) // Aby tlačidlá mali rovnakú šírku
                ) { Text("Add") } // Môžeš zvážiť "[ Save ]"

                // OutlinedButton zmenený na Button
                Button(
                    onClick = onCancel,
                    shape = RectangleShape, // Pridaný parameter
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground), // Pridaný parameter
                    colors = ButtonDefaults.buttonColors( // Pridaný parameter
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.weight(1f) // Aby tlačidlá mali rovnakú šírku
                ) { Text("Cancel") } // Môžeš zvážiť "[ Cancel ]"
            }
        }
    }
}
