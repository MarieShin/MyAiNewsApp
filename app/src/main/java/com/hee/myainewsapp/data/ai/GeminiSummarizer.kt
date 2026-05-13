package com.hee.myainewsapp.data.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.hee.myainewsapp.BuildConfig


class GeminiSummarizer {

    private val model = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.2f
        },
    )

    suspend fun summarize(text: String): String {
        return try {

            val truncatedText = text.take(500)
            val prompt = "다음 뉴스 본문을 100자 내외의 평서문으로 요약해줘:\n$truncatedText"

            val response = model.generateContent(prompt)

            response.text ?: "요약 실패"
        } catch (e: Exception) {
            Log.e("GeminiError", "요약 실패", e)
            "요약 중 오류 발생"
        }
    }
}