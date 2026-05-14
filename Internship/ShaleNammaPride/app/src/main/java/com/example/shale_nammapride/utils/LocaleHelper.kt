package com.example.shale_nammapride.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    
    // Fast memory cache to prevent "Not Responding" errors
    @Volatile
    private var cachedLanguage: String? = null

    /**
     * Initializes the language cache. Call this in Application.attachBaseContext.
     */
    private fun initCache(context: Context) {
        if (cachedLanguage == null) {
            val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            cachedLanguage = preferences.getString(SELECTED_LANGUAGE, "en") ?: "en"
        }
    }

    /**
     * Optimized attach method. Uses memory cache to ensure 0ms delay during page transitions.
     */
    fun onAttach(context: Context): Context {
        initCache(context)
        return updateResources(context, cachedLanguage!!)
    }

    fun getLanguage(context: Context): String {
        initCache(context)
        return cachedLanguage!!
    }

    /**
     * Persists new language and updates the memory cache.
     */
    fun setLocale(context: Context, language: String): Context {
        val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply()
        cachedLanguage = language
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }
}
