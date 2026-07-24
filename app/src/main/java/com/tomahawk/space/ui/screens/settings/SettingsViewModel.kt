package com.tomahawk.space.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SettingsError {
    object InvalidFormat : SettingsError()
    object ValidationFailed : SettingsError()
}

class SettingsViewModel(private val repository: AuthRepository) : ViewModel() {
    var apiKey by mutableStateOf("")
        private set

    private var _newKeyInput by mutableStateOf("")
    var newKeyInput: String
        get() = _newKeyInput
        set(value) {
            _newKeyInput = value
            if (updateError != null) {
                updateError = null
            }
        }

    var isKeyVisible by mutableStateOf(false)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    var updateError by mutableStateOf<SettingsError?>(null)
        private set

    val searchByDate: StateFlow<Boolean> = repository.searchByDate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadKey()
    }

    private fun loadKey() {
        viewModelScope.launch {
            apiKey = repository.getKey() ?: ""
            newKeyInput = apiKey
        }
    }

    fun toggleKeyVisibility() {
        isKeyVisible = !isKeyVisible
    }

    fun toggleSearchByDate(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSearchByDate(enabled)
        }
    }

    fun updateApiKey() {
        val keyToSave = newKeyInput.trim()
        val regex = Regex("^[a-zA-Z0-9]{40}$|^DEMO_KEY$")
        if (!regex.matches(keyToSave)) {
            updateError = SettingsError.InvalidFormat
            return
        }

        viewModelScope.launch {
            isUpdating = true
            updateError = null
            try {
                val isValid = repository.validateKey(keyToSave)
                if (isValid) {
                    repository.saveKey(keyToSave)
                    apiKey = keyToSave
                    newKeyInput = keyToSave
                    updateError = null
                } else {
                    updateError = SettingsError.ValidationFailed
                }
            } finally {
                isUpdating = false
            }
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
