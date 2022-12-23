package taskmodule.ui.base

import androidx.annotation.NonNull
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback

/**
 * Created by rahul on 15/11/18
 */
interface BaseSdkNavigator {
    fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun showTimeOutMessage(@NonNull callback: ApiCallback)
}