package com.example.shale_nammapride.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shale_nammapride.ui.components.FeatureCard
import com.example.shale_nammapride.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val announcements by viewModel.announcements.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Shale-Namma Pride", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Hello, $userName", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(SchoolPrimary, SchoolSecondary)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.CenterStart)
                    ) {
                        Text(
                            "Together we build\na better tomorrow",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // School illustration would go here
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    FeatureCard(
                        title = "Daily Meal",
                        icon = Icons.Default.Restaurant,
                        containerColor = Color(0xFFFFA726),
                        onClick = { onNavigate("daily_meal") },
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Facility Tour",
                        icon = Icons.Default.Apartment,
                        containerColor = Color(0xFF66BB6A),
                        onClick = { onNavigate("facility_tour") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    FeatureCard(
                        title = "Student Stars",
                        icon = Icons.Default.EmojiEvents,
                        containerColor = Color(0xFFAB47BC),
                        onClick = { onNavigate("student_stars") },
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        title = "Feedback Box",
                        icon = Icons.Default.ChatBubble,
                        containerColor = Color(0xFFEC407A),
                        onClick = { onNavigate("feedback") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Announcements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigate("announcements") }) {
                        Text("View All")
                    }
                }
            }

            items(announcements) { announcement ->
                AnnouncementItem(announcement)
            }
        }
    }
}

@Composable
fun AnnouncementItem(announcement: com.example.shale_nammapride.data.model.Announcement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftBlue.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Campaign,
                contentDescription = null,
                tint = SchoolPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = announcement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = announcement.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}
