package com.rocketflow.sdk

import android.content.Context
import com.rocketflow.sdk.util.RFLog

internal object  RocketFlyerBuilder {

    private var iRocketFlyer: IRocketFlyer? = null

    fun initialize(context: Context) {
        if (RocketFlyer.isInitialized()) {
            RFLog.d("RF Already initialized")
            return
        }

        if(iRocketFlyer ==null){
            iRocketFlyer = RocketFlyerImp(context)
        }
    }

    fun getRFInstance(): IRocketFlyer? = iRocketFlyer

}