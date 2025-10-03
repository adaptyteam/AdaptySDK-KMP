package com.adapty.kmp.internal

/**
 * AdaptyKMPInternal Annotation class that limits access for internal usage
 */
@RequiresOptIn(
    message = "This is internal API for Adapty. This shouldn't be used outside of Adapty API",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
public annotation class AdaptyKMPInternal