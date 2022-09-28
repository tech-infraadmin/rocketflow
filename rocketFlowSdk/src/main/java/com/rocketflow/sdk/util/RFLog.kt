package com.rocketflow.sdk.util

import android.util.Log
import androidx.annotation.Keep

@Keep
object RFLog {
    private const val TAG = "RF"

    @JvmStatic
    @JvmOverloads
    fun d(message: String?) {
        Log.d(TAG, message ?: "")
    }
}