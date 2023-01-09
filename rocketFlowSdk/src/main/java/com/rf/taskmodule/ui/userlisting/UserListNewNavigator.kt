package com.rf.taskmodule.ui.userlisting

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface UserListNewNavigator  : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)

}