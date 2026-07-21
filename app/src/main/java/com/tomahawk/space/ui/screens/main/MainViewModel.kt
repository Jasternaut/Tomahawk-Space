package com.tomahawk.space.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MainState {
    object Initial : MainState()
    object Loading : MainState()
    data class Success(val apod: ApodResponse) : MainState()
    data class Error(val message: String) : MainState()
}

class MainViewModel(
    private val authRepository: AuthRepository,
    private val apodRepository: ApodRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Initial)
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun loadApod() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            try {
                val key = authRepository.getKey()
                if (key != null) {
                    val response = apodRepository.getApod(key)
                    _state.value = MainState.Success(response)
                } else {
                    _state.value = MainState.Error("No API key found")
                }
            } catch (e: Exception) {
                // simple logging without long dashes or dots
                _state.value = MainState.Error(e.message ?: "unknown error")
            }
        }
    }
}

class MainViewModelFactory(
    private val authRepository: AuthRepository,
    private val apodRepository: ApodRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(authRepository, apodRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
