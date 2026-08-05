package com.tomahawk.space.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    private val apiKey = stringPreferencesKey("api_key")
    private val searchByDateKey = booleanPreferencesKey("search_by_date")
    private val highResKey = booleanPreferencesKey("high_res_images")
    private val themeKey = intPreferencesKey("app_theme")
    private val dynamicColorsKey = booleanPreferencesKey("dynamic_colors")
    private val hideDividersKey = booleanPreferencesKey("hide_dividers")

    private val api = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .build()
        .create(NasaApi::class.java)

    suspend fun validateKey(key: String): Boolean {
        return try {
            val response = api.validateKey(key)
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun saveKey(key: String) {
        context.dataStore.edit { it[apiKey] = key }
    }

    suspend fun getKey(): String? {
        return context.dataStore.data.map { it[apiKey] }.first()
    }

    val searchByDate: Flow<Boolean> = context.dataStore.data.map { it[searchByDateKey] ?: false }

    suspend fun setSearchByDate(enabled: Boolean) {
        context.dataStore.edit { it[searchByDateKey] = enabled }
    }

    val highResImages: Flow<Boolean> = context.dataStore.data.map { it[highResKey] ?: false }

    suspend fun setHighResImages(enabled: Boolean) {
        context.dataStore.edit { it[highResKey] = enabled }
    }

    val appTheme: Flow<Int> = context.dataStore.data.map { it[themeKey] ?: 0 }

    suspend fun setAppTheme(theme: Int) {
        context.dataStore.edit { it[themeKey] = theme }
    }

    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { it[dynamicColorsKey] ?: true }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[dynamicColorsKey] = enabled }
    }

    val hideDividers: Flow<Boolean> = context.dataStore.data.map { it[hideDividersKey] ?: false }

    suspend fun setHideDividers(enabled: Boolean) {
        context.dataStore.edit { it[hideDividersKey] = enabled }
    }

    interface NasaApi {
        @GET("planetary/apod")
        suspend fun validateKey(@Query("api_key") apiKey: String): retrofit2.Response<Unit>
    }
}