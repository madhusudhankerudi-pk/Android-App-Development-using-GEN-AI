package com.example.shale_nammapride.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shale_nammapride.adapters.FeedbackAdapter
import com.example.shale_nammapride.databinding.FragmentFeedbackBinding
import com.example.shale_nammapride.models.Feedback
import com.example.shale_nammapride.viewmodel.FeedbackViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * FeedbackFragment: Supports both submitting feedback (Parents) 
 * and viewing a list of feedback (Admins).
 */
class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FeedbackViewModel
    
    private val auth by lazy { 
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null } 
    }
    private val database by lazy { 
        try { FirebaseDatabase.getInstance() } catch (e: Exception) { null } 
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[FeedbackViewModel::class.java]

        checkUserRole()
        setupSubmissionForm()
    }

    private fun checkUserRole() {
        val uid = auth?.currentUser?.uid ?: return
        try {
            database?.getReference("Users")?.child(uid)?.child("role")?.get()
                ?.addOnSuccessListener { snapshot ->
                    if (isAdded) {
                        if (snapshot.value == "Admin") {
                            showAdminView()
                        } else {
                            showParentView()
                        }
                    }
                }
                ?.addOnFailureListener {
                    if (isAdded) showParentView()
                }
        } catch (e: Exception) {
            Log.e("FeedbackFragment", "Role check failed: ${e.message}")
        }
    }

    private fun showAdminView() {
        binding.scrollFeedbackForm.visibility = View.GONE
        binding.llAdminFeedback.visibility = View.VISIBLE
        
        binding.rvFeedbackList.layoutManager = LinearLayoutManager(requireContext())
        viewModel.allFeedbacks.observe(viewLifecycleOwner) { list ->
            binding.rvFeedbackList.adapter = FeedbackAdapter(list)
        }
    }

    private fun showParentView() {
        binding.scrollFeedbackForm.visibility = View.VISIBLE
        binding.llAdminFeedback.visibility = View.GONE
    }

    private fun setupSubmissionForm() {
        val categories = arrayOf("Infrastructure", "Meals", "Teaching", "Cleanliness", "Sports", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.autoCompleteCategory.setAdapter(adapter)

        binding.btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val message = binding.etFeedback.text.toString().trim()
        val category = binding.autoCompleteCategory.text.toString()
        val isAnonymous = binding.switchAnonymous.isChecked

        if (message.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val feedback = Feedback(
            message = message,
            category = category,
            anonymous = isAnonymous
        )

        val finalFeedback = if (!isAnonymous) {
            val user = auth?.currentUser
            feedback.copy(userId = user?.uid, userName = user?.email)
        } else feedback

        binding.btnSubmitFeedback.isEnabled = false
        viewModel.submitFeedback(finalFeedback) { success, error ->
            if (isAdded) {
                binding.btnSubmitFeedback.isEnabled = true
                if (success) {
                    Toast.makeText(requireContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                    binding.etFeedback.text?.clear()
                    binding.autoCompleteCategory.text?.clear()
                } else {
                    Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
