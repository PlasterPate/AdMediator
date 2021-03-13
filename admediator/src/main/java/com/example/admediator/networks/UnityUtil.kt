package com.example.admediator.networks

import android.app.Activity
import android.app.Application
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.FinishState
import com.unity3d.ads.UnityAds.UnityAdsError


internal class UnityUtil {
    companion object {

        private lateinit var adReqListener: AdRequestListener

        private lateinit var adShowListener: AdShowListener

        private var reqResponse = AdState(AdNetwork.UNITY_ADS, "", "")


        fun initialize(application: Application, appId: String) {
            UnityAds.addListener(unityAdsListener)
            UnityAds.initialize(application, appId)
        }

        fun requestAd(listener: AdRequestListener) : AdState{
            adReqListener = listener
            if (reqResponse.id != ""){
                adReqListener.onAdAvailable(reqResponse.id)
                return reqResponse
            }else{
                adReqListener.onError("Unity ad not available.")
                throw Exception("Unity ad not available.")
            }
        }

        fun showAd(activity: Activity, adId: String, listener: AdShowListener) {
            adShowListener = listener
            if (UnityAds.isReady(adId)) {
                UnityAds.show(activity, adId)
            }
        }

        private val unityAdsListener = object : IUnityAdsListener {
            override fun onUnityAdsReady(surfacingId: String) {
                reqResponse = reqResponse.copy(id = surfacingId)
                if (this@Companion::adReqListener.isInitialized) {
                    adReqListener.onAdAvailable(surfacingId)
                }
            }

            override fun onUnityAdsStart(surfacingId: String) {
                if (this@Companion::adShowListener.isInitialized) {
                    adShowListener.onOpened()
                }
            }

            override fun onUnityAdsFinish(
                surfacingId: String,
                finishState: FinishState
            ) {
                when(finishState){
                    FinishState.COMPLETED ->{
                        adShowListener.onRewarded(true)
                        // Reset the adId after ad finished successfully
                        reqResponse.copy(id = "")
                        adShowListener.onClosed()
                    }
                    FinishState.ERROR -> {
                        adShowListener.onError("Unity faced an error while showing the ad")
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
                if (this@Companion::adReqListener.isInitialized) {
                    adReqListener.onError("Unity ad not available.")
                }
                if (this@Companion::adShowListener.isInitialized) {
                    adShowListener.onError("Unity-ads service error.")
                }
                throw Exception("Unity-ads service error.")
            }
        }
    }
}