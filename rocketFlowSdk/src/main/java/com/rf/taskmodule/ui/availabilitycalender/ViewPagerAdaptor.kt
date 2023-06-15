package com.rf.taskmodule.ui.availabilitycalender

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.rf.taskmodule.data.model.response.config.Slot
import com.rf.taskmodule.ui.availabilitycalender.slots.SlotsFragment


private const val NUM_TABS = 4

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val data: List<Slot>,
    val id: String,
    val selectedDate: String,
    val type: String
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 ->{
                val bundle = Bundle()
                val gson = Gson()
                val jsonList: String = gson.toJson(data)
                bundle.putString("list", jsonList)
                bundle.putString("id", id)
                bundle.putString("date", selectedDate)
                bundle.putString("type", type)
                val fragment = SlotsFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 ->{
                val bundle = Bundle()
                val gson = Gson()
                val jsonList: String = gson.toJson(data.filter { it.status == "BOOKED" })
                bundle.putString("list", jsonList)
                bundle.putString("id", id)
                bundle.putString("date", selectedDate)
                bundle.putString("type", type)
                val fragment = SlotsFragment()
                fragment.arguments = bundle
                return fragment
            }
            2 -> {
                val bundle = Bundle()
                val gson = Gson()
                val jsonList: String = gson.toJson(data.filter { it.status == "LAPSED" })
                bundle.putString("list", jsonList)
                bundle.putString("id", id)
                bundle.putString("date", selectedDate)
                bundle.putString("type", type)
                val fragment = SlotsFragment()
                fragment.arguments = bundle
                return fragment
            }
            3 -> {
                val bundle = Bundle()
                val gson = Gson()
                val jsonList: String = gson.toJson(data.filter { it.status == "AVAILABLE" })
                bundle.putString("list", jsonList)
                bundle.putString("id", id)
                bundle.putString("date", selectedDate)
                bundle.putString("type", type)
                val fragment = SlotsFragment()
                fragment.arguments = bundle
                return fragment
            }
        }
        return SlotsFragment()
    }
}