package com.example.admediator.networks

import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.admediator.data.AdState
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener

internal abstract class BaseNetwork(val appId: String) {

    protected abstract var reqResponse: AdState

    abstract fun initialize(application: Application)

    abstract fun requestAd(
        context: Context,
        zoneId: String,
        zoneType: String,
        listener: AdRequestListener
    ): AdState

    abstract fun showAd(
        activity: Activity,
        adState: AdState,
        zoneId: String,
        listener: AdShowListener
    )
}