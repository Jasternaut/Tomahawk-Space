package com.tomahawk.space.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    private val apiKey = stringPreferencesKey("api_key")

    private val api = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .build()
        .create(NasaApi::class.java)

    suspend fun validateKey(key: String): Boolean {
        return try {
            val response = api.validateKey(key)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveKey(key: String) {
        context.dataStore.edit { it[apiKey] = key }
    }

    suspend fun getKey(): String? {
        return context.dataStore.data.map { it[apiKey] }.first()
    }

    interface NasaApi {
        @GET("planetary/apod")
        suspend fun validateKey(@Query("api_key") apiKey: String): retrofit2.Response<Unit>
    }
}