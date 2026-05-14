package com.example.shale_nammapride.ui.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shale_nammapride.ui.components.SchoolButton
import com.example.shale_nammapride.ui.theme.SchoolPrimary
import com.example.shale_nammapride.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    userId: String,
    onBack: () -> Unit,
    viewModel: FeedbackViewModel = viewModel()
) {
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Suggestion") }
    var isAnonymous by remember { mutableStateOf(false) }
    
    val submitState by viewModel.submitState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            snackbarHostState.showSnackbar("Feedback submitted successfully!")
            content = ""
            viewModel.resetState()
        } else if (submitState is UiState.Error) {
            snackbarHostState.showSnackbar((submitState as UiState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Feedback") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Type your feedback here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Type", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedType == "Suggestion",
                    onClick = { selectedType = "Suggestion" }
                )
                Text("Suggestion")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = selectedType == "Complaint",
                    onClick = { selectedType = "Complaint" }
                )
                Text("Complaint")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Submit Anonymously", fontWeight = FontWeight.Bold)
                    Text("Your name will not be stored", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Switch(
                    checked = isAnonymous,
                    onCheckedChange = { isAnonymous = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            SchoolButton(
                text = "Submit Feedback",
                onClick = {
                    viewModel.submitFeedback(content, selectedType, isAnonymous, userId)
                },
                isLoading = submitState is UiState.Loading
            )
        }
    }
}
