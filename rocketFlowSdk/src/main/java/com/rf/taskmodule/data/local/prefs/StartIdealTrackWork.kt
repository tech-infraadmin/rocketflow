package com.rf.taskmodule.data.local.prefs

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.Log
import com.trackthat.lib.TrackThat

/**
 * Created by Vikas Kesharvani on 23/07/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
internal class StartIdealTrackWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var preferencesHelper: com.rf.taskmodule.data.local.prefs.PreferencesHelper?=null
    init {
        preferencesHelper=
            com.rf.taskmodule.data.local.prefs.AppPreferencesHelper(
                context,
                com.rf.taskmodule.utils.AppConstants.PREF_NAME
            )
       // Toast.makeText(context,"Data",Toast.LENGTH_SHORT).show()
    }
    override fun doWork(): Result {
        preferencesHelper!!.idleTripActive = true
        TrackThat.startTracking(preferencesHelper!!.punchId, false)
        com.rf.taskmodule.utils.Log.e("StartIdealTrackWork=>","startIdealTrack=>")
        com.rf.taskmodule.utils.Log.e("idleTripActive=>",preferencesHelper!!.idleTripActive.toString())
        return Result.success()
    }
}