package com.rf.taskmodule.ui.addplace

import android.view.View
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

interface AddPlaceNavigator:BaseSdkNavigator {
    fun openMainActivity()
    fun openPlaceAutoComplete(view: View)
    fun addLocation(view: View)
    fun handleAddPlaceResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
    fun handleStateCityResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?)
}