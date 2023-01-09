package com.rf.taskmodule.ui.taskdetails

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface NewTaskDetailNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun expandCollapse()
}