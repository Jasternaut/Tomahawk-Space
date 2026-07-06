package com.tomahawk.space.ui.screens.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.tomahawk.space.data.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    var apiKey by mutableStateOf("")
        private set

    var isValid by mutableStateOf(true)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onKeyChange(newKey: String) {
        apiKey = newKey
        isValid = true
    }

    fun confirmKey(onSuccess: () -> Unit) {
        val regex = Regex("^[a-zA-Z0-9]{40}$|^DEMO_KEY$")
        if (!regex.matches(apiKey)) {
            isValid = false
            return
        }

        viewModelScope.launch {
            isLoading = true
            val isKeyValid = repository.validateKey(apiKey)

            if (isKeyValid) {
                repository.saveKey(apiKey)
                isLoading = false
                onSuccess()
            } else {
                isLoading = false
                isValid = false
            }
        }
    }
}