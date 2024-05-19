package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_IMAGE_URI = "extra_img_uri"
        const val TAG = "imagePicker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d(TAG, "ShowImage: $it")
            binding.resultImage.setImageURI(it)
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Log.d(TAG, "Error: $error")
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let { classifications ->
                        val topResult = classifications.firstOrNull()
                        val label = topResult?.categories?.firstOrNull()?.label ?: "Unknown"
                        val score = topResult?.categories?.firstOrNull()?.score ?: 0f

                        binding.resultText.text = "$label ${formatScore(score)}"
                    }
                }

                private fun formatScore(score: Float): String {
                    return String.format("%.2f%%", score * 100)
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }
}