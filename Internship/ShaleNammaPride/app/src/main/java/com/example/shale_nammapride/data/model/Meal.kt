package com.example.shale_nammapride.data.model

import com.google.firebase.Timestamp

data class Meal(
    val id: String = "",
    val menuItems: List<String> = emptyList(),
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val uploadedBy: String = ""
)
