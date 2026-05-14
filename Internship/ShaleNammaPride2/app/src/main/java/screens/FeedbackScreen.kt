package com.example.shalenammapride.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackScreen() {

    var feedback by remember { mutableStateOf("") }
    var anonymous by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(20.dp)) {

        OutlinedTextField(
            value = feedback,
            onValueChange = { feedback = it },
            label = { Text("Feedback") },
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Checkbox(
                checked = anonymous,
                onCheckedChange = {
                    anonymous = it
                }
            )

            Text("Submit Anonymously")
        }

        Button(onClick = { }) {
            Text("Submit")
        }
    }
}