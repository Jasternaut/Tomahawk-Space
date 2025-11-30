package com.example.tomahawkspace.ui.screens.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.squareShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSearchByDateEnabled by viewModel.isSearchByDateEnabled.collectAsState()
    val dynamicHomeBackground by viewModel.dynamicHomeBackground.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Состояние для динамической цветовой схемы
    var dynamicColorScheme by remember { mutableStateOf<ColorScheme?>(null) }
    
    val context = LocalContext.current
    
    // Сбрасываем динамическую схему, если настройка выключена или данные сменились на не-Success
    LaunchedEffect(dynamicHomeBackground, uiState) {
        if (!dynamicHomeBackground || uiState !is HomeUiState.Success) {
            dynamicColorScheme = null
        }
    }

    // Эффект для извлечения цвета при загрузке картинки
    if (dynamicHomeBackground && uiState is HomeUiState.Success) {
        val apod = (uiState as HomeUiState.Success).apod
        if (apod.mediaType == "image") {
            LaunchedEffect(apod.url) {
                try {
                    val loader = context.imageLoader
                    val request = ImageRequest.Builder(context)
                        .data(apod.url)
                        .allowHardware(false) // Важно для Palette
                        .build()

                    val result = withContext(Dispatchers.IO) {
                        loader.execute(request)
                    }

                    if (result is SuccessResult) {
                        val bitmap = (result.drawable as BitmapDrawable).bitmap
                        val palette = Palette.from(bitmap).generate()
                        
                        // Генерируем тему на основе доминирующего или насыщенного цвета
                        val seedColor = palette.vibrantSwatch?.rgb 
                            ?: palette.dominantSwatch?.rgb 
                            ?: palette.mutedSwatch?.rgb
                        
                        if (seedColor != null) {
                            dynamicColorScheme = generateColorSchemeFromSeed(seedColor, isDark = true)
                        }
                    }
                } catch (e: Exception) {
                    // Игнорируем ошибки загрузки для палитры
                }
            }
        }
    }

    // Обертка для применения темы
    val content = @Composable {
        Surface(
             modifier = Modifier.fillMaxSize(),
             color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Область контента
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (val state = uiState) {
                        is HomeUiState.Idle -> {
                            Text(
                                text = if (isSearchByDateEnabled) 
                                    "Нажмите кнопку ниже, чтобы выбрать дату и загрузить картинку" 
                                else 
                                    "Нажмите кнопку ниже, чтобы загрузить картинку дня",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        is HomeUiState.Loading -> {
                            LoadingIndicator()
                        }
                        is HomeUiState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                        is HomeUiState.Success -> {
                            val apod = state.apod
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = apod.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                if (apod.mediaType == "image") {
                                    AsyncImage(
                                        model = apod.url,
                                        contentDescription = apod.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Медиа контент (${apod.mediaType}) не поддерживается для отображения")
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = apod.explanation,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Дата: ${apod.date}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }

                // Нижняя панель с кнопками
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (isSearchByDateEnabled) {
                                showDatePicker = true
                            } else {
                                viewModel.loadApod()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = squareShape
                    ) {
                        Text(if (isSearchByDateEnabled) "Выбрать дату" else "Загрузить")
                    }

                    if (uiState is HomeUiState.Success) {
                        Spacer(modifier = Modifier.padding(start = 16.dp))

                        val successState = uiState as HomeUiState.Success
                        FilledIconButton(
                            onClick = { viewModel.toggleLike(successState.apod) },
                            shape = if (successState.isLiked) MaterialTheme.shapes.medium else CircleShape
                        ) {
                            Icon(
                                imageVector = if (successState.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Лайк",
                                tint = if (successState.isLiked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= System.currentTimeMillis()
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Date(millis)
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            val formattedDate = formatter.format(date)
                            viewModel.loadApod(formattedDate)
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Применяем динамическую тему, если она сгенерирована
    if (dynamicColorScheme != null) {
        MaterialTheme(
            colorScheme = dynamicColorScheme!!,
            content = content
        )
    } else {
        content()
    }
}

// Простая функция генерации цветовой схемы на основе одного цвета
// В реальном проекте можно использовать material-color-utilities для полноценной генерации Tonal Palettes
fun generateColorSchemeFromSeed(seedColor: Int, isDark: Boolean): ColorScheme {
    val primary = Color(seedColor)
    // Генерируем контрастный цвет для текста
    val onPrimary = if (ColorUtils.calculateLuminance(seedColor) > 0.5) Color.Black else Color.White
    
    // Простой алгоритм для фона: очень темная/светлая версия основного цвета
    val background = if (isDark) {
        Color(ColorUtils.blendARGB(seedColor, android.graphics.Color.BLACK, 0.9f))
    } else {
        Color(ColorUtils.blendARGB(seedColor, android.graphics.Color.WHITE, 0.9f))
    }
    val onBackground = if (isDark) Color.White else Color.Black
    
    val surface = if (isDark) {
        Color(ColorUtils.blendARGB(seedColor, android.graphics.Color.BLACK, 0.8f))
    } else {
        Color(ColorUtils.blendARGB(seedColor, android.graphics.Color.WHITE, 0.8f))
    }
    val onSurface = if (isDark) Color.White else Color.Black

    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            // Можно добавить secondary/tertiary через сдвиг Hue
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface
        )
    }
}
