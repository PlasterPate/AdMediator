package com.example.admediator.networks

import android.app.Activity
import android.app.Application
import android.content.Context
import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ChartboostDelegate
import com.chartboost.sdk.Model.CBError
import com.example.admediator.constants.AdNetwork
import com.example.admediator.constants.ZoneType
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener

internal class ChartboostNetwork(appId: String) : BaseNetwork(appId){

    private val CB_SIGNATURE = "dummy_signature"

    private lateinit var adReqListener: AdRequestListener

    private lateinit var adShowListener: AdShowListener

    private var reqResponse = AdState(AdNetwork.CHARTBOOST, "", "")

    override fun initialize(application: Application){
        Chartboost.startWithAppId(application, appId, CB_SIGNATURE)
    }

    override fun requestAd(context: Context, zoneId: String, zoneType: String, listener: AdRequestListener) : AdState {
        adReqListener = listener
        Chartboost.setDelegate(chartboostCacheDelegate)
        when (zoneType) {
            ZoneType.INTERSTITIAL -> {
                reqResponse = reqResponse.copy(type = ZoneType.INTERSTITIAL)
                Chartboost.cacheInterstitial(zoneId)
            }
            ZoneType.REWARDED -> {
                reqResponse = reqResponse.copy(type = ZoneType.REWARDED)
                Chartboost.cacheRewardedVideo(zoneId)
            }
            else -> {}
        }
        return reqResponse
    }

    private val chartboostCacheDelegate: ChartboostDelegate = object : ChartboostDelegate() {

        // --------Interstitial delegate methods--------

        override fun didCacheInterstitial(location: String?) {
            reqResponse = reqResponse.copy(id = location!!)
            adReqListener.onAdAvailable(location)
        }

        override fun didFailToLoadInterstitial(
            location: String?,
            error: CBError.CBImpressionError?
        ) {
            adReqListener.onError(error.toString())
            throw Exception("Chartboost ad not available.")
        }

        // --------Rewarded delegate methods--------

        override fun didCacheRewardedVideo(location: String?) {
            reqResponse = reqResponse.copy(network = AdNetwork.CHARTBOOST, id = location!!)
            adReqListener.onAdAvailable(location)
        }

        override fun didFailToLoadRewardedVideo(
            location: String?,
            error: CBError.CBImpressionError?
        ) {
            adReqListener.onError(error.toString())
            throw Exception("Chartboost ad not available.")
        }
    }

    override fun showAd(activity: Activity, adState: AdState, zoneId: String, listener: AdShowListener){
        adShowListener = listener
        Chartboost.setDelegate(chartboostShowDelegate)
        when (adState.type) {
            ZoneType.INTERSTITIAL -> {
                if (Chartboost.hasInterstitial(adState.id))
                    Chartboost.showInterstitial(adState.id)
                else
                    listener.onError("Chartboost ad not available.")
            }
            ZoneType.REWARDED -> {
                if (Chartboost.hasRewardedVideo(adState.id))
                    Chartboost.showRewardedVideo(adState.id)
                else
                    listener.onError("Chartboost ad not available.")
            }
            else -> {}
        }
    }

    private val chartboostShowDelegate: ChartboostDelegate = object : ChartboostDelegate(){

        // --------Interstitial delegate methods--------

        override fun didDisplayInterstitial(location: String?) {
            adShowListener.onOpened()
        }

        override fun didCloseInterstitial(location: String?) {
            adShowListener.onClosed()
        }

        override fun didFailToLoadInterstitial(
            location: String?,
            error: CBError.CBImpressionError?
        ) {
            adShowListener.onError(error.toString())
        }

        override fun didCompleteInterstitial(location: String?) {
            adShowListener.onRewarded(true)
        }

        // --------Rewarded delegate methods--------

        override fun didDisplayRewardedVideo(location: String?) {
            adShowListener.onOpened()
        }

        override fun didCloseRewardedVideo(location: String?) {
            adShowListener.onClosed()
        }

        override fun didFailToLoadRewardedVideo(
            location: String?,
            error: CBError.CBImpressionError?
        ) {
            adShowListener.onError(error.toString())
        }

        override fun didCompleteRewardedVideo(location: String?, reward: Int) {
            adShowListener.onRewarded(true)
        }
    }
}