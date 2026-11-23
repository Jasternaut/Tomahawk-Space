package com.tomahawk.space.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.settings_appearance
import tomahawkspace.shared.generated.resources.settings_appearance_header
import tomahawkspace.shared.generated.resources.settings_back
import tomahawkspace.shared.generated.resources.settings_dynamic_colors
import tomahawkspace.shared.generated.resources.settings_dynamic_colors_desc
import tomahawkspace.shared.generated.resources.settings_floating_nav_bar
import tomahawkspace.shared.generated.resources.settings_floating_nav_bar_desc
import tomahawkspace.shared.generated.resources.settings_hide_dividers
import tomahawkspace.shared.generated.resources.settings_hide_dividers_desc
import tomahawkspace.shared.generated.resources.settings_system_header
import tomahawkspace.shared.generated.resources.settings_theme_dark
import tomahawkspace.shared.generated.resources.settings_theme_light
import tomahawkspace.shared.generated.resources.settings_theme_preview
import tomahawkspace.shared.generated.resources.settings_theme_system
import tomahawkspace.shared.generated.resources.settings_theme_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val appTheme by viewModel.appTheme.collectAsState()
    val dynamicColors by viewModel.dynamicColors.collectAsState()
    val hideDividers by viewModel.hideDividers.collectAsState()
    val isFloatingNavBar by viewModel.isFloatingNavBar.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_appearance)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.settings_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Theme Preview Card
            item {
                SettingsSection(title = stringResource(Res.string.settings_theme_preview)) {
                    ThemeMiniPreviewCard(
                        isFloatingBar = isFloatingNavBar,
                        isPureBlack = appTheme == 2
                    )
                }
            }

            // Appearance Section
            item {
                SettingsSection(title = stringResource(Res.string.settings_appearance_header)) {
                    ThemeSelectionCard(
                        selectedTheme = appTheme,
                        onThemeSelected = { viewModel.setAppTheme(it) },
                        hideDividers = hideDividers
                    )
                }
            }

            // System & Navigation Settings Section
            item {
                SettingsSection(title = stringResource(Res.string.settings_system_header)) {
                    SettingsGroup {
                        SettingsSwitchItem(
                            title = stringResource(Res.string.settings_floating_nav_bar),
                            description = stringResource(Res.string.settings_floating_nav_bar_desc),
                            icon = Icons.Default.Navigation,
                            checked = isFloatingNavBar,
                            onCheckedChange = { viewModel.toggleFloatingNavBar(it) }
                        )
                        if (!hideDividers) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                        SettingsSwitchItem(
                            title = stringResource(Res.string.settings_dynamic_colors),
                            description = stringResource(Res.string.settings_dynamic_colors_desc),
                            icon = Icons.Default.Palette,
                            checked = dynamicColors,
                            onCheckedChange = { viewModel.toggleDynamicColors(it) }
                        )
                        if (!hideDividers) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                        SettingsSwitchItem(
                            title = stringResource(Res.string.settings_hide_dividers),
                            description = stringResource(Res.string.settings_hide_dividers_desc),
                            icon = Icons.Default.ViewStream,
                            checked = hideDividers,
                            onCheckedChange = { viewModel.toggleHideDividers(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 8.dp)
        )
        content()
    }
}

@Composable
fun ThemeMiniPreviewCard(isFloatingBar: Boolean, isPureBlack: Boolean) {
    val bgColor = if (isPureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainerLowest

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(190.dp)
                    .height(220.dp),
                shape = RoundedCornerShape(24.dp),
                color = bgColor,
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mini card 1
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.CenterEnd)
                            )
                        }
                    }

                    // Mini card 2
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(14.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Mini nav bar preview (changes shape dynamically)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .padding(horizontal = if (isFloatingBar) 6.dp else 0.dp),
                        shape = if (isFloatingBar) CircleShape else RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == 0) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionCard(
    selectedTheme: Int,
    onThemeSelected: (Int) -> Unit,
    hideDividers: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_theme_title), style = MaterialTheme.typography.titleMedium) },
                supportingContent = {
                    val themeName = when (selectedTheme) {
                        1 -> stringResource(Res.string.settings_theme_light)
                        2 -> stringResource(Res.string.settings_theme_dark)
                        else -> stringResource(Res.string.settings_theme_system)
                    }
                    Text(themeName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (selectedTheme) {
                            1 -> Icons.Default.LightMode
                            2 -> Icons.Default.DarkMode
                            else -> Icons.Default.SettingsSuggest
                        }
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                },
                trailingContent = {
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                },
                modifier = Modifier.clickable { expanded = !expanded },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    if (!hideDividers) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                    ThemeOption(
                        title = stringResource(Res.string.settings_theme_system),
                        icon = Icons.Default.SettingsSuggest,
                        selected = selectedTheme == 0,
                        onClick = {
                            onThemeSelected(0)
                            expanded = false
                        }
                    )
                    ThemeOption(
                        title = stringResource(Res.string.settings_theme_light),
                        icon = Icons.Default.LightMode,
                        selected = selectedTheme == 1,
                        onClick = {
                            onThemeSelected(1)
                            expanded = false
                        }
                    )
                    ThemeOption(
                        title = stringResource(Res.string.settings_theme_dark),
                        icon = Icons.Default.DarkMode,
                        selected = selectedTheme == 2,
                        onClick = {
                            onThemeSelected(2)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            if (selected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}