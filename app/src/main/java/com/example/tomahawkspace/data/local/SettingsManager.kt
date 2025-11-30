package com.example.tomahawkspace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Enum для режимов темы
enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

class SettingsManager @Inject constructor(private val context: Context) {

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val SEARCH_BY_DATE = booleanPreferencesKey("search_by_date")
        val HD_IMAGE = booleanPreferencesKey("hd_image")
        
        // Настройки оформления
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val HIDE_SETTINGS_DIVIDERS = booleanPreferencesKey("hide_settings_dividers")
        val DYNAMIC_HOME_BACKGROUND = booleanPreferencesKey("dynamic_home_background")
    }

    val apiKey: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[API_KEY]
        }

    val isSearchByDateEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SEARCH_BY_DATE] ?: false
        }

    val isHdImageEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HD_IMAGE] ?: false
        }

    // По умолчанию системная тема
    val themeMode: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            try {
                val mode = preferences[THEME_MODE] ?: AppTheme.SYSTEM.name
                AppTheme.valueOf(mode)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }

    // По умолчанию динамические цвета включены (true)
    val dynamicColors: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLORS] ?: true
        }

    // По умолчанию разделители видны (false)
    val hideSettingsDividers: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HIDE_SETTINGS_DIVIDERS] ?: false
        }

    // По умолчанию динамический фон выключен (false)
    val dynamicHomeBackground: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_HOME_BACKGROUND] ?: false
        }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = apiKey
        }
    }

    suspend fun setSearchByDateEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_BY_DATE] = enabled
        }
    }

    suspend fun setHdImageEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HD_IMAGE] = enabled
        }
    }
    
    suspend fun setThemeMode(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme.name
        }
    }
    
    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLORS] = enabled
        }
    }

    suspend fun setHideSettingsDividers(hidden: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIDE_SETTINGS_DIVIDERS] = hidden
        }
    }

    suspend fun setDynamicHomeBackground(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_HOME_BACKGROUND] = enabled
        }
    }
}
