package com.example.admediator

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.AdState
import com.example.admediator.data.ZoneConfigEntity
import com.example.admediator.listeners.AdRequestListener
import com.example.admediator.listeners.AdShowListener
import com.example.admediator.listeners.InitializeListener
import com.example.admediator.networks.BaseNetwork
import com.example.admediator.networks.NetworkFactory
import com.example.admediator.repository.AdRepository
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class Mediator {

    private val adRepository = AdRepository()

    private val networkFactory = NetworkFactory()

    private var networks = mutableMapOf<String, BaseNetwork>()

    fun initialize(application: Application, appId: String, listener: InitializeListener){
        adRepository.getAdNetworks(appId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({netList ->
                initNetworks(netList, application)
                listener.onSuccess()
            }, {
                Log.e("Mediator", "InitializeError:".plus(it.message))
                listener.onError(it.message!!)
            }).also {
                CompositeDisposable(it)
            }
    }

    private fun initNetworks(networkList: List<AdNetworkEntity>, application: Application ){
        networkList.forEach{ adNet ->
            val networkInstance = networkFactory.createNetwork(adNet)
            networkInstance.initialize(application)
            networks[adNet.name] = networkInstance
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
            if (netConfig.adNetwork in networks.keys){
                singles.add(Single.just(
                    networks[netConfig.adNetwork]!!.requestAd(context, netConfig.zoneId, zoneConfig.zoneType, listener)
                ).timeout(netConfig.timeout, TimeUnit.MILLISECONDS))
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
                if (state.id != "" && state.network in networks.keys){
                    networks[state.network]!!.showAd(activity, state, zoneId, listener)
                }
            }, {
                Log.e("Mediator", "ShowAdError:".plus(it.message))
            }).also {
                CompositeDisposable(it)
            }
    }
}