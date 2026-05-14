package com.example.shalenammapride.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun FacilityScreen() {

    val images = listOf(
        "image_url_1",
        "image_url_2"
    )

    LazyRow {
        items(images) {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .padding(10.dp)
            )
        }
    }
}