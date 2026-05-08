package com.voicetotext.data.remote

import com.google.gson.Gson
import com.voicetotext.data.local.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = false
)

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>?
)

data class Choice(
    val message: ChatMessage?
)

@Singleton
class TextPolishingService @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun polishText(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest(text)
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val polishedText = parseResponse(responseBody)
                Result.success(polishedText)
            } else {
                Result.failure(Exception("API Error: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildRequest(text: String): Request {
        val requestBody = ChatRequest(
            model = preferencesManager.polishModel,
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "你是一个专业的文字润色助手。请直接润色用户输入的文本，不需要解释，直接输出润色后的文本即可。保持原意，使语言更加流畅、专业、易读。"
                ),
                ChatMessage(
                    role = "user",
                    content = text
                )
            ),
            stream = false
        )

        val json = gson.toJson(requestBody)

        return Request.Builder()
            .url("${preferencesManager.polishUrl}/chat/completions")
            .addHeader("Authorization", "Bearer ${preferencesManager.polishApiKey}")
            .addHeader("Content-Type", "application/json")
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()
    }

    private fun parseResponse(responseBody: String?): String {
        return try {
            val response = gson.fromJson(responseBody, ChatResponse::class.java)
            response.choices?.firstOrNull()?.message?.content ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
