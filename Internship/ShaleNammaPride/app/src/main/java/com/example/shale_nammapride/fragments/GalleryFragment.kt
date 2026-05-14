package com.example.shale_nammapride.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shale_nammapride.activities.AddGalleryActivity
import com.example.shale_nammapride.adapters.GalleryAdapter
import com.example.shale_nammapride.databinding.FragmentGalleryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * GalleryFragment: Displays a grid of school activity photos.
 * Admins can add new photos via the Floating Action Button.
 */
class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database: DatabaseReference? by lazy { 
        try { FirebaseDatabase.getInstance().getReference("gallery") } catch (e: Exception) { null } 
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        checkUserRole()
        loadGalleryImages()

        // FAB to add new images (visible only to Admin)
        binding.fabAddGallery.setOnClickListener {
            startActivity(Intent(requireContext(), AddGalleryActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        binding.rvGallery.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    /**
     * Checks user role to show/hide Admin specific actions.
     */
    private fun checkUserRole() {
        val uid = auth?.currentUser?.uid ?: return
        try {
            FirebaseDatabase.getInstance().getReference("Users").child(uid).child("role").get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.value == "Admin" && isAdded) {
                        binding.fabAddGallery.visibility = View.VISIBLE
                    } else if (isAdded) {
                        binding.fabAddGallery.visibility = View.GONE
                    }
                }
        } catch (e: Exception) {
            Log.e("GalleryFragment", "Role check failed: ${e.message}")
        }
    }

    /**
     * Real-time listener for gallery images from Firebase.
     */
    private fun loadGalleryImages() {
        try {
            database?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return
                    val images = mutableListOf<String>()
                    for (data in snapshot.children) {
                        val url = data.getValue(String::class.java)
                        if (url != null) images.add(url)
                    }
                    // Reverse to show latest first
                    images.reverse()
                    binding.rvGallery.adapter = GalleryAdapter(images)
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to load gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("GalleryFragment", "Load images failed: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
