package com.rocketflow.sdk

import android.content.Context
import com.google.gson.GsonBuilder
import com.rocketflow.sdk.util.RFLog
import com.tracki.data.AppDataManager
import com.tracki.data.DataManager
import com.tracki.data.local.prefs.AppPreferencesHelper
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.HttpManager
import com.tracki.data.network.NetworkManager
import com.tracki.data.network.NetworkManagerImpl
import com.tracki.utils.AppConstants

internal object  RocketFlyerBuilder {

    private var iRocketFlyer: IRocketFlyer? = null
    private var preferencesHelper: PreferencesHelper? = null
    private var httpManager: HttpManager? = null
    private var dataManager: DataManager? = null
    private var networkManager: NetworkManager? = null

    fun initialize(context: Context,testServer: Boolean) {
        if (RocketFlyer.isInitialized()) {
            RFLog.d("RF Already initialized")
            return
        }

        if(iRocketFlyer ==null){
            iRocketFlyer = RocketFlyerImp(context)
        }

        if(preferencesHelper ==null){
            preferencesHelper = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        }

        if(httpManager ==null){
            httpManager = HttpManager(context, preferencesHelper)
        }

        if(networkManager ==null){
            networkManager = NetworkManagerImpl(testServer)
        }

        if(dataManager ==null){
            dataManager = AppDataManager(context,
                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(), preferencesHelper,
                networkManager)
        }
    }

    fun getRFInstance(): IRocketFlyer? = iRocketFlyer
    fun getPrefInstance(): PreferencesHelper? = preferencesHelper
    fun getHttpManagerInstance(): HttpManager? = httpManager
    fun getNetworkManagerInstance(): NetworkManager? = networkManager
    fun getDataManagerInstance(): DataManager? = dataManager

}