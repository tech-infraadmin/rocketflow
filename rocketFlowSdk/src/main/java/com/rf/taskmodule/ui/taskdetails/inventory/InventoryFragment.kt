package com.rf.taskmodule.ui.taskdetails.inventory

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.model.response.config.TaskResponse
import com.rf.taskmodule.databinding.FragmentInventoryBinding
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.ui.taskdetails.timeline.OrderListAdapter
import com.rf.taskmodule.ui.taskdetails.timeline.ProductOrderAdapter
import com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview.SkuInfoPreviewActivity
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import kotlinx.android.synthetic.main.fragment_inventory.cardOrders
import kotlinx.android.synthetic.main.fragment_inventory.rvOrderList

class InventoryFragment : BaseSdkFragment<FragmentInventoryBinding,InventoryViewModel>() {


    private var taskResponse: TaskResponse? = null

    companion object {
        fun newInstance(taskResponse: String): InventoryFragment? {
            val args = Bundle()
            args.putSerializable("taskResponse", taskResponse)
            val fragment = InventoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: InventoryViewModel
    private lateinit var fragmentInventoryBinding: FragmentInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireArguments().containsKey("taskResponse")) {
            taskResponse = Gson().fromJson(requireArguments().getString("taskResponse"), TaskResponse::class.java)
            Log.d("task_response", taskResponse?.taskDetail?.assigneeDetail?.name ?: "")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentInventoryBinding = viewDataBinding
        setData(taskResponse?.taskDetail)
    }

    private fun setData(task: Task?) {
        if (task!!.orderDetails != null && task.orderDetails!!.isNotEmpty()) {
            cardOrders.visibility = View.VISIBLE
            val adapter = OrderListAdapter(task.orderDetails)
            rvOrderList.adapter = adapter
            adapter.onItemClick = { item ->
                Log.d("TAG", item.productId)
                val intent = SkuInfoPreviewActivity.newIntent(requireActivity())
                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.taskId)
                intent.putExtra(AppConstants.Extra.EXTRA_PRODUCT_ID, item.productId.toString())
                intent.putExtra(AppConstants.Extra.EXTRA_PRODUCT_NAME, item.productName.toString())
                if(TrackiSdkApplication.getApiMap().containsKey(ApiType.UNIT_INFO_LISTING)) {
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_TAG_INVENTORY)
                }
            }
        } else if (task.products != null && task.products!!.isNotEmpty()) {
            cardOrders.visibility = View.VISIBLE
            val adapter = ProductOrderAdapter(task.products)
            rvOrderList.adapter = adapter
        } else {
            cardOrders.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_TAG_INVENTORY) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
      return  R.layout.fragment_inventory
    }

    override fun getViewModel(): InventoryViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { InventoryViewModel.Factory(it) } // Factory
        if (factory != null) {
            viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]
        }
        return viewModel
    }

}