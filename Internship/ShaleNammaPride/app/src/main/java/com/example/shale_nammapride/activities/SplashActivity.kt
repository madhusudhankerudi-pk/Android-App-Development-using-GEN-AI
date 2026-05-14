package com.example.shale_nammapride.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import com.example.shale_nammapride.databinding.ActivitySplashBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Safely initialize Firebase Auth
        try {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                auth = FirebaseAuth.getInstance()
            }
        } catch (e: Exception) {
            Log.e("SplashActivity", "Firebase initialization failed: ${e.message}")
        }

        // App logo animation
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.ivLogo.startAnimation(fadeIn)
        binding.tvAppName.startAnimation(fadeIn)
        binding.tvTagline.startAnimation(fadeIn)

        // Automatically navigate after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, 3000)
    }

    /**
     * Checks if user is already logged in to skip login screen
     */
    private fun checkUserSession() {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Default to login screen if not logged in or Firebase is unavailable
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}