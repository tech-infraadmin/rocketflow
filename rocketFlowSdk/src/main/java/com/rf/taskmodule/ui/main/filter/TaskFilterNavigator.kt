package com.rf.taskmodule.ui.main.filter

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator


/**
 * Created by Vikas Kesharvani on 04/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TaskFilterNavigator: BaseSdkNavigator {

    fun handleRegionListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleStateListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleCityListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun submitFilter()
    fun clearFilter()
}