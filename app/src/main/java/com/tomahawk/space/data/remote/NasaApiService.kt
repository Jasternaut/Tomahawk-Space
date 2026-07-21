package com.tomahawk.space.data.remote

import com.tomahawk.space.data.model.ApodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String
    ): ApodResponse
}
