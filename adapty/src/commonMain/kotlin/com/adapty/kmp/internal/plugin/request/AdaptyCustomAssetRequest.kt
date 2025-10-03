package com.adapty.kmp.internal.plugin.request

import com.adapty.kmp.models.AdaptyCustomAsset
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
internal sealed interface AdaptyCustomAssetRequest {

    // --- Colors ---
    @Serializable
    @SerialName("color")
    data class ColorRequest(
        @SerialName("id") val id: String,
        @SerialName("value") val colorHex: String
    ) : AdaptyCustomAssetRequest

    // --- Linear Gradients ---
    @Serializable
    @SerialName("linear-gradient")
    data class LinearGradientRequest(
        @SerialName("id") val id: String,
        @SerialName("values") val values: List<ColorStop>,
        @SerialName("points") val points: GradientPoints
    ) : AdaptyCustomAssetRequest {
        @Serializable
        data class ColorStop(
            @SerialName("color") val color: String,
            @SerialName("p") val p: Float
        )

        @Serializable
        data class GradientPoints(
            @SerialName("x0") val x0: Float,
            @SerialName("y0") val y0: Float,
            @SerialName("x1") val x1: Float,
            @SerialName("y1") val y1: Float
        )
    }

    // --- Images ---
    @Serializable
    @SerialName("image")
    data class LocalImageAssetRequest(
        @SerialName("id") val id: String,
        @SerialName("asset_id") val assetId: String? = null,
        @SerialName("value") val base64Value: String? = null,
        @SerialName("path") val path: String? = null
    ) : AdaptyCustomAssetRequest

    // --- Videos ---
    @Serializable
    @SerialName("video")
    data class LocalVideoRequest(
        @SerialName("id") val id: String,
        @SerialName("asset_id") val assetId: String? = null,
        @SerialName("path") val path: String? = null
    ) : AdaptyCustomAssetRequest
}

@OptIn(ExperimentalEncodingApi::class)
internal fun AdaptyCustomAsset.asAdaptyCustomAssetRequest(id: String): AdaptyCustomAssetRequest {
    return when (this) {
        is AdaptyCustomAsset.ColorAsset -> AdaptyCustomAssetRequest.ColorRequest(
            id = id,
            colorHex = this.colorHex
        )

        is AdaptyCustomAsset.LinearGradientAsset -> AdaptyCustomAssetRequest.LinearGradientRequest(
            id = id,
            values = this.colors.mapIndexed { index, color ->
                val p = this.stops?.get(index)
                    ?: if (this.colors.size == 1) 0f else (index.toFloat() / (this.colors.size - 1))
                AdaptyCustomAssetRequest.LinearGradientRequest.ColorStop(color = color, p = p)
            },
            points = AdaptyCustomAssetRequest.LinearGradientRequest.GradientPoints(
                x0 = this.startX,
                y0 = this.startY,
                x1 = this.endX,
                y1 = this.endY
            )
        )

        is AdaptyCustomAsset.LocalImageResource -> AdaptyCustomAssetRequest.LocalImageAssetRequest(
            id = id,
            assetId = this.path
        )

        is AdaptyCustomAsset.LocalImageData -> {
            AdaptyCustomAssetRequest.LocalImageAssetRequest(
                id = id,
                base64Value = Base64.encode(this.data)
            )
        }

        is AdaptyCustomAsset.LocalImageFile -> AdaptyCustomAssetRequest.LocalImageAssetRequest(
            id = id,
            path = this.path
        )

        is AdaptyCustomAsset.LocalVideoResource -> AdaptyCustomAssetRequest.LocalVideoRequest(
            id = id,
            assetId = this.assetId
        )

        is AdaptyCustomAsset.LocalVideoFile -> AdaptyCustomAssetRequest.LocalVideoRequest(
            id = id,
            path = this.path
        )
    }
}

