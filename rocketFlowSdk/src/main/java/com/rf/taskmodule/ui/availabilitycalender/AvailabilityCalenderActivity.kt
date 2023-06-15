package com.rf.taskmodule.ui.availabilitycalender

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.databinding.ActivityAvailabilityCalenderBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer

class AvailabilityCalenderActivity : BaseSdkActivity<ActivityAvailabilityCalenderBinding, AvailabilityCalenderViewModel>(),
    AvailabilityCalenderNavigator {
    private lateinit var availabilityViewModel: AvailabilityCalenderViewModel
    private lateinit var mActivityAvailabilityCalenderBinding: ActivityAvailabilityCalenderBinding
    var id = ""
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAvailabilityCalenderBinding = viewDataBinding
        availabilityViewModel.navigator = this
        val toolbar: Toolbar = mActivityAvailabilityCalenderBinding.toolbar2
        setToolbar(toolbar, "Your Calendar")
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id").toString()
            type = intent.getStringExtra("type").toString()
            Log.d("id",id);
            Log.d("id",type);
        }
        addFragmentToFragment(id,type)
    }

    private fun addFragmentToFragment(key: String, type: String) {
        val bundle = Bundle()
        bundle.putString("id", key)
        bundle.putString("type", type)
        val fragment = AvailabilityCalendarFragment.newInstance()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.root_container, fragment)
            .commitAllowingStateLoss()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_availability_calender
    }

    override fun getViewModel(): AvailabilityCalenderViewModel {
        val factory = RocketFlyer.dataManager()?.let { AvailabilityCalenderViewModel.Factory(it) }
        if (factory != null) {availabilityViewModel = ViewModelProvider(this, factory)[AvailabilityCalenderViewModel::class.java]}
        return availabilityViewModel
    }

    override fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        TODO("Not yet implemented")
    }
}