package com.maxrtb.maxrtbadsdk

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maxrtb.maxrtbadsdk.callback.AdCallback
import com.maxrtb.maxrtbadsdk.model.AdType
import com.maxrtb.sdk.R

class MainActivity : AppCompatActivity() {

    private lateinit var adSDK: MaxRTBAdSDK
    private lateinit var bannerContainer: FrameLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化SDK
        adSDK = MaxRTBAdSDK.init(this, "your_app_id")

        bannerContainer = findViewById(R.id.banner_container)

        // 示例1：加载并显示Banner广告
        loadBannerAd()

        // 示例2：加载插屏广告
        loadInterstitialAd()

        // 示例3：加载激励视频
        loadRewardedVideoAd()
    }

    private fun loadBannerAd() {
        adSDK.loadAndShowAd(
            adSlotId = "banner_slot_001",
            adType = AdType.BANNER,
            container = bannerContainer,
            callback = object : AdCallback {
                override fun onAdShow() {
                    Toast.makeText(this@MainActivity, "Banner显示", Toast.LENGTH_SHORT).show()
                }

                override fun onAdClick() {
                    Toast.makeText(this@MainActivity, "Banner点击", Toast.LENGTH_SHORT).show()
                }

                override fun onAdClose() {
                    Toast.makeText(this@MainActivity, "Banner关闭", Toast.LENGTH_SHORT).show()
                }

                override fun onAdError(error: String) {
                    Toast.makeText(this@MainActivity, "Banner错误: $error", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun loadInterstitialAd() {
        adSDK.loadAndShowAd(
            adSlotId = "interstitial_slot_001",
            adType = AdType.INTERSTITIAL,
            callback = object : AdCallback {
                override fun onAdShow() {
                    // 插屏广告显示
                }

                override fun onAdClick() {
                    // 插屏广告点击
                }

                override fun onAdClose() {
                    // 插屏广告关闭
                }

                override fun onAdError(error: String) {
                    // 错误处理
                }
            }
        )
    }

    private fun loadRewardedVideoAd() {
        adSDK.loadAndShowAd(
            adSlotId = "rewarded_slot_001",
            adType = AdType.REWARDED_VIDEO,
            callback = object : AdCallback {
                override fun onAdShow() {
                    // 视频开始播放
                }

                override fun onAdClick() {
                    // 视频点击
                }

                override fun onAdClose() {
                    // 视频关闭
                }

                override fun onAdError(error: String) {
                    // 错误处理
                }

                override fun onRewardEarned() {
                    // 用户获得奖励
                    Toast.makeText(this@MainActivity, "恭喜获得奖励！", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        adSDK.release()
    }
}