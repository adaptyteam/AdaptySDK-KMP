package com.adapty.kmp.models

public sealed interface AdaptyCustomAsset {


    // --- Images ---
    public data class LocalImageResource(val path: String) : AdaptyCustomAsset

    public class LocalImageData(public val data: ByteArray) : AdaptyCustomAsset

    public data class LocalImageFile(val path: String) : AdaptyCustomAsset

    // --- Videos ---
    public data class LocalVideoResource(val assetId: String) : AdaptyCustomAsset

    public data class LocalVideoFile(val path: String) : AdaptyCustomAsset

    // --- Color ---
    public data class ColorAsset(val colorHex: String) : AdaptyCustomAsset


    public data class LinearGradientAsset(
        val colors: List<String>,         // List of hex color strings, e.g., "#FF0000"
        val stops: List<Float>? = null, // optional stops (0.0 - 1.0)
        val startX: Float = 0f,
        val startY: Float = 0f,
        val endX: Float = 1f,
        val endY: Float = 1f
    ) : AdaptyCustomAsset


    public companion object {
        public fun localImageData(data: ByteArray): AdaptyCustomAsset =
            LocalImageData(data)

        public fun localImageResource(path: String): AdaptyCustomAsset =
            LocalImageResource(path)

        public fun localImageFile(path: String): AdaptyCustomAsset =
            LocalImageFile(path)

        public fun localVideoResource(path: String): AdaptyCustomAsset =
            LocalVideoResource(path)

        public fun localVideoFile(path: String): AdaptyCustomAsset =
            LocalVideoFile(path)

        public fun color(colorHex: String): AdaptyCustomAsset =
            ColorAsset(colorHex)

        public fun linearGradient(
            colors: List<String>,
            stops: List<Float>? = null,
            startX: Float = 0f,
            startY: Float = 0f,
            endX: Float = 1f,
            endY: Float = 1f
        ): AdaptyCustomAsset =
            LinearGradientAsset(colors, stops, startX, startY, endX, endY)
    }
}
