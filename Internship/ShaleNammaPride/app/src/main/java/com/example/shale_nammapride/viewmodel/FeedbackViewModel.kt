package com.example.shale_nammapride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shale_nammapride.models.Feedback
import com.example.shale_nammapride.repository.FeedbackRepository

class FeedbackViewModel : ViewModel() {
    private val repository = FeedbackRepository()
    
    private var _allFeedbacks: LiveData<List<Feedback>>? = null
    
    val allFeedbacks: LiveData<List<Feedback>>
        get() {
            if (_allFeedbacks == null) {
                _allFeedbacks = repository.getAllFeedbacks()
            }
            return _allFeedbacks!!
        }

    fun submitFeedback(feedback: Feedback, onComplete: (Boolean, String?) -> Unit) {
        repository.submitFeedback(feedback, onComplete)
    }
}
