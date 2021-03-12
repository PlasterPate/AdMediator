package com.example.admediator.listeners

interface AdRequestListener {

    fun onAdAvailable(adId: String?)

    fun onError(errorMessage: String?)
}