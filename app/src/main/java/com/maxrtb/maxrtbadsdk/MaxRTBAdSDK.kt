package com.maxrtb.maxrtbadsdk

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.maxrtb.maxrtbadsdk.callback.AdCallback
import com.maxrtb.maxrtbadsdk.callback.AdLoadCallback
import com.maxrtb.maxrtbadsdk.model.AdData
import com.maxrtb.maxrtbadsdk.model.AdRequest
import com.maxrtb.maxrtbadsdk.model.AdResponse
import com.maxrtb.maxrtbadsdk.model.AdType
import com.maxrtb.maxrtbadsdk.model.DeviceInfo
import com.maxrtb.maxrtbadsdk.network.NetworkManager
import com.maxrtb.maxrtbadsdk.renderer.UniversalAdRenderer
import com.maxrtb.maxrtbadsdk.utils.DeviceUtil
import com.maxrtb.maxrtbadsdk.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.TimeZone

class MaxRTBAdSDK private constructor(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: MaxRTBAdSDK? = null
        private var appId: String? = null

        fun init(context: Context, appId: String): MaxRTBAdSDK {
            this.appId = appId
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MaxRTBAdSDK(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }

    }

    private val renderer = UniversalAdRenderer(context)
    private var cachedAds = mutableMapOf<String, AdData>()

    // 加载广告
    fun loadAd(
        adSlotId: String,
        adType: AdType,
        callback: AdLoadCallback
    ) {
        val request = createAdRequest(adSlotId, adType)

        NetworkManager.apiService.requestAd(request).enqueue(object : Callback<AdResponse> {
            override fun onResponse(call: Call<AdResponse>, response: Response<AdResponse>) {
                if (response.isSuccessful) {
                    val adResponse = response.body()
                    if (adResponse?.ad != null) {
                        cachedAds[adSlotId] = adResponse.ad
                        callback.onAdLoaded(adResponse.ad)
                    } else {
                        val error = adResponse?.error?.message ?: "No ad returned"
                        callback.onAdLoadFailed(error)
                    }
                } else {
                    callback.onAdLoadFailed("Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AdResponse>, t: Throwable) {
                Log.e("MaxRTBAdSDK", "Ad request failed", t)
                callback.onAdLoadFailed(t.message ?: "Network error")
            }
        })
    }

    // 加载并显示广告
    fun loadAndShowAd(
        adSlotId: String,
        adType: AdType,
        container: ViewGroup? = null,
        callback: AdCallback
    ) {
        loadAd(adSlotId, adType, object : AdLoadCallback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onAdLoaded(adData: AdData) {
                renderer.setAdCallback(callback)
                renderer.render(adData, container)
            }

            override fun onAdLoadFailed(error: String) {
                callback.onAdError(error)
            }
        })
    }

    // 创建广告请求
    private fun createAdRequest(adSlotId: String, adType: AdType): AdRequest {
        val deviceInfo = DeviceInfo(
            deviceId = DeviceUtil.getDeviceId(context),
            osVersion = Build.VERSION.RELEASE,
            model = Build.MODEL,
            brand = Build.BRAND,
            screenWidth = context.resources.displayMetrics.widthPixels,
            screenHeight = context.resources.displayMetrics.heightPixels,
            density = context.resources.displayMetrics.density,
            network = NetworkUtil.getNetworkType(context),
            carrier = NetworkUtil.getCarrier(context),
            language = Locale.getDefault().language,
            timezone = TimeZone.getDefault().id
        )

        return AdRequest(
            appId = appId ?: "",
            adSlotId = adSlotId,
            adType = adType.tagId,
            deviceInfo = deviceInfo
        )
    }

    // 释放资源
    fun release() {
        renderer.release()
        cachedAds.clear()
    }
}
