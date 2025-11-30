package com.example.tomahawkspace.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomahawkspace.data.local.AppTheme
import com.example.tomahawkspace.data.local.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val apiKey: StateFlow<String?> = settingsManager.apiKey
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isSearchByDateEnabled: StateFlow<Boolean> = settingsManager.isSearchByDateEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isHdImageEnabled: StateFlow<Boolean> = settingsManager.isHdImageEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val themeMode: StateFlow<AppTheme> = settingsManager.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    val dynamicColors: StateFlow<Boolean> = settingsManager.dynamicColors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val hideSettingsDividers: StateFlow<Boolean> = settingsManager.hideSettingsDividers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val dynamicHomeBackground: StateFlow<Boolean> = settingsManager.dynamicHomeBackground
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsManager.saveApiKey(key)
        }
    }

    fun toggleSearchByDate(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setSearchByDateEnabled(enabled)
        }
    }

    fun toggleHdImage(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setHdImageEnabled(enabled)
        }
    }
    
    fun setThemeMode(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.setThemeMode(theme)
        }
    }
    
    fun toggleDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDynamicColors(enabled)
        }
    }

    fun toggleHideSettingsDividers(hidden: Boolean) {
        viewModelScope.launch {
            settingsManager.setHideSettingsDividers(hidden)
        }
    }

    fun toggleDynamicHomeBackground(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDynamicHomeBackground(enabled)
        }
    }
}
