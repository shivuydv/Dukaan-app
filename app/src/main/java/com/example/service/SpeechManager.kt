package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  init {
    tts = TextToSpeech(context.applicationContext, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val hindiLocale = Locale.forLanguageTag("hi-IN")
      val result = tts?.setLanguage(hindiLocale)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        // Fallback to English (India) or default
        tts?.setLanguage(Locale.forLanguageTag("en-IN"))
      }
      isInitialized = true
    } else {
      Log.e("SpeechManager", "TTS initialization failed")
    }
  }

  fun speak(text: String) {
    if (isInitialized) {
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DukaanVoice_${System.currentTimeMillis()}")
    }
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
  }
}
