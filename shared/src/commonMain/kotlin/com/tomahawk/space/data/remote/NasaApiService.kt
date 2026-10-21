package com.tomahawk.space.data.remote

import com.tomahawk.space.data.model.ApodResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NasaApiService(
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
) {
    suspend fun getApod(apiKey: String = "", date: String? = null): ApodResponse {
        val endpoint = if (date.isNullOrBlank()) {
            "https://science.nasa.gov/wp-json/wp/v2/apod-basic?per_page=1"
        } else {
            val legacyCode = convertToLegacyDateCode(date)
            "https://science.nasa.gov/wp-json/wp/v2/apod-basic/$legacyCode"
        }

        val rawResponse = if (date.isNullOrBlank()) {
            val list = client.get(endpoint).body<List<ApodResponse>>()
            list.first()
        } else {
            client.get(endpoint).body<ApodResponse>()
        }

        return rawResponse.processUrlsAndExplanation()
    }

    suspend fun validateKey(apiKey: String = ""): Boolean {
        return try {
            val response = client.get("https://science.nasa.gov/wp-json/wp/v2/apod-basic?per_page=1")
            response.status.value in 200..299
        } catch (_: Exception) {
            false
        }
    }

    private fun convertToLegacyDateCode(formattedDate: String): String {
        val parts = formattedDate.split("-")
        if (parts.size == 3 && parts[0].length == 4) {
            val yy = parts[0].substring(2)
            val mm = parts[1].padStart(2, '0')
            val dd = parts[2].padStart(2, '0')
            return "$yy$mm$dd"
        }
        return formattedDate.replace("-", "").takeLast(6)
    }

    private fun ApodResponse.processUrlsAndExplanation(): ApodResponse {
        val extractedImage = extractImageUrlFromBasicHtml(basicHtml)
        val directImage = extractedImage ?: hdUrl
        val resolvedStandardUrl = if (mediaType == "image" && !directImage.isNullOrBlank()) {
            directImage
        } else {
            url
        }

        val cleanedExplanation = explanation
            .replace(Regex("(?i)^<strong>Explanation:\\s*</strong>"), "")
            .replace(Regex("<[^>]*>"), "")
            .trim()

        return this.copy(
            url = resolvedStandardUrl,
            hdUrl = hdUrl ?: resolvedStandardUrl,
            explanation = cleanedExplanation
        )
    }

    private fun extractImageUrlFromBasicHtml(html: String): String? {
        if (html.isBlank()) return null
        val match = Regex("(?i)<img\\s+[^>]*src=[\"']([^\"']+)[\"']").find(html)
        return match?.groupValues?.get(1)
    }
}