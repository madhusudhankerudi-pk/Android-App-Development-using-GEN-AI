package com.example.shalenammapride.model

data class Feedback(
    val message: String = "",
    val anonymous: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)