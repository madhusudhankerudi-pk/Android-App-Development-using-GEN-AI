package com.example.shale_nammapride.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.example.shale_nammapride.models.StudentStar
import com.google.firebase.database.*

class AchievementRepository {
    // Use lazy to avoid blocking the main thread during instantiation
    private val database: DatabaseReference by lazy { 
        FirebaseDatabase.getInstance().getReference("Achievements") 
    }

    fun getAchievements(): LiveData<List<StudentStar>> {
        val achievementsLiveData = MutableLiveData<List<StudentStar>>()
        try {
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<StudentStar>()
                    for (data in snapshot.children) {
                        val item = data.getValue(StudentStar::class.java)
                        if (item != null) {
                            list.add(item)
                        }
                    }
                    list.sortByDescending { it.timestamp }
                    achievementsLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AchievementRepo", "Database Error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("AchievementRepo", "Firebase Error: ${e.message}")
        }
        return achievementsLiveData
    }

    fun deleteAchievement(id: String, onComplete: (Boolean) -> Unit) {
        try {
            database.child(id).removeValue().addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
        } catch (e: Exception) {
            onComplete(false)
        }
    }
}