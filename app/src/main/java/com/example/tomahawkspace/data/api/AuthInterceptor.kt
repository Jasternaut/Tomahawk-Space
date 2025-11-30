package com.example.tomahawkspace.data.api

import com.example.tomahawkspace.data.local.SettingsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val settingsManager: SettingsManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        // Получаем ключ синхронно, так как interceptor работает в фоновом потоке OkHttp, но синхронно
        val apiKey = runBlocking {
            settingsManager.apiKey.first()
        } ?: "DEMO_KEY"

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val request = originalRequest.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}
