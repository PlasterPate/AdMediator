package com.example.admediator.service

import com.example.admediator.data.AdNetworkDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface AdService {

    @GET("/{id}/networks/")
    fun getAdNetworks(@Path("id") appId: String) : Single<List<AdNetworkDto>>
}
