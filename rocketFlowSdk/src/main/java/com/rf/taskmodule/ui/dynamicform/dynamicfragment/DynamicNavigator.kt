package com.rf.taskmodule.ui.dynamicform.dynamicfragment

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by Rahul Abrol on 13/7/19.
 */
interface DynamicNavigator : BaseSdkNavigator {

    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?) {

    }

    fun handleExecutiveMapResponse(position: Int, callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

   fun  handleGetTaskDataResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

   fun  handleGetPreviousFormResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

    fun  handleUploadFileResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

}