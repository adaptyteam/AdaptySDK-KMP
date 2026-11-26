package com.adapty.exampleapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.adapty.ui.AdaptyUI


class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.initialize()

        // Flows draw their custom elements with this resolver (see CustomElements.kt).
        // AdaptyUI here is the native Android one (io.adapty:android-ui), not the KMP one.
        AdaptyUI.setCustomElementsResolver(ExampleCustomElementsResolver())
    }
}

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}

