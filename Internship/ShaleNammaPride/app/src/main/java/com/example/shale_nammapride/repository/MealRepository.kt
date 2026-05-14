package com.example.shale_nammapride.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shale_nammapride.models.MealUpdate
import com.google.firebase.database.*

class MealRepository {
    // Use lazy to avoid blocking the main thread during instantiation
    private val database: DatabaseReference by lazy { 
        FirebaseDatabase.getInstance().getReference("meal_updates") 
    }

    fun getTodayMeal(dateKey: String): LiveData<MealUpdate?> {
        val mealLiveData = MutableLiveData<MealUpdate?>()
        try {
            database.child(dateKey).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mealLiveData.value = snapshot.getValue(MealUpdate::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    mealLiveData.value = null
                }
            })
        } catch (e: Exception) {
            mealLiveData.value = null
        }
        return mealLiveData
    }

    fun uploadMealUpdate(dateKey: String, update: MealUpdate, onComplete: (Boolean, String?) -> Unit) {
        try {
            database.child(dateKey).setValue(update)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        } catch (e: Exception) {
            onComplete(false, e.message)
        }
    }
}