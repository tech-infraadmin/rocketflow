package taskmodule.ui.webview

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by rahul on 23/10/18
 */
interface WebViewNavigator : BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }
}