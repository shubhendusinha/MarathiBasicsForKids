package com.example.marathibasicsforkids

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import java.util.*

class SwarFragment : Fragment(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTestMode = false
    private var currentTestVowel: String? = null
    private lateinit var instructionTextView: TextView
    private lateinit var scoreTextView: TextView

    private var correctCount = 0
    private var totalCount = 0

    private val vowelsMap = linkedMapOf(
        "अ" to "अ",
        "आ" to "आ",
        "इ" to "इ",
        "ई" to "<speak><prosody rate='80%'>ईई</prosody></speak>",
        "उ" to "उ",
        "ऊ" to "<speak><prosody rate='80%'>ऊऊ</prosody></speak>",
        "ए" to "ए",
        "ऐ" to "ऐ",
        "ओ" to "ओ",
        "औ" to "औ",
        "अं" to "अम",
        "अः" to "अहा"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)
        instructionTextView = view.findViewById(R.id.instructionTextView)
        scoreTextView = view.findViewById(R.id.scoreTextView)

        val testButton = view.findViewById<ImageButton>(R.id.testButton)
        testButton.setOnClickListener {
            startTest()
        }

        val resetButton = view.findViewById<ImageButton>(R.id.resetButton)
        resetButton.setOnClickListener {
            isTestMode = false
            correctCount = 0
            totalCount = 0
            updateScore()
            instructionTextView.text = "Press a letter to hear the sound."
        }

        val loopButton = view.findViewById<ImageButton>(R.id.loopButton)
        loopButton.setOnClickListener {
            if (isTestMode && currentTestVowel != null) {
                val phoneticText = vowelsMap[currentTestVowel]
                if (phoneticText != null) {
                    speakChar(phoneticText)
                }
            }
        }

        setupKeypad(view)
        updateScore()
    }

    private fun setupKeypad(view: View) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridKeypad)

        for ((displayChar, phoneticText) in vowelsMap) {
            val button = Button(requireContext())
            button.text = displayChar
            button.textSize = 32f
            button.setTextColor(Color.WHITE)
            val originalColor = "#6200EE".toColorInt()
            button.setBackgroundColor(originalColor)

            val params = GridLayout.LayoutParams()
            params.width = dpToPx(100)
            params.height = dpToPx(100)
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params

            button.setOnClickListener {
                if (isTestMode) {
                    totalCount++
                    if (displayChar == currentTestVowel) {
                        correctCount++
                        blink(button, Color.GREEN, originalColor)
                        blinkScoreBackground(Color.GREEN)
                        Handler(Looper.getMainLooper()).postDelayed({
                            startTest()
                        }, 500) // Start next round after a short delay
                    } else {
                        blink(button, Color.RED, originalColor)
                        blinkScoreBackground(Color.RED)
                    }
                    updateScore()
                } else {
                    speakChar(phoneticText)
                }
            }
            gridLayout.addView(button)
        }
    }

    private fun startTest() {
        isTestMode = true
        val randomVowel = vowelsMap.keys.random()
        currentTestVowel = randomVowel

        val phoneticText = vowelsMap[randomVowel]
        if (phoneticText != null) {
            speakChar(phoneticText)
            instructionTextView.text = "What letter do you hear?"
        }
    }

    private fun updateScore() {
        scoreTextView.text = "$correctCount/$totalCount"
    }

    private fun blink(button: Button, color: Int, originalColor: Int) {
        button.setBackgroundColor(color)
        Handler(Looper.getMainLooper()).postDelayed({
            button.setBackgroundColor(originalColor)
        }, 500)
    }

    private fun blinkScoreBackground(color: Int) {
        scoreTextView.setBackgroundColor(color)
        Handler(Looper.getMainLooper()).postDelayed({
            scoreTextView.setBackgroundResource(R.drawable.score_background)
        }, 500)
    }

    private fun speakChar(phoneticText: String) {
        val utteranceId = phoneticText.hashCode().toString()
        tts?.speak(phoneticText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("mr-IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
                Toast.makeText(requireContext(), "Marathi Voice Pack Missing on Phone", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("TTS", "Initialization Failed")
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroyView()
    }
}