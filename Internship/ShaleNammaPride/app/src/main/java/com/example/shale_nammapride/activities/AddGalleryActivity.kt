package com.example.shale_nammapride.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.shale_nammapride.databinding.ActivityAddGalleryBinding
import com.example.shale_nammapride.utils.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddGalleryActivity : BaseActivity() {

    private lateinit var binding: ActivityAddGalleryBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.toolbarAddGallery.setNavigationOnClickListener { finish() }

        binding.btnSelectGalleryImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnUploadToGallery.setOnClickListener {
            uploadToGallery()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            binding.ivGalleryPreview.setImageURI(selectedImageUri)
        }
    }

    private fun uploadToGallery() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val imageId = database.getReference("gallery").push().key ?: ""
        val storageRef = storage.getReference("gallery_images").child("$imageId.jpg")

        try {
            val imageData = ImageUtils.compressImage(this, selectedImageUri!!)
            storageRef.putBytes(imageData)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        database.getReference("gallery").child(imageId).setValue(uri.toString())
                            .addOnSuccessListener {
                                setLoading(false)
                                Toast.makeText(this, "Photo added to gallery!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                    }
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarGallery.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUploadToGallery.isEnabled = !isLoading
        binding.btnSelectGalleryImage.isEnabled = !isLoading
    }
}