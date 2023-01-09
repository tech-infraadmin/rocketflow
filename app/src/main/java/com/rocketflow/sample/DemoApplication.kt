package com.rocketflow.sample

import android.app.Application
import com.rocketflow.sdk.RocketFlyer

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RocketFlyer.initializeRocketFlyer(this,true)
    }
}