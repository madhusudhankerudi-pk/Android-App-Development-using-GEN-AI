package com.example.shale_nammapride.data.model

import com.google.firebase.Timestamp

data class Feedback(
    val id: String = "",
    val userId: String? = null, // null if anonymous
    val content: String = "",
    val type: String = "Suggestion", // Suggestion or Complaint
    val isAnonymous: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)
