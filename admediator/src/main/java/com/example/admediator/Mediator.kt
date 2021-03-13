package com.example.admediator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.AdState
import com.example.admediator.data.ZoneConfigEntity
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import com.example.admediator.listeners.InitializeListener
import com.example.admediator.networks.ChartboostUtil
import com.example.admediator.networks.TapsellUtil
import com.example.admediator.networks.UnityUtil
import com.example.admediator.repository.AdRepository
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class Mediator {

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
                    TapsellUtil.initialize(application, net.appId)
                AdNetwork.CHARTBOOST ->
                    ChartboostUtil.initialize(application, net.appId)
                AdNetwork.UNITY_ADS ->
                    UnityUtil.initialize(application, net.appId)
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
        val singles = mutableListOf<Single<AdState>>()
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
                AdNetwork.UNITY_ADS -> {
                    singles.add(Single.just(
                        UnityUtil.requestAd(listener)
                    ))
                }
            }
        }

        Single.concat(singles)
            .firstElement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap{state ->
                adRepository.saveAdState(state)
                Maybe.just(state)
            }
            .subscribe({}, {
                Log.e("Mediator", "RequestAdError:".plus(it.message))
            }).also {
                CompositeDisposable(it)
            }
    }


    fun showAd(activity: Activity, zoneId: String, listener: AdShowListener){
        adRepository.getAdState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({state ->
                if (state.id != ""){
                    showAdFromNetwork(activity, zoneId, state, listener)
                }
            }, {
                Log.e("Mediator", "ShowAdError:".plus(it.message))
            }).also {
                CompositeDisposable(it)
            }
    }

    private fun showAdFromNetwork(activity: Activity, zoneId: String, adState: AdState, listener: AdShowListener){
        when(adState.network){
            AdNetwork.TAPSELL ->{
                TapsellUtil.showAd(activity, zoneId, adState.id, listener)
            }
            AdNetwork.CHARTBOOST ->{
                ChartboostUtil.showAd(adState, listener)
            }
            AdNetwork.UNITY_ADS ->{
                UnityUtil.showAd(activity, adState.id, listener)
            }
        }
    }
}