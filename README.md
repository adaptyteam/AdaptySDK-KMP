# AdaptySDK-KMP
Kotlin Multiplatform SDK of Adapty.

## Before running Demo App!

- check your system with [KDoctor](https://github.com/Kotlin/kdoctor)
- install JDK 17 or higher on your machine
- add `local.properties` file to the project root and set a path to Android SDK there
- Add Adapty API key into `local.properties`
```agsl
ADAPTY_API_KEY = YOUR_API_KEY
```

### Android

To run the example application on android device/emulator:

- open project in Android Studio and run imported android run configuration

To build the application bundle:

- run `./gradlew :example:composeApp:assembleDebug`
- find `.apk` file in `exmaple/composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### iOS

To run the example application on iPhone device/simulator:

- Open `example/iosApp/iosApp.xcproject` in Xcode and run standard configuration
- Or
  use [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
  for Android Studio
