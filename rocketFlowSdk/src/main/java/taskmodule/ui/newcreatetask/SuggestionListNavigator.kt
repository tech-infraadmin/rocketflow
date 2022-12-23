package taskmodule.ui.newcreatetask

import taskmodule.data.model.response.config.ClientData
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface SuggestionListNavigator :BaseSdkNavigator {
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun returnList(callback: ApiCallback, result: Any?, error: APIError?):List<ClientData>?
}