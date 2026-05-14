package com.example.shale_nammapride.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.shale_nammapride.databinding.ActivityMealViewBinding
import com.example.shale_nammapride.models.MealUpdate
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * MealViewActivity: Screen for parents to view today's meal update.
 */
class MealViewActivity : BaseActivity() {

    private lateinit var binding: ActivityMealViewBinding
    
    // Use lazy initialization to prevent blocking the main thread during onCreate
    private val database: DatabaseReference? by lazy {
        try {
            FirebaseDatabase.getInstance().getReference("meal_updates")
        } catch (e: Exception) {
            Log.e("MealViewActivity", "Firebase Database initialization failed: ${e.message}")
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the back button in the toolbar
        binding.toolbarMealView.setNavigationOnClickListener { finish() }

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        // Load the meal data for the current date
        loadTodayMeal(currentDate)
    }

    /**
     * Fetches today's meal record from Firebase Realtime Database.
     */
    private fun loadTodayMeal(date: String) {
        val dateKey = date.replace(" ", "_")
        val ref = database
        if (ref == null) {
            showEmptyState()
            Toast.makeText(this, "Service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        ref.child(dateKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val meal = snapshot.getValue(MealUpdate::class.java)
                    if (meal != null) {
                        displayMeal(meal)
                    }
                } else {
                    showEmptyState()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MealViewActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Updates the UI with the fetched meal data.
     */
    private fun displayMeal(meal: MealUpdate) {
        binding.tvMealDate.text = meal.date
        binding.tvMealMenu.text = meal.menu
        binding.tvUploadedBy.text = "Uploaded by ${meal.uploadedBy}"

        if (meal.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(meal.imageUrl)
                .into(binding.ivTodayMeal)
        }
    }

    /**
     * Displays a placeholder message if no meal was uploaded for today.
     */
    private fun showEmptyState() {
        binding.tvMealMenu.text = "No meal update available for today yet."
        binding.tvUploadedBy.text = ""
    }
}
