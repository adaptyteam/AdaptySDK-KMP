package com.adapty.kmp.models

public sealed interface AdaptyUIAction {
    public data object CloseAction : AdaptyUIAction
    public data object AndroidSystemBackAction : AdaptyUIAction
    public data class OpenUrlAction(val url: String) : AdaptyUIAction
    public data class CustomAction(val action: String) : AdaptyUIAction
}
