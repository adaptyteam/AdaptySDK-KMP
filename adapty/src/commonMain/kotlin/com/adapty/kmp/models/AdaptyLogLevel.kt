package com.adapty.kmp.models

/**
 * Defines the verbosity level for Adapty SDK logging output.
 *
 * Use this to control how much diagnostic information the SDK logs
 * during its operation.
 *
 * You can configure the log level in [AdaptyConfig.Builder.withLogLevel]
 * or at runtime using [com.adapty.kmp.Adapty.setLogLevel].
 *
 */
public enum class AdaptyLogLevel {

    /** Logs only critical errors that cause SDK failures. */
    ERROR,

    /** Logs warnings that indicate potential issues but not fatal errors. */
    WARN,

    /** Logs general informational messages useful for app behavior tracking. */
    INFO,

    /** Logs detailed SDK operations, useful for in-depth debugging. */
    VERBOSE,

    /** Logs the most detailed information, including internal debug data. */
    DEBUG,
}