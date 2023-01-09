package com.rf.taskmodule.ui.newcreatetask

import com.rf.taskmodule.data.model.response.config.ClientData
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface SuggestionListNavigator :BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun returnList(callback: ApiCallback, result: Any?, error: APIError?):List<ClientData>?
}