package com.example.admediator

import android.app.Application
import android.content.Context
import android.util.Log
import com.chartboost.sdk.Chartboost
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.AdResponse
import com.example.admediator.data.ZoneConfigEntity
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.InitializeListener
import com.example.admediator.networks.ChartboostUtil
import com.example.admediator.networks.TapsellUtil
import com.example.admediator.repository.AdRepository
import com.unity3d.ads.UnityAds
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.tapsell.sdk.Tapsell
import java.util.concurrent.TimeUnit


class Mediator {

    private val CB_SIGNATURE = "dummy_signature"

    private val adRepository = AdRepository()

    private lateinit var networks: List<AdNetworkEntity>

    private var availableAdNetwork = ""

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

    fun requestAd(context: Context, zoneId: String, listener: AdRequestListener){
        adRepository.initSharedPreferences(context)
        adRepository.getZoneConfig(zoneId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({zoneConfig ->
                zoneConfigTimeout(zoneId, zoneConfig.timeout)
                requestAdFromWaterfall(context, zoneConfig, listener)
            }, {
                Log.e("Mediator", "RequestAdError:".plus(it.message))
                listener.onError(it.message!!)
            }).also {
                CompositeDisposable(it)
            }
        Context.WINDOW_SERVICE
    }

    private fun zoneConfigTimeout(zoneId: String, timeout: Long){
        adRepository.removeZoneConfig(zoneId)
            .delaySubscription(timeout, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun requestAdFromWaterfall(context: Context, zoneConfig: ZoneConfigEntity, listener: AdRequestListener){
        val singles = mutableListOf<Single<AdResponse>>()
        zoneConfig.waterfall.forEach {netConfig ->
            when(netConfig.adNetwork){
                AdNetwork.TAPSELL -> {
                    singles.add(Single.just(
                        TapsellUtil.requestAd(context, netConfig.zoneId, listener)
                    ).timeout(netConfig.timeout, TimeUnit.MILLISECONDS))
                }
                AdNetwork.CHARTBOOST ->{
                    singles.add(Single.just(
                        ChartboostUtil.requestAd(netConfig.zoneId, zoneConfig.zoneType, listener)
                    ))
                }

//                AdNetwork.UNITY_ADS ->
            }
        }

        Single.concat(singles)
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap{
                res -> adRepository.saveAdId(res.adId)
                Maybe.just(res)
            }
            .subscribe({response ->
                availableAdNetwork = response.adNetwork
            }, {
                Log.e("Mediator", "RequestAdError:".plus(it.message))
            }).also {
                CompositeDisposable(it)
            }
    }
}