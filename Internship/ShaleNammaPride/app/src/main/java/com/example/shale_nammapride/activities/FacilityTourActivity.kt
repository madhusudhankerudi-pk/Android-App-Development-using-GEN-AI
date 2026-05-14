package com.example.shale_nammapride.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.shale_nammapride.adapters.FacilityAdapter
import com.example.shale_nammapride.databinding.ActivityFacilityTourBinding
import com.example.shale_nammapride.models.Facility
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class FacilityTourActivity : BaseActivity() {

    private lateinit var binding: ActivityFacilityTourBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacilityTourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarFacility.setNavigationOnClickListener { finish() }

        val facilities = listOf(
            Facility("1", "Science Lab", "Well equipped with modern equipments for hands-on learning.", ""),
            Facility("2", "Library", "A quiet place for students to explore a world of books.", ""),
            Facility("3", "Classrooms", "Spacious and ventilated classrooms for better learning.", ""),
            Facility("4", "Toilets", "Clean and hygienic toilet facilities for boys and girls.", ""),
            Facility("5", "Sports Area", "Large playground for various sports and physical activities.", "")
        )

        val adapter = FacilityAdapter(facilities)
        binding.viewPagerFacilities.adapter = adapter

        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerFacilities) { _, _ -> }.attach()

        // Auto slide effect
        setupAutoSlide(facilities.size)
    }

    private fun setupAutoSlide(size: Int) {
        val update = Runnable {
            if (currentPage == size) {
                currentPage = 0
            }
            binding.viewPagerFacilities.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 3000) // Slide every 3 seconds
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
    }
}