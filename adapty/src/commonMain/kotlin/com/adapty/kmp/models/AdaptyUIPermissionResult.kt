package com.adapty.kmp.models

/**
 * The outcome of an OS permission request, returned to the flow.
 *
 * Build one with [granted] or [denied]:
 *
 * ```
 * override suspend fun handlePermission(
 *     view: AdaptyUIFlowView,
 *     permission: AdaptyUIPermission,
 *     customArgs: Map<String, String>?
 * ): AdaptyUIPermissionResult =
 *     if (askUserFor(permission)) AdaptyUIPermissionResult.granted()
 *     else AdaptyUIPermissionResult.denied("user declined")
 * ```
 *
 * @property isGranted whether the permission was granted.
 * @property detail optional human-readable detail (e.g. the OS status string).
 */
public class AdaptyUIPermissionResult private constructor(
    public val isGranted: Boolean,
    public val detail: String? = null,
) {
    public companion object {
        /** The user granted the permission. */
        public fun granted(detail: String? = null): AdaptyUIPermissionResult =
            AdaptyUIPermissionResult(isGranted = true, detail = detail)

        /** The user denied the permission, or it could not be requested. */
        public fun denied(detail: String? = null): AdaptyUIPermissionResult =
            AdaptyUIPermissionResult(isGranted = false, detail = detail)
    }

    override fun toString(): String =
        "AdaptyUIPermissionResult(isGranted=$isGranted, detail=$detail)"
}
