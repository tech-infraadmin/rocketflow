package com.rf.taskmodule.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.UpdateFileRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class AddProductViewModel (dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<AddProductNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager

    fun addProduct(httpManager: com.rf.taskmodule.data.network.HttpManager, request: AddProductRequest?) {
        this.httpManager = httpManager
        AddProduct(request).hitApi()
    }

    inner class AddProduct(var request: AddProductRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.ADD_PRODUCT]!!

                if (dataManager != null)
                    dataManager.addProduct(this, request, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun updateProduct(httpManager: com.rf.taskmodule.data.network.HttpManager, request: AddProductRequest?) {
        this.httpManager = httpManager
        UpdateProduct(request).hitApi()
    }

    inner class UpdateProduct(var request: AddProductRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.UPDATE_PRODUCT]!!

                if (dataManager != null)
                    dataManager.updateProduct(this, request, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun uploadImage(data: UpdateFileRequest, httpManager: com.rf.taskmodule.data.network.HttpManager?) {
        this.httpManager = httpManager!!
        UpdateProfilePic(data).hitApi()
    }

    inner class UpdateProfilePic(var data: UpdateFileRequest) :
        com.rf.taskmodule.data.network.ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleSendImageResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.UPLOAD_FILE_AGAINEST_ENTITY]!!

                if (dataManager != null)
                    dataManager.updateProfilePic(this, httpManager, data, apiUrl)
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }


    fun getUnits(httpManager: com.rf.taskmodule.data.network.HttpManager, ) {
        this.httpManager = httpManager
        GetUnits().hitApi()
    }

    inner class GetUnits() : com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUnitsResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_UNITS)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.GET_UNITS]!!

                if (dataManager != null)
                    dataManager.getUnits(this, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getProductDetails(
        httpManager: com.rf.taskmodule.data.network.HttpManager,
        pid: String?,
    ) {
        this.httpManager = httpManager
        GetProductDetails(pid).hitApi()
    }

    inner class GetProductDetails(
        var pid: String?,

    ) : com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductDetailsResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.PRODUCT_DETAIL)) {
                val oldApi = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.PRODUCT_DETAIL]!!
                var apiUrl = Api()
               if (pid != null) {
                    apiUrl.url =
                        oldApi.url + "?pid=" + pid
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.PRODUCT_DETAIL
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductDetails(this, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddProductViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }

}