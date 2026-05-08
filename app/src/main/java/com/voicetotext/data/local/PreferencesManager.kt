package com.voicetotext.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "voice_to_text_settings"
        
        private const val KEY_ASR_URL = "asr_url"
        private const val KEY_ASR_API_KEY = "asr_api_key"
        private const val KEY_ASR_MODEL = "asr_model"
        
        private const val KEY_POLISH_URL = "polish_url"
        private const val KEY_POLISH_API_KEY = "polish_api_key"
        private const val KEY_POLISH_MODEL = "polish_model"
    }

    var asrUrl: String
        get() = prefs.getString(KEY_ASR_URL, "https://openspeech.bytedance.com/api/v2/asr") ?: ""
        set(value) = prefs.edit().putString(KEY_ASR_URL, value).apply()

    var asrApiKey: String
        get() = prefs.getString(KEY_ASR_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ASR_API_KEY, value).apply()

    var asrModel: String
        get() = prefs.getString(KEY_ASR_MODEL, "volc_zh_v3_streaming") ?: ""
        set(value) = prefs.edit().putString(KEY_ASR_MODEL, value).apply()

    var polishUrl: String
        get() = prefs.getString(KEY_POLISH_URL, "https://ark.cn-beijing.volces.com/api/v3") ?: ""
        set(value) = prefs.edit().putString(KEY_POLISH_URL, value).apply()

    var polishApiKey: String
        get() = prefs.getString(KEY_POLISH_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_POLISH_API_KEY, value).apply()

    var polishModel: String
        get() = prefs.getString(KEY_POLISH_MODEL, "doubao-seed-2.0-lite") ?: ""
        set(value) = prefs.edit().putString(KEY_POLISH_MODEL, value).apply()

    fun isAsrConfigured(): Boolean {
        return asrUrl.isNotBlank() && asrApiKey.isNotBlank()
    }

    fun isPolishConfigured(): Boolean {
        return polishUrl.isNotBlank() && polishApiKey.isNotBlank()
    }
}
