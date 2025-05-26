package com.adapty.kmp.models

public class AdaptyProfileParameters private constructor(
    public val email: String?,
    public val phoneNumber: String?,
    public val firstName: String?,
    public val lastName: String?,
    public val gender: String?,
    public val birthday: String?,
    public val analyticsDisabled: Boolean?,
    public val customAttributes: Map<String, Any?>?,
    public val attStatus: Int? // iOS only
) {

    public fun builder(): Builder = Builder.from(this)

    public class Builder private constructor(
        private var email: String? = null,
        private var phoneNumber: String? = null,
        private var firstName: String? = null,
        private var lastName: String? = null,
        private var gender: String? = null,
        private var birthday: String? = null,
        private var analyticsDisabled: Boolean? = null,
        private val customAttributes: MutableMap<String, Any?> = hashMapOf(),
        private var attStatus: Int? = null
    ) {

        public constructor() : this(null)

        public fun withEmail(email: String?): Builder = apply {
            this.email = email
        }

        public fun withPhoneNumber(phoneNumber: String?): Builder = apply {
            this.phoneNumber = phoneNumber
        }

        public fun withFirstName(firstName: String?): Builder = apply {
            this.firstName = firstName
        }

        public fun withLastName(lastName: String?): Builder = apply {
            this.lastName = lastName
        }

        public fun withGender(gender: AdaptyProfile.Gender?): Builder = apply {
            this.gender = gender?.toString()
        }

        public fun withBirthday(birthday: AdaptyProfile.Date?): Builder = apply {
            this.birthday = birthday?.toString()
        }

        public fun withExternalAnalyticsDisabled(disabled: Boolean?): Builder = apply {
            this.analyticsDisabled = disabled
        }

        public fun withCustomAttribute(key: String, value: String): Builder = apply {
            customAttributes[key] = value
        }

        public fun withCustomAttribute(key: String, value: Double): Builder = apply {
            customAttributes[key] = value
        }

        public fun withRemovedCustomAttribute(key: String): Builder = apply {
            if (customAttributes.containsKey(key)) customAttributes.remove(key)
            else customAttributes[key] = null
        }

        public fun withAttStatus(attStatus: Int?): Builder = apply {
            this.attStatus = attStatus
        }

        public fun build(): AdaptyProfileParameters {
            return AdaptyProfileParameters(
                email = this.email,
                phoneNumber = this.phoneNumber,
                firstName = this.firstName,
                lastName = this.lastName,
                gender = this.gender,
                birthday = this.birthday,
                analyticsDisabled = this.analyticsDisabled,
                customAttributes = this.customAttributes.takeIf { attrs -> attrs.isNotEmpty() },
                attStatus = this.attStatus
            )
        }

        internal companion object {
            fun from(params: AdaptyProfileParameters) = Builder(
                email = params.email,
                phoneNumber = params.phoneNumber,
                firstName = params.firstName,
                lastName = params.lastName,
                gender = params.gender,
                birthday = params.birthday,
                analyticsDisabled = params.analyticsDisabled,
                customAttributes = params.customAttributes?.toMutableMap() ?: mutableMapOf()
            )
        }
    }
}