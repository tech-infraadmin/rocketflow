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
       // RocketFlyer.initialize("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9tb2JpbGVwaG9uZSI6IjcwMjY2MjIyODciLCJ1bmlxdWVfbmFtZSI6WyJBLkRJTkFLQVJBTiIsIjEzMTU2IiwiMiJdLCJlbWFpbCI6IiIsInJvbGUiOiI2IiwibmJmIjoxNjg3NzYyMzE1LCJleHAiOjE2ODc4MDU1MTUsImlhdCI6MTY4Nzc2MjMxNX0.yR3BhPijdPFymoDtXEUDFTb9dajW3gMjwc3f0-DG3u0", this) // SDK init token
        RocketFlyer.initialize("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9tb2JpbGVwaG9uZSI6Ijg4NTE1MjE2NzUiLCJ1bmlxdWVfbmFtZSI6WyJBU0hBIEJSQU5DSCBUV08iLCIxMDg5MSIsIjEiXSwiZW1haWwiOiIiLCJyb2xlIjoiNiIsIm5iZiI6MTY4NzE1ODQ0NCwiZXhwIjoxNjg3MjAxNjQ0LCJpYXQiOjE2ODcxNTg0NDR9.cJbqsasztVPF4vDrEVTk-x1aqMAh_h8unQneudk4d0s", this) // SDK init token
    }

    fun start(view: View) {
        RocketFlyer.start("35a7b1f2-839a-4446-958a-cd2c218f6174",true) //ProcessId - 2.0.5
    }

    fun terminate(view: View) {
        RocketFlyer.terminate() // terminate
    }
}