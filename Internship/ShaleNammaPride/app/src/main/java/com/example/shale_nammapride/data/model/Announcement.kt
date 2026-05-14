package com.example.shale_nammapride.data.model

import com.google.firebase.Timestamp

data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: Timestamp = Timestamp.now()
)
