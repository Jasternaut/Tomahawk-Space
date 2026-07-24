package com.tomahawk.space.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed class SortOrder {
    object Newest : SortOrder()
    object Oldest : SortOrder()
}

class GalleryViewModel(
    galleryRepository: GalleryRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow<SortOrder>(SortOrder.Newest)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val likedApods: StateFlow<List<ApodResponse>> = combine(
        galleryRepository.likedApods,
        _sortOrder
    ) { list, order ->
        when (order) {
            SortOrder.Newest -> list.sortedByDescending { it.date }
            SortOrder.Oldest -> list.sortedBy { it.date }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value is SortOrder.Newest) {
            SortOrder.Oldest
        } else {
            SortOrder.Newest
        }
    }
}

class GalleryViewModelFactory(
    private val galleryRepository: GalleryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GalleryViewModel(galleryRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}
