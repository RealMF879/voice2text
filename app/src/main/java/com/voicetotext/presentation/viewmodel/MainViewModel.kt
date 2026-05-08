package com.voicetotext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voicetotext.data.local.PreferencesManager
import com.voicetotext.data.remote.RecognitionEvent
import com.voicetotext.data.remote.SpeechRecognitionService
import com.voicetotext.data.remote.TextPolishingService
import com.voicetotext.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

data class MainUiState(
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val recordingDuration: String = "00:00",
    val recognitionText: String = "",
    val polishedText: String = "",
    val error: String? = null,
    val amplitude: Int = 0,
    val isConnected: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val isAsrConfigured: Boolean = false,
    val isPolishConfigured: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val speechRecognitionService: SpeechRecognitionService,
    private val textPolishingService: TextPolishingService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var recordingJob: Job? = null
    private var amplitudeJob: Job? = null
    private var timerJob: Job? = null
    private var recognitionJob: Job? = null
    private var startTime: Long = 0L
    private var currentAudioFile: File? = null

    init {
        checkConfiguration()
        observeAmplitude()
    }

    private fun checkConfiguration() {
        _uiState.update {
            it.copy(
                isAsrConfigured = preferencesManager.isAsrConfigured(),
                isPolishConfigured = preferencesManager.isPolishConfigured()
            )
        }
    }

    private fun observeAmplitude() {
        viewModelScope.launch {
            audioRecorder.amplitude.collect { amp ->
                _uiState.update { it.copy(amplitude = amp) }
            }
        }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.update { it.copy(hasAudioPermission = hasPermission) }
    }

    fun startRecording() {
        if (!_uiState.value.hasAudioPermission) {
            _uiState.update { it.copy(error = "需要麦克风权限") }
            return
        }

        if (!preferencesManager.isAsrConfigured()) {
            _uiState.update { it.copy(error = "请先配置语音识别服务") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(error = null, recognitionText = "") }

            audioRecorder.startRecording()
                .onSuccess { file ->
                    currentAudioFile = file
                    _uiState.update { it.copy(isRecording = true, recordingDuration = "00:00") }
                    startTimer()
                    startAmplitudeMonitoring()
                    startRecognition()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = "启动录音失败：${error.message}") }
                }
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val seconds = (elapsed / 1000) % 60
                val minutes = (elapsed / 1000) / 60
                val duration = String.format("%02d:%02d", minutes, seconds)
                _uiState.update { it.copy(recordingDuration = duration) }
                delay(1000)
            }
        }
    }

    private fun startAmplitudeMonitoring() {
        amplitudeJob = viewModelScope.launch {
            while (isActive) {
                audioRecorder.getMaxAmplitude()
                delay(100)
            }
        }
    }

    private fun startRecognition() {
        recognitionJob = viewModelScope.launch {
            speechRecognitionService.connect().collect { event ->
                handleRecognitionEvent(event)
            }
        }
    }

    private fun handleRecognitionEvent(event: RecognitionEvent) {
        when (event) {
            is RecognitionEvent.Connected -> {
                _uiState.update { it.copy(isConnected = true) }
            }
            is RecognitionEvent.Disconnected -> {
                _uiState.update { it.copy(isConnected = false) }
            }
            is RecognitionEvent.Text -> {
                _uiState.update { state ->
                    val newText = if (event.isFinal) {
                        state.recognitionText + event.text
                    } else {
                        event.text
                    }
                    state.copy(recognitionText = newText)
                }
            }
            is RecognitionEvent.Error -> {
                _uiState.update { it.copy(error = event.message) }
            }
        }
    }

    fun stopRecording() {
        stopAllJobs()
        
        viewModelScope.launch {
            _uiState.update { it.copy(isRecording = false) }
            
            audioRecorder.stopRecording()
                .onSuccess {
                    currentAudioFile?.let { file ->
                        uploadAndRecognize(file)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = "停止录音失败：${error.message}") }
                }
            
            speechRecognitionService.disconnect()
        }
    }

    private suspend fun uploadAndRecognize(file: File) {
        try {
            val audioData = audioRecorder.readAudioFile(file)
            speechRecognitionService.sendAudioChunk(audioData)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "上传音频失败：${e.message}") }
        }
    }

    fun confirmAndPolish() {
        val recognitionText = _uiState.value.recognitionText
        if (recognitionText.isBlank()) {
            _uiState.update { it.copy(error = "没有可润色的文本") }
            return
        }

        if (!preferencesManager.isPolishConfigured()) {
            _uiState.update { it.copy(error = "请先配置AI润色服务") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }

            textPolishingService.polishText(recognitionText)
                .onSuccess { polishedText ->
                    _uiState.update {
                        it.copy(
                            polishedText = polishedText,
                            isProcessing = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            error = "润色失败：${error.message}",
                            isProcessing = false
                        )
                    }
                }
        }
    }

    fun cancelRecording() {
        stopAllJobs()
        audioRecorder.cancelRecording()
        speechRecognitionService.disconnect()
        
        _uiState.update {
            MainUiState(
                hasAudioPermission = it.hasAudioPermission,
                isAsrConfigured = it.isAsrConfigured,
                isPolishConfigured = it.isPolishConfigured
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updatePolishedText(text: String) {
        _uiState.update { it.copy(polishedText = text) }
    }

    private fun stopAllJobs() {
        recordingJob?.cancel()
        amplitudeJob?.cancel()
        timerJob?.cancel()
        recognitionJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        stopAllJobs()
        audioRecorder.cancelRecording()
        speechRecognitionService.disconnect()
    }
}
