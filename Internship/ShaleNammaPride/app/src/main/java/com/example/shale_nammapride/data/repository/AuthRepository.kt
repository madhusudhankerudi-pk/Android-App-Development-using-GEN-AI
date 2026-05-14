package com.example.shale_nammapride.data.repository

import com.example.shale_nammapride.data.model.User
import com.example.shale_nammapride.data.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun logout()
    suspend fun getUserDetails(uid: String): User?
}
