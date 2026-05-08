package com.voicetotext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetotext.data.local.PreferencesManager
import com.voicetotext.data.remote.RecognitionEvent
import com.voicetotext.data.remote.SpeechRecognitionService
import com.voicetotext.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SpeechDebugUiState(
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val recognizedText: String = "",
    val error: String? = null,
    val isConfigured: Boolean = false,
    val hasPermission: Boolean = false
)

@HiltViewModel
class SpeechDebugViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val speechRecognitionService: SpeechRecognitionService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeechDebugUiState())
    val uiState: StateFlow<SpeechDebugUiState> = _uiState.asStateFlow()

    private var recordingJob: Job? = null
    private var recognitionJob: Job? = null
    private var currentAudioFile: File? = null

    init {
        checkConfiguration()
    }

    private fun checkConfiguration() {
        _uiState.update {
            it.copy(isConfigured = preferencesManager.isAsrConfigured())
        }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.update { it.copy(hasPermission = hasPermission) }
    }

    fun startRecording() {
        if (!_uiState.value.hasPermission) {
            _uiState.update { it.copy(error = "需要麦克风权限") }
            return
        }

        if (!preferencesManager.isAsrConfigured()) {
            _uiState.update { it.copy(error = "请先配置语音识别服务") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(error = null, recognizedText = "", isRecording = true) }

            val result = audioRecorder.startRecording()
            result.fold(
                onSuccess = { file ->
                    currentAudioFile = file
                    startRecognition()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = "启动录音失败：${error.message}", isRecording = false) }
                }
            )
        }
    }

    private fun startRecognition() {
        recognitionJob = viewModelScope.launch {
            try {
                speechRecognitionService.connect().collect { event ->
                    handleRecognitionEvent(event)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "语音识别失败：${e.message}") }
            }
        }
    }

    private fun handleRecognitionEvent(event: RecognitionEvent) {
        when (event) {
            is RecognitionEvent.Connected -> {
                // Connected
            }
            is RecognitionEvent.Disconnected -> {
                _uiState.update { it.copy(isRecording = false) }
            }
            is RecognitionEvent.Text -> {
                _uiState.update { state ->
                    state.copy(recognizedText = state.recognizedText + event.text)
                }
            }
            is RecognitionEvent.Error -> {
                _uiState.update { it.copy(error = event.message) }
            }
        }
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recognitionJob?.cancel()

        viewModelScope.launch {
            _uiState.update { it.copy(isRecording = false) }

            audioRecorder.stopRecording()
            currentAudioFile?.let { file ->
                uploadAudio(file)
            }

            speechRecognitionService.disconnect()
        }
    }

    private suspend fun uploadAudio(file: File) {
        try {
            val audioData = audioRecorder.readAudioFile(file)
            speechRecognitionService.sendAudioChunk(audioData)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "上传音频失败：${e.message}") }
        }
    }

    fun clearText() {
        _uiState.update { it.copy(recognizedText = "") }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        recordingJob?.cancel()
        recognitionJob?.cancel()
        audioRecorder.cancelRecording()
        speechRecognitionService.disconnect()
    }
}
