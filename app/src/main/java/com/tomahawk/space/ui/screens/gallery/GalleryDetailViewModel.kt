package com.tomahawk.space.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryDetailViewModel(
    private val date: String,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    val apod: StateFlow<ApodResponse?> = galleryRepository.likedApods.map { list ->
        list.find { it.date == date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun removeLike() {
        viewModelScope.launch {
            galleryRepository.removeLike(date)
        }
    }
}

class GalleryDetailViewModelFactory(
    private val date: String,
    private val galleryRepository: GalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryDetailViewModel(date, galleryRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
