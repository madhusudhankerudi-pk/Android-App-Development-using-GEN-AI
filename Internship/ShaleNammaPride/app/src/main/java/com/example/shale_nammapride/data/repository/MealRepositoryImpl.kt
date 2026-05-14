package com.example.shale_nammapride.data.repository

import android.net.Uri
import com.example.shale_nammapride.data.model.Meal
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class MealRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : MealRepository {

    override fun getDailyMeal(): Flow<Meal?> = callbackFlow {
        val subscription = firestore.collection("meals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val meal = snapshot?.documents?.firstOrNull()?.toObject(Meal::class.java)
                trySend(meal)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun uploadMeal(meal: Meal, imageUri: Uri): Result<Unit> {
        return try {
            if (!canUploadToday()) throw Exception("Only one meal upload per day allowed")

            val imageRef = storage.reference.child("meals/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            
            val finalMeal = meal.copy(imageUrl = downloadUrl, timestamp = Timestamp.now())
            firestore.collection("meals").add(finalMeal).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun canUploadToday(): Boolean {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val query = firestore.collection("meals")
            .whereGreaterThanOrEqualTo("timestamp", Timestamp(startOfDay))
            .limit(1)
            .get()
            .await()
        
        return query.isEmpty
    }
}
