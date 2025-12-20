package com.example.learnmarathibysound

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import java.util.*

class SwarFragment : Fragment(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)
        setupKeypad(view)
    }

    private fun setupKeypad(view: View) {
        val gridLayout = view.findViewById<GridLayout>(R.id.gridKeypad)

        for ((displayChar, phoneticText) in vowelsMap) {
            val button = Button(requireContext())

            button.text = displayChar
            button.textSize = 32f
            button.setTextColor(Color.WHITE)
            button.setBackgroundColor("#6200EE".toColorInt()) // Purple buttons

            val params = GridLayout.LayoutParams()
            params.width = dpToPx(100)
            params.height = dpToPx(100)
            params.setMargins(10, 10, 10, 10)
            button.layoutParams = params

            button.setOnClickListener {
                speakChar(phoneticText)
            }
            gridLayout.addView(button)
        }
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