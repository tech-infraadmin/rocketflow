package taskmodule.ui.addplace

import android.view.View
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.ui.base.BaseSdkNavigator

interface AddPlaceNavigator:BaseSdkNavigator {
    fun openMainActivity()
    fun openPlaceAutoComplete(view: View)
    fun addLocation(view: View)
    fun handleAddPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?)
    fun handleStateCityResponse(callback: ApiCallback, result: Any?, error: APIError?)
}