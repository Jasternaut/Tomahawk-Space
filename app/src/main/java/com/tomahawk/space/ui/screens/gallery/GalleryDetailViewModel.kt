package com.tomahawk.space.ui.screens.gallery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryDetailViewModel(
    private val date: String,
    private val galleryRepository: GalleryRepository,
    private val apodRepository: ApodRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val apod: StateFlow<ApodResponse?> = galleryRepository.likedApods.map { list ->
        list.find { it.date == date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val highResImages: StateFlow<Boolean> = authRepository.highResImages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var isRefreshing by mutableStateOf(false)
        private set

    fun removeLike() {
        viewModelScope.launch {
            galleryRepository.removeLike(date)
        }
    }

    fun refreshApodMetadata() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                val apiKey = authRepository.getKey()
                if (apiKey != null) {
                    val freshApod = apodRepository.getApod(apiKey, date)
                    galleryRepository.updateApod(freshApod)
                }
            } catch (_: Exception) {
                // we might show a toast.
            } finally {
                isRefreshing = false
            }
        }
    }
}

class GalleryDetailViewModelFactory(
    private val date: String,
    private val galleryRepository: GalleryRepository,
    private val apodRepository: ApodRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryDetailViewModel(date, galleryRepository, apodRepository, authRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
