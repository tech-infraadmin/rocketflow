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
    dataManager: DataManager,
    schedulerProvider: SchedulerProvider
) :
    BaseSdkViewModel<ProductCategoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getProductCategory(
        httpManager: HttpManager,
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
    ) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.PRODUCT_CATEGORIES)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.PRODUCT_CATEGORIES]!!
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

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun deleteProductCategory(httpManager: HttpManager, request: CataLogProductCategory?) {
        this.httpManager = httpManager
        DeleteProductCategory(request).hitApi()
    }

    inner class DeleteProductCategory(var request: CataLogProductCategory?) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleDeleteProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.DELETE_PRODUCT_CATEGORY)) {
                val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.DELETE_PRODUCT_CATEGORY]
                if (dataManager != null)
                    dataManager.deleteProductCategory(this, request, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun updateStatusProductCategory(httpManager: HttpManager, request: CataLogProductCategory?) {
        this.httpManager = httpManager
        UpdateProductCategory(request).hitApi()
    }

    inner class UpdateProductCategory(var request: CataLogProductCategory?) :
        ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleUpdateProductStatusCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_CATEGORY_STATUS)) {
                val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.UPDATE_PRODUCT_CATEGORY_STATUS]
                if (dataManager != null)
                    dataManager.deleteProductCategory(this, request, httpManager, apiUrl)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }


    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GetFlavourCategoryListViewModel(mDataManager,
                AppSchedulerProvider()
            ) as T
        }
    }

}