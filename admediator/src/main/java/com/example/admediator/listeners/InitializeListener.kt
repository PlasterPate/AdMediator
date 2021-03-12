package com.example.admediator.listeners

interface InitializeListener {

    fun onSuccess()

    fun onError(errorMessage: String)
}