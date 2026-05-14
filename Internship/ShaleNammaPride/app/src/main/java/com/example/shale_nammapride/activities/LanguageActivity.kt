package com.example.shale_nammapride.activities

import android.os.Bundle
import android.widget.Toast
import com.example.shale_nammapride.databinding.ActivityLanguageBinding
import com.example.shale_nammapride.utils.LocaleHelper

class LanguageActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarLanguage.setNavigationOnClickListener { finish() }

        // Load current selection
        val currentLang = getSharedPreferences("settings", MODE_PRIVATE)
            .getString("Locale.Helper.Selected.Language", "en")
        if (currentLang == "kn") {
            binding.rbKannada.isChecked = true
            binding.rbEnglish.isChecked = false
        } else {
            binding.rbEnglish.isChecked = true
            binding.rbKannada.isChecked = false
        }

        binding.cardEnglish.setOnClickListener {
            updateLanguage("en")
        }

        binding.cardKannada.setOnClickListener {
            updateLanguage("kn")
        }
        
        binding.rbEnglish.setOnClickListener { updateLanguage("en") }
        binding.rbKannada.setOnClickListener { updateLanguage("kn") }
    }

    private fun updateLanguage(langCode: String) {
        LocaleHelper.setLocale(this, langCode)
        Toast.makeText(this, "Language updated. Please restart the app if changes don't reflect immediately.", Toast.LENGTH_LONG).show()
        
        // Restart the app to apply language change globally
        val intent = intent
        finish()
        startActivity(intent)
    }
}