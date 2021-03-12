package com.example.admediator.networks

import android.content.Context
import android.util.Log
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdResponse
import com.example.admediator.listeners.AdRequestListener
import ir.tapsell.sdk.Tapsell
import ir.tapsell.sdk.TapsellAdRequestListener
import ir.tapsell.sdk.TapsellAdRequestOptions

class TapsellUtil {
    companion object {
        private var response = AdResponse("", "")

        fun requestAd(context: Context, zoneId: String, listener: AdRequestListener) : AdResponse {
            Tapsell.requestAd(context, zoneId, TapsellAdRequestOptions(),
                object : TapsellAdRequestListener() {
                    override fun onAdAvailable(adId: String?) {
                        response = AdResponse(AdNetwork.TAPSELL, adId!!)
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
    }
}