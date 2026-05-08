package com.voicetotext.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    
    private val _amplitude = MutableStateFlow(0)
    val amplitude: Flow<Int> = _amplitude.asStateFlow()
    
    private val _isRecording = MutableStateFlow(false)
    val isRecordingFlow: Flow<Boolean> = _isRecording.asStateFlow()

    suspend fun startRecording(): Result<File> = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            outputFile = File(cacheDir, "temp_audio_${System.currentTimeMillis()}.m4a")
            
            mediaRecorder = createMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setAudioEncodingBitRate(128000)
                setOutputFile(outputFile!!.absolutePath)
                prepare()
                start()
            }
            
            isRecording = true
            _isRecording.value = true
            
            Result.success(outputFile!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    suspend fun stopRecording(): Result<File?> = withContext(Dispatchers.IO) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            _isRecording.value = false
            _amplitude.value = 0
            
            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMaxAmplitude(): Int {
        return try {
            val amp = mediaRecorder?.maxAmplitude ?: 0
            _amplitude.value = amp
            amp
        } catch (e: Exception) {
            0
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            outputFile?.delete()
            mediaRecorder = null
            isRecording = false
            _isRecording.value = false
            _amplitude.value = 0
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun readAudioFile(file: File): ByteArray = withContext(Dispatchers.IO) {
        FileInputStream(file).use { fis ->
            fis.readBytes()
        }
    }
}
