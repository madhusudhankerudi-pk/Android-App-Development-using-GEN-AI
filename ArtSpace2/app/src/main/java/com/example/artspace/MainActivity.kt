package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArtSpaceApp()
                }
            }
        }
    }
}

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)
@Composable
fun ArtSpaceApp() {

    val artworks = listOf(
        Artwork(R.drawable.r, "The Horse", "Sharath", "2002"),
        Artwork(R.drawable.oip, "Butterfly skitch", "bheema", "2022"),
        Artwork(R.drawable.iaa_a_000165_1_s, "Lamp", "Madhu", "2021"),

    )

    var currentIndex by remember { mutableStateOf(0) }

    val currentArtwork = artworks[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(id = currentArtwork.imageRes),
            contentDescription = currentArtwork.title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < 0) {
                            // Swipe Left → Next
                            currentIndex =
                                if (currentIndex < artworks.lastIndex) currentIndex + 1
                                else 0
                        } else {
                            // Swipe Right → Previous
                            currentIndex =
                                if (currentIndex > 0) currentIndex - 1
                                else artworks.lastIndex
                        }
                    }
                }
        )
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = currentArtwork.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${currentArtwork.artist} (${currentArtwork.year})",
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(onClick = {
                currentIndex =
                    if (currentIndex > 0) currentIndex - 1
                    else artworks.lastIndex
            }) {
                Text("Previous")
            }

            Button(onClick = {
                currentIndex =
                    if (currentIndex < artworks.lastIndex) currentIndex + 1
                    else 0
            }) {
                Text("Next")
            }
        }
    }
}