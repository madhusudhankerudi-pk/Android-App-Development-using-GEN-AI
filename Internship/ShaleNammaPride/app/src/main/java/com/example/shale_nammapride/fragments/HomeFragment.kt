package com.example.shale_nammapride.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shale_nammapride.R
import com.example.shale_nammapride.activities.*
import com.example.shale_nammapride.databinding.FragmentHomeBinding
import com.example.shale_nammapride.models.MealUpdate
import com.example.shale_nammapride.models.StudentStar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HomeFragment: The main dashboard of the application.
 * Shows quick highlights and access to all modules.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } 
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        loadUserName()
        loadBannerData()
    }

    private fun setupClickListeners() {
        binding.cardHighlight.setOnClickListener {
            startActivity(Intent(requireContext(), StudentStarsActivity::class.java))
        }

        binding.cardMeal.setOnClickListener {
            checkRoleAndNavigateMeal()
        }

        binding.cardFacility.setOnClickListener {
            startActivity(Intent(requireContext(), FacilityTourActivity::class.java))
        }

        binding.cardStars.setOnClickListener {
            startActivity(Intent(requireContext(), StudentStarsActivity::class.java))
        }

        binding.cardAnnouncements.setOnClickListener {
            startActivity(Intent(requireContext(), AnnouncementsActivity::class.java))
        }

        binding.cardFeedback.setOnClickListener {
            findNavController().navigate(R.id.navigation_feedback)
        }

        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

        binding.btnLanguageToggle.setOnClickListener {
            startActivity(Intent(requireContext(), LanguageActivity::class.java))
        }
    }

    private fun checkRoleAndNavigateMeal() {
        val uid = auth?.currentUser?.uid
        if (uid == null || database == null) {
             startActivity(Intent(requireContext(), MealViewActivity::class.java))
             return
        }
        
        database?.getReference("Users")?.child(uid)?.child("role")?.get()?.addOnSuccessListener { snapshot ->
            val role = snapshot.value.toString()
            if (role == "Admin") {
                startActivity(Intent(requireContext(), MealUpdateActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), MealViewActivity::class.java))
            }
        }?.addOnFailureListener {
             startActivity(Intent(requireContext(), MealViewActivity::class.java))
        }
    }

    private fun loadUserName() {
        val uid = auth?.currentUser?.uid ?: return
        database?.getReference("Users")?.child(uid)?.child("fullName")?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists() && isAdded) {
                val name = snapshot.value.toString().split(" ")[0]
                binding.tvGreeting.text = "Namaskara, $name! 👋"
            }
        }
    }

    private fun loadBannerData() {
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val dateKey = currentDate.replace(" ", "_")
        
        try {
            database?.getReference("meal_updates")?.child(dateKey)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && isAdded) {
                        val meal = snapshot.getValue(MealUpdate::class.java)
                        if (meal != null) {
                            binding.tvHighlightTitle.text = "Today's Meal"
                            binding.tvHighlightDesc.text = meal.menu
                            if (meal.imageUrl.isNotEmpty()) {
                                Glide.with(this@HomeFragment).load(meal.imageUrl).into(binding.ivHighlight)
                            }
                        }
                    } else {
                        loadSOWToBanner()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        } catch (e: Exception) {
            Log.e("HomeFragment", "Banner data load failed: ${e.message}")
        }
    }

    private fun loadSOWToBanner() {
        database?.getReference("StudentOfWeek")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && isAdded) {
                    val sow = snapshot.getValue(StudentStar::class.java)
                    if (sow != null) {
                        binding.tvHighlightTitle.text = "Student of the Week: ${sow.name}"
                        binding.tvHighlightDesc.text = sow.achievement
                        if (sow.imageUrl.isNotEmpty()) {
                            Glide.with(this@HomeFragment).load(sow.imageUrl).into(binding.ivHighlight)
                        }
                    }
                } else if (isAdded) {
                    binding.tvHighlightTitle.text = "Welcome to Our School"
                    binding.tvHighlightDesc.text = "Education is the most powerful weapon."
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
