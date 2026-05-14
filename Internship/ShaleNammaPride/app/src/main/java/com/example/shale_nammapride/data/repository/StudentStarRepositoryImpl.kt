package com.example.shale_nammapride.data.repository

import android.net.Uri
import com.example.shale_nammapride.data.model.StudentStar
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class StudentStarRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val generativeModel: GenerativeModel // Should be initialized with API Key
) : StudentStarRepository {

    override fun getStudentStars(): Flow<List<StudentStar>> = callbackFlow {
        val subscription = firestore.collection("student_stars")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val stars = snapshot?.documents?.mapNotNull { it.toObject(StudentStar::class.java) } ?: emptyList()
                trySend(stars)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun uploadStudentStar(studentStar: StudentStar, imageUri: Uri): Result<Unit> {
        return try {
            val imageRef = storage.reference.child("stars/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            
            val finalStar = studentStar.copy(imageUrl = downloadUrl, timestamp = Timestamp.now())
            firestore.collection("student_stars").add(finalStar).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateAppreciation(studentName: String, achievement: String): Result<String> {
        return try {
            val prompt = "Generate a short, encouraging appreciation message for a student named $studentName who won $achievement. Keep it child-friendly and inspiring."
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "Great job, $studentName!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
