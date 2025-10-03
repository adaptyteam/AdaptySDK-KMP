package com.adapty.kmp.internal.plugin

import com.adapty.kmp.internal.AdaptyKMPInternal
import com.adapty.kmp.internal.logger
import com.adapty.kmp.internal.plugin.constants.AdaptyPluginEvent
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsFailEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyOnInstallationDetailsSuccessEventResponse
import com.adapty.kmp.internal.plugin.response.AdaptyProfileUpdatedResponse
import com.adapty.kmp.internal.plugin.response.asAdaptyError
import com.adapty.kmp.internal.plugin.response.asAdaptyInstallationDetails
import com.adapty.kmp.internal.plugin.response.asAdaptyProfile
import com.adapty.kmp.internal.utils.decodeJsonString
import com.adapty.kmp.models.AdaptyInstallationDetails
import com.adapty.kmp.models.AdaptyProfile
import com.adapty.kmp.models.AdaptyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


@AdaptyKMPInternal
public object AdaptyPluginEventHandler {

    private val appMainScope = MainScope()
    private val defaultDispatcher = Dispatchers.Default

    private val profileChannelFlow = Channel<AdaptyProfile>(Channel.BUFFERED)
    internal val profileFlow = profileChannelFlow.receiveAsFlow()

    private val _installationEventResultFlow =
        Channel<AdaptyResult<AdaptyInstallationDetails>>(Channel.BUFFERED)
    internal val installationEventResultFlow = _installationEventResultFlow.receiveAsFlow()

    private val viewEventChannelFlow = Channel<Pair<AdaptyPluginEvent, String>>(Channel.BUFFERED)
    internal val viewEventFlow = viewEventChannelFlow.receiveAsFlow()

    public fun onNewEvent(eventName: String?, eventDataJsonString: String) {
        appMainScope.launch {
            try {
                logger.log("CrossPlatformHelper, onNewEvent: $eventName, eventData: $eventDataJsonString")
                val event = AdaptyPluginEvent.entries.find { it.eventName == eventName }
                when (event) {
                    null -> logger.log("Event with name $eventName is not found in AdaptyPluginEvent enums")
                    AdaptyPluginEvent.DID_LOAD_LATEST_PROFILE -> onLoadLatestProfile(
                        eventDataJsonString
                    )

                    AdaptyPluginEvent.ON_INSTALLATION_DETAILS_FAIL -> onInstallationFailEvent(
                        eventDataJsonString
                    )

                    AdaptyPluginEvent.ON_INSTALLATION_DETAILS_SUCCESS -> onInstallationSuccessEvent(
                        eventDataJsonString
                    )

                    else -> viewEventChannelFlow.send(Pair(event, eventDataJsonString))
                }

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                logger.log("AdaptyPluginEventHandler, onNewEvent, error: $e")
            }
        }
    }

    private suspend fun onLoadLatestProfile(eventDataJsonString: String) =
        withContext(defaultDispatcher) {
            val profile =
                eventDataJsonString.decodeJsonString<AdaptyProfileUpdatedResponse>()?.profile?.asAdaptyProfile()
            profile?.let {
                profileChannelFlow.send(profile)
            }
        }

    private suspend fun onInstallationFailEvent(eventDataJsonString: String) =
        withContext(defaultDispatcher) {
            val error =
                eventDataJsonString.decodeJsonString<AdaptyOnInstallationDetailsFailEventResponse>()?.error?.asAdaptyError()
            error?.let {
                _installationEventResultFlow.send(AdaptyResult.Error(it))
            }
        }

    private suspend fun onInstallationSuccessEvent(eventDataJsonString: String) =
        withContext(defaultDispatcher) {
            val details =
                eventDataJsonString.decodeJsonString<AdaptyOnInstallationDetailsSuccessEventResponse>()?.details?.asAdaptyInstallationDetails()
            details?.let {
                _installationEventResultFlow.send(AdaptyResult.Success(it))
            }
        }

}