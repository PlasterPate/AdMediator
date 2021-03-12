package com.example.admediator

interface InitializeCallback {

    fun onSuccess()

    fun onError(errorMessage: String)
}