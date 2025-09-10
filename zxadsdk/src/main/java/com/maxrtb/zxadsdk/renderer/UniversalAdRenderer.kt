@file:Suppress("DEPRECATION")

package com.maxrtb.zxadsdk.renderer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.maxrtb.zxadsdk.callback.AdCallback
import com.maxrtb.zxadsdk.model.AdData
import com.maxrtb.zxadsdk.model.AdType
import com.maxrtb.zxadsdk.utils.AdTracker
import com.maxrtb.zxadsdk.utils.DensityUtil
import com.maxrtb.zxadsdk.R


class UniversalAdRenderer(private val context: Context) {

    private var adCallback: AdCallback? = null
    private var currentDialog: Dialog? = null
    private var exoPlayer: ExoPlayer? = null

    fun setAdCallback(callback: AdCallback) {
        this.adCallback = callback
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun render(adData: AdData, container: ViewGroup? = null) {
        val adType = AdType.entries.find { it.displayName == adData.type }

        when (adType) {
            AdType.BANNER -> renderBanner(adData, container)
            AdType.INTERSTITIAL -> renderInterstitial(adData)
            AdType.NATIVE -> renderNative(adData, container)
            AdType.SPLASH -> renderSplash(adData)
            AdType.REWARDED_VIDEO -> renderRewardedVideo(adData)
            AdType.FEED -> renderFeed(adData, container)
            AdType.FLOAT ->
                renderFloat(adData)

            AdType.FULL_SCREEN_VIDEO -> renderFullScreenVideo(adData)
            AdType.PRE_ROLL, AdType.MID_ROLL, AdType.POST_ROLL -> renderRollAd(adData)
            AdType.DRAW_VIDEO -> renderDrawVideo(adData)
            AdType.PATCH -> renderPatch(adData, container)
            else -> {
                Log.e("AdRenderer", "Unknown ad type: ${adData.type}")
                adCallback?.onAdError("Unknown ad type")
            }
        }

        // 发送展示追踪
        adData.impressionUrls?.forEach { url ->
            AdTracker.trackImpression(url)
        }
        adCallback?.onAdShow()
    }

    // Banner广告渲染
    private fun renderBanner(adData: AdData, container: ViewGroup?) {
        if (container == null) {
            adCallback?.onAdError("Banner container is null")
            return
        }

        val bannerView = LayoutInflater.from(context).inflate(
            R.layout.ad_banner_layout, container, false
        )

        val imageView = bannerView.findViewById<ImageView>(R.id.banner_image)
        val titleText = bannerView.findViewById<TextView>(R.id.banner_title)
        val closeBtn = bannerView.findViewById<ImageButton>(R.id.banner_close)

        // 加载图片
        adData.imageUrl?.let {
            Glide.with(context).load(it).into(imageView)
        }

        titleText.text = adData.title ?: ""

        // 点击事件
        bannerView.setOnClickListener {
            handleAdClick(adData)
        }

        closeBtn.setOnClickListener {
            container.removeView(bannerView)
            adCallback?.onAdClose()
        }

        container.addView(bannerView)
    }

    // 插屏广告渲染
    private fun renderInterstitial(adData: AdData) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_interstitial_layout)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val contentContainer = dialog.findViewById<FrameLayout>(R.id.interstitial_content)
        val closeBtn = dialog.findViewById<ImageButton>(R.id.interstitial_close)
        val countdownText = dialog.findViewById<TextView>(R.id.countdown_text)

        // 根据内容类型渲染
        if (!adData.videoUrl.isNullOrEmpty()) {
            renderVideoContent(adData.videoUrl, contentContainer)
        } else if (!adData.imageUrl.isNullOrEmpty()) {
            val imageView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            Glide.with(context).load(adData.imageUrl).into(imageView)
            contentContainer.addView(imageView)
        }

        // 点击事件
        contentContainer.setOnClickListener {
            handleAdClick(adData)
        }

        // 倒计时关闭
        startCountdown(adData.duration, countdownText) {
            closeBtn.visibility = View.VISIBLE
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
            adCallback?.onAdClose()
        }

