package com.maxrtb.zxadsdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.util.*

object DeviceUtil {
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: UUID.randomUUID().toString()
        } catch (_: Exception) {
            UUID.randomUUID().toString()
        }
    }
}