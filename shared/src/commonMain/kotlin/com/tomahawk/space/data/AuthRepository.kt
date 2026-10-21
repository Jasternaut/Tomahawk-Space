package com.tomahawk.space.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tomahawk.space.createDataStore
import com.tomahawk.space.data.remote.NasaApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val dataStore: DataStore<Preferences> = createDataStore("auth_prefs"),
    private val apiService: NasaApiService = NasaApiService()
) {
    private val apiKey = stringPreferencesKey("api_key")
    private val searchByDateKey = booleanPreferencesKey("search_by_date")
    private val highResKey = booleanPreferencesKey("high_res_images")
    private val themeKey = intPreferencesKey("app_theme")
    private val dynamicColorsKey = booleanPreferencesKey("dynamic_colors")
    private val hideDividersKey = booleanPreferencesKey("hide_dividers")
    private val floatingNavBarKey = booleanPreferencesKey("floating_nav_bar")

    suspend fun validateKey(key: String): Boolean {
        return apiService.validateKey(key)
    }

    suspend fun saveKey(key: String) {
        dataStore.edit { it[apiKey] = key }
    }

    suspend fun getKey(): String? {
        return dataStore.data.map { it[apiKey] }.first()
    }

    val searchByDate: Flow<Boolean> = dataStore.data.map { it[searchByDateKey] ?: false }

    suspend fun setSearchByDate(enabled: Boolean) {
        dataStore.edit { it[searchByDateKey] = enabled }
    }

    val highResImages: Flow<Boolean> = dataStore.data.map { it[highResKey] ?: false }

    suspend fun setHighResImages(enabled: Boolean) {
        dataStore.edit { it[highResKey] = enabled }
    }

    val appTheme: Flow<Int> = dataStore.data.map { it[themeKey] ?: 0 }

    suspend fun setAppTheme(theme: Int) {
        dataStore.edit { it[themeKey] = theme }
    }

    val dynamicColors: Flow<Boolean> = dataStore.data.map { it[dynamicColorsKey] ?: true }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { it[dynamicColorsKey] = enabled }
    }

    val hideDividers: Flow<Boolean> = dataStore.data.map { it[hideDividersKey] ?: false }

    suspend fun setHideDividers(enabled: Boolean) {
        dataStore.edit { it[hideDividersKey] = enabled }
    }

    val isFloatingNavBar: Flow<Boolean> = dataStore.data.map { it[floatingNavBarKey] ?: true }

    suspend fun setFloatingNavBar(enabled: Boolean) {
        dataStore.edit { it[floatingNavBarKey] = enabled }
    }
}