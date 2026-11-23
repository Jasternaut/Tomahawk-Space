package com.tomahawk.space

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.tomahawk.space.ui.theme.MonochromeDarkColorScheme
import com.tomahawk.space.ui.theme.MonochromeLightColorScheme
import com.tomahawk.space.ui.theme.Typography
import okio.Path.Companion.toPath
import java.io.File

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun createDataStore(name: String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val dir = File(System.getProperty("user.home"), ".tomahawk_space")
            dir.mkdirs()
            File(dir, "$name.preferences_pb").absolutePath.toPath()
        }
    )
}

actual fun showToast(message: String) {
    println("Toast: $message")
}

@Composable
actual fun PlatformTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MonochromeDarkColorScheme else MonochromeLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}