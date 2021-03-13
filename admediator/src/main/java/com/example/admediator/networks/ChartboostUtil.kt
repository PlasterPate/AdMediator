package com.example.admediator.networks

import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ChartboostDelegate
import com.chartboost.sdk.Model.CBError
import com.example.admediator.constants.AdNetwork
import com.example.admediator.constants.ZoneType
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener

class ChartboostUtil {
    companion object {
        private lateinit var adListener: AdRequestListener
        private lateinit var adShowListener: AdShowListener
        private var response = AdState("", "", "")

        fun requestAd(zoneId: String, zoneType: String, listener: AdRequestListener) : AdState {
            adListener = listener
            Chartboost.setDelegate(chartboostCacheDelegate)
            when (zoneType) {
                ZoneType.INTERSTITIAL -> {
                    response = response.copy(type = ZoneType.INTERSTITIAL)
                    Chartboost.cacheInterstitial(zoneId)
                }
                ZoneType.REWARDED -> {
                    response = response.copy(type = ZoneType.REWARDED)
                    Chartboost.cacheRewardedVideo(zoneId)
                }
                else -> {}
            }
            return response
        }

        private val chartboostCacheDelegate: ChartboostDelegate = object : ChartboostDelegate() {
            override fun didCacheInterstitial(location: String?) {
                response = response.copy(network = AdNetwork.CHARTBOOST, id = location!!)
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
                response = response.copy(network = AdNetwork.CHARTBOOST, id = location!!)
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

        fun showAd(adState: AdState, listener: AdShowListener){
            adShowListener = listener
            Chartboost.setDelegate(chartboostShowDelegate)
            when (adState.type) {
                ZoneType.INTERSTITIAL -> {
                    Chartboost.showInterstitial(adState.id)
                }
                ZoneType.REWARDED -> {
                    Chartboost.showRewardedVideo(adState.id)
                }
                else -> {}
            }
        }

        private val chartboostShowDelegate: ChartboostDelegate = object : ChartboostDelegate(){

        }
    }
}