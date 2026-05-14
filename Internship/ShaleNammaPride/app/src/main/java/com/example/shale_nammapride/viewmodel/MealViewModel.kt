package com.example.shale_nammapride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shale_nammapride.models.MealUpdate
import com.example.shale_nammapride.repository.MealRepository

class MealViewModel : ViewModel() {
    private val repository = MealRepository()

    fun getTodayMeal(dateKey: String): LiveData<MealUpdate?> {
        return repository.getTodayMeal(dateKey)
    }

    fun uploadMealUpdate(dateKey: String, update: MealUpdate, onComplete: (Boolean, String?) -> Unit) {
        repository.uploadMealUpdate(dateKey, update, onComplete)
    }
}