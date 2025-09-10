package com.maxrtb.zxadsdk.callback

interface AdCallback {
    fun onAdShow()
    fun onAdClick()
    fun onAdClose()
    fun onAdError(error: String)
    fun onRewardEarned() {} // 可选，用于激励视频
}