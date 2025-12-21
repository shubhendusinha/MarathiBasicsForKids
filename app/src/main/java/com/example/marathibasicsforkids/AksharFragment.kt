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

class AksharFragment : Fragment(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTestMode = false
    private var currentTestConsonant: String? = null
    private lateinit var instructionTextView: TextView
    private lateinit var scoreTextView: TextView

    private var correctCount = 0
    private var totalCount = 0

    private val consonantsMap = linkedMapOf(
        "क" to "क", "ख" to "ख", "ग" to "ग", "घ" to "घ", "ङ" to "ङ",
        "च" to "च", "छ" to "छ", "ज" to "ज", "झ" to "झ", "ञ" to "ञ",
        "ट" to "ट", "ठ" to "ठ", "ड" to "ड",
        "ढ" to "<speak><prosody rate='100%'>ढग</prosody></speak>",
        "ण" to "ण",
        "त" to "त", "थ" to "थ", "द" to "द", "ध" to "ध", "न" to "न",
        "प" to "प", "फ" to "फ", "ब" to "ब", "भ" to "भ", "म" to "म",
        "य" to "य", "र" to "र", "ल" to "ल", "व" to "व", "श" to "श",
        "ष" to "सष",
        "स" to "स", "ह" to "ह", "ळ" to "ळ", "क्ष" to "क्ष", "ज्ञ" to "ज्ञ"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_akshar, container, false)
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
            if (isTestMode && currentTestConsonant != null) {
                val phoneticText = consonantsMap[currentTestConsonant]
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
        gridLayout.removeAllViews()

        for ((displayChar, phoneticText) in consonantsMap) {
            val button = Button(requireContext())
            button.text = displayChar
            button.textSize = 28f // Slightly smaller for a denser grid
            button.setTextColor(Color.WHITE)
            val originalColor = "#6200EE".toColorInt()
            button.setBackgroundColor(originalColor)

            val params = GridLayout.LayoutParams()
            params.width = dpToPx(60) // Smaller buttons for a 5-column layout
            params.height = dpToPx(60)
            params.setMargins(4, 4, 4, 4)
            button.layoutParams = params

            button.setOnClickListener {
                if (isTestMode) {
                    totalCount++
                    if (displayChar == currentTestConsonant) {
                        correctCount++
                        blink(button, Color.GREEN, originalColor)
                        blinkScoreBackground(Color.GREEN)
                        Handler(Looper.getMainLooper()).postDelayed({
                            startTest()
                        }, 500)
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
        val randomConsonant = consonantsMap.keys.random()
        currentTestConsonant = randomConsonant

        val phoneticText = consonantsMap[randomConsonant]
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
            val result = tts!!.setLanguage(Locale("mr", "IN"))
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