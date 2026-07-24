package com.tomahawk.space.data

import com.tomahawk.space.data.model.ApodResponse
import com.tomahawk.space.data.remote.NasaApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApodRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NasaApiService::class.java)

    suspend fun getApod(apiKey: String, date: String? = null): ApodResponse {
        return api.getApod(apiKey, date)
    }
}
