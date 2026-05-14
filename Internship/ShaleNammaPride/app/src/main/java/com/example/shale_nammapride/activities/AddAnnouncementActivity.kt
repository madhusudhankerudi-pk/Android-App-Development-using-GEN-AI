package com.example.shale_nammapride.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.shale_nammapride.databinding.ActivityAddAnnouncementBinding
import com.example.shale_nammapride.models.Announcement
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddAnnouncementActivity : BaseActivity() {

    private lateinit var binding: ActivityAddAnnouncementBinding
    private lateinit var database: FirebaseDatabase
    private var announcementId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnouncementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        binding.toolbarAddAnnouncement.setNavigationOnClickListener { finish() }

        // Setup Categories
        val categories = arrayOf("Notice", "Holiday", "Event", "Exam")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteAnnouncementType.setAdapter(adapter)

        // Check if editing
        announcementId = intent.getStringExtra("announcementId")
        if (announcementId != null) {
            binding.toolbarAddAnnouncement.title = "Edit Announcement"
            binding.btnSaveAnnouncement.text = "Update Announcement"
            loadAnnouncementData(announcementId!!)
        }

        binding.btnSaveAnnouncement.setOnClickListener {
            saveAnnouncement()
        }
    }

    private fun loadAnnouncementData(id: String) {
        database.getReference("Announcements").child(id).get().addOnSuccessListener { snapshot ->
            val announcement = snapshot.getValue(Announcement::class.java)
            if (announcement != null) {
                binding.etAnnouncementTitle.setText(announcement.title)
                binding.etAnnouncementMessage.setText(announcement.message)
                binding.autoCompleteAnnouncementType.setText(announcement.type, false)
            }
        }
    }

    private fun saveAnnouncement() {
        val title = binding.etAnnouncementTitle.text.toString().trim()
        val message = binding.etAnnouncementMessage.text.toString().trim()
        val type = binding.autoCompleteAnnouncementType.text.toString()

        if (title.isEmpty() || message.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val id = announcementId ?: database.getReference("Announcements").push().key ?: ""
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        
        val announcement = Announcement(
            id = id,
            title = title,
            message = message,
            type = type,
            date = currentDate,
            timestamp = System.currentTimeMillis()
        )

        database.getReference("Announcements").child(id).setValue(announcement)
            .addOnSuccessListener {
                setLoading(false)
                val msg = if (announcementId == null) "Announcement posted successfully" else "Announcement updated successfully"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Failed to post: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarAnnouncement.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSaveAnnouncement.isEnabled = !isLoading
    }
}