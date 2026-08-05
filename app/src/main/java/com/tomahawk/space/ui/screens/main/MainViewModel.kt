package com.tomahawk.space.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class MainState {
    object Initial : MainState()
    object Loading : MainState()
    data class Success(val apod: ApodResponse) : MainState()
    data class Error(val message: String) : MainState()
}

class MainViewModel(
    private val authRepository: AuthRepository,
    private val apodRepository: ApodRepository,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Initial)
    val state: StateFlow<MainState> = _state.asStateFlow()

    val searchByDate: StateFlow<Boolean> = authRepository.searchByDate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val highResImages: StateFlow<Boolean> = authRepository.highResImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isCurrentLiked: StateFlow<Boolean> = combine(_state, galleryRepository.likedApods) { state, likedList ->
        if (state is MainState.Success) {
            likedList.any { it.date == state.apod.date }
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = false)

    fun loadApod(dateMillis: Long? = null) {
        viewModelScope.launch {
            _state.value = MainState.Loading
            try {
                val key = authRepository.getKey()
                if (key != null) {
                    val dateStr = dateMillis?.let {
                        val instant = Instant.ofEpochMilli(it)
                        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
                        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    val response = apodRepository.getApod(key, dateStr)
                    _state.value = MainState.Success(response)
                } else {
                    _state.value = MainState.Error("No API key found")
                }
            } catch (_: Exception) {
                _state.value = MainState.Error("Failed to load the picture of the day")
            }
        }
    }

    fun toggleLike() {
        val currentState = _state.value
        if (currentState is MainState.Success) {
            viewModelScope.launch {
                galleryRepository.toggleLike(currentState.apod)
            }
        }
    }
}

class MainViewModelFactory(
    private val authRepository: AuthRepository,
    private val apodRepository: ApodRepository,
    private val galleryRepository: GalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(authRepository, apodRepository, galleryRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
