package com.adapty.exampleapp.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.adapty.kmp.models.AdaptyOnboarding


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingNativeViewScreen(
    onboarding: AdaptyOnboarding,
    showToastEvents: Boolean,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = { Text("Onboarding ${onboarding.placement.id}") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(painterResource(com.adapty.exampleapp.R.drawable.ic_close), contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            if (showToastEvents) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }

    ) {

//        AdaptyUIOnboardingPlatformView(
//            modifier = Modifier.fillMaxSize().padding(it),
//            onboarding = onboarding,
//            onDidFinishLoading = { meta ->
//                AppLogger.d("#Example# Platform View onDidFinishLoading: $meta")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onDidFinishLoading', 'Meta: $meta")
//
//                }
//            },
//            onDidFailWithError = { error ->
//                AppLogger.d("#Example# onDidFailWithError: $error")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onDidFailWithError: ${error.message}")
//                }
//            },
//            onCloseAction = { meta, id ->
//                AppLogger.d("#Example# onCloseAction: $meta, $id")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onCloseAction, Id: $id")
//                }
//                onNavigateBack()
//            },
//            onPaywallAction = { meta, id ->
//                AppLogger.d("#Example# onPaywallAction: $meta, $id")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onPaywallAction, Id: $id")
//                }
//            },
//            onCustomAction = { meta, id ->
//                AppLogger.d("#Example# onCustomAction: $meta, $id")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onCustomAction, Id: $id")
//                }
//            },
//            onStateUpdatedAction = { meta, elementId, params ->
//                AppLogger.d("#Example# onStateUpdatedAction: $meta, $elementId, $params")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onStateUpdatedAction, ElementId: $elementId, Params: $params")
//                }
//            },
//            onAnalyticsEvent = { meta, event ->
//                AppLogger.d("#Example# onAnalyticsEvent: $meta, $event")
//                coroutineScope.launch {
//                    snackbarHostState.showSnackbar("Action: onAnalyticsEvent, Event: $event")
//                }
//            }
//        )
    }

}