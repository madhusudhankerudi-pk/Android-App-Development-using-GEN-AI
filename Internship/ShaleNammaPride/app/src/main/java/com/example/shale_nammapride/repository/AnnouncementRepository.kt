package com.example.shale_nammapride.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.example.shale_nammapride.models.Announcement
import com.google.firebase.database.*

class AnnouncementRepository {
    // Use lazy to avoid blocking the main thread during instantiation
    private val database: DatabaseReference by lazy { 
        FirebaseDatabase.getInstance().getReference("Announcements") 
    }

    fun getAnnouncements(): LiveData<List<Announcement>> {
        val announcementsLiveData = MutableLiveData<List<Announcement>>()
        try {
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Announcement>()
                    for (data in snapshot.children) {
                        val announcement = data.getValue(Announcement::class.java)
                        if (announcement != null) {
                            list.add(announcement)
                        }
                    }
                    list.sortByDescending { it.timestamp }
                    announcementsLiveData.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AnnouncementRepo", "Database Error: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("AnnouncementRepo", "Firebase Error: ${e.message}")
        }
        return announcementsLiveData
    }

    fun addAnnouncement(announcement: Announcement, onComplete: (Boolean, String?) -> Unit) {
        try {
            val id = database.push().key ?: return
            val newAnnouncement = announcement.copy(id = id, timestamp = System.currentTimeMillis())
            database.child(id).setValue(newAnnouncement)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        } catch (e: Exception) {
            onComplete(false, e.message)
        }
    }
}