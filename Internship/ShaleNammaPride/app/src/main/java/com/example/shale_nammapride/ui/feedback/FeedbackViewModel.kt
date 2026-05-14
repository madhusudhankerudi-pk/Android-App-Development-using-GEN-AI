package com.example.shale_nammapride.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shale_nammapride.data.model.Feedback
import com.example.shale_nammapride.data.repository.FeedbackRepository
import com.example.shale_nammapride.data.repository.FeedbackRepositoryImpl
import com.example.shale_nammapride.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedbackViewModel(
    private val repository: FeedbackRepository = FeedbackRepositoryImpl()
) : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val submitState: StateFlow<UiState<Unit>> = _submitState.asStateFlow()

    fun submitFeedback(content: String, type: String, isAnonymous: Boolean, userId: String?) {
        viewModelScope.launch {
            _submitState.value = UiState.Loading
            val feedback = Feedback(
                content = content,
                type = type,
                isAnonymous = isAnonymous,
                userId = if (isAnonymous) null else userId
            )
            repository.submitFeedback(feedback)
                .onSuccess { _submitState.value = UiState.Success(Unit) }
                .onFailure { _submitState.value = UiState.Error(it.message ?: "Submission failed") }
        }
    }
    
    fun resetState() {
        _submitState.value = UiState.Idle
    }
}
