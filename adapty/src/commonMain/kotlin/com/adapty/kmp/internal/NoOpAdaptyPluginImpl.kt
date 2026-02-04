package com.adapty.kmp.internal

import com.adapty.kmp.internal.plugin.AdaptyPlugin

@AdaptyKMPInternal
public class NoOpAdaptyPluginImpl : AdaptyPlugin {
    override fun initialize() {
        logger.log("NoOpAdaptyPluginImpl.initialize()")
    }

    override fun executePlatformSpecific(
        method: String,
        data: String,
        onResult: (String?) -> Unit
    ) {
        logger.log("NoOpAdaptyPluginImpl.executePlatformSpecific()")
        onResult(null)
    }
}