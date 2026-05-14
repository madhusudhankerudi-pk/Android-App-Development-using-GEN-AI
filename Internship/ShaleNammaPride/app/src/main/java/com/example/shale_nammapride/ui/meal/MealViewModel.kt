package com.example.shale_nammapride.ui.meal

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shale_nammapride.data.model.Meal
import com.example.shale_nammapride.data.repository.MealRepository
import com.example.shale_nammapride.data.repository.MealRepositoryImpl
import com.example.shale_nammapride.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(
    private val repository: MealRepository = MealRepositoryImpl()
) : ViewModel() {

    private val _mealState = MutableStateFlow<Meal?>(null)
    val mealState: StateFlow<Meal?> = _mealState.asStateFlow()

    private val _uploadState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uploadState: StateFlow<UiState<Unit>> = _uploadState.asStateFlow()

    private val _canUpload = MutableStateFlow(true)
    val canUpload: StateFlow<Boolean> = _canUpload.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getDailyMeal().collect {
                _mealState.value = it
            }
        }
        checkUploadStatus()
    }

    fun checkUploadStatus() {
        viewModelScope.launch {
            _canUpload.value = repository.canUploadToday()
        }
    }

    fun uploadMeal(menuItems: List<String>, imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            val meal = Meal(menuItems = menuItems)
            repository.uploadMeal(meal, imageUri)
                .onSuccess {
                    _uploadState.value = UiState.Success(Unit)
                    checkUploadStatus()
                }
                .onFailure { error ->
                    _uploadState.value = UiState.Error(error.message ?: "Upload failed")
                }
        }
    }
}
