package com.rf.taskmodule.ui.dynamicform

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by rahul on 20/3/19
 */
interface DynamicFormNavigator : BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun upLoadFileDisposeApiResponse(callback: ApiCallback, result: Any?, error: APIError?)
//    fun handleFormListResponse(apiCallback: ApiCallback, result: Any?, error: APIError?)
}