package com.example.shale_nammapride.data.repository

import android.net.Uri
import com.example.shale_nammapride.data.model.StudentStar
import kotlinx.coroutines.flow.Flow

interface StudentStarRepository {
    fun getStudentStars(): Flow<List<StudentStar>>
    suspend fun uploadStudentStar(studentStar: StudentStar, imageUri: Uri): Result<Unit>
    suspend fun generateAppreciation(studentName: String, achievement: String): Result<String>
}
