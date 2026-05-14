package com.example.shale_nammapride.ui.facility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.AsyncImage
import com.example.shale_nammapride.ui.theme.SchoolPrimary

data class Facility(
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: String
)

val facilities = listOf(
    Facility("Science Lab", "Well equipped science lab for hands-on learning", "https://example.com/lab.jpg", "Labs"),
    Facility("School Library", "A quiet place with a vast collection of books", "https://example.com/library.jpg", "Library"),
    Facility("Smart Classroom", "Modern classrooms with digital teaching aids", "https://example.com/class.jpg", "Classes"),
    Facility("Playground", "Spacious area for sports and physical activities", "https://example.com/play.jpg", "Playground"),
    Facility("Clean Toilets", "Hygienic and well-maintained sanitation facilities", "https://example.com/toilet.jpg", "Toilets")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityTourScreen(onBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Labs", "Library", "Classes", "Toilets", "Playground")
    
    val filteredFacilities = if (selectedCategory == "All") facilities else facilities.filter { it.category == selectedCategory }
    val pagerState = rememberPagerState(pageCount = { filteredFacilities.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Facility Tour") },
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
        ) {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredFacilities.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 16.dp),
                        pageSpacing = 16.dp
                    ) { page ->
                        val facility = filteredFacilities[page]
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            AsyncImage(
                                model = facility.imageUrl,
                                contentDescription = facility.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        val currentFacility = filteredFacilities[pagerState.currentPage]
                        Text(
                            text = currentFacility.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = SchoolPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentFacility.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Indicator
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(filteredFacilities.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) SchoolPrimary else Color.LightGray
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(color)
                                        .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
