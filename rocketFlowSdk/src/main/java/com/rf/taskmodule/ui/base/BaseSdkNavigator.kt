package com.rf.taskmodule.ui.base

import androidx.annotation.NonNull
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback

/**
 * Created by rahul on 15/11/18
 */
interface BaseSdkNavigator {
    fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun showTimeOutMessage(@NonNull callback: ApiCallback)
}