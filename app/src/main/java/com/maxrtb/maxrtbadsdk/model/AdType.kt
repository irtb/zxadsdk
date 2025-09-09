package com.maxrtb.maxrtbadsdk.model

enum class AdType(val tagId: String, val displayName: String) {
    // 基础广告类型
    BANNER("1000000010", "banner"),           // 横幅广告
    INTERSTITIAL("1000000011", "interstitial"), // 插屏广告
    NATIVE("1000000012", "native"),           // 原生广告

    // 中国国内常见广告类型
    SPLASH("1000000013", "splash"),           // 开屏广告
    REWARDED_VIDEO("1000000014", "rewarded"), // 激励视频广告
    FEED("1000000015", "feed"),               // 信息流广告
    FLOAT("1000000016", "float"),             // 浮窗广告
    FULL_SCREEN_VIDEO("1000000017", "fullscreen"), // 全屏视频广告
    PRE_ROLL("1000000018", "preroll"),        // 前贴片广告
    MID_ROLL("1000000019", "midroll"),        // 中贴片广告
    POST_ROLL("1000000020", "postroll"),      // 后贴片广告
    DRAW_VIDEO("1000000021", "draw"),         // Draw视频广告（类似抖音）
    PATCH("1000000022", "patch")              // 贴片广告
}