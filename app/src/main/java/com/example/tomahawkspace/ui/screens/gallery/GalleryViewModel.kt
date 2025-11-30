package com.example.tomahawkspace.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomahawkspace.data.local.entity.ApodEntity
import com.example.tomahawkspace.data.repository.ApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    apodRepository: ApodRepository
) : ViewModel() {

    val savedApods: StateFlow<List<ApodEntity>> = apodRepository.allApods
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
