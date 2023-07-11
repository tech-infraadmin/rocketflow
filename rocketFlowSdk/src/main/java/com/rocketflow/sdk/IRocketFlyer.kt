package com.rocketflow.sdk

import android.content.Context

internal interface  IRocketFlyer {
    fun initialize(sdkInitToken: String, context: Context)
    fun start(processId: String, startActivity : Boolean)
    fun copy(processId: String, startActivity : Boolean)
    fun terminate()
    fun initializeAndStart(sdkInitToken: String, context: Context)
}