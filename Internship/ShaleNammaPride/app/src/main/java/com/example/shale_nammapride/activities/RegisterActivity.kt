package com.example.shale_nammapride.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.shale_nammapride.databinding.ActivityRegisterBinding
import com.example.shale_nammapride.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toggleGroupRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == binding.btnRoleAdmin.id) {
                    binding.tilSchoolName.visibility = View.VISIBLE
                    binding.tilChildName.visibility = View.GONE
                } else {
                    binding.tilSchoolName.visibility = View.GONE
                    binding.tilChildName.visibility = View.VISIBLE
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.etRegEmail.text.toString().trim()
        val password = binding.etRegPassword.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val contact = binding.etContact.text.toString().trim()
        val village = binding.etVillage.text.toString().trim()
        val taluk = binding.etTaluk.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val role = if (binding.toggleGroupRole.checkedButtonId == binding.btnRoleAdmin.id) "Admin" else "Parent"

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || contact.isEmpty() || village.isEmpty() || taluk.isEmpty() || district.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: ""
                    val user = if (role == "Admin") {
                        User(
                            uid = uid,
                            fullName = fullName,
                            email = email,
                            role = role,
                            contactInfo = contact,
                            village = village,
                            taluk = taluk,
                            district = district,
                            schoolName = binding.etSchoolName.text.toString()
                        )
                    } else {
                        User(
                            uid = uid,
                            fullName = fullName,
                            email = email,
                            role = role,
                            contactInfo = contact,
                            village = village,
                            taluk = taluk,
                            district = district,
                            childName = binding.etChildName.text.toString()
                        )
                    }

                    database.getReference("Users").child(uid).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finishAffinity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val exception = task.exception
                    Log.e("RegisterActivity", "Registration Failed", exception)
                    val errorMessage = when (exception) {
                        is FirebaseAuthException -> "Auth Error: ${exception.errorCode} - ${exception.message}"
                        else -> "Registration Failed: ${exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }
}
