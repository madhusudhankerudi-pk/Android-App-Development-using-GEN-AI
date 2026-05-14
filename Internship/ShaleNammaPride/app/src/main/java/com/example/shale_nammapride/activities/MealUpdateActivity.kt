package com.example.shale_nammapride.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.shale_nammapride.databinding.ActivityMealUpdateBinding
import com.example.shale_nammapride.models.MealUpdate
import com.example.shale_nammapride.utils.ImageUtils
import com.example.shale_nammapride.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class MealUpdateActivity : BaseActivity() {

    private lateinit var binding: ActivityMealUpdateBinding
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } 
    }
    private val storage by lazy { 
        try { FirebaseStorage.getInstance() } catch (e: Exception) { null } 
    }
    
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        binding.tvCurrentDate.text = "Today's Date: $currentDate"

        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnUpload.setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                uploadMealUpdate(currentDate)
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivMealPhoto.setImageURI(selectedImageUri)
        }
    }

    private fun uploadMealUpdate(date: String) {
        val menu = binding.etMenu.text.toString().trim()
        if (menu.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please provide menu and photo", Toast.LENGTH_SHORT).show()
            return
        }

        if (database == null) {
            Toast.makeText(this, "Service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // Check if update already exists for today
        database?.getReference("meal_updates")?.child(date.replace(" ", "_"))?.get()
            ?.addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    setLoading(false)
                    Toast.makeText(this, "Only one update allowed per day!", Toast.LENGTH_LONG).show()
                } else {
                    performUpload(date, menu)
                }
            }
            ?.addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun performUpload(date: String, menu: String) {
        val storageRef = storage?.getReference("meal_images")?.child("meal_${System.currentTimeMillis()}.jpg")
        
        if (storageRef == null) {
            setLoading(false)
            Toast.makeText(this, "Storage unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val imageData = ImageUtils.compressImage(this, selectedImageUri!!)
            storageRef.putBytes(imageData)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val update = MealUpdate(
                            date = date,
                            imageUrl = uri.toString(),
                            menu = menu,
                            uploadedBy = auth?.currentUser?.email ?: "Admin",
                            timestamp = System.currentTimeMillis()
                        )

                        database?.getReference("meal_updates")?.child(date.replace(" ", "_"))?.setValue(update)
                            ?.addOnSuccessListener {
                                setLoading(false)
                                Toast.makeText(this, "Meal updated successfully!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            ?.addOnFailureListener {
                                setLoading(false)
                                Toast.makeText(this, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            setLoading(false)
            Log.e("MealUpdate", "Upload Error: ${e.message}")
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUpload.isEnabled = !isLoading
        binding.btnSelectImage.isEnabled = !isLoading
    }
}
