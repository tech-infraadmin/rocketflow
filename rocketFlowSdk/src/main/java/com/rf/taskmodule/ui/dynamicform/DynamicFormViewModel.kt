package com.rf.taskmodule.ui.dynamicform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.DynamicFormMainData
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider
import java.io.File

/**
 * Created by rahul on 20/3/19
 */
class DynamicFormViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<DynamicFormNavigator>(dataManager, schedulerProvider) {

    /*  private var httpManager: HttpManager? = null
      private var api: Api? = null

      fun onProceedClick() {
          navigator.onSubmitClick()
      }*/

    private var httpManager: com.rf.taskmodule.data.network.HttpManager? = null
    private var api: Api? = null


    fun uploadTaskData(dynamicFormMainData: DynamicFormMainData, httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api?) {
        this.httpManager = httpManager
        this.api = api
        UploadFormList(dynamicFormMainData).hitApi()
    }


    inner class UploadFormList(val dynamicFormMainData: DynamicFormMainData) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(navigator!=null)
            navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if(dataManager!=null)
            dataManager.uploadFormList(this@UploadFormList, httpManager, dynamicFormMainData, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if(navigator!=null)
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }

    }

    fun uploadFileList(hashMap: HashMap<String, ArrayList<File>>, httpManager: com.rf.taskmodule.data.network.HttpManager, api: Api?, isApi:Boolean) {
        this.httpManager = httpManager
        this.api = api!!
        UploadFiles(hashMap,isApi).hitApi()
    }

    inner class UploadFiles(val hashMap: HashMap<String, ArrayList<File>>,var isApi:Boolean) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if(isApi) {
                if(navigator!=null)
                navigator.upLoadFileApiResponse(this, result, error)
            }
            else {
                if(navigator!=null)
                navigator.upLoadFileDisposeApiResponse(this, result, error)
            }
        }

        override fun hitApi() {
            if(dataManager!=null)
            dataManager.uploadFiles(this@UploadFiles, httpManager, hashMap, api)
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if(navigator!=null)
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }

    }

    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DynamicFormViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }

}