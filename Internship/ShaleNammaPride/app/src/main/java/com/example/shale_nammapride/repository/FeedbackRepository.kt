package com.example.shale_nammapride.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.example.shale_nammapride.models.Feedback
import com.google.firebase.database.*

class FeedbackRepository {
    // Use lazy to avoid blocking the main thread during instantiation
    private val database: DatabaseReference by lazy { 
        FirebaseDatabase.getInstance().getReference("Feedback") 
    }

    fun submitFeedback(feedback: Feedback, onComplete: (Boolean, String?) -> Unit) {
        try {
            val id = database.push().key ?: return
            val finalFeedback = feedback.copy(feedbackId = id, timestamp = System.currentTimeMillis())
            database.child(id).setValue(finalFeedback)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        } catch (e: Exception) {
            onComplete(false, e.message)
        }
    }

    fun getAllFeedbacks(): LiveData<List<Feedback>> {
        val feedbackLiveData = MutableLiveData<List<Feedback>>()
        try {
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Feedback>()
                    for (data in snapshot.children) {
                        val feedback = data.getValue(Feedback::class.java)
                        if (feedback != null) list.add(feedback)
                    }
                    list.sortByDescending { it.timestamp }
                    feedbackLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FeedbackRepo", "Database Error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("FeedbackRepo", "Firebase Error: ${e.message}")
        }
        return feedbackLiveData
    }
}