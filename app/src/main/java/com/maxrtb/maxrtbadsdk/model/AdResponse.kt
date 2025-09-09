package com.maxrtb.maxrtbadsdk.model

import com.google.gson.annotations.SerializedName

data class AdResponse(
    @SerializedName("ad")
    val ad: AdData? = null,
    @SerializedName("error")
    val error: ErrorInfo? = null
)

data class AdData(
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("videoUrl")
    val videoUrl: String? = null,
    @SerializedName("clickUrl")
    val clickUrl: String,
    @SerializedName("duration")
    val duration: Int = 5000,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("deepLink")
    val deepLink: String? = null,
    @SerializedName("downloadUrl")
    val downloadUrl: String? = null,
    @SerializedName("appName")
    val appName: String? = null,
    @SerializedName("appIcon")
    val appIcon: String? = null,
    @SerializedName("ctaText")
    val ctaText: String? = null,
    @SerializedName("rating")
    val rating: Float? = null,
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("impressionUrls")
    val impressionUrls: List<String>? = null,
    @SerializedName("clickTrackers")
    val clickTrackers: List<String>? = null
)

data class ErrorInfo(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)
