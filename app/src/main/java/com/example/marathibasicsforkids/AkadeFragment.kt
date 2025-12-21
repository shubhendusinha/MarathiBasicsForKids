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

class AkadeFragment : Fragment(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTestMode = false
    private var currentTestNumber: String? = null
    private lateinit var instructionTextView: TextView
    private lateinit var scoreTextView: TextView

    private var correctCount = 0
    private var totalCount = 0

    private val numbersMap = linkedMapOf(
        "१" to "एक", "२" to "दोन", "३" to "तीन", "४" to "चार", "५" to "पाच",
        "६" to "सहा", "७" to "सात", "८" to "आठ", "९" to "नऊ", "१०" to "दहा",
        "११" to "अकरा", "१२" to "बारा", "१३" to "तेरा", "१४" to "चौदा", "१५" to "पंधरा",
        "१६" to "सोळा", "१७" to "सतरा", "१८" to "अठरा", "१९" to "एकोणीस", "२०" to "वीस",
        "२१" to "एकवीस", "२२" to "बावीस", "२३" to "तेवीस", "२४" to "चोवीस", "२५" to "पंचवीस",
        "२६" to "सव्वीस", "२७" to "सत्तावीस", "२८" to "अठ्ठावीस", "२९" to "एकोणतीस", "३०" to "तीस"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_akade, container, false)
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
            instructionTextView.text = "Press a number to hear the sound."
        }

        val loopButton = view.findViewById<ImageButton>(R.id.loopButton)
        loopButton.setOnClickListener {
            if (isTestMode && currentTestNumber != null) {
                val phoneticText = numbersMap[currentTestNumber]
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

        for ((displayChar, phoneticText) in numbersMap) {
            val button = Button(requireContext())
            button.text = displayChar
            button.textSize = 28f
            button.setTextColor(Color.WHITE)
            val originalColor = "#6200EE".toColorInt()
            button.setBackgroundColor(originalColor)

            val params = GridLayout.LayoutParams()
            params.width = dpToPx(60)
            params.height = dpToPx(60)
            params.setMargins(4, 4, 4, 4)
            button.layoutParams = params

            button.setOnClickListener {
                if (isTestMode) {
                    totalCount++
                    if (displayChar == currentTestNumber) {
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
        val randomNumber = numbersMap.keys.random()
        currentTestNumber = randomNumber

        val phoneticText = numbersMap[randomNumber]
        if (phoneticText != null) {
            speakChar(phoneticText)
            instructionTextView.text = "What number do you hear?"
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