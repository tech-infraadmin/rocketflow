package com.rf.taskmodule.ui.userlisting

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface UserListNavigator  : BaseSdkNavigator {

    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
     fun handleDeleteResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)

}