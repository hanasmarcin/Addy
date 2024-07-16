package com.hanas.addy.home

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.hanas.addy.BuildConfig


class GeminiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.apiKey
    )

    suspend fun generateContent(content: List<Bitmap>, prompt: String): GenerateContentResponse {
        return generativeModel.generateContent(
            content {
                content.forEach { bitmap ->
                    image(bitmap)
                }
                text(prompt)
            }
        )
    }
}