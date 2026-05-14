package com.example.shale_nammapride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shale_nammapride.models.StudentStar
import com.example.shale_nammapride.repository.AchievementRepository

class AchievementViewModel : ViewModel() {
    private val repository = AchievementRepository()
    
    private var _achievements: LiveData<List<StudentStar>>? = null
    
    val achievements: LiveData<List<StudentStar>>
        get() {
            if (_achievements == null) {
                _achievements = repository.getAchievements()
            }
            return _achievements!!
        }

    fun deleteAchievement(id: String, onComplete: (Boolean) -> Unit) {
        repository.deleteAchievement(id, onComplete)
    }
}
