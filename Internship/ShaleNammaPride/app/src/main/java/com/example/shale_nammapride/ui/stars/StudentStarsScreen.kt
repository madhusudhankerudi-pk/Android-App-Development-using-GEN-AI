package com.example.shale_nammapride.ui.stars

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.shale_nammapride.data.model.StudentStar
import com.example.shale_nammapride.data.model.UserRole
import com.example.shale_nammapride.ui.theme.SchoolPrimary
import com.example.shale_nammapride.ui.theme.SoftPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentStarsScreen(
    userRole: UserRole,
    onBack: () -> Unit,
    viewModel: StudentStarsViewModel
) {
    val stars by viewModel.stars.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Stars") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (userRole == UserRole.ADMIN || userRole == UserRole.TEACHER) {
                FloatingActionButton(
                    onClick = { showUploadDialog = true },
                    containerColor = SchoolPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Star")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(stars) { star ->
                StarCard(star)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showUploadDialog) {
        UploadStarDialog(
            viewModel = viewModel,
            onDismiss = { showUploadDialog = false }
        )
    }
}

@Composable
fun StarCard(star: StudentStar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = star.imageUrl,
                contentDescription = star.studentName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = star.studentName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = star.achievement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SchoolPrimary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftPink.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = star.appreciationText,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UploadStarDialog(
    viewModel: StudentStarsViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var achievement by remember { mutableStateOf("") }
    val appreciation by viewModel.appreciationState.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appreciate a Student") },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = achievement,
                    onValueChange = { achievement = it },
                    label = { Text("Achievement") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { viewModel.generateAppreciation(name, achievement) },
                    enabled = name.isNotBlank() && achievement.isNotBlank()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Generate Appreciation")
                }
                
                if (appreciation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = appreciation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(if (imageUri == null) "Select Photo" else "Photo Selected")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (imageUri != null) {
                        viewModel.uploadStar(name, achievement, appreciation, imageUri!!)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() && achievement.isNotBlank() && imageUri != null
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
