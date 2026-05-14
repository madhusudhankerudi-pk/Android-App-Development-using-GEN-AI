package com.example.shale_nammapride.models

data class MealUpdate(
    val date: String = "",
    val imageUrl: String = "",
    val menu: String = "",
    val uploadedBy: String = "",
    val timestamp: Long = 0
)