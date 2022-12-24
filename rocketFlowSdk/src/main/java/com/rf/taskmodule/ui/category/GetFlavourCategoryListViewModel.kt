package com.rf.taskmodule.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.ui.selectorder.CataLogProductCategory
import com.rf.taskmodule.ui.selectorder.PaginationRequest
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class GetFlavourCategoryListViewModel(
    dataManager: com.rf.taskmodule.data.DataManager,
    schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider
) :
    com.rf.taskmodule.ui.base.BaseSdkViewModel<ProductCategoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager


    fun getProductCategory(
        httpManager: com.rf.taskmodule.data.network.HttpManager,
        request: PaginationRequest?,
        flavorId: String?,
        categoryId: String?,
        loadBy:String="ALL"
    ) {
        this.httpManager = httpManager
        GetProductCategory(request, flavorId,categoryId,loadBy).hitApi()
    }

    inner class GetProductCategory(
        var paginationRequest: PaginationRequest?,
        var flavorId: String?,
        var categoryId: String?,
        var loadBy:String
    ) : com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.PRODUCT_CATEGORIES)) {
                val oldApi = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.PRODUCT_CATEGORIES]!!
                var apiUrl = Api()
                if (flavorId != null && paginationRequest != null&&categoryId!=null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId +"&loadBy="+loadBy+"&categoryId="+categoryId
                }
                else if (flavorId != null && paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&flavorId=" + flavorId +"&loadBy="+loadBy
                } else if (paginationRequest != null) {
                    apiUrl.url =
                        oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset+"&loadBy="+loadBy
                } else {
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.PRODUCT_CATEGORIES
                apiUrl.timeOut = oldApi.timeOut
                if (dataManager != null)
                    dataManager.getProductsCategory(this, httpManager, apiUrl)
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

    fun deleteProductCategory(httpManager: com.rf.taskmodule.data.network.HttpManager, request: CataLogProductCategory?) {
        this.httpManager = httpManager
        DeleteProductCategory(request).hitApi()
    }

    inner class DeleteProductCategory(var request: CataLogProductCategory?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleDeleteProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.DELETE_PRODUCT_CATEGORY)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.DELETE_PRODUCT_CATEGORY]
                if (dataManager != null)
                    dataManager.deleteProductCategory(this, request, httpManager, apiUrl)
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

    fun updateStatusProductCategory(httpManager: com.rf.taskmodule.data.network.HttpManager, request: CataLogProductCategory?) {
        this.httpManager = httpManager
        UpdateProductCategory(request).hitApi()
    }

    inner class UpdateProductCategory(var request: CataLogProductCategory?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateProductStatusCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_CATEGORY_STATUS)) {
                val apiUrl = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.UPDATE_PRODUCT_CATEGORY_STATUS]
                if (dataManager != null)
                    dataManager.deleteProductCategory(this, request, httpManager, apiUrl)
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
            return GetFlavourCategoryListViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }

}