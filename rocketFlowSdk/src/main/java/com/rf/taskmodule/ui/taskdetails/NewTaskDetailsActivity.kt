package com.rf.taskmodule.ui.taskdetails


//import androidmads.library.qrgenearator.QRGContents
//import androidmads.library.qrgenearator.QRGEncoder
//import com.bumptech.glide.Glide
//import com.google.zxing.WriterException
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.TaskResponse
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityNewTaskDetailsSdkBinding
import com.rf.taskmodule.ordercode.OrderCodeFragment
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.ui.taskdetails.inventory.InventoryFragment
import com.rf.taskmodule.ui.taskdetails.newtimeline.TimelineFragment
import com.rf.taskmodule.ui.taskdetails.subtask.SubTaskFragment
import com.rf.taskmodule.ui.taskdetails.timeline.TaskDetailsFragment
import com.rf.taskmodule.ui.taskdetails.tripdetails.TripDetailsFragment
import com.rf.taskmodule.ui.tasklisting.TaskPagerAdapter
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.TabDataClass
import com.rf.taskmodule.utils.*
import com.rocketflow.sdk.RocketFlyer
import com.trackthat.lib.TrackThat
import kotlinx.android.synthetic.main.activity_new_task_details_sdk.*


data class UserDetails(
    var title: String,
    var image: String,
    var name: String,
    var phone: String,
    var email: String
)

