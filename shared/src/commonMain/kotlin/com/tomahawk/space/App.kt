package com.tomahawk.space

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tomahawk.space.data.ApodRepository
import com.tomahawk.space.data.AuthRepository
import com.tomahawk.space.data.GalleryRepository
import com.tomahawk.space.ui.navigation.RootNavigation
import com.tomahawk.space.ui.theme.TomahawkSpaceTheme

@Composable
fun App() {
    val authRepository = remember { AuthRepository() }
    val apodRepository = remember { ApodRepository() }
    val galleryRepository = remember { GalleryRepository() }

    TomahawkSpaceTheme(authRepository = authRepository) {
        RootNavigation(
            authRepository = authRepository,
            apodRepository = apodRepository,
            galleryRepository = galleryRepository
        )
    }
}