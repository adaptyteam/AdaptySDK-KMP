package com.adapty.exampleapp

sealed interface Platform {
    data object Android : Platform
    data object Ios : Platform
}

expect fun getPlatform(): Platform