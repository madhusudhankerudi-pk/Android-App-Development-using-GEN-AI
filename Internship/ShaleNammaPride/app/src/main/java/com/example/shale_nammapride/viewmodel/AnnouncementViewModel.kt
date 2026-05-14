package com.example.shale_nammapride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shale_nammapride.models.Announcement
import com.example.shale_nammapride.repository.AnnouncementRepository

class AnnouncementViewModel : ViewModel() {
    private val repository = AnnouncementRepository()
    
    // Change to private backing field to prevent immediate loading
    private var _announcements: LiveData<List<Announcement>>? = null
    
    val announcements: LiveData<List<Announcement>>
        get() {
            if (_announcements == null) {
                _announcements = repository.getAnnouncements()
            }
            return _announcements!!
        }

    fun addAnnouncement(announcement: Announcement, onComplete: (Boolean, String?) -> Unit) {
        repository.addAnnouncement(announcement, onComplete)
    }
}
