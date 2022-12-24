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
    fun handleUpdateResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun pointOfContactClicked()
    fun checkBuddyResponse(callback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
    fun checkFleetResponse(callback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileApiResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileDisposeApiResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleRegionListResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleStateListResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleCityListResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun handleHubListResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?, isStart:Boolean)
    fun getSlotDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleMyPlaceResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
}