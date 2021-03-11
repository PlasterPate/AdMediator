package com.example.admediator

import android.app.Application
import android.util.Log
import com.chartboost.sdk.Chartboost
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.repository.AdRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.tapsell.sdk.Tapsell

class Mediator {

    private val CB_SIGNATURE = "dummy_signature"
    private val adRepository = AdRepository()
    private lateinit var networks: List<AdNetworkEntity>

    fun initialize(application: Application , appId: String){
        adRepository.getAdNetworks(appId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                networks = it
                initNetworks(application)
            }, {
                Log.e("Mediator", "throwable + ${it.message}")
            }).also {
                CompositeDisposable(it)
            }
    }

    private fun initNetworks(application: Application ){
        networks.forEach{ net ->
            if ("Tapsell" in net.name){
                Tapsell.initialize(application, net.appId)
            }else if ("Chartboost" in net.name){
                Chartboost.startWithAppId(application, net.appId, CB_SIGNATURE)
            }
        }

    }
}