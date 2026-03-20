package com.adapty.exampleapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    AppInitializer.initialize()
    Window(
        onCloseRequest = ::exitApplication,
        title = "AdaptyExampleApp",
    ) {
        App()
    }
}