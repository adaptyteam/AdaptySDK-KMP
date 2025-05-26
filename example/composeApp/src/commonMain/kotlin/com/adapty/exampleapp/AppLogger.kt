package com.adapty.exampleapp

interface Logger {
    // Should be called on Application start
    fun initialize(isDebug: Boolean = true)
    fun e(message: String, throwable: Throwable? = null, tag: String? = null)
    fun d(message: String, throwable: Throwable? = null, tag: String? = null)
    fun i(message: String, throwable: Throwable? = null, tag: String? = null)
}

private class ConsoleLogger : Logger {

    private var isDebug = true

    companion object {
        private const val LOG_PREFIX = "AdaptySampleApp: "
    }

    override fun initialize(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    override fun e(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return

        println("${LOG_PREFIX}[ERROR] ${tag.orEmpty()} - $message")
        throwable?.printStackTrace()

    }

    override fun d(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return
        println("${LOG_PREFIX}[DEBUG] ${tag.orEmpty()} - $message")
        throwable?.printStackTrace()

    }

    override fun i(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return
        println("${LOG_PREFIX}[INFO] ${tag.orEmpty()} - $message")
        throwable?.printStackTrace()
    }


}

object AppLogger : Logger by ConsoleLogger()