package taskmodule.ui.taskdetails

import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface NewTaskDetailNavigator :BaseSdkNavigator{
    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleExecuteUpdateResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?)
    fun expandCollapse()
}