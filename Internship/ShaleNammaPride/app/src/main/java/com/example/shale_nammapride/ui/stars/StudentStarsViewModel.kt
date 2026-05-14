package com.example.shale_nammapride.ui.stars

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shale_nammapride.data.model.StudentStar
import com.example.shale_nammapride.data.repository.StudentStarRepository
import com.example.shale_nammapride.data.repository.StudentStarRepositoryImpl
import com.google.ai.client.generativeai.GenerativeModel
import com.example.shale_nammapride.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentStarsViewModel(
    private val repository: StudentStarRepository = StudentStarRepositoryImpl(
        generativeModel = GenerativeModel(modelName = "gemini-pro", apiKey = "AIzaSyCuXhUq9YazgdLnnxf_gg3PjSPeatMGwE4")
    )
) : ViewModel() {

    private val _stars = MutableStateFlow<List<StudentStar>>(emptyList())
    val stars: StateFlow<List<StudentStar>> = _stars.asStateFlow()

    private val _uploadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uploadState: StateFlow<UiState<Unit>> = _uploadState.asStateFlow()

    private val _appreciationState = MutableStateFlow<String>("")
    val appreciationState: StateFlow<String> = _appreciationState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getStudentStars().collect {
                _stars.value = it
            }
        }
    }

    fun generateAppreciation(name: String, achievement: String) {
        viewModelScope.launch {
            repository.generateAppreciation(name, achievement)
                .onSuccess { _appreciationState.value = it }
        }
    }

    fun uploadStar(name: String, achievement: String, appreciation: String, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            val star = StudentStar(
                studentName = name,
                achievement = achievement,
                appreciationText = appreciation
            )
            repository.uploadStudentStar(star, imageUri)
                .onSuccess { _uploadState.value = UiState.Success(Unit) }
                .onFailure { _uploadState.value = UiState.Error(it.message ?: "Upload failed") }
        }
    }
}
