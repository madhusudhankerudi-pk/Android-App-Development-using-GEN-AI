package com.example.shale_nammapride.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shale_nammapride.data.model.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    init {
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("announcements")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .await()
                val list = snapshot.toObjects(Announcement::class.java)
                _announcements.value = list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
