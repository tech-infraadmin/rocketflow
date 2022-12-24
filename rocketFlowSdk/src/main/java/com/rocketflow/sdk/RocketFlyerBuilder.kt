package com.rocketflow.sdk

import android.content.Context
import com.google.gson.GsonBuilder
import com.rocketflow.sdk.util.RFLog

internal object  RocketFlyerBuilder {

    private var iRocketFlyer: IRocketFlyer? = null
    private var preferencesHelper: com.rf.taskmodule.data.local.prefs.PreferencesHelper? = null
    private var httpManager: com.rf.taskmodule.data.network.HttpManager? = null
    private var dataManager: com.rf.taskmodule.data.DataManager? = null
    private var networkManager: com.rf.taskmodule.data.network.NetworkManager? = null

    fun initialize(context: Context,testServer: Boolean) {
        if (RocketFlyer.isInitialized()) {
            RFLog.d("RF Already initialized")
            return
        }

        if(iRocketFlyer ==null){
            iRocketFlyer = RocketFlyerImp(context)
        }

        if(preferencesHelper ==null){
            preferencesHelper =
                com.rf.taskmodule.data.local.prefs.AppPreferencesHelper(
                    context,
                    com.rf.taskmodule.utils.AppConstants.PREF_NAME
                )
        }

        if(httpManager ==null){
            httpManager = com.rf.taskmodule.data.network.HttpManager(
                context,
                preferencesHelper
            )
        }

        if(networkManager ==null){
            networkManager =
                com.rf.taskmodule.data.network.NetworkManagerImpl(testServer)
        }

        if(dataManager ==null){
            dataManager = com.rf.taskmodule.data.AppDataManager(
                context,
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(), preferencesHelper,
                networkManager
            )
        }
    }

    fun getRFInstance(): IRocketFlyer? = iRocketFlyer
    fun getPrefInstance(): com.rf.taskmodule.data.local.prefs.PreferencesHelper? = preferencesHelper
    fun getHttpManagerInstance(): com.rf.taskmodule.data.network.HttpManager? = httpManager
    fun getNetworkManagerInstance(): com.rf.taskmodule.data.network.NetworkManager? = networkManager
    fun getDataManagerInstance(): com.rf.taskmodule.data.DataManager? = dataManager

}