package com.adapty.kmp.utils

import android.net.Uri
import com.adapty.utils.FileLocation as AdaptyFileLocationAndroid

internal fun FileLocation.asAdaptyFileLocationAndroid(): AdaptyFileLocationAndroid {
    return AdaptyFileLocationAndroid.fromFileUri(Uri.EMPTY) //TODO fix this
}