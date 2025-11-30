package com.example.tomahawkspace.data.api

import com.example.tomahawkspace.data.api.model.ApodResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApi {
    // Пример запроса: Astronomy Picture of the Day
    // api_key добавляется автоматически через AuthInterceptor
    @GET("planetary/apod")
    suspend fun getAstronomyPictureOfTheDay(
        @Query("date") date: String? = null
    ): Response<ApodResponse>
}
