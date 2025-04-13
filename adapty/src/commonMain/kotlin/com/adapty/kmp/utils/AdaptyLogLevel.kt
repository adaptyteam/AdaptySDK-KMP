package com.adapty.kmp.utils

public class AdaptyLogLevel private constructor(
    private val name: String,
    internal val value: Int
) {

    public companion object {

        public val NONE: AdaptyLogLevel = AdaptyLogLevel("NONE", 0b0)

        public val ERROR: AdaptyLogLevel = AdaptyLogLevel("ERROR", 0b1)

        public val WARN: AdaptyLogLevel = AdaptyLogLevel("WARN", 0b11)

        public val INFO: AdaptyLogLevel = AdaptyLogLevel("INFO", 0b111)

        public val VERBOSE: AdaptyLogLevel = AdaptyLogLevel("VERBOSE", 0b1111)
    }

    override fun toString(): String {
        return name
    }
}