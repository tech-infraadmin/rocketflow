package com.rf.taskmodule.ui.taskdetails.tripdetails

import android.location.Location
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.ui.base.BaseSdkNavigator
import com.rf.taskmodule.ui.taskdetails.AlertEvent

interface TripDetailsNavigator :BaseSdkNavigator{
    fun showEventDialog(showDialog: Boolean, eventName: String)
    fun onLocationChange(location: Location?)
    fun isolateEventResponse(osObject: ArrayList<Any?>, ostObject: ArrayList<Any?>,
                             hcObject: ArrayList<Any?>, hbObject: ArrayList<Any?>,
                             haObject: ArrayList<Any?>)
    fun allEventList(list:ArrayList<AlertEvent>)

    fun onCallClick()
}