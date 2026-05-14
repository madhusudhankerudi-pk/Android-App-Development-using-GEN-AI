package com.example.shale_nammapride.data.repository

import com.example.shale_nammapride.data.model.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FeedbackRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FeedbackRepository {

    override suspend fun submitFeedback(feedback: Feedback): Result<Unit> {
        return try {
            firestore.collection("feedbacks").add(feedback).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFeedbacks(): Flow<List<Feedback>> = callbackFlow {
        val subscription = firestore.collection("feedbacks")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val feedbacks = snapshot?.documents?.mapNotNull { it.toObject(Feedback::class.java) } ?: emptyList()
                trySend(feedbacks)
            }
        awaitClose { subscription.remove() }
    }
}
