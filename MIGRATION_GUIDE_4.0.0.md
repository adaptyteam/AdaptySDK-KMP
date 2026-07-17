# Migration Guide: KMPAdapty 4.0.0 (Paywall → Flow)

Version 4.0.0 adopts the cross-platform contract 4.0.0, which reorganizes the SDK around **flows** instead of paywalls, and bumps the underlying native SDKs to 4.0.0.

**This release is not backward compatible.** The paywall API is **deleted**, not deprecated — the same break the React Native and Flutter v4 SDKs made. Every paywall call site has a direct flow equivalent; this guide maps them one to one.

---

## TL;DR

- `getPaywall` → `getFlow`, `AdaptyPaywall` → `AdaptyFlow`, `AdaptyUIPaywallView` → `AdaptyUIFlowView` — mechanical renames for most code.
- **Flow callbacks are now split across three interfaces**, by whether Adapty is waiting on an answer from you. This is the one change that isn't a rename.
- Onboarding APIs are unchanged and still work, but are now `@Deprecated`.
- iOS deployment target stays **15.0** — unchanged by this release.

---

## What is a flow?

A **flow** groups one or more paywall variations together with flow-level metadata and per-language remote configs. Where `getPaywall()` returned a single paywall, `getFlow()` returns an `AdaptyFlow` whose `paywalls` are `AdaptyFlowPaywall`s.

```kotlin
val flow = (Adapty.getFlow("placement_id") as? AdaptyResult.Success)?.value ?: return
val paywall: AdaptyFlowPaywall? = flow.paywalls.firstOrNull()
```

---

## Installation

4.0.0 is a pre-release, so pin the exact version — Gradle does not resolve pre-release versions through dynamic ranges (`+`, `latest.release`):

```toml title="libs.versions.toml"
[versions]
adapty-kmp = "4.0.0-beta.1"

[libraries]
adapty-kmp = { module = "io.adapty:adapty-kmp", version.ref = "adapty-kmp" }
adapty-kmp-ui = { module = "io.adapty:adapty-kmp-ui", version.ref = "adapty-kmp" }
```

---

## Core API (`Adapty`)

| Removed | Replacement |
|---|---|
| `getPaywall(placementId, locale, fetchPolicy, loadTimeout)` | `getFlow(placementId, fetchPolicy, loadTimeout)` |
| `getPaywallForDefaultAudience(placementId, locale, fetchPolicy)` | `getFlowForDefaultAudience(placementId, fetchPolicy)` |
| `getPaywallProducts(paywall)` | `getPaywallProducts(flow)` |
| `logShowPaywall(paywall)` | `logShowFlow(flow)` |
| `createWebPaywallUrl(paywall)` | `createWebPaywallUrl(flowPaywall)` or `createWebPaywallUrl(product)` |
| `openWebPaywall(paywall, openIn)` | `openWebPaywall(flowPaywall, openIn)` or `openWebPaywall(product, openIn)` |

**`locale` is gone** from flow fetching — the wire no longer carries it, and the locale is resolved at render time. Delete the argument.

`getPaywallProducts`, `createWebPaywallUrl` and `openWebPaywall` keep their paywall names on purpose: those are the v4 wire names, shared across all platform SDKs.

```kotlin
// Before
val paywall = (Adapty.getPaywall("placement_id", locale = "en") as? AdaptyResult.Success)?.value ?: return
val products = Adapty.getPaywallProducts(paywall)
Adapty.logShowPaywall(paywall)

// After
val flow = (Adapty.getFlow("placement_id") as? AdaptyResult.Success)?.value ?: return
val products = Adapty.getPaywallProducts(flow)
Adapty.logShowFlow(flow)
```

---

## UI API (`AdaptyUI`)

