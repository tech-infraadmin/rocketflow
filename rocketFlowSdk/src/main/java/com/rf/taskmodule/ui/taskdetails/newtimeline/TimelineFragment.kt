package com.rf.taskmodule.ui.taskdetails.newtimeline

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.model.response.config.TaskResponse
import com.rf.taskmodule.databinding.FragmentTimelineBinding
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity
import com.rf.taskmodule.ui.taskdetails.StageHistoryData
import com.rf.taskmodule.ui.taskdetails.TaskTimeLineAdapter
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.Log
import com.rf.taskmodule.utils.TrackiToast
import com.rocketflow.sdk.RocketFlyer
import java.util.ArrayList

class TimelineFragment : BaseSdkFragment<FragmentTimelineBinding, TimelineViewModel>(),
    TaskTimeLineAdapter.PreviousFormListener {

    private var taskResponse: TaskResponse? = null
    private var task: Task? = null
    var taskId: String? = null

    private lateinit var timeLineAdapter: TaskTimeLineAdapter

    companion object {
        fun newInstance(taskResponse: String, forms: Boolean): TimelineFragment {
            val args = Bundle()
            args.putSerializable("taskResponse", taskResponse)
            args.putBoolean("forms", forms)
            val fragment = TimelineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var timelineViewModel: TimelineViewModel
    private lateinit var fragmentTimelineBinding: FragmentTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments().containsKey("taskResponse")) {
            taskResponse = Gson().fromJson(requireArguments().getString("taskResponse"), TaskResponse::class.java)
            Log.d("task_response", taskResponse?.taskDetail?.assigneeDetail?.name ?: "")
            task = taskResponse?.taskDetail
            taskId = task?.taskId
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentTimelineBinding = viewDataBinding

        timeLineAdapter = TaskTimeLineAdapter(this)
        fragmentTimelineBinding.timeLineAdapter = timeLineAdapter

        if (task!!.stageHistory != null && task!!.stageHistory!!.isNotEmpty()) {
            if (requireArguments().containsKey("forms")) {
                if (requireArguments().getBoolean("forms")) {
                    fragmentTimelineBinding.tvTitle.text = "Submitted Forms"
                    val list =
                        ArrayList(task!!.stageHistory!!).sortedWith(compareByDescending<StageHistoryData> { it.timeStamp })
                            .filter { it.dfdId != null && it.dfdId!!.isNotEmpty() }
                    setAdapter(list)
                } else {
                    fragmentTimelineBinding.tvTitle.text = "Time Line"
                    val list = ArrayList(task!!.stageHistory!!).sortedWith(compareByDescending<StageHistoryData> { it.timeStamp })
                    setAdapter(list)
                }
            }
        }
    }

    fun setAdapter(list: List<StageHistoryData>) {
        Log.e("ListSize", "${list.size}")
        timeLineAdapter.addData(list)
    }

    override fun openForm(dataModel: StageHistoryData) {
        if (dataModel.dfdId != null) {
            startActivityForResult(
                DynamicFormActivity.newIntent(baseActivity)
                    .putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, dataModel.stage!!.name)
                    .putExtra(AppConstants.Extra.EXTRA_FORM_ID, dataModel.dfdId)
                    .putExtra(AppConstants.Extra.EXTRA_TCF_ID, dataModel.dfdId)
                    .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
                    .putExtra(AppConstants.Extra.EXTRA_CTA_ID, dataModel.ctaId)
                    .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false)
                    .putExtra(AppConstants.Extra.HIDE_BUTTON, true),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM
            )
        } else {
            TrackiToast.Message.showShort(requireContext(), "form not available")
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_timeline
    }

    override fun getViewModel(): TimelineViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { TimelineViewModel.Factory(it) } // Factory
        if (factory != null) {
            timelineViewModel = ViewModelProvider(this, factory)[TimelineViewModel::class.java]
        }
        return timelineViewModel
    }
}