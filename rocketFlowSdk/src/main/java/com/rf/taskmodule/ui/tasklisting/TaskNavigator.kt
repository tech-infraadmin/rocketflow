package com.rf.taskmodule.ui.tasklisting

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by rahul on 8/10/18
 */
interface TaskNavigator : BaseSdkNavigator {
    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?) {

    }

    fun checkBuddyResponse(callback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
    fun checkFleetResponse(callback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
    fun checkInventory(callback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?)
}