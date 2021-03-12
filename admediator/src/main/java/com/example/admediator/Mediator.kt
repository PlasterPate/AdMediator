package com.example.admediator

import android.app.Application
import android.content.Context
import android.util.Log
import com.chartboost.sdk.Chartboost
import com.example.admediator.constants.AdNetwork
import com.example.admediator.constants.ZoneType
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.repository.AdRepository
import com.unity3d.ads.UnityAds
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.tapsell.sdk.Tapsell

class Mediator {

    private val CB_SIGNATURE = "dummy_signature"

    private val adRepository = AdRepository()
    private lateinit var networks: List<AdNetworkEntity>

    fun initialize(application: Application, appId: String, listener: InitializeListener){
        adRepository.getAdNetworks(appId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                networks = it
                initNetworks(application)
                listener.onSuccess()
            }, {
                Log.e("Mediator", "InitializeError:".plus(it.message))
                listener.onError(it.message!!)
            }).also {
                CompositeDisposable(it)
            }
    }

    private fun initNetworks(application: Application ){
        networks.forEach{ net ->
            when(net.name){
                AdNetwork.TAPSELL ->
                    Tapsell.initialize(application, net.appId)
                AdNetwork.CHARTBOOST ->
                    Chartboost.startWithAppId(application, net.appId, CB_SIGNATURE)
                AdNetwork.UNITY_ADS ->
                    UnityAds.initialize(application, net.appId)
            }
        }
    }
}