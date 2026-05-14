package com.example.shale_nammapride.data.repository

import com.example.shale_nammapride.data.model.Feedback
import kotlinx.coroutines.flow.Flow

interface FeedbackRepository {
    suspend fun submitFeedback(feedback: Feedback): Result<Unit>
    fun getFeedbacks(): Flow<List<Feedback>>
}
