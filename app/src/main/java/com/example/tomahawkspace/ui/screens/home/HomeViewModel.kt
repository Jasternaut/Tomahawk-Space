package com.example.tomahawkspace.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomahawkspace.data.api.NasaApi
import com.example.tomahawkspace.data.api.model.ApodResponse
import com.example.tomahawkspace.data.local.SettingsManager
import com.example.tomahawkspace.data.local.entity.ApodEntity
import com.example.tomahawkspace.data.repository.ApodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Idle : HomeUiState
    object Loading : HomeUiState
    data class Success(val apod: ApodResponse, val isLiked: Boolean = false) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val nasaApi: NasaApi,
    private val apodRepository: ApodRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val isSearchByDateEnabled: StateFlow<Boolean> = settingsManager.isSearchByDateEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val dynamicHomeBackground: StateFlow<Boolean> = settingsManager.dynamicHomeBackground
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private var observerJob: Job? = null

    fun loadApod(date: String? = null) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = nasaApi.getAstronomyPictureOfTheDay(date)
                if (response.isSuccessful && response.body() != null) {
                    var apod = response.body()!!
                    
                    // Проверяем настройку HD качества
                    val isHdEnabled = settingsManager.isHdImageEnabled.first()
                    if (isHdEnabled && !apod.hdUrl.isNullOrBlank()) {
                        // Если включено HD и есть ссылка, используем её как основную
                        apod = apod.copy(url = apod.hdUrl)
                    }

                    // Начинаем подписку на состояние лайка для этого APOD
                    observeApodStatus(apod)
                } else {
                    _uiState.value = HomeUiState.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun observeApodStatus(apod: ApodResponse) {
        // Отменяем предыдущую подписку, если она была
        observerJob?.cancel()
        
        observerJob = viewModelScope.launch {
            apodRepository.getApodFlowByDate(apod.date).collectLatest { entity ->
                // Каждый раз, когда меняется запись в БД для этой даты, обновляем UI
                _uiState.value = HomeUiState.Success(apod, isLiked = entity != null)
            }
        }
    }

    fun toggleLike(apod: ApodResponse) {
        viewModelScope.launch {
            // Используем getApodByDate для получения текущего состояния (entity) для операции
            val entity = apodRepository.getApodByDate(apod.date)
            if (entity != null) {
                // Удаляем из БД
                apodRepository.deleteApod(entity)
            } else {
                // Сохраняем в БД
                val newEntity = ApodEntity(
                    date = apod.date,
                    title = apod.title,
                    url = apod.url, // Здесь уже будет HD ссылка, если она была подменена при загрузке
                    explanation = apod.explanation,
                    mediaType = apod.mediaType,
                    hdUrl = apod.hdUrl
                )
                apodRepository.insertApod(newEntity)
            }
            // Состояние UI обновится автоматически благодаря observeApodStatus
        }
    }
}
