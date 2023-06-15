package com.rf.taskmodule.ui.availabilitycalender

import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface AvailabilityCalenderNavigator : BaseSdkNavigator{
    fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?)

}