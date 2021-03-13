package com.example.admediator.listeners

interface AdShowListener {

    fun onOpened()

    fun onClosed()

    fun onError(message: String?)

    fun onRewarded(completed: Boolean)
}