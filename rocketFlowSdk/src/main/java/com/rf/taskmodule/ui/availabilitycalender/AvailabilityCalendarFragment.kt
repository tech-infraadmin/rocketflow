package com.rf.taskmodule.ui.availabilitycalender

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.Slot
import com.rf.taskmodule.data.model.response.config.SlotDataResponse
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.FragmentAvailabilityCalanderBinding
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.DateTimeUtil
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AvailabilityCalendarFragment : BaseSdkFragment<FragmentAvailabilityCalanderBinding, AvailabilityCalenderViewModel>(),
    AvailabilityCalenderNavigator {

    private lateinit var fragmentAvailabilityCalendarBinding: FragmentAvailabilityCalanderBinding
    private lateinit var availabilityViewModel: AvailabilityCalenderViewModel


    lateinit var preferencesHelper: PreferencesHelper
    lateinit var httpManager: HttpManager
    private var selectedDate = ""
    private var id = ""
    private var type = ""

    companion object {
        fun newInstance() = AvailabilityCalendarFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAvailabilityCalendarBinding = viewDataBinding
        availabilityViewModel.navigator = this
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        //current date
        selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        id = requireArguments().getString("id").toString()
        type = requireArguments().getString("type").toString()
        Log.d("id",id);
        Log.d("type",type);
        // Get Slots
        getSlots(selectedDate, id)

        //on calendar date change
        fragmentAvailabilityCalendarBinding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = DateTimeUtil.parsePickerDate(year, month, dayOfMonth)
            getSlots(selectedDate, id)
        }
    }

    private fun getSlots(selectedDate: String, id: String) {
        fragmentAvailabilityCalendarBinding.progressBarSlots.visibility = View.VISIBLE
        availabilityViewModel.getSlotAvailability(
            httpManager,
            TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!,
            id,
            selectedDate
        )
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_availability_calander
    }


    private fun setTabLayout(data: List<Slot>) {
        val adaptor = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, data, id, selectedDate,type)
        fragmentAvailabilityCalendarBinding.viewPager.adapter = adaptor

        val titleArray = arrayOf("All", "Booked", "Lapsed", "Available")
        val imagesArray = arrayOf(
            R.drawable.ic_list,
            R.drawable.ic_calender,
            R.drawable.ic_laps,
            R.drawable.ic_available
        )

        TabLayoutMediator(
            fragmentAvailabilityCalendarBinding.tabLayout,
            fragmentAvailabilityCalendarBinding.viewPager
        ) { tab, position ->
            val tabView = LayoutInflater.from(requireContext()).inflate(
                R.layout.custom_available_calender_tab,
                fragmentAvailabilityCalendarBinding.tabLayout,
                false
            )
            val title = tabView.findViewById<TextView>(R.id.textViewTitle)
            val imageView = tabView.findViewById<ImageView>(R.id.imageView)
            val count = tabView.findViewById<TextView>(R.id.textView2)
            title.text = titleArray[position]
            imageView.setImageResource(imagesArray[position])
            when (position) {
                0 -> {
                    count.text = data.size.toString()
                    imageView!!.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.blue), PorterDuff.Mode.SRC_IN)
                    title!!.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
                    count!!.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
                }
                1 -> {
                    count.text = data.filter { it.status == "BOOKED" }.size.toString()
                }
                2 -> {
                    count.text = data.filter { it.status == "LAPSED" }.size.toString()
                }
                3 -> {
                    count.text = data.filter { it.status == "AVAILABLE" }.size.toString()
                }
            }
            tab.customView = tabView
        }.attach()

        fragmentAvailabilityCalendarBinding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val imageView = tab.customView?.findViewById<ImageView>(R.id.imageView)
                val title =  tab.customView?.findViewById<TextView>(R.id.textViewTitle)
                val count =  tab.customView?.findViewById<TextView>(R.id.textView2)
                imageView!!.setColorFilter(ContextCompat.getColor(activity!!, R.color.blue), PorterDuff.Mode.SRC_IN)
                title!!.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
                count!!.setTextColor(ContextCompat.getColor(activity!!, R.color.blue))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val imageView = tab.customView?.findViewById<ImageView>(R.id.imageView)
                val title =  tab.customView?.findViewById<TextView>(R.id.textViewTitle)
                val count =  tab.customView?.findViewById<TextView>(R.id.textView2)
                imageView!!.setColorFilter(ContextCompat.getColor(activity!!, R.color.gray), PorterDuff.Mode.SRC_IN)
                title!!.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
                count!!.setTextColor(ContextCompat.getColor(activity!!, R.color.gray))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    override fun getViewModel(): AvailabilityCalenderViewModel {
        val factory = RocketFlyer.dataManager()?.let { AvailabilityCalenderViewModel.Factory(it) }
        availabilityViewModel = ViewModelProvider(this, factory!!)[AvailabilityCalenderViewModel::class.java]
        return availabilityViewModel
    }

    override fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        fragmentAvailabilityCalendarBinding.progressBarSlots.visibility = View.GONE
        val slotDataResponse = Gson().fromJson(result.toString(), SlotDataResponse::class.java)
        if (slotDataResponse.successful) {
            if (!slotDataResponse.data?.get(selectedDate)?.slots.isNullOrEmpty()) {
                setTabLayout(slotDataResponse.data!![selectedDate]!!.slots)
            } else {
                val list: List<Slot> = listOf()
                setTabLayout(list)
            }
        } else {
            val list: List<Slot> = listOf()
            setTabLayout(list)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        TODO("Not yet implemented")
    }
}