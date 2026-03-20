package com.adapty.exampleapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    AppInitializer.initialize()
    ComposeViewport {
        App()
    }
}