        currentDialog = dialog
        dialog.show()
    }

    // 原生广告渲染
    @SuppressLint("MissingInflatedId")
    private fun renderNative(adData: AdData, container: ViewGroup?) {
        if (container == null) {
            adCallback?.onAdError("Native container is null")
            return
        }

        val nativeView = LayoutInflater.from(context).inflate(
            R.layout.ad_native_layout, container, false
        )

        val iconView = nativeView.findViewById<ImageView>(R.id.native_icon)
        val titleView = nativeView.findViewById<TextView>(R.id.native_title)
        val descView = nativeView.findViewById<TextView>(R.id.native_description)
        val mediaView = nativeView.findViewById<FrameLayout>(R.id.native_media)
        val ctaButton = nativeView.findViewById<Button>(R.id.native_cta)
        val ratingBar = nativeView.findViewById<RatingBar>(R.id.native_rating)
        val priceView = nativeView.findViewById<TextView>(R.id.native_price)

        // 填充数据
        adData.appIcon?.let {
            Glide.with(context).load(it).circleCrop().into(iconView)
        }

        titleView.text = adData.title ?: adData.appName ?: ""
        descView.text = adData.description ?: ""
        ctaButton.text = adData.ctaText ?: "立即下载"

        adData.rating?.let {
            ratingBar.visibility = View.VISIBLE
            ratingBar.rating = it
        }

        adData.price?.let {
            priceView.visibility = View.VISIBLE
            priceView.text = it
        }

        // 媒体内容
        if (!adData.videoUrl.isNullOrEmpty()) {
            renderVideoContent(adData.videoUrl, mediaView)
        } else if (!adData.imageUrl.isNullOrEmpty()) {
            val imageView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                adjustViewBounds = true
            }
            Glide.with(context).load(adData.imageUrl).into(imageView)
            mediaView.addView(imageView)
        }

        // 点击事件
        ctaButton.setOnClickListener {
            handleAdClick(adData)
        }

        nativeView.setOnClickListener {
            handleAdClick(adData)
        }

        container.addView(nativeView)
    }

    // 开屏广告渲染
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun renderSplash(adData: AdData) {
        val activity = context as? Activity ?: return

        val splashView = LayoutInflater.from(context).inflate(
            R.layout.ad_splash_layout, null
        )

        val contentView = splashView.findViewById<FrameLayout>(R.id.splash_content)
        val skipBtn = splashView.findViewById<TextView>(R.id.splash_skip)
        splashView.findViewById<ImageView>(R.id.splash_logo)

        // 渲染内容
        if (!adData.videoUrl.isNullOrEmpty()) {
            renderVideoContent(adData.videoUrl, contentView)
        } else if (!adData.imageUrl.isNullOrEmpty()) {
            val imageView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(context).load(adData.imageUrl).into(imageView)
            contentView.addView(imageView)
        }

        // 点击事件
        contentView.setOnClickListener {
            handleAdClick(adData)
        }

        // 倒计时跳过
        var countdown = adData.duration / 1000
        skipBtn.text = "跳过 ${countdown}s"

        val timer = object : CountDownTimer(adData.duration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown--
                skipBtn.text = "跳过 ${countdown}s"
            }

            override fun onFinish() {
                closeSplash(splashView)
            }
        }.start()

        skipBtn.setOnClickListener {
            timer.cancel()
            closeSplash(splashView)
        }

        // 添加到Activity
        val decorView = activity.window.decorView as ViewGroup
        decorView.addView(splashView)
    }

    // 激励视频广告渲染
    private fun renderRewardedVideo(adData: AdData) {
        if (adData.videoUrl.isNullOrEmpty()) {
            adCallback?.onAdError("Rewarded video URL is empty")
            return
        }

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_rewarded_video_layout)
        dialog.setCancelable(false)

        val playerView = dialog.findViewById<PlayerView>(R.id.rewarded_player)
        val progressBar = dialog.findViewById<ProgressBar>(R.id.rewarded_progress)
        val closeBtn = dialog.findViewById<ImageButton>(R.id.rewarded_close)
        val rewardInfo = dialog.findViewById<TextView>(R.id.reward_info)

        closeBtn.visibility = View.GONE
        rewardInfo.text = "观看完整视频获得奖励"

        // 初始化播放器
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(adData.videoUrl))
            prepare()
            playWhenReady = true
        }

        playerView.player = exoPlayer

        // 播放完成
        exoPlayer?.addListener(object : com.google.android.exoplayer2.Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    ExoPlayer.STATE_ENDED -> {
                        closeBtn.visibility = View.VISIBLE
                        rewardInfo.text = "恭喜获得奖励！"
                        adCallback?.onRewardEarned()
                    }
                    ExoPlayer.STATE_READY -> {
                        progressBar.visibility = View.GONE
                    }

                    Player.STATE_BUFFERING -> {
                        TODO()
                    }

                    Player.STATE_IDLE -> {
                        TODO()
                    }
                }
            }
        })

        closeBtn.setOnClickListener {
            exoPlayer?.release()
            dialog.dismiss()
            adCallback?.onAdClose()
        }

        currentDialog = dialog
        dialog.show()
    }

    // 信息流广告渲染
    private fun renderFeed(adData: AdData, container: ViewGroup?) {
        if (container == null) {
            adCallback?.onAdError("Feed container is null")
            return
        }

        val feedView = LayoutInflater.from(context).inflate(
            R.layout.ad_feed_layout, container, false
        )

        feedView.findViewById<CardView>(R.id.feed_card)
        val imageView = feedView.findViewById<ImageView>(R.id.feed_image)
        val titleView = feedView.findViewById<TextView>(R.id.feed_title)
        val descView = feedView.findViewById<TextView>(R.id.feed_description)
        val sourceView = feedView.findViewById<TextView>(R.id.feed_source)
        feedView.findViewById<TextView>(R.id.feed_ad_mark)

        // 填充数据
        adData.imageUrl?.let {
            Glide.with(context).load(it).centerCrop().into(imageView)
        }

        titleView.text = adData.title ?: ""
        descView.text = adData.description ?: ""
        sourceView.text = adData.appName ?: "推广"

        // 点击事件
        feedView.setOnClickListener {
            handleAdClick(adData)
        }

        container.addView(feedView)
    }

    // 浮窗广告渲染
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    private fun renderFloat(adData: AdData) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val floatView = LayoutInflater.from(context).inflate(
            R.layout.ad_float_layout, null
        )

        val contentView = floatView.findViewById<ImageView>(R.id.float_content)
        val closeBtn = floatView.findViewById<ImageButton>(R.id.float_close)

        adData.imageUrl?.let {
            Glide.with(context).load(it).into(contentView)
        }

        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = android.graphics.PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.TOP or Gravity.END
            width = DensityUtil.dp2px(context, 100f)
            height = DensityUtil.dp2px(context, 100f)
            x = DensityUtil.dp2px(context, 20f)
            y = DensityUtil.dp2px(context, 100f)
        }

        contentView.setOnClickListener {
            handleAdClick(adData)
        }

        closeBtn.setOnClickListener {
            windowManager.removeView(floatView)
            adCallback?.onAdClose()
        }

        windowManager.addView(floatView, params)
    }

    // 全屏视频广告渲染
    private fun renderFullScreenVideo(adData: AdData) {
        if (adData.videoUrl.isNullOrEmpty()) {
            adCallback?.onAdError("Full screen video URL is empty")
            return
        }

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_fullscreen_video_layout)

        val playerView = dialog.findViewById<PlayerView>(R.id.fullscreen_player)
        val skipBtn = dialog.findViewById<Button>(R.id.fullscreen_skip)
        val muteBtn = dialog.findViewById<ImageButton>(R.id.fullscreen_mute)

        // 初始化播放器
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(adData.videoUrl))
            prepare()
            playWhenReady = true
        }

        playerView.player = exoPlayer
        playerView.useController = false

        // 跳过按钮倒计时
        startCountdown(5000, null) {
            skipBtn.visibility = View.VISIBLE
        }

        skipBtn.setOnClickListener {
            exoPlayer?.release()
            dialog.dismiss()
            adCallback?.onAdClose()
        }

        // 静音控制
        var isMuted = false
        muteBtn.setOnClickListener {
            isMuted = !isMuted
            exoPlayer?.volume = if (isMuted) 0f else 1f
            muteBtn.setImageResource(
                if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_on
            )
        }

        // 点击广告
        playerView.setOnClickListener {
            handleAdClick(adData)
        }

        currentDialog = dialog
        dialog.show()
    }

    // 贴片广告渲染（前中后贴）
    private fun renderRollAd(adData: AdData) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_roll_layout)

        val playerView = dialog.findViewById<PlayerView>(R.id.roll_player)
        val skipBtn = dialog.findViewById<TextView>(R.id.roll_skip)
        val adInfo = dialog.findViewById<TextView>(R.id.roll_ad_info)

        if (!adData.videoUrl.isNullOrEmpty()) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(adData.videoUrl))
                prepare()
                playWhenReady = true
            }
            playerView.player = exoPlayer
        }

        // 显示广告信息
        adInfo.text = when(adData.type) {
            "preroll" -> "广告后即将播放"
            "midroll" -> "广告播放中"
            "postroll" -> "感谢观看"
            else -> "广告"
        }

        // 倒计时跳过
        startCountdown(adData.duration, skipBtn) {
            skipBtn.isEnabled = true
            skipBtn.text = "跳过广告"
        }

        skipBtn.setOnClickListener {
            if (skipBtn.isEnabled) {
                exoPlayer?.release()
                dialog.dismiss()
                adCallback?.onAdClose()
            }
        }

        currentDialog = dialog
        dialog.show()
    }

    // Draw视频广告渲染（抖音样式）
    @SuppressLint("ClickableViewAccessibility")
    private fun renderDrawVideo(adData: AdData) {
        if (adData.videoUrl.isNullOrEmpty()) {
            adCallback?.onAdError("Draw video URL is empty")
            return
        }

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.ad_draw_video_layout)

        val playerView = dialog.findViewById<PlayerView>(R.id.draw_player)
        val titleView = dialog.findViewById<TextView>(R.id.draw_title)
        val descView = dialog.findViewById<TextView>(R.id.draw_description)
        val actionBtn = dialog.findViewById<Button>(R.id.draw_action)
        val closeBtn = dialog.findViewById<ImageButton>(R.id.draw_close)

        // 设置信息
        titleView.text = adData.title ?: ""
        descView.text = adData.description ?: ""
        actionBtn.text = adData.ctaText ?: "查看详情"

        // 初始化播放器
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(adData.videoUrl))
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
        }

        playerView.player = exoPlayer
        playerView.useController = false

        // 点击事件
        actionBtn.setOnClickListener {
            handleAdClick(adData)
        }

        closeBtn.setOnClickListener {
            exoPlayer?.release()
            dialog.dismiss()
            adCallback?.onAdClose()
        }

        // 上滑关闭
        playerView.setOnTouchListener { _, event ->
            // 实现上滑手势检测
            false
        }

        currentDialog = dialog
        dialog.show()
    }

    // 贴片广告渲染
    private fun renderPatch(adData: AdData, container: ViewGroup?) {
        if (container == null) {
            adCallback?.onAdError("Patch container is null")
            return
        }

        val patchView = LayoutInflater.from(context).inflate(
            R.layout.ad_patch_layout, container, false
        )

        val imageView = patchView.findViewById<ImageView>(R.id.patch_image)
        val closeBtn = patchView.findViewById<ImageButton>(R.id.patch_close)

        adData.imageUrl?.let {
            Glide.with(context).load(it).into(imageView)
        }

        patchView.setOnClickListener {
            handleAdClick(adData)
        }

        closeBtn.setOnClickListener {
            container.removeView(patchView)
            adCallback?.onAdClose()
        }

        container.addView(patchView)

        // 自动关闭
        patchView.postDelayed({
            container.removeView(patchView)
            adCallback?.onAdClose()
        }, adData.duration.toLong())
    }

    // 渲染视频内容
    private fun renderVideoContent(videoUrl: String, container: FrameLayout) {
        val playerView = PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            useController = false
        }

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }

        playerView.player = exoPlayer
        container.addView(playerView)
    }

    // 处理广告点击
    private fun handleAdClick(adData: AdData) {
        // 追踪点击
        adData.clickTrackers?.forEach { url ->
            AdTracker.trackClick(url)
        }

        // 处理深度链接
        if (!adData.deepLink.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, adData.deepLink.toUri())
                context.startActivity(intent)
                adCallback?.onAdClick()
                return
            } catch (e: Exception) {
                Log.e("AdRenderer", "Failed to open deep link", e)
            }
        }

        // 处理普通链接
        try {
            val intent = Intent(Intent.ACTION_VIEW, adData.clickUrl.toUri())
            context.startActivity(intent)
            adCallback?.onAdClick()
        } catch (e: Exception) {
            Log.e("AdRenderer", "Failed to open click URL", e)
            adCallback?.onAdError("Failed to open link")
        }
    }

    // 倒计时辅助方法
    private fun startCountdown(
        duration: Int,
        textView: TextView?,
        onFinish: () -> Unit
    ) {
        object : CountDownTimer(duration.toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                textView?.text = "${seconds}s"
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    // 关闭开屏广告
    private fun closeSplash(splashView: View) {
        val parent = splashView.parent as? ViewGroup
        parent?.removeView(splashView)
        adCallback?.onAdClose()
    }

    // 释放资源
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        currentDialog?.dismiss()
        currentDialog = null
    }
}