package com.example.shale_nammapride.models

data class StudentStar(
    val id: String = "",
    val name: String = "",
    val grade: String = "",
    val achievement: String = "",
    val imageUrl: String = "",
    val isStudentOfWeek: Boolean = false,
    val timestamp: Long = 0
)