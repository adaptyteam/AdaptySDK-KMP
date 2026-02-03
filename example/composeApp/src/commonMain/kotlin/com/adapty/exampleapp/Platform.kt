package com.adapty.exampleapp

sealed interface Platform {
    data object Android : Platform
    data object Ios : Platform
    data object Desktop : Platform
    data object Web : Platform
}

expect fun getPlatform(): Platform