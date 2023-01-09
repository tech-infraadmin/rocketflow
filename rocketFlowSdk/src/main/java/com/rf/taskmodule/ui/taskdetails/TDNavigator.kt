package com.rf.taskmodule.ui.taskdetails

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface TDNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSlotResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun handlePaymentUrlResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleVerifyCtaOtpResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleSendCtaOtpResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleMyPlaceResponse(
        apiCallback: ApiCallback, result: Any?,
        error: APIError?
    )
    fun expandCollapse()
}