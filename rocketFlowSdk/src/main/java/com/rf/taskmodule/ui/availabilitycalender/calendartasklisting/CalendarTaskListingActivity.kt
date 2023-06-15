package com.rf.taskmodule.ui.availabilitycalender.calendartasklisting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.request.TaskRequestCalendar
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.model.response.config.TaskListing
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityCalendarTaskListingBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity.Companion.newIntent
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity
import com.rf.taskmodule.ui.tasklisting.TaskItemClickListener
import com.rf.taskmodule.utils.ApiType
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import com.rocketflow.sdk.RocketFlyer.Companion.httpManager
import com.rocketflow.sdk.RocketFlyer.Companion.preferenceHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class CalendarTaskListingActivity : BaseSdkActivity<ActivityCalendarTaskListingBinding,CalenderTaskListingViewModel>(),
    CalenderTaskListingNavigator, TaskItemClickListener {

    private lateinit var calenderTaskListingViewModel: CalenderTaskListingViewModel
    private lateinit var calendarTaskListingBinding: ActivityCalendarTaskListingBinding
    var httpManager: HttpManager? = null

    var preferencesHelper: PreferencesHelper? = null
    var id = ""
    var type = ""
    var fromDate = 1675677600000
    var toDate = 1675677600000

    var adapter: CalendarTaskListingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarTaskListingBinding = viewDataBinding
        calenderTaskListingViewModel.navigator = this
        httpManager = httpManager()
        preferencesHelper = preferenceHelper()

        val toolbar: Toolbar = calendarTaskListingBinding.toolbar2
        setToolbar(toolbar, "Task Listing")

        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id").toString()
            type = intent.getStringExtra("type").toString()
            val time = intent.getStringExtra("time").toString()
            val date = intent.getStringExtra("date").toString()

            Log.d("id",id)
            Log.d("time",time)
            Log.d("date",date)
            val fromToTime = time.split("-").toTypedArray()

           val fromDateString = date+" "+fromToTime[0]
           val toDateString = date+" "+fromToTime[1]

            fromDate = getLongFromDate(fromDateString)
            toDate = getLongFromDate(toDateString)

            Log.d("fromDate",fromDate.toString())
            Log.d("toDate",toDate.toString())
        }

        val taskRequest = TaskRequestCalendar(
            id = id,
            from = fromDate,
            to = toDate,
            limit = 100,
            page = 0,
            type = type
        )

        calendarTaskListingBinding.progressBar2.visibility = View.VISIBLE
        val apiMain = TrackiSdkApplication.getApiMap()[ApiType.TASK_BY_ENTITY]!!
        calenderTaskListingViewModel.getTaskList(httpManager!!,apiMain,taskRequest)
    }

    private fun getLongFromDate(date: String): Long {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
        try {
            val mDate: Date = sdf.parse(date)
            val timeInMilliseconds: Long = mDate.time
            println("Date in milli :: $timeInMilliseconds")
            return timeInMilliseconds
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_calendar_task_listing
    }

    override fun getViewModel(): CalenderTaskListingViewModel {
        val factory = RocketFlyer.dataManager()?.let { CalenderTaskListingViewModel.Factory(it) }
        if (factory != null) {calenderTaskListingViewModel = ViewModelProvider(this, factory)[CalenderTaskListingViewModel::class.java]}
        return calenderTaskListingViewModel
    }

    override fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        calendarTaskListingBinding.progressBar2.visibility = View.GONE
        Log.d("CalendarTaskListingActivity",result.toString())
            if (CommonUtils.handleResponse(callback, error, result, this)) {
                val taskListing = Gson().fromJson(
                    result.toString(),
                    TaskListing::class.java
                )
                val list = taskListing.tasks
                calenderTaskListingViewModel.getTaskListLiveData()!!.value = list

                if (list.isNullOrEmpty()){
                    calendarTaskListingBinding.emptyLayout.visibility = View.VISIBLE
                }else{
                    setRecyclerView(list)
                }
                Log.d("CalendarTaskListingActivity",list.toString())
        }else{
                calendarTaskListingBinding.emptyLayout.visibility = View.VISIBLE
        }
    }

    private fun setRecyclerView(list: List<Task>?) {
        adapter = CalendarTaskListingAdapter(list, preferencesHelper)
        calendarTaskListingBinding.rvCalendarTasks.layoutManager = LinearLayoutManager(this)
        adapter!!.setListener(this)
        calendarTaskListingBinding.rvCalendarTasks.adapter = adapter
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onItemClick(task: Task?, position: Int) {
        val intent = Intent(this, NewTaskDetailsActivity::class.java)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task!!.taskId)
        intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.allowSubTask)
        intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.subCategoryIds)
        intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.referenceId)
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, task.categoryId)
        intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_BY_ME)
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate)
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate)
        startActivity(intent)
    }

    override fun onCallClick(mobile: String?) {
        CommonUtils.openDialer(this, mobile)
    }

    override fun onDetailsTaskClick(task: Task?) {
        startActivityForResult(
            newIntent(this)
                .putExtra(AppConstants.Extra.EXTRA_FORM_ID, task!!.tcfId)
                .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.taskId)
                .putExtra(AppConstants.Extra.EXTRA_TCF_ID, task.tcfId)
                .putExtra(
                    AppConstants.Extra.HIDE_BUTTON,
                    true
                )
                .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false),
            AppConstants.REQUEST_CODE_DYNAMIC_FORM
        )
    }
}