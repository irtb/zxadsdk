package com.maxrtb.zxadsdk.network

import com.maxrtb.zxadsdk.model.AdRequest
import com.maxrtb.zxadsdk.model.AdResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/bid/req")
    fun requestAd(@Body request: AdRequest): Call<AdResponse>
}