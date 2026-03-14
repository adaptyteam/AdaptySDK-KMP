package com.adapty.exampleapp

interface Logger {
    // Should be called on Application start
    fun initialize(isDebug: Boolean = true)
    fun e(message: String, throwable: Throwable? = null, tag: String? = null)
    fun d(message: String, throwable: Throwable? = null, tag: String? = null)
    fun i(message: String, throwable: Throwable? = null, tag: String? = null)

    val logs: List<String>
}

private class ConsoleLogger : Logger {

    private var isDebug = true
    private val internalLogs = ArrayDeque<String>()


    companion object {
        private const val LOG_PREFIX = "AdaptySampleApp: "
    }

    override fun initialize(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    override fun e(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return

        val log = "${LOG_PREFIX}[ERROR] ${tag.orEmpty()} - $message"
        internalLogs.addFirst(log)
        println(log)
        throwable?.printStackTrace()
    }

    override fun d(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return
        val log = "${LOG_PREFIX}[DEBUG] ${tag.orEmpty()} - $message"
        internalLogs.addFirst(log)
        println(log)
        throwable?.printStackTrace()

    }

    override fun i(message: String, throwable: Throwable?, tag: String?) {
        if (isDebug.not()) return
        val log = "${LOG_PREFIX}[INFO] ${tag.orEmpty()} - $message"
        internalLogs.addFirst(log)
        println(log)
        throwable?.printStackTrace()
    }

    override val logs: List<String>
        get() = internalLogs


}

object AppLogger : Logger by ConsoleLogger()