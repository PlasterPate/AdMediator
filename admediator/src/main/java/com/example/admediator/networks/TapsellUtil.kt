package com.example.admediator.networks

import android.content.Context
import android.util.Log
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import ir.tapsell.sdk.*

internal class TapsellUtil {
    companion object {
        private var response = AdState("", "", "")

        fun requestAd(context: Context, zoneId: String, listener: AdRequestListener) : AdState {
            Tapsell.requestAd(context, zoneId, TapsellAdRequestOptions(),
                object : TapsellAdRequestListener() {
                    override fun onAdAvailable(adId: String?) {
                        response = response.copy(network = AdNetwork.TAPSELL, id = adId!!)
                        listener.onAdAvailable(adId)
                    }

                    override fun onError(message: String?) {
                        Log.w("Mediator", message!!)
                        listener.onError(message)
                        throw Exception("Tapsell ad not available.")
                    }
                })
            return response
        }

        fun showAd(context: Context, zoneId: String, adId: String, listener: AdShowListener){
            Tapsell.showAd(context, zoneId, adId, TapsellShowOptions(),
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