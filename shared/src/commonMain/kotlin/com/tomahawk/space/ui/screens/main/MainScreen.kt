package com.tomahawk.space.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.tomahawk.space.ui.theme.getGeologicaFontFamily
import org.jetbrains.compose.resources.stringResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.bento_description_title
import tomahawkspace.shared.generated.resources.confirm
import tomahawkspace.shared.generated.resources.gallery_detail_close
import tomahawkspace.shared.generated.resources.general_error_format
import tomahawkspace.shared.generated.resources.general_load
import tomahawkspace.shared.generated.resources.general_load_by_date
import tomahawkspace.shared.generated.resources.general_press_button
import tomahawkspace.shared.generated.resources.like_button_desc
import tomahawkspace.shared.generated.resources.resolution_high
import tomahawkspace.shared.generated.resources.resolution_standard
import tomahawkspace.shared.generated.resources.unlike_button_desc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val state by viewModel.state.collectAsState()
    val isLiked by viewModel.isCurrentLiked.collectAsState()
    val searchByDate by viewModel.searchByDate.collectAsState()
    val highResImages by viewModel.highResImages.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.loadApod(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.gallery_detail_close))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val geologica = getGeologicaFontFamily()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header (FlClash style: title on left, circular button on right)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "Tomahawk ",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = geologica,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Space",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = geologica,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        if (searchByDate) {
                            showDatePicker = true
                        } else {
                            viewModel.loadApod()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (searchByDate) Icons.Default.CalendarToday else Icons.Default.Refresh,
                        contentDescription = stringResource(
                            if (searchByDate) Res.string.general_load_by_date else Res.string.general_load
                        )
                    )
                }
            }
        }

        when (val s = state) {
            is MainState.Initial -> {
                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.general_press_button),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (searchByDate) {
                                        showDatePicker = true
                                    } else {
                                        viewModel.loadApod()
                                    }
                                },
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Icon(
                                    if (searchByDate) Icons.Default.CalendarToday else Icons.Default.Refresh,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    stringResource(
                                        if (searchByDate) Res.string.general_load_by_date else Res.string.general_load
                                    )
                                )
                            }
                        }
                    }
                }
            }
            is MainState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is MainState.Error -> {
                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.general_error_format, s.message),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is MainState.Success -> {
                val apod = s.apod
                val isHighRes = highResImages && !apod.hdUrl.isNullOrEmpty()
                val imageUrl = if (isHighRes) apod.hdUrl else apod.url

                // 2. Hero Image Card
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (apod.mediaType == "image") {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = apod.title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(28.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = apod.mediaType.uppercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // HD badge overlay
                            Surface(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = stringResource(if (isHighRes) Res.string.resolution_high else Res.string.resolution_standard),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Metadata & Action Card
                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "• ${apod.date}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = apod.mediaType.uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = apod.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (searchByDate) {
                                            showDatePicker = true
                                        } else {
                                            viewModel.loadApod()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Icon(
                                        if (searchByDate) Icons.Default.CalendarToday else Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        stringResource(
                                            if (searchByDate) Res.string.general_load_by_date else Res.string.general_load
                                        )
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = { viewModel.toggleLike() },
                                    modifier = Modifier.size(52.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (isLiked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                        contentColor = if (isLiked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = stringResource(if (isLiked) Res.string.unlike_button_desc else Res.string.like_button_desc),
                                        tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Description Card
                if (apod.explanation.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = stringResource(Res.string.bento_description_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = apod.explanation,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}