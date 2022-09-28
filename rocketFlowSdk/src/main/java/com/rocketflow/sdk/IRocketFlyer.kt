package com.rocketflow.sdk

internal interface  IRocketFlyer {
    fun initialize( sdkInitToken: String)
    fun start(processId: String, taskId : String?)
    fun terminate()
}