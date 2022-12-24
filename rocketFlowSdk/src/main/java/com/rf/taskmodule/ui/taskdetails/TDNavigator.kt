package com.rf.taskmodule.ui.taskdetails

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface TDNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleSlotResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleGetTaskDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
    fun handlePaymentUrlResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleVerifyCtaOtpResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleSendCtaOtpResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun getSlotDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleMyPlaceResponse(
        apiCallback: com.rf.taskmodule.data.network.ApiCallback, result: Any?,
        error: APIError?
    )
    fun expandCollapse()
}