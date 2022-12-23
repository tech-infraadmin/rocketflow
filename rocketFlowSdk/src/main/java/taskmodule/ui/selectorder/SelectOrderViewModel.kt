package taskmodule.ui.selectorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import taskmodule.TrackiSdkApplication
import taskmodule.data.DataManager
import taskmodule.data.model.request.GetManualLocationRequest
import taskmodule.data.model.request.LinkInventoryRequest
import taskmodule.data.model.response.config.Api
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.ui.base.BaseSdkViewModel
import taskmodule.utils.ApiType
import taskmodule.utils.CommonUtils
import taskmodule.utils.Log
import taskmodule.utils.rx.AppSchedulerProvider
import taskmodule.utils.rx.SchedulerProvider

class SelectOrderViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseSdkViewModel<SelectOrderNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: HttpManager


    fun getProductCategory( flavourId: String?,categoryId:String?,httpManager: HttpManager, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductCategory(flavourId,categoryId,request).hitApi()
    }

    inner class GetProductCategory(var flavourId: String?,var categoryId: String?,var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductCategoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.TASK_PRODUCT_CATEGORIES)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.TASK_PRODUCT_CATEGORIES]!!
                var apiUrl = Api()
                if (paginationRequest != null&&flavourId!=null&&categoryId!=null) {
                    apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset+ "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                } else if(flavourId!=null&&categoryId!=null) {
                    apiUrl.url = oldApi.url+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                }else if(flavourId!=null) {
                    apiUrl.url = oldApi.url+ "?flavorId=${flavourId}"
                }else if(categoryId!=null) {
                    apiUrl.url = oldApi.url+ "?categoryId=${categoryId}"
                }else{
                    apiUrl.url = oldApi.url
                }
                apiUrl.name = ApiType.TASK_PRODUCT_CATEGORIES
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

    fun getProductByKeyWord(categoryId:String?,flavourId: String?,geoId:String?,httpManager: HttpManager, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductListByKeyWord(categoryId,flavourId,geoId, request).hitApi()
    }

    inner class GetProductListByKeyWord(var categoryId:String?,var flavourId: String?,var geoId:String?, var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_TASK_PRODUCTS)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_PRODUCTS]!!
                var apiUrl = Api()
                if (paginationRequest != null&&flavourId!=null&&categoryId!=null&&geoId!=null) {

                    apiUrl.url =
                        oldApi.url + "?limit=" +
                                paginationRequest!!.limit + "&offset=" +
                                paginationRequest!!.offset  +
                                "&keyword=${paginationRequest!!.keyword}"+ "&categoryId=${categoryId}"+ "&flavorId=${flavourId}"+"&geoId=${geoId}"
                }else{

                    apiUrl.url =
                        oldApi.url + "?limit=" +
                                paginationRequest!!.limit + "&offset=" +
                                paginationRequest!!.offset  +
                                "&keyword=${paginationRequest!!.keyword}"+ "&categoryId=${categoryId}"+ "&flavorId=${flavourId}"
                }
                apiUrl.name = ApiType.GET_TASK_PRODUCTS
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.getProducts(this, httpManager, apiUrl)
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

    fun getProduct(httpManager: HttpManager,categoryId:String?, cid: String?,flavourId:String?,geoId:String?, request: PaginationRequest?) {
        this.httpManager = httpManager
        GetProductList(categoryId,cid,flavourId,geoId, request).hitApi()
    }

    inner class GetProductList(var categoryId:String?,var cid: String?,var flavourId: String?,var geoId:String?, var paginationRequest: PaginationRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleProductResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.GET_TASK_PRODUCTS)) {
                val oldApi = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_PRODUCTS]!!
                var apiUrl = Api()
                if (paginationRequest != null) {
                    if (cid != null&&flavourId!=null&&geoId!=null) {
                        apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy + "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"+"&geoId=${geoId}"

                    }
                    else if (cid != null&&flavourId!=null) {
                        apiUrl.url =
                                oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy + "&flavorId=${flavourId}"+ "&categoryId=${categoryId}"

                    }else if (cid != null) {
                        apiUrl.url =
                            oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&cid=${cid}" + "&loadBy=" + paginationRequest!!.loadBy+ "&categoryId=${categoryId}"

                    }else {
                        apiUrl.url =
                                oldApi.url + "?limit=" + paginationRequest!!.limit + "&offset=" + paginationRequest!!.offset + "&loadBy=" + paginationRequest!!.loadBy+ "&categoryId=${categoryId}"
                    }
                } else {
                    if (cid != null&&flavourId!=null&&geoId!=null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"+"&geoId=${geoId}"
                    }
                    else if (cid != null&&flavourId!=null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "?flavorId=${flavourId}"+ "&categoryId=${categoryId}"
                    } else if (cid != null) {
                        apiUrl.url = oldApi.url + "?cid=${cid}"+ "&categoryId=${categoryId}"
                    } else {
                        apiUrl.url = oldApi.url
                    }
                }
                apiUrl.name = ApiType.GET_TASK_PRODUCTS
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.getProducts(this, httpManager, apiUrl)
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

    fun linkInventory(httpManager: HttpManager, request: LinkInventoryRequest) {
        this.httpManager = httpManager
        LinkInventory(request).hitApi()
    }

    inner class LinkInventory(private var data: LinkInventoryRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.linkInventoryResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiSdkApplication.getApiMap().containsKey(ApiType.LINK_INVENTORY)) {
                val api = TrackiSdkApplication.getApiMap()[ApiType.LINK_INVENTORY]!!
//            val api = Api()
//            api.url = "https://qa2.rocketflyer.in/rfapi/secure/tracki/linkInventory"
//            api.name = ApiType.LINK_INVENTORY
//            api.timeOut = 50
                if(dataManager!=null)
                    dataManager.linkInventory(this, httpManager, data, api)
            }
        }

        override fun isAvailable(): Boolean {
            return true
        }

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    fun getHubList(httpManager: HttpManager, getManualLocationRequest: GetManualLocationRequest) {
        this.httpManager = httpManager
        HubList(getManualLocationRequest).hitApi()
    }

    inner class HubList(var manualLocationRequest: GetManualLocationRequest) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.handleHubListResponse(this@HubList, result, error)
        }

        override fun hitApi() {
            var api = TrackiSdkApplication.getApiMap()[ApiType.GET_HUBS]
            if (api != null) {
                dataManager.getHubList(this@HubList, httpManager, manualLocationRequest, api)
            } else {
                Log.e("message", "GET_HUBS api is null")
            }
        }

        override fun isAvailable() = true

        override fun onNetworkErrorClose() {
        }

        override fun onRequestTimeOut(callBack: ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    internal class Factory(private val mDataManager: DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SelectOrderViewModel(mDataManager, AppSchedulerProvider()) as T
        }
    }


}