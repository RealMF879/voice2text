package com.voicetotext.data.remote

import com.google.gson.Gson
import com.voicetotext.data.local.PreferencesManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed class RecognitionEvent {
    data class Text(val text: String, val isFinal: Boolean) : RecognitionEvent()
    data class Error(val message: String) : RecognitionEvent()
    object Connected : RecognitionEvent()
    object Disconnected : RecognitionEvent()
}

@Singleton
class SpeechRecognitionService @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    fun connect(): Flow<RecognitionEvent> = callbackFlow {
        val request = Request.Builder()
            .url(buildWebSocketUrl())
            .addHeader("Authorization", "Bearer ${preferencesManager.asrApiKey}")
            .addHeader("Content-Type", "application/json")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(RecognitionEvent.Connected)
                sendStartCommand()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val event = parseMessage(text)
                if (event != null) {
                    trySend(event)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(RecognitionEvent.Error(t.message ?: "WebSocket error"))
                close()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                trySend(RecognitionEvent.Disconnected)
                close()
            }
        })

        awaitClose {
            disconnect()
        }
    }

    private fun buildWebSocketUrl(): String {
        return "${preferencesManager.asrUrl}?model=${preferencesManager.asrModel}"
    }

    private fun sendStartCommand() {
        val startMessage = mapOf(
            "appid" to "your_app_id",
            "timestamp" to System.currentTimeMillis() / 1000,
            "signature" to "test_signature",
            "trans_type" to "online",
            "lang" to "zh-CN",
            "codec" to "mp3"
        )
        
        webSocket?.send(gson.toJson(startMessage))
    }

    private fun parseMessage(text: String): RecognitionEvent? {
        return try {
            val json = gson.fromJson(text, Map::class.java)
            val code = json["code"] as? Number
            val message = json["message"] as? String
            
            if (code == 1000 || code == 1001) {
                val result = json["result"] as? Map<*, *>
                val text = result?.get("text") as? String ?: ""
                val isFinal = code == 1000
                
                if (text.isNotBlank()) {
                    RecognitionEvent.Text(text, isFinal)
                } else {
                    null
                }
            } else if (code != null && code != 0) {
                RecognitionEvent.Error(message ?: "Recognition error")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun sendAudioChunk(audioData: ByteArray) {
        val audioBase64 = android.util.Base64.encodeToString(audioData, android.util.Base64.NO_WRAP)
        val message = mapOf(
            "event" to "data",
            "data" to audioBase64,
            "trans_type" to "online"
        )
        webSocket?.send(gson.toJson(message))
    }

    fun disconnect() {
        webSocket?.close(1000, "Recording finished")
        webSocket = null
    }
}
