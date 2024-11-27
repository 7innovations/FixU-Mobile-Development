package com.example.fixu

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fixu.databinding.ActivityResultBinding
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra(EXTRA_RESULT)
        val feedback = intent.getStringExtra(EXTRA_FEEDBACK)
        val probability = intent.getStringExtra(EXTRA_PROBABILITY)
        val probabilityPercent = formatPercentage(probability ?: "0")

        binding.tvDiagnoseResult.text = result
        binding.tvProbabilityResult.text = probabilityPercent
        binding.tvFeedbackResult.text = feedback

        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun formatPercentage(value: String): String {
        val numericValue = value.toDoubleOrNull() ?: 0.0

        return if (numericValue < 1.0) {
            "1%"
        } else {
            String.format(Locale.US, "%.2f%%", numericValue)
        }
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_PROBABILITY = "extra_probability"
        const val EXTRA_FEEDBACK = "extra_feedback"
    }
}