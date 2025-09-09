package com.maxrtb.maxrtbadsdk.utils

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

object AdTracker {
    fun trackImpression(url: String) {
        track(url, "impression")
    }

    fun trackClick(url: String) {
        track(url, "click")
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun track(url: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("AdTracker", "$type tracked successfully")
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("AdTracker", "Failed to track $type", e)
            }
        }
    }
}