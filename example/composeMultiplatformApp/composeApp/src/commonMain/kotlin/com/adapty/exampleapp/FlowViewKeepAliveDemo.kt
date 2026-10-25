package com.adapty.exampleapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adapty.exampleapp.components.ListActionTile
import com.adapty.kmp.AdaptyUI
import com.adapty.kmp.models.AdaptyFlow
import com.adapty.kmp.models.AdaptyUIFlowView
import com.adapty.kmp.models.onError
import com.adapty.kmp.models.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Demonstrates keeping a flow view alive across dismissals:
 *
 * 1. "Present (keep on close)" creates a view and presents it. Closing it calls
 *    `view.dismiss(destroy = false)` (see [AdaptyUIFlowsEventsObserverImpl]), so the native view
 *    stays in memory.
 * 2. "Present kept view" presents the same view again: it resumes where the user left off.
 * 3. "Destroy kept view" releases it with `view.destroy()`. The reference is kept on purpose, so
 *    presenting it afterwards shows the SDK's error for a released view.
 */
object FlowViewKeepAliveDemo {

    private val _keptView = MutableStateFlow<AdaptyUIFlowView?>(null)
    val keptView: StateFlow<AdaptyUIFlowView?> = _keptView.asStateFlow()

    fun isKeptAlive(view: AdaptyUIFlowView): Boolean = _keptView.value?.id == view.id

    suspend fun createAndPresent(flow: AdaptyFlow) {
        AdaptyUI.createFlowView(flow = flow)
            .onSuccess { view ->
                _keptView.value = view
                view.present().onError { AppLogger.e("KeepAliveDemo, present error: $it") }
            }
            .onError { AppLogger.e("KeepAliveDemo, createFlowView error: $it") }
    }

    suspend fun presentAgain() {
        val view = _keptView.value ?: return
        view.present()
            .onSuccess { AppLogger.d("KeepAliveDemo, kept view ${view.id} presented again") }
            .onError { AppLogger.e("KeepAliveDemo, present kept view error: $it") }
    }

    suspend fun destroy() {
        val view = _keptView.value ?: return
        view.destroy()
            .onSuccess { AppLogger.d("KeepAliveDemo, view ${view.id} destroyed") }
            .onError { AppLogger.e("KeepAliveDemo, destroy error: $it") }
    }
}

@Composable
fun FlowViewKeepAliveTiles(flow: AdaptyFlow) {
    val scope = rememberCoroutineScope()
    val keptView by FlowViewKeepAliveDemo.keptView.collectAsStateWithLifecycle()

    ListActionTile(
        title = "Present (keep on close)",
        onClick = { scope.launch { FlowViewKeepAliveDemo.createAndPresent(flow) } }
    )
    ListActionTile(
        title = "Present kept view",
        isActive = keptView != null,
        onClick = { scope.launch { FlowViewKeepAliveDemo.presentAgain() } }
    )
    ListActionTile(
        title = "Destroy kept view",
        isActive = keptView != null,
        onClick = { scope.launch { FlowViewKeepAliveDemo.destroy() } }
    )
}
