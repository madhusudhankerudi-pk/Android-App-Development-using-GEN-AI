package com.example.shale_nammapride.models

data class Feedback(
    val feedbackId: String = "",
    val message: String = "",
    val category: String = "",
    val anonymous: Boolean = true,
    val userId: String? = null,
    val userName: String? = null,
    val timestamp: Long = 0
)