package com.rocketflow.sample

import android.app.Application
import com.rocketflow.sdk.RocketFlyer
import com.tracki.TrackiApplication
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector

class DemoApplication : TrackiApplication(), HasActivityInjector,
    HasServiceInjector, HasBroadcastReceiverInjector {
    override fun onCreate() {
        super.onCreate()
        RocketFlyer.initializeRocketFlyer(this)
    }
}