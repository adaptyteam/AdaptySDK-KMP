package com.adapty.nativeuiexample

import com.adapty.kmp.AdaptyUIPaywallsEventsObserver

/**
 * Open base class implementing [AdaptyUIPaywallsEventsObserver] so that
 * iOS/Swift code can subclass it instead of conforming to the full protocol.
 *
 * Kotlin handles [mainUiScope] and all default method implementations;
 * Swift only needs to override the callbacks it cares about.
 */
open class AdaptyUIPaywallsEventsObserverAdapter : AdaptyUIPaywallsEventsObserver
