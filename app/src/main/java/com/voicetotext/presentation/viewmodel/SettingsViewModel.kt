package com.voicetotext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.voicetotext.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val asrUrl: String = "",
    val asrApiKey: String = "",
    val asrModel: String = "",
    val polishUrl: String = "",
    val polishApiKey: String = "",
    val polishModel: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                asrUrl = preferencesManager.asrUrl,
                asrApiKey = preferencesManager.asrApiKey,
                asrModel = preferencesManager.asrModel,
                polishUrl = preferencesManager.polishUrl,
                polishApiKey = preferencesManager.polishApiKey,
                polishModel = preferencesManager.polishModel
            )
        }
    }

    fun updateAsrUrl(url: String) {
        _uiState.update { it.copy(asrUrl = url, isSaved = false) }
    }

    fun updateAsrApiKey(apiKey: String) {
        _uiState.update { it.copy(asrApiKey = apiKey, isSaved = false) }
    }

    fun updateAsrModel(model: String) {
        _uiState.update { it.copy(asrModel = model, isSaved = false) }
    }

    fun updatePolishUrl(url: String) {
        _uiState.update { it.copy(polishUrl = url, isSaved = false) }
    }

    fun updatePolishApiKey(apiKey: String) {
        _uiState.update { it.copy(polishApiKey = apiKey, isSaved = false) }
    }

    fun updatePolishModel(model: String) {
        _uiState.update { it.copy(polishModel = model, isSaved = false) }
    }

    fun saveSettings() {
        val state = _uiState.value

        if (state.asrUrl.isBlank()) {
            _uiState.update { it.copy(error = "语音识别URL不能为空") }
            return
        }

        if (state.asrApiKey.isBlank()) {
            _uiState.update { it.copy(error = "语音识别API密钥不能为空") }
            return
        }

        if (state.polishUrl.isBlank()) {
            _uiState.update { it.copy(error = "AI润色URL不能为空") }
            return
        }

        if (state.polishApiKey.isBlank()) {
            _uiState.update { it.copy(error = "AI润色API密钥不能为空") }
            return
        }

        preferencesManager.asrUrl = state.asrUrl
        preferencesManager.asrApiKey = state.asrApiKey
        preferencesManager.asrModel = state.asrModel.ifBlank { "volc_zh_v3_streaming" }
        preferencesManager.polishUrl = state.polishUrl
        preferencesManager.polishApiKey = state.polishApiKey
        preferencesManager.polishModel = state.polishModel.ifBlank { "doubao-seed-2.0-lite" }

        _uiState.update { it.copy(isSaved = true, error = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
