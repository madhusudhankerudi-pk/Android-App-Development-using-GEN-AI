package com.example.shale_nammapride.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shale_nammapride.R
import com.example.shale_nammapride.activities.LoginActivity
import com.example.shale_nammapride.databinding.FragmentProfileBinding
import com.example.shale_nammapride.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loadUserProfile()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        database.getReference("Users").child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists() && isAdded) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.tvProfileName.text = user.fullName
                    binding.tvProfileRole.text = user.role
                    binding.tvContact.text = getString(R.string.contact_label, user.contactInfo ?: "-")
                    
                    // Display common location info
                    binding.tvVillage.text = getString(R.string.village_label, user.village ?: "-")
                    binding.tvTaluk.text = "Taluk: ${user.taluk ?: "-"}"
                    binding.tvDistrict.text = "District: ${user.district ?: "-"}"

                    if (user.role == "Admin") {
                        binding.llAdminInfo.visibility = View.VISIBLE
                        binding.llParentInfo.visibility = View.GONE
                        binding.tvSchoolName.text = getString(R.string.school_label, user.schoolName ?: "-")
                    } else {
                        binding.llAdminInfo.visibility = View.GONE
                        binding.llParentInfo.visibility = View.VISIBLE
                        binding.tvChildName.text = getString(R.string.child_name_label, user.childName ?: "-")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}