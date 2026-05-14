package com.example.shale_nammapride.data.model

enum class UserRole {
    ADMIN, TEACHER, PARENT
}

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: UserRole = UserRole.PARENT
)
