package taskmodule.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.TrackiSdkApplication
import taskmodule.data.DataManager
import taskmodule.data.model.request.UpdateFileRequest
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.ApiType
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider

class AddProductCategoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
    BaseSdkViewModel<AddProductCategoryNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager

    fun addProductCategory(httpManager: HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        AddProductCategory(request).hitApi()
    }

    inner class AddProductCategory(var request: AddCategoryRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT_CATEGORY)) {
                val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.ADD_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.addProductCategory(this, request, httpManager, apiUrl)
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

    fun updateProductcategory(httpManager: HttpManager, request: AddCategoryRequest?) {
        this.httpManager = httpManager
        UpdateProductCategory(request).hitApi()
    }

    inner class UpdateProductCategory(var request: AddCategoryRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        ///UPDATE_PRODUCT_CATEGORY
        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT_CATEGORY)) {
                val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.UPDATE_PRODUCT_CATEGORY]!!

                if (dataManager != null)
                    dataManager.updateProductCategory(this, request, httpManager, apiUrl)
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

    fun uploadImage(data: UpdateFileRequest, httpManager: HttpManager?) {
        this.httpManager = httpManager!!
        UpdateProfilePic(data).hitApi()
    }

    inner class UpdateProfilePic(var data: UpdateFileRequest) : ApiCallback {
        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleSendImageResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)) {
                val apiUrl = TrackiSdkApplication.getApiMap()[ApiType.UPLOAD_FILE_AGAINEST_ENTITY]!!

                if (dataManager != null)
                    dataManager.updateProfilePic(this, httpManager, data, apiUrl)
            }

        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {}
        override fun onRequestTimeOut(callBack: ApiCallback) {
            if (navigator != null)
                navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {}
    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddProductCategoryViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }

}