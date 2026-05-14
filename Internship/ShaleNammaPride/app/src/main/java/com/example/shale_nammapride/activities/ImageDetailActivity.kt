package com.example.shale_nammapride.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.shale_nammapride.databinding.ActivityImageDetailBinding

/**
 * ImageDetailActivity: Displays a gallery image in full screen.
 */
class ImageDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityImageDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarImageDetail.setNavigationOnClickListener { finish() }

        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.ivFullScreen)
        }
    }
}