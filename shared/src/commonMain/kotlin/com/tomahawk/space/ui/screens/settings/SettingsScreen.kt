package com.tomahawk.space.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tomahawk.space.showToast
import org.jetbrains.compose.resources.stringResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.api_key_label
import tomahawkspace.shared.generated.resources.invalid_key
import tomahawkspace.shared.generated.resources.settings_api_category
import tomahawkspace.shared.generated.resources.settings_api_category_desc
import tomahawkspace.shared.generated.resources.settings_api_info
import tomahawkspace.shared.generated.resources.settings_appearance
import tomahawkspace.shared.generated.resources.settings_appearance_desc
import tomahawkspace.shared.generated.resources.settings_apply_changes
import tomahawkspace.shared.generated.resources.settings_back
import tomahawkspace.shared.generated.resources.settings_current_key
import tomahawkspace.shared.generated.resources.settings_error_validation_failed
import tomahawkspace.shared.generated.resources.settings_general_category
import tomahawkspace.shared.generated.resources.settings_hide
import tomahawkspace.shared.generated.resources.settings_high_res
import tomahawkspace.shared.generated.resources.settings_high_res_desc
import tomahawkspace.shared.generated.resources.settings_key_copied
import tomahawkspace.shared.generated.resources.settings_key_setup
import tomahawkspace.shared.generated.resources.settings_load_params
import tomahawkspace.shared.generated.resources.settings_search_by_date
import tomahawkspace.shared.generated.resources.settings_search_by_date_desc
import tomahawkspace.shared.generated.resources.settings_show
import tomahawkspace.shared.generated.resources.settings_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(
    viewModel: SettingsViewModel,
    onNavigateToGeneral: () -> Unit,
    onNavigateToApi: () -> Unit
) {
    val hideDividers by viewModel.hideDividers.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(Res.string.settings_title), fontWeight = FontWeight.Bold) }
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
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(Res.string.settings_general_category),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    SettingsGroup {
                        SettingsMenuItem(
                            title = stringResource(Res.string.settings_appearance),
                            description = stringResource(Res.string.settings_appearance_desc),
                            icon = Icons.Default.Palette,
                            onClick = onNavigateToGeneral
                        )
                        if (!hideDividers) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                        SettingsMenuItem(
                            title = stringResource(Res.string.settings_api_category),
                            description = stringResource(Res.string.settings_api_category_desc),
                            icon = Icons.Default.Key,
                            onClick = onNavigateToApi
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiSettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_api_category)) },
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
        ApiSettingsContent(viewModel, modifier = Modifier.padding(padding))
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
fun SettingsMenuItem(
    title: String,
    description: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        supportingContent = description?.let { { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) } },
        leadingContent = {
            SettingsIcon(icon = icon)
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        },
        modifier = Modifier.clickable { onClick() },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        supportingContent = { Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
        leadingContent = {
            SettingsIcon(icon = icon)
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        },
        modifier = Modifier.clickable { onCheckedChange(!checked) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingsIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ApiSettingsContent(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val searchByDate by viewModel.searchByDate.collectAsState()
    val highResImages by viewModel.highResImages.collectAsState()
    val hideDividers by viewModel.hideDividers.collectAsState()
    val keyCopiedMsg = stringResource(Res.string.settings_key_copied)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Info Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(Res.string.settings_api_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Key Setup Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.settings_key_setup),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )

                SettingsGroup {
                    // Current Key Card
                    ListItem(
                        headlineContent = {
                            Text(
                                stringResource(Res.string.settings_current_key),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        },
                        supportingContent = {
                            val displayKey =
                                if (viewModel.apiKey.isBlank()) "—" else if (viewModel.isKeyVisible) viewModel.apiKey else "•".repeat(
                                    viewModel.apiKey.length.coerceAtMost(20)
                                )
                            Text(
                                text = displayKey,
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.blur(if (viewModel.isKeyVisible) 0.dp else 8.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { viewModel.toggleKeyVisibility() }) {
                                    val (icon, descriptionRes) = if (viewModel.isKeyVisible) {
                                        Icons.Default.VisibilityOff to Res.string.settings_hide
                                    } else {
                                        Icons.Default.Visibility to Res.string.settings_show
                                    }

                                    Icon(
                                        imageVector = icon,
                                        contentDescription = stringResource(descriptionRes),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(viewModel.apiKey))
                                    showToast(keyCopiedMsg)
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    if (!hideDividers) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.newKeyInput,
                            onValueChange = { viewModel.newKeyInput = it },
                            label = { Text(stringResource(Res.string.api_key_label)) },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.toggleNewKeyVisibility() }) {
                                    val (icon, descriptionRes) = if (viewModel.isNewKeyVisible) {
                                        Icons.Default.VisibilityOff to Res.string.settings_hide
                                    } else {
                                        Icons.Default.Visibility to Res.string.settings_show
                                    }

                                    Icon(
                                        imageVector = icon,
                                        contentDescription = stringResource(descriptionRes),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (viewModel.isNewKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = viewModel.updateError != null,
                            supportingText = {
                                viewModel.updateError?.let { error ->
                                    val message = when (error) {
                                        is SettingsError.InvalidFormat -> stringResource(Res.string.invalid_key)
                                        is SettingsError.ValidationFailed -> stringResource(Res.string.settings_error_validation_failed)
                                    }
                                    Text(message)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !viewModel.isUpdating
                        )

                        Button(
                            onClick = { viewModel.updateApiKey() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !viewModel.isUpdating && viewModel.newKeyInput.isNotBlank()
                        ) {
                            if (viewModel.isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(stringResource(Res.string.settings_apply_changes))
                            }
                        }
                    }
                }
            }
        }

        // Parameters Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.settings_load_params),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )

                SettingsGroup {
                    SettingsSwitchItem(
                        title = stringResource(Res.string.settings_search_by_date),
                        description = stringResource(Res.string.settings_search_by_date_desc),
                        icon = Icons.Default.CalendarToday,
                        checked = searchByDate,
                        onCheckedChange = { viewModel.toggleSearchByDate(it) }
                    )
                    if (!hideDividers) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                    SettingsSwitchItem(
                        title = stringResource(Res.string.settings_high_res),
                        description = stringResource(Res.string.settings_high_res_desc),
                        icon = Icons.Default.Image,
                        checked = highResImages,
                        onCheckedChange = { viewModel.toggleHighResImages(it) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}