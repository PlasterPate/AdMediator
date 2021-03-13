package com.example.admediator.networks

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import ir.tapsell.sdk.*

internal class TapsellUtil {
    companion object {
        private var reqResponse = AdState(AdNetwork.TAPSELL, "", "")

        fun initialize(application: Application, appId: String){
            Tapsell.initialize(application, appId)
        }

        fun requestAd(context: Context, zoneId: String, listener: AdRequestListener) : AdState {
            Tapsell.requestAd(context, zoneId, TapsellAdRequestOptions(),
                object : TapsellAdRequestListener() {
                    override fun onAdAvailable(adId: String?) {
                        reqResponse = reqResponse.copy(id = adId!!)
                        listener.onAdAvailable(adId)
                    }

                    override fun onError(message: String?) {
                        Log.w("Mediator", message!!)
                        listener.onError(message)
                        throw Exception("Tapsell ad not available.")
                    }
                })
            return reqResponse
        }

        fun showAd(activity: Activity, zoneId: String, adId: String, listener: AdShowListener){
            Tapsell.showAd(activity, zoneId, adId, TapsellShowOptions(),
            object : TapsellAdShowListener(){
                override fun onOpened() {
                    listener.onOpened()
                }

                override fun onClosed() {
                    listener.onClosed()
                }

                override fun onError(message: String?) {
                    listener.onError(message)
                }

                override fun onRewarded(completed: Boolean) {
                    listener.onRewarded(completed)
                }
            })
        }
    }
}