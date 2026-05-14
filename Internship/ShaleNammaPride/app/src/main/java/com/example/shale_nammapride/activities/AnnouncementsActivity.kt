package com.example.shale_nammapride.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shale_nammapride.adapters.AnnouncementAdapter
import com.example.shale_nammapride.databinding.ActivityAnnouncementsBinding
import com.example.shale_nammapride.models.Announcement
import com.example.shale_nammapride.viewmodel.AnnouncementViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AnnouncementsActivity : BaseActivity() {

    private lateinit var binding: ActivityAnnouncementsBinding
    private lateinit var viewModel: AnnouncementViewModel
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } 
    }

    private var allAnnouncements = mutableListOf<Announcement>()
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AnnouncementViewModel::class.java]

        binding.toolbarAnnouncements.setNavigationOnClickListener { finish() }
        
        binding.fabAddAnnouncement.setOnClickListener {
            startActivity(Intent(this, AddAnnouncementActivity::class.java))
        }

        setupRecyclerView()
        setupSearch()
        checkUserRole()
        observeAnnouncements()
    }

    private fun setupRecyclerView() {
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAnnouncements(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterAnnouncements(query: String) {
        val filteredList = allAnnouncements.filter {
            it.title.contains(query, ignoreCase = true) || it.message.contains(query, ignoreCase = true)
        }
        (binding.rvAnnouncements.adapter as? AnnouncementAdapter)?.updateList(filteredList)
    }

    private fun checkUserRole() {
        val uid = auth?.currentUser?.uid ?: return
        database?.getReference("Users")?.child(uid)?.child("role")?.get()
            ?.addOnSuccessListener { snapshot ->
                if (snapshot.value == "Admin") {
                    isAdmin = true
                    binding.fabAddAnnouncement.visibility = View.VISIBLE
                    updateAdapter()
                }
            }
    }

    private fun observeAnnouncements() {
        viewModel.announcements.observe(this) { list ->
            allAnnouncements = list.toMutableList()
            updateAdapter()
        }
    }

    private fun updateAdapter() {
        binding.rvAnnouncements.adapter = AnnouncementAdapter(allAnnouncements, isAdmin) { item, action ->
            when (action) {
                "DELETE" -> showDeleteConfirmation(item)
                "EDIT" -> {
                    val intent = Intent(this, AddAnnouncementActivity::class.java)
                    intent.putExtra("announcementId", item.id)
                    startActivity(intent)
                }
            }
        }
    }

    private fun showDeleteConfirmation(item: Announcement) {
        AlertDialog.Builder(this)
            .setTitle("Delete Announcement")
            .setMessage("Are you sure you want to delete this notice?")
            .setPositiveButton("Delete") { _, _ ->
                database?.getReference("Announcements")?.child(item.id)?.removeValue()
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
