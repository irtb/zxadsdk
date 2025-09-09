package com.maxrtb.maxrtbadsdk.network

import com.maxrtb.maxrtbadsdk.model.AdRequest
import com.maxrtb.maxrtbadsdk.model.AdResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/bid/req")
    fun requestAd(@Body request: AdRequest): Call<AdResponse>
}