package com.rf.taskmodule.ui.taskdetails

import android.location.Location
import com.rf.taskmodule.ui.base.BaseSdkNavigator

/**
 * Created by rahul on 16/10/18
 */
interface TaskDetailNavigator : BaseSdkNavigator {
    fun showEventDialog(showDialog: Boolean, eventName: String)
    fun onLocationChange(location: Location?)
    fun isolateEventResponse(osObject: ArrayList<Any?>, ostObject: ArrayList<Any?>,
                             hcObject: ArrayList<Any?>, hbObject: ArrayList<Any?>,
                             haObject: ArrayList<Any?>)
    fun allEventList(list:ArrayList<AlertEvent>)

    fun onCallClick()
}