package com.maxrtb.maxrtbadsdk.callback

import com.maxrtb.maxrtbadsdk.model.AdData

interface AdLoadCallback {
    fun onAdLoaded(adData: AdData)
    fun onAdLoadFailed(error: String)
}