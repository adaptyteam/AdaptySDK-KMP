package com.adapty.kmp.models

import com.adapty.models.AdaptyProfileParameters as AdaptyProfileParametersAndroid

internal fun AdaptyProfileParameters.asAdaptyProfileParametersAndroid(): AdaptyProfileParametersAndroid {

    val builder = AdaptyProfileParametersAndroid.Builder()
        .withEmail(email)
        .withPhoneNumber(phoneNumber)
        .withFirstName(firstName)
        .withLastName(lastName)
        .withGender(AdaptyProfile.Gender.from(gender)?.asAdaptyGenderAndroid())
        .withBirthday(AdaptyProfile.Date.from(birthday)?.asAdaptyDateAndroid())
        .withExternalAnalyticsDisabled(analyticsDisabled)

    customAttributes?.forEach { (key, value) ->
        when (value) {
            is String -> builder.withCustomAttribute(key, value)
            is Double -> builder.withCustomAttribute(key, value)
            null -> builder.withRemovedCustomAttribute(key)
        }
    }

    return builder.build()
}