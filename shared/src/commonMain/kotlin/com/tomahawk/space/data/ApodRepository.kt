package com.tomahawk.space.data

import com.tomahawk.space.data.model.ApodResponse
import com.tomahawk.space.data.remote.NasaApiService

class ApodRepository(
    private val apiService: NasaApiService = NasaApiService()
) {
    suspend fun getApod(apiKey: String, date: String? = null): ApodResponse {
        return apiService.getApod(apiKey, date)
    }
}