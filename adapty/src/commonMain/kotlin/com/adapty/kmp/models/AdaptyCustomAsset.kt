package com.adapty.kmp.models

/**
 * Represents a customizable media or color asset used within Adapty Paywalls and Onboardings.
 *
 * Use **custom assets** to override default images, videos, or colors displayed in your paywalls.
 * This allows you to personalize the user experience — for example, showing a different image
 * to specific user segments or providing a local placeholder while remote media loads.
 *
 * ### Hero Assets
 * Hero elements have predefined IDs:
 * - `"hero_image"`
 * - `"hero_video"`
 *
 * You can replace these assets directly using their IDs.
 *
 * ### Custom IDs
 * For other images and videos, set a custom ID in the **Adapty Dashboard** to target them
 * from your custom asset bundle.
 *
 * ### Example use cases
 * - Show different visuals for different audiences.
 * - Display a local fallback image while a remote image is downloading.
 * - Show a preview image before playing a video.
 *
 * @see com.adapty.kmp.AdaptyUI.createPaywallView for passing custom assets
 */
public sealed interface AdaptyCustomAsset {


    // --- Images ---
    /**
     * References a local drawable or image resource from your app bundle.
     *
     * The file should be placed under:
     * `commonMain/composeResources/files/...`
     *
     *
     * The resource path can be obtained using:
     *  ```
     *  val imagePath = Res.getUri("files/images/hero_banner.png")
     *  AdaptyCustomAsset.LocalImageResource(path = imagePath)
     *  ```
     *
     * @param path path URI of the local image resource (from `Res.getUri()`).
     */
    public data class LocalImageResource(val path: String) : AdaptyCustomAsset

    /**
     * Represents an image stored as raw byte data.
     * Useful when loading images dynamically (e.g., from memory or a cache).
     *
     * Example:
     * ```
     * val bytes = Res.readBytes("files/images/avatar.png")
     * AdaptyCustomAsset.LocalImageData(bytes)
     * ```
     *
     * @param data raw image bytes.
     */
    public class LocalImageData(public val data: ByteArray) : AdaptyCustomAsset

    /**
     * References an image file stored on the device file system.
     * Use this if your image is located outside the app bundle.
     *
     * Example:
     * ```
     * AdaptyCustomAsset.LocalImageFile("/storage/emulated/0/Download/preview.png")
     * ```
     *
     * @param path absolute path to the image file.
     */
    public data class LocalImageFile(val path: String) : AdaptyCustomAsset

    // --- Videos ---

    /**
     * References a local video resource bundled with the app.
     * The file should be placed under:
     * `commonMain/composeResources/files/...`
     *
     * The resource path can be obtained using:
     * ```
     * val videoPath = Res.getUri("files/videos/demo_video.mp4")
     * AdaptyCustomAsset.LocalVideoResource(assetId = videoPath)
     * ```
     *
     * @param assetId URI of the local video resource (from `Res.getUri()`).
     */
    public data class LocalVideoResource(val assetId: String) : AdaptyCustomAsset

    /**
     * References a video stored on the device file system.
     *
     * @param path absolute path to the video file.
     */
    public data class LocalVideoFile(val path: String) : AdaptyCustomAsset

    // --- Color ---
    /**
     * Defines a color asset using a HEX color string.
     *
     * Example:
     * ```
     * AdaptyCustomAsset.ColorAsset("#FFAA00")
     * ```
     *
     * @param colorHex HEX color code (e.g., "#RRGGBB" or "#AARRGGBB").
     */
    public data class ColorAsset(val colorHex: String) : AdaptyCustomAsset


    /**
     * Defines a linear gradient composed of multiple colors.
     *
     * Example:
     * ```
     * AdaptyCustomAsset.LinearGradientAsset(
     *     colors = listOf("#FF512F", "#DD2476"),
     *     stops = listOf(0.0f, 1.0f)
     * )
     * ```
     *
     * @param colors list of HEX color strings (e.g., "#FF0000").
     * @param stops optional list of color stops in the range 0.0–1.0.
     * @param startX gradient start X coordinate.
     * @param startY gradient start Y coordinate.
     * @param endX gradient end X coordinate.
     * @param endY gradient end Y coordinate.
     */
    public data class LinearGradientAsset(
        val colors: List<String>,         // List of hex color strings, e.g., "#FF0000"
        val stops: List<Float>? = null, // optional stops (0.0 - 1.0)
        val startX: Float = 0f,
        val startY: Float = 0f,
        val endX: Float = 1f,
        val endY: Float = 1f
    ) : AdaptyCustomAsset


    public companion object {

        /** @see LocalImageData */
        public fun localImageData(data: ByteArray): AdaptyCustomAsset =
            LocalImageData(data)

        /** @see LocalImageResource */
        public fun localImageResource(path: String): AdaptyCustomAsset =
            LocalImageResource(path)

        /** @see LocalImageFile */
        public fun localImageFile(path: String): AdaptyCustomAsset =
            LocalImageFile(path)

        /** @see LocalVideoResource */
        public fun localVideoResource(path: String): AdaptyCustomAsset =
            LocalVideoResource(path)

        /** @see LocalVideoFile */
        public fun localVideoFile(path: String): AdaptyCustomAsset =
            LocalVideoFile(path)

        /** @see ColorAsset */
        public fun color(colorHex: String): AdaptyCustomAsset =
            ColorAsset(colorHex)

        /** @see LinearGradientAsset */
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
