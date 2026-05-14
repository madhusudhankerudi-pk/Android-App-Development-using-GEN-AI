package com.example.shale_nammapride.models

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "", // "Admin" or "Parent"
    val schoolName: String? = null,
    val childName: String? = null,
    val village: String? = null,
    val taluk: String? = null,
    val district: String? = null,
    val contactInfo: String? = null
)