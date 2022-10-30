package com.rf.rocketflowdemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rocketflow.sdk.RocketFlyer

class DemoMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun initialize(view: View) {
        RocketFlyer.initialize("7aa1fa51f3e7") // SDK init token
    }

    fun start(view: View) {
        RocketFlyer.start("05bc0d97-2119-43a9-8b63-7aa1fa51f3e7") //ProcessId UAT
        //RocketFlyer.start("35a7b1f2-839a-4446-958a-cd2c218f6174") //ProcessId PROD
    }

    fun terminate(view: View) {
        RocketFlyer.terminate() // terminate
    }
}