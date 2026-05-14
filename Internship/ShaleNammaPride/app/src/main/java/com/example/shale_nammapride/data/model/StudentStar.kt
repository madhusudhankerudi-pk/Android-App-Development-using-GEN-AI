package com.example.shale_nammapride.data.model

import com.google.firebase.Timestamp

data class StudentStar(
    val id: String = "",
    val studentName: String = "",
    val achievement: String = "",
    val appreciationText: String = "",
    val imageUrl: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
