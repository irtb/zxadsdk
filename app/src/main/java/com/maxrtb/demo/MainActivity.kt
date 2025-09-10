package com.maxrtb.demo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.maxrtb.zxadsdk.ZxAdSDK
import com.maxrtb.zxadsdk.callback.AdCallback
import com.maxrtb.zxadsdk.callback.AdLoadCallback
import com.maxrtb.zxadsdk.model.AdData
import com.maxrtb.zxadsdk.model.AdType

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val APP_ID = "demo_app_123456" // 替换为实际的APP ID
        private const val PERMISSION_REQUEST_CODE = 1001

        // 测试广告位ID映射
        private val AD_SLOT_IDS = mapOf(
            AdType.BANNER to "banner_slot_001",
            AdType.INTERSTITIAL to "interstitial_slot_001",
            AdType.NATIVE to "native_slot_001",
            AdType.SPLASH to "splash_slot_001",
            AdType.REWARDED_VIDEO to "rewarded_slot_001",
            AdType.FEED to "feed_slot_001",
            AdType.FLOAT to "float_slot_001",
            AdType.FULL_SCREEN_VIDEO to "fullscreen_slot_001",
            AdType.PRE_ROLL to "preroll_slot_001",
            AdType.MID_ROLL to "midroll_slot_001",
            AdType.POST_ROLL to "postroll_slot_001",
            AdType.DRAW_VIDEO to "draw_slot_001",
            AdType.PATCH to "patch_slot_001"
        )
    }

    private lateinit var sdk: ZxAdSDK

    // UI Components
    private lateinit var rootLayout: LinearLayout
    private lateinit var statusTextView: TextView
    private lateinit var adTypeChipGroup: ChipGroup
    private lateinit var loadAdButton: MaterialButton
    private lateinit var showAdButton: MaterialButton
    private lateinit var bannerContainer: FrameLayout
    private lateinit var nativeAdContainer: FrameLayout
    private lateinit var feedAdContainer: FrameLayout
    private lateinit var patchAdContainer: FrameLayout
    private lateinit var logRecyclerView: RecyclerView
    private lateinit var clearLogsButton: Button
    private lateinit var rewardStatusText: TextView
    private lateinit var settingsFab: ExtendedFloatingActionButton

    // State
    private var selectedAdType: AdType = AdType.BANNER
    private var loadedAd: AdData? = null
    private val logAdapter = LogAdapter()
    private var rewardPoints = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContentView())

        initializeSDK()
        setupUI()
        checkPermissions()

        // 显示开屏广告示例（可选）
        if (savedInstanceState == null) {
            showSplashAdExample()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun createContentView(): View {
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // 标题卡片
        val headerCard = CardView(this).apply {
            radius = 12f
            cardElevation = 4f
            setContentPadding(16, 16, 16, 16)
        }

        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        TextView(this).apply {
            text = "ZxAdSDK Demo"
            textSize = 24f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            headerLayout.addView(this)
        }

        statusTextView = TextView(this).apply {
            text = "SDK状态: 初始化中..."
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            headerLayout.addView(this)
        }

        rewardStatusText = TextView(this).apply {
            text = "奖励积分: 0"
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            headerLayout.addView(this)
        }

        headerCard.addView(headerLayout)
        rootLayout.addView(headerCard, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 16
        })

        // 广告类型选择
        TextView(this).apply {
            text = "选择广告类型:"
            textSize = 16f
            setPadding(0, 16, 0, 8)
            rootLayout.addView(this)
        }

        val scrollView = HorizontalScrollView(this)
        adTypeChipGroup = ChipGroup(this).apply {
            isSingleSelection = true
        }
        scrollView.addView(adTypeChipGroup)
        rootLayout.addView(scrollView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 16
        })

        // 操作按钮
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        loadAdButton = MaterialButton(this).apply {
            text = "加载广告"
            isAllCaps = false
            buttonLayout.addView(this, LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginEnd = 8
            })
        }

        showAdButton = MaterialButton(this).apply {
            text = "显示广告"
            isAllCaps = false
            isEnabled = false
            buttonLayout.addView(this, LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginStart = 8
            })
        }

        rootLayout.addView(buttonLayout, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 16
        })

        // 广告容器区域
        TextView(this).apply {
            text = "广告显示区域:"
            textSize = 16f
            setPadding(0, 16, 0, 8)
            rootLayout.addView(this)
        }

        // Banner容器
        bannerContainer = FrameLayout(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            minimumHeight = 150
        }
        rootLayout.addView(bannerContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 8
        })

        // Native容器
        nativeAdContainer = FrameLayout(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            visibility = View.GONE
        }
        rootLayout.addView(nativeAdContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 8
        })

        // Feed容器
        feedAdContainer = FrameLayout(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            visibility = View.GONE
        }
        rootLayout.addView(feedAdContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 8
        })

        // Patch容器
        patchAdContainer = FrameLayout(this).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            visibility = View.GONE
        }
        rootLayout.addView(patchAdContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 16
        })

        // 日志区域
        TextView(this).apply {
            text = "操作日志:"
            textSize = 16f
            setPadding(0, 16, 0, 8)
            rootLayout.addView(this)
        }

        logRecyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = logAdapter
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.background_light))
        }
        rootLayout.addView(logRecyclerView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        clearLogsButton = Button(this).apply {
            text = "清空日志"
            rootLayout.addView(this, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            })
        }

        // FAB设置按钮
        settingsFab = ExtendedFloatingActionButton(this).apply {
            text = "高级设置"
            setOnClickListener {
                showAdvancedSettings()
            }
        }

        val scrollContainer = ScrollView(this)
        scrollContainer.addView(rootLayout)

        return FrameLayout(this).apply {
            addView(scrollContainer)
            addView(settingsFab, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
                marginEnd = 16
                bottomMargin = 16
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeSDK() {
        try {
            sdk = ZxAdSDK.init(this, APP_ID)
            statusTextView.text = "SDK状态: 已初始化"
            addLog("SDK初始化成功", LogType.SUCCESS)
        } catch (e: Exception) {
            statusTextView.text = "SDK状态: 初始化失败"
            addLog("SDK初始化失败: ${e.message}", LogType.ERROR)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        // 添加广告类型选项
        AdType.entries.forEach { adType ->
            val chip = Chip(this).apply {
                text = adType.displayName
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedAdType = adType
                        updateContainerVisibility()
                        addLog("选择广告类型: ${adType.displayName}", LogType.INFO)
                    }
                }
            }
            adTypeChipGroup.addView(chip)

            // 默认选中第一个
            if (adType == AdType.BANNER) {
                chip.isChecked = true
            }
        }

        // 加载广告按钮
        loadAdButton.setOnClickListener {
            loadAd()
        }

        // 显示广告按钮
        showAdButton.setOnClickListener {
            showLoadedAd()
        }

        // 清空日志按钮
        clearLogsButton.setOnClickListener {
            logAdapter.clear()
        }
    }

    private fun updateContainerVisibility() {
        // 根据选择的广告类型显示对应容器
        bannerContainer.visibility = if (selectedAdType == AdType.BANNER) View.VISIBLE else View.GONE
        nativeAdContainer.visibility = if (selectedAdType == AdType.NATIVE) View.VISIBLE else View.GONE
        feedAdContainer.visibility = if (selectedAdType == AdType.FEED) View.VISIBLE else View.GONE
        patchAdContainer.visibility = if (selectedAdType == AdType.PATCH) View.VISIBLE else View.GONE
    }

    private fun loadAd() {
        val adSlotId = AD_SLOT_IDS[selectedAdType] ?: return

        addLog("开始加载 ${selectedAdType.displayName} 广告...", LogType.INFO)
        showAdButton.isEnabled = false
        loadedAd = null

        sdk.loadAd(adSlotId, selectedAdType, object : AdLoadCallback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onAdLoaded(adData: AdData) {
                runOnUiThread {
                    loadedAd = adData
                    showAdButton.isEnabled = true
                    addLog("${selectedAdType.displayName} 广告加载成功", LogType.SUCCESS)

                    // 对于某些类型的广告，直接显示
                    when (selectedAdType) {
                        AdType.BANNER, AdType.NATIVE, AdType.FEED, AdType.PATCH -> {
                            showLoadedAd()
                        }
                        else -> {}
                    }
                }
            }

            override fun onAdLoadFailed(error: String) {
                runOnUiThread {
                    addLog("${selectedAdType.displayName} 广告加载失败: $error", LogType.ERROR)
                    Snackbar.make(rootLayout, "广告加载失败: $error", Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showLoadedAd() {
        if (loadedAd == null) {
            addLog("没有已加载的广告", LogType.WARNING)
            return
        }

        val container = when (selectedAdType) {
            AdType.BANNER -> bannerContainer
            AdType.NATIVE -> nativeAdContainer
            AdType.FEED -> feedAdContainer
            AdType.PATCH -> patchAdContainer
            else -> null
        }

        // 清空容器
        container?.removeAllViews()

        sdk.loadAndShowAd(
            AD_SLOT_IDS[selectedAdType] ?: "",
            selectedAdType,
            container,
            createAdCallback()
        )
    }

    private fun createAdCallback(): AdCallback {
        return object : AdCallback {
            override fun onAdShow() {
                runOnUiThread {
                    addLog("${selectedAdType.displayName} 广告展示", LogType.SUCCESS)
                }
            }

            override fun onAdClick() {
                runOnUiThread {
                    addLog("${selectedAdType.displayName} 广告被点击", LogType.INFO)
                }
            }

            override fun onAdClose() {
                runOnUiThread {
                    addLog("${selectedAdType.displayName} 广告关闭", LogType.INFO)
                    showAdButton.isEnabled = false
                    loadedAd = null
                }
            }

            override fun onAdError(error: String) {
                runOnUiThread {
                    addLog("${selectedAdType.displayName} 广告错误: $error", LogType.ERROR)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onRewardEarned() {
                runOnUiThread {
                    rewardPoints += 10
                    rewardStatusText.text = "奖励积分: $rewardPoints"
                    addLog("获得奖励! 当前积分: $rewardPoints", LogType.SUCCESS)

                    // 显示奖励动画或提示
                    Toast.makeText(
                        this@MainActivity,
                        "恭喜获得10积分奖励！",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSplashAdExample() {
        // 延迟显示，模拟应用启动
        window.decorView.postDelayed({
            sdk.loadAndShowAd(
                AD_SLOT_IDS[AdType.SPLASH] ?: "",
                AdType.SPLASH,
                null,
                object : AdCallback {
                    override fun onAdShow() {
                        addLog("开屏广告展示", LogType.SUCCESS)
                    }

                    override fun onAdClick() {
                        addLog("开屏广告被点击", LogType.INFO)
                    }

                    override fun onAdClose() {
                        addLog("开屏广告关闭", LogType.INFO)
                    }

                    override fun onAdError(error: String) {
                        addLog("开屏广告错误: $error", LogType.ERROR)
                    }
                }
            )
        }, 500)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAdvancedSettings() {
        val options = arrayOf(
            "测试插屏广告",
            "测试激励视频",
            "测试全屏视频",
            "测试Draw视频",
            "测试浮窗广告",
            "测试前贴片广告",
            "测试中贴片广告",
            "测试后贴片广告",
            "查看SDK信息",
            "模拟崩溃测试",
            "性能测试"
        )

        AlertDialog.Builder(this)
            .setTitle("高级功能")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> testInterstitialAd()
                    1 -> testRewardedVideoAd()
                    2 -> testFullScreenVideoAd()
                    3 -> testDrawVideoAd()
                    4 -> testFloatAd()
                    5 -> testPreRollAd()
                    6 -> testMidRollAd()
                    7 -> testPostRollAd()
                    8 -> showSDKInfo()
                    9 -> simulateCrash()
                    10 -> performanceTest()
                }
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testInterstitialAd() {
        addLog("测试插屏广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.INTERSTITIAL] ?: "",
            AdType.INTERSTITIAL,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testRewardedVideoAd() {
        addLog("测试激励视频广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.REWARDED_VIDEO] ?: "",
            AdType.REWARDED_VIDEO,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testFullScreenVideoAd() {
        addLog("测试全屏视频广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.FULL_SCREEN_VIDEO] ?: "",
            AdType.FULL_SCREEN_VIDEO,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testDrawVideoAd() {
        addLog("测试Draw视频广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.DRAW_VIDEO] ?: "",
            AdType.DRAW_VIDEO,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testFloatAd() {
        // 浮窗广告需要特殊权限
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder(this)
                .setTitle("需要权限")
                .setMessage("浮窗广告需要悬浮窗权限")
                .setPositiveButton("去设置") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        "package:$packageName".toUri()
                    )
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("取消", null)
                .show()
        } else {
            addLog("测试浮窗广告", LogType.INFO)
            sdk.loadAndShowAd(
                AD_SLOT_IDS[AdType.FLOAT] ?: "",
                AdType.FLOAT,
                null,
                createAdCallback()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testPreRollAd() {
        addLog("测试前贴片广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.PRE_ROLL] ?: "",
            AdType.PRE_ROLL,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testMidRollAd() {
        addLog("测试中贴片广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.MID_ROLL] ?: "",
            AdType.MID_ROLL,
            null,
            createAdCallback()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testPostRollAd() {
        addLog("测试后贴片广告", LogType.INFO)
        sdk.loadAndShowAd(
            AD_SLOT_IDS[AdType.POST_ROLL] ?: "",
            AdType.POST_ROLL,
            null,
            createAdCallback()
        )
    }

    private fun showSDKInfo() {
        val info = """
            SDK信息:
            - 名称: ZxAdSDK
            - 版本: 1.0.0
            - APP ID: $APP_ID
            - 支持广告类型: ${AdType.entries.size}
            - 设备ID: $deviceId
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("SDK信息")
            .setMessage(info)
            .setPositiveButton("确定", null)
            .show()

        addLog("查看SDK信息", LogType.INFO)
    }

    private fun simulateCrash() {
        AlertDialog.Builder(this)
            .setTitle("警告")
            .setMessage("这将模拟应用崩溃，仅用于测试。确定继续？")
            .setPositiveButton("确定") { _, _ ->
                throw RuntimeException("模拟崩溃测试")
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun performanceTest() {
        addLog("开始性能测试...", LogType.INFO)

        Thread {
            val startTime = System.currentTimeMillis()
            var successCount = 0
            var failCount = 0

            // 测试加载10个广告
            for (i in 1..10) {
                sdk.loadAd(
                    AD_SLOT_IDS[AdType.BANNER] ?: "",
                    AdType.BANNER,
                    object : AdLoadCallback {
                        override fun onAdLoaded(adData: AdData) {
                            successCount++
                        }

                        override fun onAdLoadFailed(error: String) {
                            failCount++
                        }
                    }
                )
                Thread.sleep(100)
            }

            Thread.sleep(3000) // 等待所有请求完成

            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            runOnUiThread {
                val result = """
                    性能测试结果:
                    - 总耗时: ${duration}ms
                    - 成功数: $successCount
                    - 失败数: $failCount
                    - 平均耗时: ${duration / 10}ms
                """.trimIndent()

                AlertDialog.Builder(this)
                    .setTitle("性能测试结果")
                    .setMessage(result)
                    .setPositiveButton("确定", null)
                    .show()

                addLog("性能测试完成", LogType.SUCCESS)
            }
        }.start()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // 浮窗权限需要特殊处理
                addLog("浮窗权限未授予", LogType.WARNING)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                addLog(
                    "权限 $permission: ${if (granted) "已授予" else "被拒绝"}",
                    if (granted) LogType.SUCCESS else LogType.WARNING
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    addLog("浮窗权限已授予", LogType.SUCCESS)
                } else {
                    addLog("浮窗权限被拒绝", LogType.WARNING)
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun getDeviceId(): Int {
        return (Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown") as Int
    }

    private fun addLog(message: String, type: LogType) {
        Log.d(TAG, message)
        logAdapter.addLog(LogEntry(message, type))
        logRecyclerView.scrollToPosition(logAdapter.itemCount - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        sdk.release()
        addLog("SDK资源已释放", LogType.INFO)
    }

    // 日志相关类
    enum class LogType {
        INFO, SUCCESS, WARNING, ERROR
    }

    data class LogEntry(
        val message: String,
        val type: LogType,
        val timestamp: Long = System.currentTimeMillis()
    )

    inner class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
        private val logs = mutableListOf<LogEntry>()

        fun addLog(entry: LogEntry) {
            logs.add(entry)
            notifyItemInserted(logs.size - 1)
        }

        @SuppressLint("NotifyDataSetChanged")
        fun clear() {
            logs.clear()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): LogViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 4, 8, 4)
                }
                setPadding(12, 8, 12, 8)
                textSize = 12f
            }
            return LogViewHolder(textView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            val entry = logs[position]
            val timeStr = android.text.format.DateFormat.format("HH:mm:ss", entry.timestamp)
            holder.textView.text = "[$timeStr] ${entry.message}"

            val color = when (entry.type) {
                LogType.SUCCESS -> android.R.color.holo_green_dark
                LogType.ERROR -> android.R.color.holo_red_dark
                LogType.WARNING -> android.R.color.holo_orange_dark
                LogType.INFO -> android.R.color.black
            }
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.context, color))

            val bgColor = when (entry.type) {
                LogType.SUCCESS -> android.R.color.holo_green_light
                LogType.ERROR -> android.R.color.holo_red_light
                LogType.WARNING -> android.R.color.holo_orange_light
                LogType.INFO -> android.R.color.white
            }
            holder.textView.setBackgroundColor(
                ContextCompat.getColor(holder.textView.context, bgColor).let {
                    android.graphics.Color.argb(50,
                        android.graphics.Color.red(it),
                        android.graphics.Color.green(it),
                        android.graphics.Color.blue(it)
                    )
                }
            )
        }

        override fun getItemCount() = logs.size

        inner class LogViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }
}