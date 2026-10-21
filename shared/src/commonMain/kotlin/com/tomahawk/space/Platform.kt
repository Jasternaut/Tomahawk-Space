package com.tomahawk.space

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun createDataStore(name: String): DataStore<Preferences>

expect fun showToast(message: String)

@Composable
expect fun PlatformTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
)