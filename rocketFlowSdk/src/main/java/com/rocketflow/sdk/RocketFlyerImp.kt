package com.rocketflow.sdk

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.rocketflow.sdk.util.RFLog
import com.tracki.ui.tasklisting.TaskActivity
import com.tracki.utils.AppConstants
import java.lang.ref.WeakReference

internal class RocketFlyerImp(
    context: Context
) : IRocketFlyer {
    private val contextRef = WeakReference(context.applicationContext)
    override fun initialize(sdkInitToken: String) {

        //https://uat.rocketflyer.in/rfapi/sdk/token/exchange
        //send init token in  X-SDK-INIT-TOKEN header
//        {
//            "successful": true,
//            "responseMsg": "Success",
//            "responseCode": 200,
//            "accessId": "2YwC80gKsM",
//            "token": "92c509f4-d87f-4ccc-83e1-72c424e5f918"
//        }

        contextRef.get()?.let {
            if (sdkInitToken.isEmpty()) throw Exception("Token cannot be null")
            RFLog.d("Token value : $sdkInitToken")
            showToast(it,"Initialized : Token value : $sdkInitToken");
        }
    }

    override fun start(processId: String, taskId: String?) {
        contextRef.get()?.let {
            if (processId.isEmpty()) throw Exception("ProcessId cannot be null")
            RFLog.d("ProcessId : $processId")
            //showToast(it,"Started : ProcessId : $processId");

            val intent = TaskActivity.newIntent(it)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra(AppConstants.Extra.TITLE, "RocketFlow Demo")
            intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, processId)
            it.startActivity(intent);
//            intent.putExtra(
//                AppConstants.Extra.EXTRA_CATEGORIES,
//                Gson().toJson(response)
//            )
//            if (categoryName != null) intent.putExtra(AppConstants.Extra.TITLE, categoryName)
//            intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, response.getStageId())
//            intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate)
//            intent.putExtra(AppConstants.Extra.FROM_TO, toDate)
//            intent.putExtra(AppConstants.Extra.LOAD_BY, LOADBY)
//            intent.putExtra(AppConstants.Extra.GEO_FILTER, userGeoReq)
            //ActivitCocontextRef.get().startActivityForResult(intent)

        }
    }

    override fun terminate() {
        contextRef.get()?.let {
            RFLog.d("Terminate")
            showToast(it,"Terminate");
        }
    }


    private fun showToast(context: Context, str : String){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show()
    }

}