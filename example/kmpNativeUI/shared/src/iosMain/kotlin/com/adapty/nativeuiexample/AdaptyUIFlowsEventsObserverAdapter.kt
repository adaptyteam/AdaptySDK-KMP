package com.adapty.nativeuiexample

import com.adapty.kmp.AdaptyUIFlowsEventsObserver

/**
 * Open base class implementing [AdaptyUIFlowsEventsObserver] so that
 * iOS/Swift code can subclass it instead of conforming to the full protocol.
 *
 * Kotlin handles [mainUiScope] and all default method implementations;
 * Swift only needs to override the callbacks it cares about.
 */
open class AdaptyUIFlowsEventsObserverAdapter : AdaptyUIFlowsEventsObserver
