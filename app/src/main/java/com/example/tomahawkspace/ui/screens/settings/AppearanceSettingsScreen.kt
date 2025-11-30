package com.example.tomahawkspace.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.tomahawkspace.data.local.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val dynamicColors by viewModel.dynamicColors.collectAsState()
    val hideSettingsDividers by viewModel.hideSettingsDividers.collectAsState()
    val dynamicHomeBackground by viewModel.dynamicHomeBackground.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Оформление") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- Тема приложения ---
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Тема приложения",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(modifier = Modifier.selectableGroup()) {
                ThemeOption(
                    text = "Системная",
                    selected = themeMode == AppTheme.SYSTEM,
                    onClick = { viewModel.setThemeMode(AppTheme.SYSTEM) }
                )
                ThemeOption(
                    text = "Светлая",
                    selected = themeMode == AppTheme.LIGHT,
                    onClick = { viewModel.setThemeMode(AppTheme.LIGHT) }
                )
                ThemeOption(
                    text = "Тёмная",
                    selected = themeMode == AppTheme.DARK,
                    onClick = { viewModel.setThemeMode(AppTheme.DARK) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            // --- Динамические цвета (Android 12+) ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsSwitchRow(
                    title = "Динамические цвета",
                    subtitle = "Использовать цвета обоев устройства",
                    checked = dynamicColors,
                    onCheckedChange = { viewModel.toggleDynamicColors(it) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Разделители ---
            SettingsSwitchRow(
                title = "Скрыть разделители",
                subtitle = "Убрать линии между разделами в настройках",
                checked = hideSettingsDividers,
                onCheckedChange = { viewModel.toggleHideSettingsDividers(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Динамический фон ---
            SettingsSwitchRow(
                title = "Динамический фон главной",
                subtitle = "Использовать картинку дня как фон",
                checked = dynamicHomeBackground,
                onCheckedChange = { viewModel.toggleDynamicHomeBackground(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null 
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
