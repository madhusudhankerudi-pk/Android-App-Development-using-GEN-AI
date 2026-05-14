package com.example.shale_nammapride.ui.meal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restaurant
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
import com.example.shale_nammapride.data.model.UserRole
import com.example.shale_nammapride.ui.components.SchoolButton
import com.example.shale_nammapride.ui.theme.SchoolPrimary
import com.example.shale_nammapride.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMealScreen(
    userRole: UserRole,
    onBack: () -> Unit,
    viewModel: MealViewModel = viewModel()
) {
    val meal by viewModel.mealState.collectAsState()
    val canUpload by viewModel.canUpload.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Meal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            if ((userRole == UserRole.ADMIN || userRole == UserRole.TEACHER) && canUpload) {
                FloatingActionButton(
                    onClick = { showUploadDialog = true },
                    containerColor = SchoolPrimary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload Meal")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()),
                style = MaterialTheme.typography.titleMedium,
                color = SchoolPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (meal == null) {
                EmptyMealView()
            } else {
                MealDetailView(meal!!)
            }
        }
    }

    if (showUploadDialog) {
        UploadMealDialog(
            onDismiss = { showUploadDialog = false },
            onUpload = { menu, uri ->
                viewModel.uploadMeal(menu, uri)
                showUploadDialog = false
            }
        )
    }
}

@Composable
fun MealDetailView(meal: com.example.shale_nammapride.data.model.Meal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = "Today's Meal",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Today's Menu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                meal.menuItems.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(SchoolPrimary)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = item, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Today's meal has been uploaded",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMealView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No meal update for today yet", color = Color.Gray)
        }
    }
}

@Composable
fun UploadMealDialog(
    onDismiss: () -> Unit,
    onUpload: (List<String>, Uri) -> Unit
) {
    var menuText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Daily Meal") },
        text = {
            Column {
                OutlinedTextField(
                    value = menuText,
                    onValueChange = { menuText = it },
                    label = { Text("Menu Items (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(if (imageUri == null) "Select Image" else "Image Selected")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (menuText.isNotBlank() && imageUri != null) {
                        onUpload(menuText.split(",").map { it.trim() }, imageUri!!)
                    }
                },
                enabled = menuText.isNotBlank() && imageUri != null
            ) {
                Text("Upload")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
