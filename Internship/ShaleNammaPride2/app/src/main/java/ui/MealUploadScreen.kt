package com.example.shalenammapride.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MealUploadScreen() {

    var menu by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Text(
            text = "Upload Daily Meal",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = menu,
            onValueChange = { menu = it },
            label = { Text("Today's Menu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { }) {
            Text("Upload")
        }
    }
}