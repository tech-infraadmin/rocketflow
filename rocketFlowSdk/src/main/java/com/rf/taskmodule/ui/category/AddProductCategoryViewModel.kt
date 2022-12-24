package com.rf.taskmodule.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.UpdateFileRequest
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class AddProductCategoryViewModel(dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<AddProductCategoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager

    fun addProductCategory(httpManager: com.rf.taskmodule.data.network.HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        AddProductCategory(request).hitApi()
    }

    inner class AddProductCategory(var request: AddCategoryRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT_CATEGORY)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.ADD_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.addProductCategory(this, request, httpManager, apiUrl)
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

    fun updateProductcategory(httpManager: com.rf.taskmodule.data.network.HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        UpdateProductCategory(request).hitApi()
    }

    inner class UpdateProductCategory(var request: AddCategoryRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        ///UPDATE_PRODUCT_CATEGORY
        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_CATEGORY)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.UPDATE_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.updateProductCategory(this, request, httpManager, apiUrl)
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

    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddProductCategoryViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }

}