open class NewTaskDetailsActivity :
    BaseSdkActivity<ActivityNewTaskDetailsSdkBinding, TaskDetailsActivityViewModel>(),
    NewTaskDetailNavigator {

    private var categoryId: String? = null
    private var from: String? = null
    private var taskResponse: TaskResponse? = null

    //@Inject
    private lateinit var mNewTaskViewModel: TaskDetailsActivityViewModel
    private lateinit var mActivityNewTaskDetailBinding: ActivityNewTaskDetailsSdkBinding

    //@Inject
    lateinit var mPagerAdapter: TaskPagerAdapter

    lateinit var httpManager: HttpManager
    lateinit var preferencesHelper: PreferencesHelper

    private val fragments: MutableList<TabDataClass> = ArrayList()

    private val TAG = "NewTaskDetailsActivity"

    private var fromDate: Long = 0
    private var toDate: Long = 0
    private var allowSubTask: Boolean = false
    private var subTaskCategoryId: ArrayList<String>? = null
    var taskId: String? = null
    private var referenceId: String? = null
    private var snackBar: Snackbar? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            mActivityNewTaskDetailBinding.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_task_details_sdk
    }

    override fun getViewModel(): TaskDetailsActivityViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { TaskDetailsActivityViewModel.Factory(it) } // Factory
        if (factory != null) {
            mNewTaskViewModel =
                ViewModelProvider(this, factory)[TaskDetailsActivityViewModel::class.java]
        }
        return mNewTaskViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityNewTaskDetailBinding = viewDataBinding
        mNewTaskViewModel.navigator = this
        setToolbar(mActivityNewTaskDetailBinding.toolbar, "Task Details")

        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        mPagerAdapter = TaskPagerAdapter(supportFragmentManager)

        getTaskData()
    }


    private fun getTaskData() {
        showLoading()
        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                from = intent.getStringExtra(AppConstants.Extra.FROM)
                categoryId = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)
                allowSubTask =
                    intent.getBooleanExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, false)
                if (intent.hasExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID))
                    subTaskCategoryId =
                        intent.getStringArrayListExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID)
                if (intent.hasExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID))
                    referenceId = intent.getStringExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)
                //task= intent.getSerializableExtra(AppConstants.Extra.EXTRA_TASK) as Task?
                if (intent.hasExtra(AppConstants.Extra.FROM_DATE)) {
                    fromDate = intent.getLongExtra(AppConstants.Extra.FROM_DATE, 0)
                }
                if (intent.hasExtra(AppConstants.Extra.FROM_TO)) {
                    toDate = intent.getLongExtra(AppConstants.Extra.FROM_TO, 0)
                }
            }

            mActivityNewTaskDetailBinding.topHeaderLayout.visibility = View.GONE
            mActivityNewTaskDetailBinding.tabLayout.visibility = View.GONE
            mActivityNewTaskDetailBinding.shimmerViewContainer.visibility = View.VISIBLE
            mNewTaskViewModel.getTaskDetails(httpManager, taskId)
        }
    }

    fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories>? =
            preferencesHelper.workFlowCategoriesList
        var lebel: String? = "Sub Task"

        workFlowCategoriesList?.let {
            for (i in it) {
                if (i.categoryId != null)
                    if (i.categoryId == categoryId) {
                        if (i.subTaskConfig != null && i.subTaskConfig!!.label != null)
                            lebel = i.subTaskConfig!!.label
                    }
            }
        }

        return lebel
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                if (mPagerAdapter.getItem(0) is TaskDetailsFragment) {
                    (mPagerAdapter.getItem(0) as TaskDetailsFragment).hideBottomSheetFromOutSide(
                        event
                    )
                } else if (mPagerAdapter.getItem(1) is TaskDetailsFragment) {
                    (mPagerAdapter.getItem(1) as TaskDetailsFragment).hideBottomSheetFromOutSide(
                        event
                    )
                }
            } catch (e: ClassCastException) {

            }

        }
        return super.dispatchTouchEvent(event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        try {
            mActivityNewTaskDetailBinding.shimmerViewContainer.visibility = View.GONE
            mActivityNewTaskDetailBinding.topHeaderLayout.visibility = View.VISIBLE
            mActivityNewTaskDetailBinding.tabLayout.visibility = View.VISIBLE

            hideLoading()
            if (CommonUtils.handleResponse(callback, error, result, this)) {
                taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
                mActivityNewTaskDetailBinding.data = taskResponse
                setData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside handleResponse(): $e")
        }
    }

    private fun setData() {
        val tabIcons: ArrayList<Int> = ArrayList()
        val jsonString =
            GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(taskResponse)

        if (taskResponse?.taskDetail!!.clientTaskId != null && taskResponse?.taskDetail!!.clientTaskId!!.isNotEmpty()) {
            textViewId.text = taskResponse?.taskDetail!!.clientTaskId!!
        }

        fragments.add(
            TabDataClass(
                TaskDetailsFragment.newInstance(taskId, categoryId, from, jsonString),
                "Details"
            )
        )
        tabIcons.add(R.drawable.ic_details)

        if (allowSubTask) {
            tabLayout.visibility = View.VISIBLE
            fragments.add(
                TabDataClass(
                    SubTaskFragment.newInstance(
                        allowSubTask,
                        subTaskCategoryId,
                        categoryId,
                        fromDate,
                        toDate,
                        taskId,
                        referenceId
                    ),
                    getLabelName(categoryId)
                )
            )
            tabIcons.add(R.drawable.ic_details)
        }

        if (!taskResponse?.taskDetail!!.orderDetails.isNullOrEmpty()) {
            fragments.add(
                TabDataClass(
                    InventoryFragment.newInstance(jsonString),
                    "Orders"
                )
            )
            tabIcons.add(R.drawable.ic_inventory)
        }

        val listHistory = ArrayList(taskResponse?.taskDetail!!.stageHistory!!)
        if (!listHistory.isNullOrEmpty()) {
            fragments.add(
                TabDataClass(
                    TimelineFragment.newInstance(jsonString, false),
                    "Timeline"
                )
            )
            tabIcons.add(R.drawable.ic_timeline)
        }

        val list =
            ArrayList(taskResponse?.taskDetail!!.stageHistory!!).filter { it.dfdId != null && it.dfdId!!.isNotEmpty() }
        if (!list.isNullOrEmpty()) {
            fragments.add(
                TabDataClass(
                    TimelineFragment.newInstance(jsonString, true),
                    "Forms"
                )
            )
            tabIcons.add(R.drawable.ic_forms)
        }

        if (taskResponse?.taskDetail!!.trackable) {
            Log.d("getCurrentTrackingId", taskId)
            if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                fragments.add(
                    TabDataClass(
                        TripDetailsFragment.newInstance(jsonString, false),
                        "Trip Details"
                    )
                )
            } else {
                if (preferencesHelper.userDetail != null && preferencesHelper.userDetail.userId != null && preferencesHelper.userDetail!!.userId!!.isNotEmpty()) {
                    fragments.add(
                        TabDataClass(
                            TripDetailsFragment.newInstance(jsonString, true),
                            "Trip Details"
                        )
                    )
                }
            }
            tabIcons.add(R.drawable.ic_tracking)
        }

        if (!taskResponse?.taskDetail!!.encCodeUrl.isNullOrEmpty()) {
            fragments.add(
                TabDataClass(
                    OrderCodeFragment.newInstance(
                        taskResponse?.taskDetail!!.encCodeUrl!!,
                        categoryId
                    ),
                    "QR/Bar Code"
                )
            )
            tabIcons.add(R.drawable.ic_qr)
        }

        mPagerAdapter.setFragments(fragments)
        vpTask.adapter = mPagerAdapter
        tabLayout.setupWithViewPager(vpTask)

        vpTask.offscreenPageLimit = tabLayout.tabCount
        vpTask.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        setupRecyclerView()
        setupTabIcons(tabIcons)
    }

    private fun setupTabIcons(tabIcons: ArrayList<Int>) {
        tabIcons.forEachIndexed { index, i ->
            tabLayout.getTabAt(index)?.icon = ContextCompat.getDrawable(applicationContext, i)
        }
    }

    private fun setupRecyclerView() {
        mActivityNewTaskDetailBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CustomAdapter(createHeroList()) { hero, position ->
                // Create an alert builder
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@NewTaskDetailsActivity)
                // set the custom layout
                val customLayout: View = layoutInflater.inflate(R.layout.action_layout, null)
                builder.setView(customLayout)
                val dialog: AlertDialog = builder.create()

                val profileTitle: TextView = customLayout.findViewById(R.id.textViewTitle)
                profileTitle.text = hero.title

                val profileName: TextView = customLayout.findViewById(R.id.textViewName)
                profileName.text = hero.name

                val profileImage: ImageView = customLayout.findViewById(R.id.textView11)
                if (!hero.image.isNullOrEmpty()) {
                    GlideApp.with(this@NewTaskDetailsActivity)
                        .asBitmap()
                        .load(profileImage)
                        .apply(
                            RequestOptions()
                                .transform(com.rf.taskmodule.ui.custom.CircleTransform())
                                .placeholder(R.drawable.ic_user)
                        ).error(R.drawable.ic_user)
                }

                val mobileButton: Button = customLayout.findViewById(R.id.button5)
                val emailButton: Button = customLayout.findViewById(R.id.button6)

                val mobile: TextView = customLayout.findViewById(R.id.textViewMobile)
                mobile.text = hero.phone

                mobileButton.setOnClickListener {
                    CommonUtils.openDialer(this@NewTaskDetailsActivity, hero.phone)
                    dialog.dismiss()
                }
                val email: TextView = customLayout.findViewById(R.id.textViewEmail)
                val emailIcon: ImageView = customLayout.findViewById(R.id.textView15)

                if (hero.email.isNullOrEmpty()) {
                    email.visibility = View.GONE
                    emailIcon.visibility = View.GONE
                    emailButton.visibility = View.GONE
                } else {
                    email.text = hero.email
                    emailButton.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val data = Uri.parse(
                            "mailto:" + hero.email
                        )
                        intent.data = data
                        startActivity(intent)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
        }
    }

    private fun createHeroList(): ArrayList<UserDetails> {
        val list: ArrayList<UserDetails> = ArrayList()

        if (!taskResponse?.taskDetail?.contact?.name.isNullOrEmpty()) {
            val pocLabel = CommonUtils.getAllowFieldLabelName(
                "POINT_OF_CONTACT",
                categoryId,
                preferencesHelper
            )

            val label = pocLabel.ifEmpty {
                "Point of Contact"
            }
            list.add(
                UserDetails(
                    label,
                    taskResponse?.taskDetail?.contact?.profileImage ?: "",
                    taskResponse?.taskDetail?.contact?.name ?: "",
                    taskResponse?.taskDetail?.contact?.mobile ?: "",
                    taskResponse?.taskDetail?.contact?.email ?: "",
                )
            )
        }

        if (!taskResponse?.taskDetail?.assignedToDetails.isNullOrEmpty() && !taskResponse?.taskDetail?.assignedToDetails?.get(
                0
            )?.name.isNullOrEmpty()
        ) {
            list.add(
                UserDetails(
                    "Assign To",
                    taskResponse?.taskDetail?.assignedToDetails?.get(0)?.profileImage ?: "",
                    taskResponse?.taskDetail?.assignedToDetails?.get(0)?.name ?: "",
                    taskResponse?.taskDetail?.assignedToDetails?.get(0)?.mobile ?: "",
                    taskResponse?.taskDetail?.assignedToDetails?.get(0)?.email ?: "",
                )
            )
        }

        if (!taskResponse?.taskDetail?.requestedUser?.name.isNullOrEmpty()) {
            val requestedByLabel = CommonUtils.getRequestedByLabel(
                categoryId,
                preferencesHelper
            )
            val label = requestedByLabel.ifEmpty {
                "Client Details"
            }

            list.add(
                UserDetails(
                    label,
                    taskResponse?.taskDetail?.requestedUser?.profileImage ?: "",
                    taskResponse?.taskDetail?.requestedUser?.name ?: "",
                    taskResponse?.taskDetail?.requestedUser?.mobile ?: "",
                    taskResponse?.taskDetail?.requestedUser?.email ?: "",
                )
            )
        }

        if (!taskResponse?.taskDetail?.assigneeDetail?.name.isNullOrEmpty()) {
            list.add(
                UserDetails(
                    "Created By",
                    taskResponse?.taskDetail?.assigneeDetail?.profileImage ?: "",
                    taskResponse?.taskDetail?.assigneeDetail?.name ?: "",
                    taskResponse?.taskDetail?.assigneeDetail?.mobile ?: "",
                    taskResponse?.taskDetail?.assigneeDetail?.email ?: "",
                )
            )
        }

        Log.d("list", list.toString())
        return list
    }


    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
    }

    override fun expandCollapse() {
    }

    fun updateData(jsonString: String) {
        taskResponse = Gson().fromJson(jsonString, TaskResponse::class.java)
        mActivityNewTaskDetailBinding.data = taskResponse
        setupRecyclerView()
        Log.d("update", "update Data")
    }
}
