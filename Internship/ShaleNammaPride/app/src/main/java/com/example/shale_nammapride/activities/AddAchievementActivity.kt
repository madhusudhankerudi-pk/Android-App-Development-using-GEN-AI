package com.example.shale_nammapride.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.shale_nammapride.databinding.ActivityAddAchievementBinding
import com.example.shale_nammapride.models.StudentStar
import com.example.shale_nammapride.utils.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddAchievementActivity : BaseActivity() {

    private lateinit var binding: ActivityAddAchievementBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private var achievementId: String? = null
    private var existingImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAchievementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.toolbarAddAchievement.setNavigationOnClickListener { finish() }

        achievementId = intent.getStringExtra("achievementId")
        if (achievementId != null) {
            binding.toolbarAddAchievement.title = "Edit Achievement"
            loadAchievementData(achievementId!!)
        }

        binding.btnSelectStudentImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnSaveAchievement.setOnClickListener {
            saveAchievement()
        }
    }

    private fun loadAchievementData(id: String) {
        database.getReference("Achievements").child(id).get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(StudentStar::class.java)
            if (item != null) {
                binding.etStudentName.setText(item.name)
                binding.etStudentGrade.setText(item.grade)
                binding.etAchievementTitle.setText(item.achievement)
                binding.cbStudentOfWeek.isChecked = item.isStudentOfWeek
                existingImageUrl = item.imageUrl
                if (existingImageUrl.isNotEmpty()) {
                    Glide.with(this).load(existingImageUrl).into(binding.ivStudentPhoto)
                }
            }
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivStudentPhoto.setImageURI(selectedImageUri)
        }
    }

    private fun saveAchievement() {
        val name = binding.etStudentName.text.toString().trim()
        val grade = binding.etStudentGrade.text.toString().trim()
        val achievement = binding.etAchievementTitle.text.toString().trim()
        val isSOW = binding.cbStudentOfWeek.isChecked

        if (name.isEmpty() || grade.isEmpty() || achievement.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val id = achievementId ?: database.getReference("Achievements").push().key ?: ""

        if (selectedImageUri != null) {
            val imageData = ImageUtils.compressImage(this, selectedImageUri!!)
            val storageRef = storage.getReference("achievement_images").child("$id.jpg")
            
            storageRef.putBytes(imageData)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        uploadData(id, name, grade, achievement, uri.toString(), isSOW)
                    }
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            uploadData(id, name, grade, achievement, existingImageUrl, isSOW)
        }
    }

    private fun uploadData(id: String, name: String, grade: String, title: String, imageUrl: String, isSOW: Boolean) {
        val studentStar = StudentStar(id, name, grade, title, imageUrl, isSOW, System.currentTimeMillis())
        
        database.getReference("Achievements").child(id).setValue(studentStar)
            .addOnSuccessListener {
                if (isSOW) {
                    database.getReference("StudentOfWeek").setValue(studentStar)
                }
                setLoading(false)
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarAchievement.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveAchievement.isEnabled = !isLoading
    }
}