package com.rf.taskmodule.data.network

/**
 * Created by rahul on 11/4/19
 */
interface SyncCallback : com.rf.taskmodule.data.network.ApiCallback {

    override fun onNetworkErrorClose() {}

    override fun onLogout() {}

    override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {}

    override fun hitApi() {}
}