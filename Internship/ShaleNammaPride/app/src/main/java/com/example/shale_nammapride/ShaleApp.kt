package com.example.shale_nammapride

import android.app.Application
import android.content.Context
import com.example.shale_nammapride.utils.LocaleHelper
import com.google.firebase.FirebaseApp

class ShaleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. Warm up the language cache immediately.
        LocaleHelper.getLanguage(this)

        // 2. Initialize Firebase correctly (standard)
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Usually handled by the auto-initializer
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}
