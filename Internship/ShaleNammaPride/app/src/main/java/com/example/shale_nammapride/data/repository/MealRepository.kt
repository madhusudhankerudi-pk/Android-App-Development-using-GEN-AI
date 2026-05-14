package com.example.shale_nammapride.data.repository

import android.net.Uri
import com.example.shale_nammapride.data.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getDailyMeal(): Flow<Meal?>
    suspend fun uploadMeal(meal: Meal, imageUri: Uri): Result<Unit>
    suspend fun canUploadToday(): Boolean
}
