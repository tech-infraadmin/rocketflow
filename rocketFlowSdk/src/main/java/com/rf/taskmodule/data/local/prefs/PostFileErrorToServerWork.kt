package com.rf.taskmodule.data.local.prefs

import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rf.taskmodule.data.AppDataManager
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.response.config.ApiErrorRequest
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.data.network.NetworkManagerImpl
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.rx.AppSchedulerProvider

import java.util.HashMap

class PostFileErrorToServerWork(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams),
   PostErrorToServerNavigator {



    lateinit var postErrorViewModel: PostFileErrorToServerViewModel


    lateinit var httpManager: HttpManager

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var dataManager: DataManager
    override fun doWork(): Result {
        preferencesHelper=
            AppPreferencesHelper(
                context,
                AppConstants.PREF_NAME
            )

        dataManager= AppDataManager(
            context,
            null,
            preferencesHelper,
            NetworkManagerImpl(true)
        )
        httpManager = HttpManager.getInstance(context, preferencesHelper)
        postErrorViewModel= PostFileErrorToServerViewModel(dataManager,
            AppSchedulerProvider()
        )

        val request1 = ApiErrorRequest()
        request1.appVersion = "3.0.4"
        try {
            if(inputData.getString("error")!=null)
            {
                val error = inputData.getString("error")
                request1.error = error
            }

        }catch (e:Exception){

        }

        val deviceMeta = HashMap<String, String>()
        try {
            deviceMeta["MANUFACTURER"] = Build.MANUFACTURER
            deviceMeta["MODEL"] = Build.MODEL
            deviceMeta["BOARD"] = Build.BOARD
            deviceMeta["HARDWARE"] = Build.HARDWARE
            deviceMeta["API_LEVEL"] = Build.VERSION.SDK_INT.toString() + ""
            deviceMeta["BUILD_ID"] = Build.ID
            val code = inputData.getInt("code",0)
            deviceMeta["ERROR_CODE"] = code.toString();
            deviceMeta["BUILD_TIME"] = Build.TIME.toString() + ""
            deviceMeta["Version"] = Build.VERSION.RELEASE + ""
            request1.deviceMeta = deviceMeta
        }catch (e:Exception){

        }


        postErrorViewModel.navigator = this
        postErrorViewModel.postFileErrorToServer(httpManager, request1)
        return Result.success()
    }

    override fun showTimeOutMessage(callback: ApiCallback) {
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handlePostErrorResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(apiCallback, error, result, context)) {

        }
    }


}