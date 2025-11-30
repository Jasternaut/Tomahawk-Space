package com.example.tomahawkspace.ui.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomahawkspace.data.local.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            settingsManager.saveApiKey(key)
        }
    }
}
