package com.rocketflow.sdk

internal interface  IRocketFlyer {
    fun initialize( sdkInitToken: String)
    fun start(processId: String, startActivity : Boolean)
    fun copy(processId: String, startActivity : Boolean)
    fun terminate()
}