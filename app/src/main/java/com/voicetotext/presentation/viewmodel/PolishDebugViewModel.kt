package com.voicetotext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetotext.data.local.PreferencesManager
import com.voicetotext.data.remote.TextPolishingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PolishDebugUiState(
    val inputText: String = "",
    val polishedText: String = "",
    val isProcessing: Boolean = false,
    val error: String? = null,
    val isConfigured: Boolean = false
)

@HiltViewModel
class PolishDebugViewModel @Inject constructor(
    private val textPolishingService: TextPolishingService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PolishDebugUiState())
    val uiState: StateFlow<PolishDebugUiState> = _uiState.asStateFlow()

    init {
        checkConfiguration()
    }

    private fun checkConfiguration() {
        _uiState.update {
            it.copy(isConfigured = preferencesManager.isPolishConfigured())
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun polishText() {
        val inputText = _uiState.value.inputText

        if (inputText.isBlank()) {
            _uiState.update { it.copy(error = "请输入要润色的文本") }
            return
        }

        if (!preferencesManager.isPolishConfigured()) {
            _uiState.update { it.copy(error = "请先配置AI润色服务") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            val result = textPolishingService.polishText(inputText)
            result.fold(
                onSuccess = { polished ->
                    _uiState.update {
                        it.copy(
                            polishedText = polished,
                            isProcessing = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = "润色失败：${error.message}",
                            isProcessing = false
                        )
                    }
                }
            )
        }
    }

    fun clearText() {
        _uiState.update { it.copy(inputText = "", polishedText = "") }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
