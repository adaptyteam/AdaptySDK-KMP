package com.adapty.kmp.models


public interface AdaptyPaywall{

    public sealed class FetchPolicy {

        public class ReloadRevalidatingCacheData private constructor(): FetchPolicy() {

            internal companion object {
                fun create() = ReloadRevalidatingCacheData()
            }

            override fun toString(): String {
                return "ReloadRevalidatingCacheData"
            }
        }

        public class ReturnCacheDataElseLoad private constructor(): FetchPolicy() {

            internal companion object {
                fun create() = ReturnCacheDataElseLoad()
            }

            override fun toString(): String {
                return "ReturnCacheDataElseLoad"
            }
        }

        public class ReturnCacheDataIfNotExpiredElseLoad private constructor(public val maxAgeMillis: Long) : FetchPolicy() {

            internal companion object {
                fun create(maxAgeMillis: Long) = ReturnCacheDataIfNotExpiredElseLoad(maxAgeMillis)
            }

            override fun toString(): String {
                return "ReturnCacheDataIfNotExpiredElseLoad(maxAgeMillis=$maxAgeMillis)"
            }
        }

        public companion object {

            public val ReloadRevalidatingCacheData: FetchPolicy = FetchPolicy.ReloadRevalidatingCacheData.create()

            public val ReturnCacheDataElseLoad: FetchPolicy = FetchPolicy.ReturnCacheDataElseLoad.create()

            public fun ReturnCacheDataIfNotExpiredElseLoad(maxAgeMillis: Long): FetchPolicy = ReturnCacheDataIfNotExpiredElseLoad.create(maxAgeMillis)

            public val Default: FetchPolicy = ReloadRevalidatingCacheData
        }
    }

}