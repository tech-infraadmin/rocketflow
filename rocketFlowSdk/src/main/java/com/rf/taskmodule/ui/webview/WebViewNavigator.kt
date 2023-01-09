package com.rf.taskmodule.ui.webview

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by rahul on 23/10/18
 */
interface WebViewNavigator : BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}