| Removed | Replacement |
|---|---|
| `createPaywallView(paywall, …)` → `AdaptyUIPaywallView` | `createFlowView(flow, …)` → `AdaptyUIFlowView` |
| `presentPaywallView` / `dismissPaywallView` | `presentFlowView` / `dismissFlowView` |
| `setPaywallsEventsObserver` | `setFlowsEventsObserver` |
| `register/unregisterPaywallEventsListener` | `register/unregisterFlowEventsListener` |
| `AdaptyUI.createNativePaywallView(…)` → `AdaptyNativePaywallView` | `AdaptyUI.createNativeFlowView(…)` → `AdaptyNativeFlowView` |
| `AdaptyUIPaywallPlatformView(paywall, …)` | `AdaptyUIFlowPlatformView(flow, …)` |
| — | `AdaptyUI.requestAppReview()`, `AdaptyUI.openWebUrl(url, openIn)` *(new)* |

---

## Events vs requests: the three interfaces

This is the change that needs real thought, not a rename. In 3.x every flow callback lived on one observer. In 4.0.0 they are split by whether Adapty is **waiting on an answer**:

| Interface | Register with | Holds | If you don't register it |
|---|---|---|---|
| `AdaptyUIFlowsEventsObserver` | `setFlowsEventsObserver` | Things that already happened: appear, purchase finished, error, analytics… | Views still work — a default observer dismisses on close/error and opens URLs |
| `AdaptyUISystemRequestsHandler` | `setSystemRequestsHandler` | `handlePermission`, `handleAppReviewRequest` | Permission requests get **no answer**, and the flow resolves them as denied at teardown. App review still triggers the native prompt |
| `AdaptyUIObserverModeResolver` | `setObserverModeResolver` | `observerModeDidInitiatePurchase`, `observerModeDidInitiateRestore` | **Nothing happens when the user taps buy** — observer mode needs this |

### Renamed observer callbacks

`AdaptyUIPaywallsEventsObserver` → `AdaptyUIFlowsEventsObserver`. Callbacks go `paywallView*` → `flowView*` and take `AdaptyUIFlowView`. One is renamed further:

- `paywallViewDidFailRendering` → **`flowViewDidReceiveError`** (widened to any flow error, not just rendering)

Plus a new event: `flowViewDidReceiveAnalyticEvent(view, name, paramsJsonString)`.

### Moved off the observer

If you implemented these on your observer, or passed them to the platform view, they now live elsewhere:

```kotlin
// Permissions + app review
AdaptyUI.setSystemRequestsHandler(object : AdaptyUISystemRequestsHandler {
    override suspend fun handlePermission(
        view: AdaptyUIFlowView,
        permission: AdaptyUIPermission,
        customArgs: Map<String, String>?,
    ): AdaptyUIPermissionResult = when (permission) {
        AdaptyUIPermission.PUSH ->
            if (requestNotifications()) AdaptyUIPermissionResult.granted()
            else AdaptyUIPermissionResult.denied("user declined")
        // Unknown / platform-specific / future ids arrive verbatim
        else -> AdaptyUIPermissionResult.denied("unsupported: ${permission.value}")
    }

    // Optional — defaults to the native review prompt
    override suspend fun handleAppReviewRequest(view: AdaptyUIFlowView) {
        myOwnReviewPrompt()
    }
})

// Observer mode
AdaptyUI.setObserverModeResolver(object : AdaptyUIObserverModeResolver {
    override fun observerModeDidInitiatePurchase(
        view: AdaptyUIFlowView,
        product: AdaptyPaywallProduct,
        onStartPurchase: () -> Unit,
        onFinishPurchase: () -> Unit,
    ) {
        onStartPurchase()
        myPurchaseFlow(product) { onFinishPurchase() }
    }

    override fun observerModeDidInitiateRestore(
        view: AdaptyUIFlowView,
        onStartRestore: () -> Unit,
        onFinishRestore: () -> Unit,
    ) {
        onStartRestore()
        myRestoreFlow { onFinishRestore() }
    }
})
```

`handlePermission` and both resolver methods are **abstract on purpose**: if you opt in, you must answer. `onStartPurchase` / `onFinishPurchase` only drive the flow view's loading indicator — report the transaction to Adapty yourself.

---

## Default behavior changes

The flow observer's defaults differ from the old paywall observer's:

