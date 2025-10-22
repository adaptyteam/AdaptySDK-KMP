package com.adapty.kmp.models

/**
 * Represents parameters for updating or creating an Adapty profile.
 *
 * Use the [Builder] to create instances of this class. It supports common profile fields
 * such as email, phone number, name, gender, birthday, analytics settings, custom attributes,
 * and iOS ATT status.
 *
 * @property email User's email address. Nullable.
 * @property phoneNumber User's phone number. Nullable.
 * @property firstName User's first name. Nullable.
 * @property lastName User's last name. Nullable.
 * @property gender User's gender as a string. Use [AdaptyProfile.Gender] when setting it. Nullable.
 * @property birthday User's birthday in ISO-8601 string format (yyyy-MM-dd). Use [AdaptyProfile.Date] when setting it. Nullable.
 * @property analyticsDisabled Whether analytics are disabled for this profile. Nullable.
 * @property customAttributes Map of custom key-value attributes previously set by the developer. Nullable.
 * @property attStatus App Tracking Transparency status (iOS only). Nullable.
 */
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

    /** Returns a [Builder] initialized with this object's current values. */
    public fun builder(): Builder = Builder.from(this)

    /**
     * Builder for [AdaptyProfileParameters].
     *
     * Allows incremental construction of profile parameters.
     */
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

        /** Sets the email address for the profile. */
        public fun withEmail(email: String?): Builder = apply {
            this.email = email
        }

        /** Sets the phone number for the profile. */
        public fun withPhoneNumber(phoneNumber: String?): Builder = apply {
            this.phoneNumber = phoneNumber
        }

        /** Sets the first name for the profile. */
        public fun withFirstName(firstName: String?): Builder = apply {
            this.firstName = firstName
        }

        /** Sets the last name for the profile. */
        public fun withLastName(lastName: String?): Builder = apply {
            this.lastName = lastName
        }

        /** Sets the gender for the profile. Use [AdaptyProfile.Gender] enum. */
        public fun withGender(gender: AdaptyProfile.Gender?): Builder = apply {
            this.gender = gender?.toString()
        }

        /** Sets the birthday for the profile. Use [AdaptyProfile.Date] object. */
        public fun withBirthday(birthday: AdaptyProfile.Date?): Builder = apply {
            this.birthday = birthday?.toString()
        }

        /** Enables or disables analytics for this profile. */
        public fun withExternalAnalyticsDisabled(disabled: Boolean?): Builder = apply {
            this.analyticsDisabled = disabled
        }

        /** Adds or updates a custom attribute (String value). */
        public fun withCustomAttribute(key: String, value: String): Builder = apply {
            customAttributes[key] = value
        }

        /** Adds or updates a custom attribute (Double value). */
        public fun withCustomAttribute(key: String, value: Double): Builder = apply {
            customAttributes[key] = value
        }

        /** Removes a custom attribute. Sets it to `null` if it doesn't exist. */
        public fun withRemovedCustomAttribute(key: String): Builder = apply {
            if (customAttributes.containsKey(key)) customAttributes.remove(key)
            else customAttributes[key] = null
        }

        /** Sets the App Tracking Transparency (ATT) status for iOS devices. */
        public fun withAttStatus(attStatus: Int?): Builder = apply {
            this.attStatus = attStatus
        }

        /** Builds an instance of [AdaptyProfileParameters] with the current values. */
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