package com.rf.taskmodule.ui.dynamicform.dynamicfragment

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by Rahul Abrol on 13/7/19.
 */
interface DynamicNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    fun handleExecutiveMapResponse(position: Int, callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?)

   fun  handleGetPreviousFormResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun  handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?)

}