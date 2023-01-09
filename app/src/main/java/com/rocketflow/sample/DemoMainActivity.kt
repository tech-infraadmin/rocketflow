package com.rocketflow.sample

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
        //this.preferencesHelper.setAccessId("2YwC80gKsM");
        //this.preferencesHelper.setLoginToken("abb55484-3d4f-4335-aa01-cd0320208dc9");
        RocketFlyer.initialize("2ec9d9ae-cd7b-4ed2-806c-2786cbd2c07f") // SDK init token
        //RocketFlyer.initialize("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9tb2JpbGVwaG9uZSI6Ijk3NzM3ODI0MTQiLCJ1bmlxdWVfbmFtZSI6WyJBU0hVIiwiMTA4OTEiLCIxIl0sImVtYWlsIjoiIiwicm9sZSI6IjYiLCJuYmYiOjE2NzI4OTkzOTQsImV4cCI6MTY3Mjk0MjU5NCwiaWF0IjoxNjcyODk5Mzk0fQ.xi7nAEsCxFLqOEbaF_2UDecT0CfodlWxExrgcrsa3MM") // SDK init token
    }

    fun start(view: View) {
        //RocketFlyer.start("05bc0d97-2119-43a9-8b63-7aa1fa51f3e7") //ProcessId - 2.0.4
        RocketFlyer.start("35a7b1f2-839a-4446-958a-cd2c218f6174",true) //ProcessId - 2.0.5
    }

    fun terminate(view: View) {
        RocketFlyer.terminate() // terminate
    }
}