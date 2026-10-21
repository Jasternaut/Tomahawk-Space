package com.tomahawk.space

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.tomahawk.space.ui.theme.MonochromeDarkColorScheme
import com.tomahawk.space.ui.theme.MonochromeLightColorScheme
import com.tomahawk.space.ui.theme.Typography
import okio.Path.Companion.toPath
import java.io.File

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.RELEASE}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

private var androidContext: Context? = null

fun initAndroidContext(context: Context) {
    androidContext = context.applicationContext
}

actual fun createDataStore(name: String): DataStore<Preferences> {
    val ctx = checkNotNull(androidContext) {
        "Android Context not initialized. Call initAndroidContext(applicationContext) before using DataStore."
    }
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            File(ctx.filesDir, "$name.preferences_pb").absolutePath.toPath()
        }
    )
}

actual fun showToast(message: String) {
    androidContext?.let {
        Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
actual fun PlatformTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MonochromeDarkColorScheme
        else -> MonochromeLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            if (activity != null) {
                WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}