package com.rf.taskmodule.ui.newcreatetask

import android.view.View
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface NewCreateTaskNavigator :BaseSdkNavigator {
    fun onAssignNowClicked()
    fun selectDateTime(v: View)
    fun openMainActivity(taskId:String)
    fun openPlaceAutoComplete(view: View)
    fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun pointOfContactClicked()
    fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    fun checkFleetResponse(callback: ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleTaskResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleTDResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileDisposeApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleRegionListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleStateListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleCityListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?)
}