package com.rf.taskmodule.ui.facility

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rocketflow.sdk.RocketFlyer
import com.trackthat.lib.TrackThat
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.ConfigResponse
import com.rf.taskmodule.data.model.response.config.GetSavedServicesResponse
import com.rf.taskmodule.data.model.response.config.InitiateSignUpResponse
import com.rf.taskmodule.data.model.response.config.Service
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityServicesSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.JSONConverter
import com.rf.taskmodule.utils.Log
import com.rf.taskmodule.utils.NextScreen
import com.rf.taskmodule.utils.TrackiToast
import java.util.*
import kotlin.collections.ArrayList

class ServicesSDKActivity : BaseSdkActivity<ActivityServicesSdkBinding, ServiceViewModel>(),
    ServiceNavigator, View.OnClickListener {

    private var categoryIds: ArrayList<String>? = ArrayList()
    lateinit var mServiceViewModel: ServiceViewModel
    lateinit var mServicesAdapter: ServicesAdapterSdk
    lateinit var httpManager: HttpManager
    lateinit var preferencesHelper: PreferencesHelper
    lateinit var binding: ActivityServicesSdkBinding
    private var listServices: ArrayList<Service>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mServiceViewModel.navigator = this
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!
        setToolbar(binding.toolbar, "Select Services")
        if (intent != null && intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)) {
            val result = intent!!.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)
            Log.d("result",result)
            val listType = object : TypeToken<ArrayList<String>>() {}.type
            categoryIds = Gson().fromJson(result, listType)
        }

        categoryIds?.forEach {
            val list = ArrayList(preferencesHelper.services.filter { item -> item.categoryId.equals(it) && item.selected == true })
            listServices?.addAll(list)
        }

        listServices!!.filter { it.selected == true}.forEach { it.selected = false }

        mServicesAdapter = ServicesAdapterSdk(listServices as ArrayList<Service>)
        binding.adapter = mServicesAdapter
        binding.btnNext.setOnClickListener(this)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_services_sdk
    }

    override fun getViewModel(): ServiceViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { ServiceViewModel.Factory(it) } // Factory
        if (factory != null) {
            mServiceViewModel = ViewModelProvider(this, factory)[ServiceViewModel::class.java]
        }
        return mServiceViewModel
    }

    override fun handleUpdateServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            var jsonConverter = JSONConverter<InitiateSignUpResponse>()
            var response = jsonConverter.jsonToObject(result.toString(), InitiateSignUpResponse::class.java)
            if (response != null) {
                if (response.loginToken != null && response.loginToken!!.isNotEmpty()) {
                    preferencesHelper.loginToken = response.loginToken
                }
                if (response.accessId != null && response.accessId!!.isNotEmpty()) {
                    preferencesHelper.accessId = response.accessId
                }

            }

            if (response?.nextScreen != null) {
                Log.e("nextScreen","${response.nextScreen} and refresh -> ${response.refreshConfig}")

                if (response.refreshConfig) {
                    showLoading()

                    mServiceViewModel.getConfig(httpManager, response.nextScreen, response.sdkAccessId,"")
                } else {

                    if (response.sdkAccessId != null && response.sdkAccessId!!.isNotEmpty()) {
                        TrackThat.setAccessId(response.sdkAccessId)
                    }
                    Log.e("nextScreen","go0")
                    CommonUtils.otpgoToNext(this, response.nextScreen, "")
                }

            }
        }

    }

    override fun handleConfigResponse(callback: ApiCallback, result: Any?, error: APIError?, nextScreen: NextScreen?, sdkAccessId: String?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            if (sdkAccessId != null) {
                // send accessId to SDK
                TrackThat.setAccessId(sdkAccessId)
            }
            if (result != null) {
                val gson = Gson()
                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
                Log.e("nextScreen","config response -> $configResponse check-> ${configResponse.appConfig}")
                CommonUtils.saveConfigDetails(this, configResponse, preferencesHelper, "2", "")

                if (configResponse.appConfig != null) {
                    if (Objects.requireNonNull(configResponse.appConfig)!!.idleTrackingInfo != null) {
                        Log.e("nextScreen", "go01")
                        if (configResponse.appConfig!!.idleTrackingInfo!!.mode != null && configResponse.appConfig!!.idleTrackingInfo!!.mode == "ON_PUNCH" && Objects.requireNonNull(
                                configResponse.appConfig!!.idleTrackingInfo
                            )!!.enableIdleTracking
                        ) {
                            preferencesHelper!!.isIdealTrackingEnable =
                                configResponse.appConfig!!.idleTrackingInfo!!.enableIdleTracking
                            showLoading()
                            mServiceViewModel.getPunchInPunchOutData(httpManager, nextScreen)
                        } else {
                            Log.e("nextScreen", "go1")
                            CommonUtils.otpgoToNext(this, nextScreen, "")
                        }
                    } else {
                        Log.e("nextScreen", "go2")
                        CommonUtils.otpgoToNext(this, nextScreen, "")
                    }
                }
                else {
                    Log.e("nextScreen", "go3")
                    CommonUtils.otpgoToNext(this, nextScreen, "")
                }
            }
        }
    }

    override fun handlePunchInPunchOutResponse(apiCallback: ApiCallback?, result: Any?, error: APIError?, nextScreen: NextScreen?) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            val jsonConverter: JSONConverter<PunchInPunchOutData> = JSONConverter<PunchInPunchOutData>()
            val punchInPunchOutData = jsonConverter.jsonToObject(result.toString(), PunchInPunchOutData::class.java) as PunchInPunchOutData
            if (punchInPunchOutData.data != null) {
                if (punchInPunchOutData.data!!.status != null && !punchInPunchOutData.data!!.status!!.isEmpty()) {
                    if (punchInPunchOutData.data!!.status == "PRESENT") {
                        if (preferencesHelper!!.isIdealTrackingEnable) {
                            if (punchInPunchOutData.data!!.punchOutAt != 0L) {
                                preferencesHelper!!.punchOutTime = punchInPunchOutData.data!!.punchOutAt
                                if (TrackThat.isTracking()) {
                                    preferencesHelper!!.idleTripActive = false
                                    TrackThat.stopTracking()
                                    preferencesHelper!!.punchId = null
                                }
                            } else {
                                if (punchInPunchOutData.data != null && punchInPunchOutData.data!!.punchInAt != 0L) {
                                    preferencesHelper!!.punchStatus = true
                                    if (!preferencesHelper!!.idleTripActive) {
                                        preferencesHelper!!.punchId = punchInPunchOutData.data!!.punchId
                                        if (!TrackThat.isTracking()) {
                                            preferencesHelper!!.idleTripActive = true
                                            TrackThat.startTracking(punchInPunchOutData.data!!.punchId, false)
                                        }
                                    }
                                    preferencesHelper!!.punchInTime = punchInPunchOutData.data!!.punchInAt
                                    if (punchInPunchOutData.data!!.punchData != null && punchInPunchOutData.data!!.punchData!!.punchInData != null) {
                                        if (punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie != null) {
                                            val imageUrl = punchInPunchOutData.data!!.punchData!!.punchInData!!.selfie
                                            preferencesHelper!!.selfieUrl = imageUrl
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //CommonUtils.updateSharedContentProvider(this, preferencesHelper)
        }
        CommonUtils.otpgoToNext(this, nextScreen, "")
    }

    override fun handleGetSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter = JSONConverter<GetSavedServicesResponse>()
            val response = jsonConverter.jsonToObject(result.toString(), GetSavedServicesResponse::class.java)
            if (response?.services != null) {
                listServices = response.services as ArrayList<Service>?
//                mServicesAdapter.addData(listServices as java.util.ArrayList<Service>)
//                binding.adapter = mServicesAdapter
            }
        }

    }

    override fun handleUpdateSavedServiceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter = JSONConverter<InitiateSignUpResponse>()
            val response = jsonConverter.jsonToObject(result.toString(), InitiateSignUpResponse::class.java)
            if (response.refreshConfig) {
                showLoading()
                mServiceViewModel.getConfig(
                    httpManager,
                    NextScreen.HOME,
                    response.sdkAccessId,
                    preferencesHelper.configVersion
                )
            } else {

                if (response.sdkAccessId != null && response.sdkAccessId!!.isNotEmpty()) {
                    TrackThat.setAccessId(response.sdkAccessId)
                }
                CommonUtils.otpgoToNext(this, NextScreen.HOME, "")
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnNext -> {
                val listSelected = mServicesAdapter.getList().filter { it -> it.selected!! }
                if (listSelected.isNullOrEmpty()) {
                    TrackiToast.Message.showShort(this, "Please select service ")
                } else {
                    Log.d("serviceIds", listSelected.toString());
                    val returnIntent = Intent()
                    returnIntent.putExtra("serviceIds", Gson().toJson(listSelected).toString())
                    setResult(RESULT_OK, returnIntent)
                    finish()
                }
            }
        }
    }
}