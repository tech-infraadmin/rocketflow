package com.rocketflow.sdk

import android.content.Context
import androidx.annotation.Keep
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.HttpManager
import com.tracki.data.network.NetworkManager

@Keep
class RocketFlyer private constructor(){

    @Keep
    companion object {
        private val rocketFlyerInst = RocketFlyer()

        /**
         *  Call this method in onCreate method of Application class
         */
        @JvmStatic
        fun initializeRocketFlyer(context: Context) {
            RocketFlyerBuilder.initialize(context)
        }

        /**
         *  Get RocketFlyer SDK Instance
         */
        @JvmStatic
        fun getInstance(): RocketFlyer {
            if (!isInitialized()) {
                throw Exception("RocketFlyer is not initialized.")
            }
            return rocketFlyerInst
        }

        /**
         * Checks if RocketFlyer SDK is initialized.
         * */
        @JvmStatic
        fun isInitialized(): Boolean = RocketFlyerBuilder.getRFInstance() != null

        /**
         *  call to initialize
         */
        fun initialize(sdkInitToken: String) {
            RocketFlyerBuilder.getRFInstance()!!.initialize(sdkInitToken)
        }

        /**
         *  call to start
         */
       fun start(processId: String, taskId: String?) {
           RocketFlyerBuilder.getRFInstance()!!.start(processId,taskId)
        }

        /**
         *  call to start without taskId
         */
        fun start(processId: String) {
            RocketFlyerBuilder.getRFInstance()!!.start(processId,null)
        }

        /**
         *  call to terminate
         */
        fun terminate() {
            RocketFlyerBuilder.getRFInstance()!!.terminate()
        }

        fun preferenceHelper() : PreferencesHelper? {
            return RocketFlyerBuilder.getPrefInstance()
        }

        fun httpManager() : HttpManager? {
            return RocketFlyerBuilder.getHttpManagerInstance()
        }

        fun dataManager() : DataManager? {
            return RocketFlyerBuilder.getDataManagerInstance()
        }

        fun networkManager() : NetworkManager? {
            return RocketFlyerBuilder.getNetworkManagerInstance()
        }

    }
}