package com.tomahawk.space

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import tomahawkspace.shared.generated.resources.Res
import tomahawkspace.shared.generated.resources.ic_launcher

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tomahawk Space",
        icon = painterResource(Res.drawable.ic_launcher),
        state = rememberWindowState(width = 1100.dp, height = 750.dp)
    ) {
        App()
    }
}
