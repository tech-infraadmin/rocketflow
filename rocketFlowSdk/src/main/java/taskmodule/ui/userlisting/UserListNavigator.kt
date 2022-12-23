package taskmodule.ui.userlisting

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface UserListNavigator  : BaseSdkNavigator {

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
     fun handleDeleteResponse(callback: ApiCallback, result: Any?, error: APIError?)

}