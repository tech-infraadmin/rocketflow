package com.rf.taskmodule.data.local.prefs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.rf.taskmodule.data.model.response.config.TaskListing
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedToMeViewModel
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeNavigator
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log
import com.trackthat.lib.TrackThat
import javax.inject.Inject


/**
 * Created by Vikas Kesharvani on 15/12/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class ApplyReminderWork(var context: Context, workerParams: WorkerParameters) : Worker(context, workerParams),
    com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeNavigator {

    @Inject
    lateinit var assignedToMeViewModel: com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedToMeViewModel

    @Inject
    lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager

    @Inject
    lateinit var preferencesHelper: com.rf.taskmodule.data.local.prefs.PreferencesHelper

    override fun doWork(): Result {
        preferencesHelper!!.idleTripActive = true
        TrackThat.startTracking(preferencesHelper!!.punchId, false)
        com.rf.taskmodule.utils.Log.e("StartIdealTrackWork=>", "startIdealTrack=>")
        com.rf.taskmodule.utils.Log.e("idleTripActive=>", preferencesHelper!!.idleTripActive.toString())
        assignedToMeViewModel.navigator=this
//        var api = TrackiApplication.getApiMap()[ApiType.TASKS]
//        if (api != null) {
//            api.appendWithKey = "ASSIGNED_TO_ME"
//        }
        // assignedToMeViewModel.getTaskList(httpManager, api, buddyRequest)
        return Result.success()
    }

    override fun showTimeOutMessage(callback: com.rf.taskmodule.data.network.ApiCallback) {
    }

    override fun handleResponse(callback: com.rf.taskmodule.data.network.ApiCallback, result: Any?, error: APIError?) {
        if (com.rf.taskmodule.utils.CommonUtils.handleResponse(callback, error, result, context)) {
            val taskListing = Gson().fromJson(result.toString(), TaskListing::class.java)
            if (taskListing.tasks != null && taskListing.tasks!!.isNotEmpty())
                for (task in taskListing.tasks!!) {
                    var map = preferencesHelper.eventIdMap
                    if (map != null && map.isNotEmpty()) {
                        if (map.containsKey(task.taskId)) {
                            var eventId = map[task.taskId]
                            com.rf.taskmodule.utils.CommonUtils.deleteEventUri(context, eventId, task.taskId, preferencesHelper)
                        }

                    }
                    if(task.startTime>0){
                        var titile=""
                        var desc=""
                        var place=""
                        var startDate=task.startTime
                        var needReminder=true
                        var needMainSerVice=false
                        var taskId=task.taskId
                        var timerBefore=5
                        if(task.taskName!=null)
                            titile=task.taskName!!
                        if(task.description!=null)
                            desc=task.description!!
                        if(task.source!=null&&task.source!!.address!=null)
                            place=task.source!!.address!!
                    }

                }

        }
    }

    override fun handleExecuteUpdateResponse(apiCallback: com.rf.taskmodule.data.network.ApiCallback?, result: Any?, error: APIError?) {
    }


}