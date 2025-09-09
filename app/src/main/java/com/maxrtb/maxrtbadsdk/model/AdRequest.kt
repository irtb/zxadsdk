package com.maxrtb.maxrtbadsdk.model

import com.google.gson.annotations.SerializedName

data class AdRequest(
    @SerializedName("app_id")
    val appId: String,
    @SerializedName("ad_slot_id")
    val adSlotId: String,
    @SerializedName("ad_type")
    val adType: String,
    @SerializedName("device_info")
    val deviceInfo: DeviceInfo,
    @SerializedName("user_info")
    val userInfo: UserInfo? = null
)

data class DeviceInfo(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("os")
    val os: String = "android",
    @SerializedName("os_version")
    val osVersion: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("screen_width")
    val screenWidth: Int,
    @SerializedName("screen_height")
    val screenHeight: Int,
    @SerializedName("density")
    val density: Float,
    @SerializedName("network")
    val network: String,
    @SerializedName("carrier")
    val carrier: String? = null,
    @SerializedName("ip")
    val ip: String? = null,
    @SerializedName("language")
    val language: String,
    @SerializedName("timezone")
    val timezone: String
)

data class UserInfo(
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("interests")
    val interests: List<String>? = null
)