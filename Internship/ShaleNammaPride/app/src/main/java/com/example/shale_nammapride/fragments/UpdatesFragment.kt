package com.example.shale_nammapride.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shale_nammapride.adapters.AnnouncementAdapter
import com.example.shale_nammapride.databinding.FragmentUpdatesBinding
import com.example.shale_nammapride.models.Announcement
import com.google.firebase.database.*

class UpdatesFragment : Fragment() {
    private var _binding: FragmentUpdatesBinding? = null
    private val binding get() = _binding!!
    
    // Use lazy to prevent main thread blocking during fragment startup
    private val database: DatabaseReference? by lazy {
        try {
            FirebaseDatabase.getInstance().getReference("Announcements")
        } catch (e: Exception) {
            Log.e("UpdatesFragment", "Firebase Database error: ${e.message}")
            null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpdatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvUpdates.layoutManager = LinearLayoutManager(requireContext())
        
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        val ref = database
        if (ref == null) {
            if (isAdded) Toast.makeText(requireContext(), "Service Unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return
                    val announcements = mutableListOf<Announcement>()
                    for (data in snapshot.children) {
                        val announcement = data.getValue(Announcement::class.java)
                        if (announcement != null) {
                            announcements.add(announcement)
                        }
                    }
                    announcements.sortByDescending { it.timestamp }
                    binding.rvUpdates.adapter = AnnouncementAdapter(announcements)
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to load updates: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("UpdatesFragment", "fetch error: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
