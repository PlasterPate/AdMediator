package com.example.admediator.networks

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.admediator.constants.AdNetwork
import com.example.admediator.constants.Strings
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.FinishState
import com.unity3d.ads.UnityAds.UnityAdsError


internal class UnityNetwork(appId: String) : BaseNetwork(appId) {

    private lateinit var adReqListener: AdRequestListener

    private lateinit var adShowListener: AdShowListener

    override var reqResponse = AdState(AdNetwork.UNITY_ADS, "", "")

    override fun initialize(application: Application) {
        UnityAds.addListener(unityAdsListener)
        UnityAds.initialize(application, appId)
    }

    override fun requestAd(
        context: Context,
        zoneId: String,
        zoneType: String,
        listener: AdRequestListener
    ): AdState {
        adReqListener = listener
        if (reqResponse.id != "") {
            adReqListener.onAdAvailable(reqResponse.id)
            return reqResponse
        } else {
            adReqListener.onError(Strings.unity_ad_not_available)
            throw Exception(Strings.unity_ad_not_available)
        }
    }

    override fun showAd(
        activity: Activity,
        adState: AdState,
        zoneId: String,
        listener: AdShowListener
    ) {
        adShowListener = listener
        if (UnityAds.isReady(adState.id)) {
            UnityAds.show(activity, adState.id)
        }
    }

    private val unityAdsListener = object : IUnityAdsListener {
        override fun onUnityAdsReady(surfacingId: String) {
            reqResponse = reqResponse.copy(id = surfacingId)
            if (this@UnityNetwork::adReqListener.isInitialized) {
                adReqListener.onAdAvailable(surfacingId)
            }
        }

        override fun onUnityAdsStart(surfacingId: String) {
            if (this@UnityNetwork::adShowListener.isInitialized) {
                adShowListener.onOpened()
            }
        }

        override fun onUnityAdsFinish(
            surfacingId: String,
            finishState: FinishState
        ) {
            when (finishState) {
                FinishState.COMPLETED -> {
                    adShowListener.onRewarded(true)
                    // Reset the adId after ad finished successfully
                    reqResponse.copy(id = "")
                    adShowListener.onClosed()
                }
                FinishState.ERROR -> {
                    adShowListener.onError(Strings.unity_ad_show_error)
                }
                FinishState.SKIPPED -> {
                    adShowListener.onClosed()
                }
            }
        }

        override fun onUnityAdsError(
            error: UnityAdsError,
            message: String
        ) {
            if (this@UnityNetwork::adReqListener.isInitialized) {
                Log.e("UnityNetwork", error.toString())
                adReqListener.onError(Strings.unity_ad_not_available)
            }
            if (this@UnityNetwork::adShowListener.isInitialized) {
                Log.e("UnityNetwork", error.toString())
                adShowListener.onError(Strings.unity_ads_service_error)
            }
            throw Exception(Strings.unity_ads_service_error)
        }
    }
}