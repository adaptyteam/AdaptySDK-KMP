package com.adapty.kmp.internal.plugin

import com.adapty.kmp.internal.logger
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.response.AdaptyProfileUpdatedResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.models.AdaptyProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal object AdaptyPluginEventHandler {

    private val appMainScope = MainScope()
    private val ioDispatcher = Dispatchers.IO

    private val profileChannelFlow = Channel<AdaptyProfile>(Channel.BUFFERED)
    val profileFlow = profileChannelFlow.receiveAsFlow()

    private val viewEventChannelFlow = Channel<Pair<AdaptyPluginEvent, String>>(Channel.BUFFERED)
    val viewEventFlow = viewEventChannelFlow.receiveAsFlow()

    fun onNewEvent(eventName: String?, eventDataJsonString: String) = appMainScope.launch {
        try {
            logger.log("CrossPlatformHelper, onNewEvent: $eventName, eventData: $eventDataJsonString")
            val event = AdaptyPluginEvent.entries.find { it.eventName == eventName }
            when (event) {
                null -> logger.log("Event with name $eventName is not found in AdaptyPluginEvent enums")
                AdaptyPluginEvent.DID_LOAD_LATEST_PROFILE -> onLoadLatestProfile(eventDataJsonString)
                else -> viewEventChannelFlow.send(Pair(event, eventDataJsonString))
            }

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            logger.log("AdaptyPluginEventHandler, onNewEvent, error: $e")
        }
    }

    private suspend fun onLoadLatestProfile(eventDataJsonString: String) =
        withContext(ioDispatcher) {
            val profile =
                eventDataJsonString.decodeJsonString<AdaptyProfileUpdatedResponse>()?.profile?.asAdaptyProfile()
            profile?.let {
                profileChannelFlow.send(profile)
            }
        }

}