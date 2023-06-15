package com.rf.taskmodule.ui.availabilitycalender.slots

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Slot
import com.rf.taskmodule.databinding.FragmentSlotsBinding
import com.rf.taskmodule.ui.availabilitycalender.calendartasklisting.CalendarTaskListingActivity
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.SlotChildAdapter
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import java.util.ArrayList

class SlotsFragment : BaseSdkFragment<FragmentSlotsBinding,SlotsViewModel>() {

    private lateinit var fragmentSlotsBinding : FragmentSlotsBinding
    var id=""
    var date=""
    var type=""

    companion object {
        fun newInstance() = SlotsFragment()
    }

    private lateinit var viewModel: SlotsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSlotsBinding = viewDataBinding
        val list = requireArguments().getString("list")
        id = requireArguments().getString("id").toString()
        date = requireArguments().getString("date").toString()
        type = requireArguments().getString("type").toString()
        val gson = Gson()
       val slotsData = gson.fromJson(list, Array<Slot>::class.java).toCollection(ArrayList())
        if (slotsData.size == 0){
            fragmentSlotsBinding.emptyLayout.visibility = View.VISIBLE
        }
        setSlotsData(slotsData)
    }

    private fun setSlotsData(slotsData: ArrayList<Slot>) {
        fragmentSlotsBinding.gvSlot.layoutManager = GridLayoutManager(activity, 3)
        val slotsAdapter = SlotsAdapter(slotsData, requireContext())
        fragmentSlotsBinding.gvSlot.adapter = slotsAdapter
        slotsAdapter.onItemClick = { time ->
            Log.d("SlotsFragment",time)
            Log.d("SlotsFragment",date)
            Log.d("SlotsFragment",id)
           val intent = Intent(requireContext(),CalendarTaskListingActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("time",time)
            intent.putExtra("date",date)
            intent.putExtra("type",type)
            startActivity(intent)
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
            return R.layout.fragment_slots
    }

    override fun getViewModel(): SlotsViewModel {
        val factory = RocketFlyer.dataManager()?.let { SlotsViewModel.Factory(it) }
        viewModel = ViewModelProvider(this, factory!!)[SlotsViewModel::class.java]
        return viewModel
    }

}