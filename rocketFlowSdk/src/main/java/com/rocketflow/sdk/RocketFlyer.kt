package com.rocketflow.sdk

import android.content.Context
import androidx.annotation.Keep
import taskmodule.data.DataManager
import taskmodule.data.local.prefs.PreferencesHelper
import taskmodule.data.network.HttpManager
import taskmodule.data.network.NetworkManager
import taskmodule.utils.CommonUtils
import com.trackthat.lib.TrackThat
import taskmodule.TrackiSdkApplication

@Keep
class RocketFlyer private constructor(){

    @Keep
    companion object {
        private val rocketFlyerInst = RocketFlyer()

        /**
         *  Call this method in onCreate method of Application class
         */
        @JvmStatic
        fun initializeRocketFlyer(context: Context,stageServer: Boolean = false) {
            TrackThat.initialize(context, CommonUtils.getIMEINumber(context))
            RocketFlyerBuilder.initialize(context,stageServer)
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

        /**
         *  call to start without taskId
         */

        @JvmStatic
        fun start(processId: String, startActivity : Boolean) {
            RocketFlyerBuilder.getRFInstance()!!.start(processId,startActivity)
        }

    }
}