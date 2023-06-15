package com.rf.taskmodule.ui.facility

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator
import com.rf.taskmodule.utils.NextScreen

interface ServiceNavigator : BaseSdkNavigator {

    fun handleUpdateServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?)

    fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?)

    fun handleGetSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

    fun handleUpdateSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?)

}