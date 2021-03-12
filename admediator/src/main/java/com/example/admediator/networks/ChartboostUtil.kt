package com.example.admediator.networks

import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ChartboostDelegate
import com.chartboost.sdk.Model.CBError
import com.example.admediator.constants.AdNetwork
import com.example.admediator.constants.ZoneType
import com.example.admediator.data.AdResponse
import com.example.admediator.listeners.AdRequestListener
import java.time.ZoneId

class ChartboostUtil {
    companion object {
        private lateinit var adListener: AdRequestListener
        private var response = AdResponse("", "")

        fun requestAd(zoneId: String, zoneType: String, listener: AdRequestListener) : AdResponse {
            adListener = listener
            Chartboost.setDelegate(chartboostDelegateCache)
            when (zoneType) {
                ZoneType.INTERSTITIAL -> {
                    Chartboost.cacheInterstitial(zoneId)
                }
                ZoneType.REWARDED -> {
                    Chartboost.cacheRewardedVideo(zoneId)
                }
                else -> {}
            }
            return response
        }

        private val chartboostDelegateCache: ChartboostDelegate = object : ChartboostDelegate() {
            override fun didCacheInterstitial(location: String?) {
                response = AdResponse(AdNetwork.CHARTBOOST, location!!)
                adListener.onAdAvailable(location)
            }

            override fun didFailToLoadInterstitial(
                location: String?,
                error: CBError.CBImpressionError?
            ) {
                adListener.onError(error.toString())
                throw Exception("Chartboost ad not available.")
            }

            override fun didCacheRewardedVideo(location: String?) {
                response = AdResponse(AdNetwork.CHARTBOOST, location!!)
                adListener.onAdAvailable(location)
            }

            override fun didFailToLoadRewardedVideo(
                location: String?,
                error: CBError.CBImpressionError?
            ) {
                adListener.onError(error.toString())
                throw Exception("Chartboost ad not available.")
            }
        }
    }
}