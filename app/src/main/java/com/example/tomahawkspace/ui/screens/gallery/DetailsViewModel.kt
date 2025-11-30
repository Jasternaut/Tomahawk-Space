package com.example.tomahawkspace.ui.screens.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomahawkspace.data.local.entity.ApodEntity
import com.example.tomahawkspace.data.repository.ApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val apodRepository: ApodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val date: String? = savedStateHandle["date"]

    private val _apod = MutableStateFlow<ApodEntity?>(null)
    val apod: StateFlow<ApodEntity?> = _apod.asStateFlow()

    init {
        loadApod()
    }

    private fun loadApod() {
        viewModelScope.launch {
            if (date != null) {
                _apod.value = apodRepository.getApodByDate(date)
            }
        }
    }

    fun deleteApod(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val currentApod = _apod.value
            if (currentApod != null) {
                apodRepository.deleteApod(currentApod)
                onDeleted()
            }
        }
    }
}
