package com.example.shale_nammapride.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shale_nammapride.adapters.AchievementAdapter
import com.example.shale_nammapride.databinding.ActivityStudentStarsBinding
import com.example.shale_nammapride.models.StudentStar
import com.example.shale_nammapride.viewmodel.AchievementViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StudentStarsActivity : BaseActivity() {

    private lateinit var binding: ActivityStudentStarsBinding
    private lateinit var viewModel: AchievementViewModel
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } 
    }
    
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentStarsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AchievementViewModel::class.java]

        binding.toolbarStars.setNavigationOnClickListener { finish() }
        
        binding.fabAddAchievement.setOnClickListener {
            startActivity(Intent(this, AddAchievementActivity::class.java))
        }

        setupRecyclerView()
        observeAchievements()
        
        // Load data safely
        checkUserRole()
        loadStudentOfWeek()
    }

    private fun checkUserRole() {
        val uid = auth?.currentUser?.uid ?: return
        database?.getReference("Users")?.child(uid)?.child("role")?.get()
            ?.addOnSuccessListener { snapshot ->
                if (snapshot.value == "Admin") {
                    isAdmin = true
                    binding.fabAddAchievement.visibility = View.VISIBLE
                    updateAdapter()
                }
            }
    }

    private fun loadStudentOfWeek() {
        try {
            database?.getReference("StudentOfWeek")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val sow = snapshot.getValue(StudentStar::class.java)
                        if (sow != null) {
                            binding.tvSOWName.text = sow.name
                            binding.tvSOWClass.text = "Class ${sow.grade}th"
                            if (sow.imageUrl.isNotEmpty()) {
                                Glide.with(this@StudentStarsActivity).load(sow.imageUrl).into(binding.ivSOWPhoto)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        } catch (e: Exception) {
            Log.e("StudentStars", "Firebase Error: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        binding.rvAchievements.layoutManager = LinearLayoutManager(this)
    }

    private fun observeAchievements() {
        viewModel.achievements.observe(this) { list ->
            binding.rvAchievements.adapter = AchievementAdapter(list, isAdmin) { item, action ->
                when (action) {
                    "DELETE" -> showDeleteConfirmation(item)
                    "EDIT" -> {
                        val intent = Intent(this, AddAchievementActivity::class.java)
                        intent.putExtra("achievementId", item.id)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun updateAdapter() {
        val currentList = viewModel.achievements.value ?: emptyList()
        binding.rvAchievements.adapter = AchievementAdapter(currentList, isAdmin) { item, action ->
            when (action) {
                "DELETE" -> showDeleteConfirmation(item)
                "EDIT" -> {
                    val intent = Intent(this, AddAchievementActivity::class.java)
                    intent.putExtra("achievementId", item.id)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showDeleteConfirmation(item: StudentStar) {
        AlertDialog.Builder(this)
            .setTitle("Delete Achievement")
            .setMessage("Are you sure you want to delete this achievement?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAchievement(item.id) { success ->
                    if (success) {
                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
