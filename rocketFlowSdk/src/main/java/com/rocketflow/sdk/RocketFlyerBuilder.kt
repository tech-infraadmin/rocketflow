package com.rocketflow.sdk

import android.content.Context
import com.google.gson.GsonBuilder
import com.rocketflow.sdk.util.RFLog

internal object  RocketFlyerBuilder {

    private var iRocketFlyer: IRocketFlyer? = null
    private var preferencesHelper: taskmodule.data.local.prefs.PreferencesHelper? = null
    private var httpManager: taskmodule.data.network.HttpManager? = null
    private var dataManager: taskmodule.data.DataManager? = null
    private var networkManager: taskmodule.data.network.NetworkManager? = null

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
                taskmodule.data.local.prefs.AppPreferencesHelper(
                    context,
                    taskmodule.utils.AppConstants.PREF_NAME
                )
        }

        if(httpManager ==null){
            httpManager = taskmodule.data.network.HttpManager(
                context,
                preferencesHelper
            )
        }

        if(networkManager ==null){
            networkManager =
                taskmodule.data.network.NetworkManagerImpl(testServer)
        }

        if(dataManager ==null){
            dataManager = taskmodule.data.AppDataManager(
                context,
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(), preferencesHelper,
                networkManager
            )
        }
    }

    fun getRFInstance(): IRocketFlyer? = iRocketFlyer
    fun getPrefInstance(): taskmodule.data.local.prefs.PreferencesHelper? = preferencesHelper
    fun getHttpManagerInstance(): taskmodule.data.network.HttpManager? = httpManager
    fun getNetworkManagerInstance(): taskmodule.data.network.NetworkManager? = networkManager
    fun getDataManagerInstance(): taskmodule.data.DataManager? = dataManager

}