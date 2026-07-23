package com.tomahawk.space.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.AuthRepository
import kotlinx.coroutines.launch

sealed class SettingsNav {
    object Main : SettingsNav()
    object ApiSettings : SettingsNav()
}

class SettingsViewModel(private val repository: AuthRepository) : ViewModel() {
    var currentNav by mutableStateOf<SettingsNav>(SettingsNav.Main)
        private set

    var apiKey by mutableStateOf("")
        private set

    var newKeyInput by mutableStateOf("")

    var isKeyVisible by mutableStateOf(false)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    var updateError by mutableStateOf<String?>(null)
        private set

    init {
        loadKey()
    }

    private fun loadKey() {
        viewModelScope.launch {
            apiKey = repository.getKey() ?: ""
            newKeyInput = apiKey
        }
    }

    fun navigateTo(nav: SettingsNav) {
        currentNav = nav
    }

    fun goBack() {
        currentNav = SettingsNav.Main
    }

    fun toggleKeyVisibility() {
        isKeyVisible = !isKeyVisible
    }

    fun updateApiKey() {
        val keyToSave = newKeyInput.trim()
        val regex = Regex("^[a-zA-Z0-9]{40}$|^DEMO_KEY$")
        if (!regex.matches(keyToSave)) {
            updateError = "Invalid key format"
            return
        }

        viewModelScope.launch {
            isUpdating = true
            val isValid = repository.validateKey(keyToSave)
            if (isValid) {
                repository.saveKey(keyToSave)
                apiKey = keyToSave
                updateError = null
            } else {
                updateError = "Key validation failed"
            }
            isUpdating = false
        }
    }
}

class SettingsViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
