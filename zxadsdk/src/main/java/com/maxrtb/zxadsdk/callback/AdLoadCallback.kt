package com.maxrtb.zxadsdk.callback

import com.maxrtb.zxadsdk.model.AdData

interface AdLoadCallback {
    fun onAdLoaded(adData: AdData)
    fun onAdLoadFailed(error: String)
}