| Event | 3.x paywall observer | 4.0.0 flow observer |
|---|---|---|
| Close button | dismisses | dismisses |
| Android system back | dismisses | **keeps open** — override and dismiss to restore |
| Purchase completed | dismisses (unless cancelled) | **does not auto-dismiss** |
| Error | (none) | **dismisses** |
| URL tapped | opened by the SDK | opened natively |

To keep the old close-on-purchase behavior, dismiss the view yourself:

```kotlin
override fun flowViewDidFinishPurchase(
    view: AdaptyUIFlowView,
    product: AdaptyPaywallProduct,
    purchaseResult: AdaptyPurchaseResult
) {
    if (purchaseResult !is AdaptyPurchaseResult.UserCanceled) {
        mainUiScope.launch { view.dismiss() }
    }
}
```

Per-view observers (registered via `registerFlowEventsListener`, or created for you by `AdaptyUIFlowPlatformView`) run **in addition to** the global observer, not instead of it. Your callback observes an event; it does not replace the global default.

---

## Models

- **`AdaptyFlow.paywalls`** — the list of `AdaptyFlowPaywall` variations. (Named `paywalls`, matching RN; the wire field stays `variations`.)
- **`AdaptyUIPermission`** is a class wrapping the raw id, with the 21 known ids as constants — not an enum. Unknown, platform-specific (Android `phone` / `sms`) and future ids arrive **verbatim** via `permission.value` instead of collapsing to `UNKNOWN`. Compare against `AdaptyUIPermission.PUSH` and friends, and use an `else` branch for the rest.
- **`AdaptyUIPermissionResult.granted(detail)` / `.denied(detail)`** — what you return from `handlePermission`.
- **`AdaptyConfig.ServerCluster.CN`** — new, alongside `DEFAULT` and `EU`.

---

## Compose Multiplatform

```kotlin
// Before
val paywall = (Adapty.getPaywall("placement_id") as? AdaptyResult.Success)?.value ?: return
AdaptyUIPaywallPlatformView(paywall = paywall, onDidFinishPurchase = { _, _, _ -> })

// After
val flow = (Adapty.getFlow("placement_id") as? AdaptyResult.Success)?.value ?: return
AdaptyUIFlowPlatformView(flow = flow, onDidFinishPurchase = { _, _, _ -> })
```

`AdaptyUIFlowPlatformView` takes the 14 event callbacks only. The `onDidAskPermission`, `onDidRequestAppReview`, `onObserverDidInitiatePurchase` and `onObserverDidInitiateRestore` params are gone — register the handler and resolver above instead.

---

## Native embedded views

```kotlin
// Android
val nativeView = AdaptyUI.createNativeFlowView(
    context = context,
    viewModelStoreOwner = activity,
    flow = flow,
    observer = myFlowObserver,
)
// nativeView.view → AdaptyFlowView; call nativeView.dispose() when done

// iOS
val nativeView = AdaptyUI.createNativeFlowView(flow = flow, observer = myFlowObserver)
// nativeView.viewController → UIViewController; call nativeView.dispose() when done
```

---

## Deprecated onboarding

The whole onboarding surface is deprecated: the methods (`getOnboarding`, `getOnboardingForDefaultAudience`, `createOnboardingView`, `presentOnboardingView`, `dismissOnboardingView`, `createNativeOnboardingView`, `setOnboardingsEventsObserver`, `register`/`unregisterOnboardingEventsListener`), the observer (`AdaptyUIOnboardingsEventsObserver`), the composable (`AdaptyUIOnboardingPlatformView`), and the models (`AdaptyOnboarding`, `AdaptyUIOnboardingView`, `AdaptyUIOnboardingMeta`, `AdaptyOnboardingsAnalyticsEvent`, `AdaptyOnboardingsInput`, `AdaptyOnboardingsStateUpdatedParams`, `AdaptyNativeOnboardingView`). They are unchanged and fully supported this release, but marked `@Deprecated` — a future major migrates onboardings into the Flow Builder. Suppress the warnings for now; no code change is needed.

---

## Native SDK / build requirements

- Android: native Adapty BOM 4.0.0 (auto-resolved).
- iOS: native Adapty iOS SDK 4.0.1 via SPM; deployment target 15.0 (unchanged).
