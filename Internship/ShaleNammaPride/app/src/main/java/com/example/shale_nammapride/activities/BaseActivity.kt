package com.example.shale_nammapride.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.shale_nammapride.utils.LocaleHelper

/**
 * BaseActivity: Shared logic for all activities.
 * Optimized to prevent "Not Responding" hangs during page transitions.
 */
open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        // Use the optimized attach method that uses memory caching
        // to avoid main thread hangs caused by disk I/O.
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }
}
