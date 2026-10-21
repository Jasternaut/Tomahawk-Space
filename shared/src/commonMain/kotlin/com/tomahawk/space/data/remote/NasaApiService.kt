package com.tomahawk.space.data.remote

import com.tomahawk.space.data.model.ApodResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
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
    suspend fun getApod(apiKey: String, date: String? = null): ApodResponse {
        return client.get("https://api.nasa.gov/planetary/apod") {
            parameter("api_key", apiKey)
            if (!date.isNullOrBlank()) {
                parameter("date", date)
            }
        }.body()
    }

    suspend fun validateKey(apiKey: String): Boolean {
        return try {
            val response = client.get("https://api.nasa.gov/planetary/apod") {
                parameter("api_key", apiKey)
            }
            response.status.isSuccess()
        } catch (_: Exception) {
            false
        }
    }
}