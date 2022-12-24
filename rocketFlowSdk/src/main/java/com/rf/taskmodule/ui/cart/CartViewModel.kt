package com.rf.taskmodule.ui.cart

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.DataManager
import com.rf.taskmodule.data.model.request.LinkInventoryRequest
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.ui.base.BaseSdkViewModel
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.rx.AppSchedulerProvider
import com.rf.taskmodule.utils.rx.SchedulerProvider

class CartViewModel (dataManager: com.rf.taskmodule.data.DataManager, schedulerProvider: com.rf.taskmodule.utils.rx.SchedulerProvider) :
        com.rf.taskmodule.ui.base.BaseSdkViewModel<CartNavigator>(dataManager, schedulerProvider) {

    private lateinit var httpManager: com.rf.taskmodule.data.network.HttpManager


    fun getCart(httpManager: com.rf.taskmodule.data.network.HttpManager, request: CartRequest) {
        this.httpManager = httpManager
        GetCart(request).hitApi()
    }

    inner class GetCart(var request: CartRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleCartResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.VIEW_CART)) {
                val oldApi = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.VIEW_CART]!!
               /* var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.VIEW_CART
                apiUrl.timeOut = oldApi.timeOut*/
                if (dataManager != null)
                    dataManager.getCart(this, httpManager, request,oldApi)
            } /*else {
                val api = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/order/cart"

                api.url = url
                api.name = ApiType.VIEW_CART
                api.timeOut = 12
                if (dataManager != null)
                    dataManager.getCart(this, httpManager,request, api)
            }*/
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

    fun applyCoupon(httpManager: com.rf.taskmodule.data.network.HttpManager, request: ApplyCouponRequest?) {
        this.httpManager = httpManager
        ApplyCoupon(request).hitApi()
    }

    inner class ApplyCoupon( var request: ApplyCouponRequest?) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleApplyCouponResponse(this, result, error)
        }

        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.VALIDATE_COUPON)) {
                val oldApi = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.VALIDATE_COUPON]!!
                var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.VALIDATE_COUPON
                apiUrl.timeOut = oldApi.timeOut

                com.rf.taskmodule.utils.CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.applyCoupon(this, httpManager,request, apiUrl)
            } /*else {
                var apiUrl = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/coupon/validatePromo"
                apiUrl.url = url
                apiUrl.name = ApiType.VALIDATE_COUPON
                apiUrl.timeOut = 12

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.applyCoupon(this, httpManager,request, apiUrl)
            }*/
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

  /*  fun createOrder(httpManager: HttpManager, request: CreateOrderRequest?) {
        this.httpManager = httpManager
        CreateOrder(request).hitApi()
    }*/

   /* inner class CreateOrder( var request: CreateOrderRequest?) : ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            if (navigator != null)
                navigator.handleCreateOrderResponse(this, result, error)
        }

        override fun hitApi() {
            if (TrackiApplication.getApiMap().containsKey(ApiType.CREATE_ORDER)) {
                val oldApi = TrackiApplication.getApiMap()[ApiType.CREATE_ORDER]!!
                var apiUrl = Api()
                apiUrl.url = oldApi.url
                apiUrl.name = ApiType.CREATE_ORDER
                apiUrl.timeOut = oldApi.timeOut

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.createOrder(this, httpManager,request, apiUrl)
            } else {
                var apiUrl = Api()
                var url = "https://qa2.rocketflyer.in/rfapi/secure/rb/store/order/create"
                apiUrl.url = url
                apiUrl.name = ApiType.CREATE_ORDER
                apiUrl.timeOut = 12

                CommonUtils.showLogMessage("e", "URL==", apiUrl.url)
                if (dataManager != null)
                    dataManager.createOrder(this, httpManager,request, apiUrl)
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
    }*/

    fun linkInventory(httpManager: com.rf.taskmodule.data.network.HttpManager, request: LinkInventoryRequest) {
        this.httpManager = httpManager
        LinkInventory(request).hitApi()
    }

    inner class LinkInventory(private var data: LinkInventoryRequest) :
        com.rf.taskmodule.data.network.ApiCallback {

        override fun onResponse(result: Any?, error: APIError?) {
            navigator.linkInventoryResponse(this, result, error)
        }

        @SuppressLint("SuspiciousIndentation")
        override fun hitApi() {
            if (com.rf.taskmodule.TrackiSdkApplication.getApiMap().containsKey(ApiType.LINK_INVENTORY)) {
                val api = com.rf.taskmodule.TrackiSdkApplication.getApiMap()[ApiType.LINK_INVENTORY]!!
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

        override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
            navigator.showTimeOutMessage(callBack)
        }

        override fun onLogout() {
        }
    }

    internal class Factory(private val mDataManager: com.rf.taskmodule.data.DataManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CartViewModel(mDataManager,
                com.rf.taskmodule.utils.rx.AppSchedulerProvider()
            ) as T
        }
    }

}