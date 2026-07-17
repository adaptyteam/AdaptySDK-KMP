# Migration Guide: KMPAdapty 4.0.0 (Paywall → Flow)

4.0.0 reorganizes the SDK around **flows** instead of paywalls.

**The paywall API is deleted, not deprecated** — your code won't compile until you port it. The substitutions are mechanical, and this guide lists them all.

---

## TL;DR

- `getPaywall` → `getFlow`, `AdaptyPaywall` → `AdaptyFlow`, `AdaptyUIPaywallView` → `AdaptyUIFlowView`, `paywallView*` callbacks → `flowView*`.
- Some defaults changed — see [Default behavior changes](#default-behavior-changes).
- Onboarding APIs still work, but are now `@Deprecated`.

---

## What is a flow?

A flow groups one or more paywall variations with flow-level metadata and per-language remote configs. Where `getPaywall()` returned one paywall, `getFlow()` returns an `AdaptyFlow` whose `paywalls` are `AdaptyFlowPaywall`s.

```kotlin
val flow = (Adapty.getFlow("placement_id") as? AdaptyResult.Success)?.value ?: return
val paywall: AdaptyFlowPaywall? = flow.paywalls.firstOrNull()
```

---

## Installation

4.0.0 is a pre-release, so pin the exact version — Gradle does not resolve pre-releases through dynamic ranges (`+`, `latest.release`):

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

**`locale` is gone** from flow fetching — it's resolved at render time. Delete the argument.

`getPaywallProducts`, `createWebPaywallUrl` and `openWebPaywall` keep their paywall names: those are the v4 wire names.

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

Purchases, profile, and fallbacks (`makePurchase`, `restorePurchases`, `getProfile`, `identify`, `updateProfile`, `setFallback`) are unchanged.

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

## Porting your observer

`AdaptyUIPaywallsEventsObserver` is deleted; implement `AdaptyUIFlowsEventsObserver` instead. Each callback has a direct equivalent: `paywallView*` becomes `flowView*` and takes `AdaptyUIFlowView`. One is named differently:

- `paywallViewDidFailRendering` → **`flowViewDidReceiveError`** (widened to any flow error, not just rendering)

One event is new: `flowViewDidReceiveAnalyticEvent(view, name, paramsJsonString)`. It defaults to a no-op.

Same for `AdaptyUIFlowPlatformView`: your `onDid…` params carry over, `onDidFailRendering` becomes `onDidReceiveError`, and `onDidReceiveAnalyticEvent` is added.

```kotlin
// Before
val paywall = (Adapty.getPaywall("placement_id") as? AdaptyResult.Success)?.value ?: return
AdaptyUIPaywallPlatformView(paywall = paywall, onDidFinishPurchase = { _, _, _ -> })

// After
val flow = (Adapty.getFlow("placement_id") as? AdaptyResult.Success)?.value ?: return
AdaptyUIFlowPlatformView(flow = flow, onDidFinishPurchase = { _, _, _ -> })
```

---

## Default behavior changes

These don't cause compile errors, so check them at runtime:

| Event | v3 | 4.0.0 |
|---|---|---|
| Close button | dismisses | dismisses |
| Android system back | dismisses | **keeps open** — dismiss yourself in `flowViewDidPerformAction` to restore |
| Purchase completed | dismisses (unless cancelled) | **does not auto-dismiss** |
| Error | (none) | **dismisses** |
| URL tapped | opened by the SDK | opened natively |

To keep the old close-on-purchase behavior:

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

Per-view observers (from `registerFlowEventsListener` or `AdaptyUIFlowPlatformView`) run **in addition to** the global observer, not instead of it — your callback observes an event, it doesn't replace the default.

---

## Optional: system requests and observer mode

New in 4.0.0, with no v3 equivalent — **skip unless you need them.** Flows can ask your app for an OS permission or an in-app review, and observer mode can hand purchases to your app from inside a flow view. Both are registered globally, because each is a request the flow waits on an answer for.

```kotlin
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

If you register a handler you must answer: `handlePermission` and both resolver methods are abstract. With no handler registered, a permission request resolves as denied when the flow tears down, and observer mode does nothing when the user taps buy. `onStartPurchase` / `onFinishPurchase` only drive the loading indicator — report the transaction to Adapty yourself.

---

## Models

- **`AdaptyFlow.paywalls`** — the flow's `AdaptyFlowPaywall` variations.
- **`AdaptyUIPermission`** wraps the raw permission id, with the known ids as constants (`AdaptyUIPermission.PUSH`). Unknown, platform-specific (Android `phone` / `sms`) and future ids arrive verbatim via `permission.value`, so match with an `else` branch.
- **`AdaptyUIPermissionResult.granted(detail)` / `.denied(detail)`** — what `handlePermission` returns.
- **`AdaptyConfig.ServerCluster.CN`** — new, alongside `DEFAULT` and `EU`.

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

The whole onboarding surface is now `@Deprecated` — methods (`getOnboarding`, `getOnboardingForDefaultAudience`, `createOnboardingView`, `presentOnboardingView`, `dismissOnboardingView`, `createNativeOnboardingView`, `setOnboardingsEventsObserver`, `register`/`unregisterOnboardingEventsListener`), the observer (`AdaptyUIOnboardingsEventsObserver`), the composable (`AdaptyUIOnboardingPlatformView`), and the models (`AdaptyOnboarding`, `AdaptyUIOnboardingView`, `AdaptyUIOnboardingMeta`, `AdaptyOnboardingsAnalyticsEvent`, `AdaptyOnboardingsInput`, `AdaptyOnboardingsStateUpdatedParams`, `AdaptyNativeOnboardingView`).

Everything still works unchanged this release; a future major migrates onboardings into the Flow Builder. Suppress the warnings for now — no code change needed.

---
