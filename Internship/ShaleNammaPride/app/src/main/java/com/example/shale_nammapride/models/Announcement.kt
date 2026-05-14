package com.example.shale_nammapride.models

data class Announcement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "Notice", // "Holiday", "Event", "Exam", "Notice"
    val date: String = "",
    val timestamp: Long = 0
)