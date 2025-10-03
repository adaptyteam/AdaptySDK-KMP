package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AdaptyConfigurationRequest(
    @SerialName("configuration") val configuration: AdaptyConfigurationRequestData
)

@Serializable
internal data class AdaptyConfigurationRequestData(
    @SerialName("api_key") val apiKey: String,
    @SerialName("customer_user_id") val customerUserId: String? = null,
    @SerialName("observer_mode") val observerMode: Boolean = false,
    @SerialName("apple_idfa_collection_disabled") val appleIdfaCollectionDisabled: Boolean = false,
    @SerialName("google_adid_collection_disabled") val googleAdidCollectionDisabled: Boolean = false,
    @SerialName("ip_address_collection_disabled") val ipAddressCollectionDisabled: Boolean = false,
    @SerialName("server_cluster") val serverCluster: String? = null,
    @SerialName("backend_base_url") val backendBaseUrl: String? = null,
    @SerialName("backend_fallback_base_url") val backendFallbackBaseUrl: String? = null,
    @SerialName("backend_configs_base_url") val backendConfigsBaseUrl: String? = null,
    @SerialName("backend_ua_base_url") val backendUaBaseUrl: String? = null,
    @SerialName("backend_proxy_host") val backendProxyHost: String? = null,
    @SerialName("backend_proxy_port") val backendProxyPort: Int? = null,
    @SerialName("log_level") val logLevel: AdaptyLogLevelRequestResponse? = null,
    @SerialName("cross_platform_sdk_name") val crossPlatformSdkName: String,
    @SerialName("cross_platform_sdk_version") val crossPlatformSdkVersion: String,
    @SerialName("activate_ui") val activateUi: Boolean = false,
    @SerialName("media_cache") val mediaCache: MediaCacheConfiguration? = null
) {


    @Serializable
    data class MediaCacheConfiguration(
        @SerialName("memory_storage_total_cost_limit")
        val memoryStorageTotalCostLimit: Int? = null,

        @SerialName("memory_storage_count_limit")
        val memoryStorageCountLimit: Int? = null,

        @SerialName("disk_storage_size_limit")
        val diskStorageSizeLimit: Int? = null
    )
}

internal fun AdaptyConfig.MediaCacheConfiguration.asAdaptyMediaCacheConfigurationRequest(): AdaptyConfigurationRequestData.MediaCacheConfiguration {
    return AdaptyConfigurationRequestData.MediaCacheConfiguration(
        memoryStorageTotalCostLimit = memoryStorageTotalCostLimit,
        memoryStorageCountLimit = memoryStorageCountLimit,
        diskStorageSizeLimit = diskStorageSizeLimit
    )
}


internal fun AdaptyConfig.asAdaptyConfigurationRequest(): AdaptyConfigurationRequest {

    val configurationData = AdaptyConfigurationRequestData(
        apiKey = apiKey,
        customerUserId = customerUserId,
        observerMode = observerMode,
        appleIdfaCollectionDisabled = appleIdfaCollectionDisabled,
        googleAdidCollectionDisabled = googleAdvertisingIdCollection,
        ipAddressCollectionDisabled = ipAddressCollectionDisabled,
        serverCluster = serverCluster,
        backendBaseUrl = backendBaseUrl,
        backendFallbackBaseUrl = backendFallbackBaseUrl,
        backendConfigsBaseUrl = backendConfigsBaseUrl,
        backendProxyHost = backendProxyHost,
        backendProxyPort = backendProxyPort,
        logLevel = logLevel?.asAdaptyLogLevelRequest(),
        crossPlatformSdkName = crossPlatformSDKName,
        crossPlatformSdkVersion = crossPlatformSDKVersion,
        activateUi = activateUI,
        mediaCache = mediaCache.asAdaptyMediaCacheConfigurationRequest()
    )
    return AdaptyConfigurationRequest(configuration = configurationData)
}