package com.rocketflow.sdk

import android.content.Context
import androidx.annotation.Keep
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.data.network.NetworkManager
import com.rf.taskmodule.utils.CommonUtils
import com.trackthat.lib.TrackThat
import com.rf.taskmodule.TrackiSdkApplication

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
            TrackThat.initialize(context, com.rf.taskmodule.utils.CommonUtils.getIMEINumber(context))
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

        fun preferenceHelper() : com.rf.taskmodule.data.local.prefs.PreferencesHelper? {
            return RocketFlyerBuilder.getPrefInstance()
        }

        fun httpManager() : com.rf.taskmodule.data.network.HttpManager? {
            return RocketFlyerBuilder.getHttpManagerInstance()
        }

        fun dataManager() : com.rf.taskmodule.data.DataManager? {
            return RocketFlyerBuilder.getDataManagerInstance()
        }

        fun networkManager() : com.rf.taskmodule.data.network.NetworkManager? {
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