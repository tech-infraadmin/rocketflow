package com.rf.taskmodule.ui.taskdetails.timeline

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.rocketflow.sdk.RocketFlyer
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.local.prefs.StartIdealTrackWork
import com.rf.taskmodule.data.model.BaseResponse
import com.rf.taskmodule.data.model.GeofenceData
import com.rf.taskmodule.data.model.request.*
import com.rf.taskmodule.data.model.response.config.*
import com.rf.taskmodule.data.model.response.config.TaskData
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ItemDynamicFormVideoSdkBinding
import com.rf.taskmodule.databinding.LayoutFrgmrntTaskDetailsSdkBinding
import com.rf.taskmodule.ordercode.OrderCodeActivity
import com.rf.taskmodule.ui.base.BaseSdkFragment
//import com.rf.taskmodule.ui.chat.ChatActivity
import com.rf.taskmodule.ui.common.DoubleButtonDialog
import com.rf.taskmodule.ui.common.OnClickListener
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.ui.custom.socket.*
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity.Companion.newIntent
//import com.rf.taskmodule.ui.messages.MessagesActivity
import com.rf.taskmodule.ui.newcreatetask.NewCreateTaskActivity
//import com.rf.taskmodule.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.rf.taskmodule.ui.selectorder.SelectOrderActivity
import com.rf.taskmodule.ui.taskdetails.*
import com.rf.taskmodule.ui.taskdetails.timeline.skuinfo.SkuInfoActivity
import com.rf.taskmodule.ui.taskdetails.timeline.skuinfopreview.SkuInfoPreviewActivity
import com.rf.taskmodule.ui.tasklisting.TaskClickListener
import com.rf.taskmodule.ui.tasklisting.assignedtome.TaskAssignToMeViewModel
import com.rf.taskmodule.ui.userlisting.UserListNewActivity
import com.rf.taskmodule.ui.webview.PaymentViewActivity
import com.rf.taskmodule.ui.webview.WebViewActivity
import com.rf.taskmodule.utils.*
import com.rf.taskmodule.utils.AppConstants.Extra
import com.rf.taskmodule.utils.geofence.AddGeoFenceUtil
//import com.rf.taskmodule.utils.geofence.AddGeoFenceUtil
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import kotlinx.android.synthetic.main.item_dynamic_form_time_sdk.*
import kotlinx.android.synthetic.main.layout_frgmrnt_task_details_sdk.*
import com.rf.taskmodule.ui.addplace.Hub
import com.rf.taskmodule.ui.addplace.LocationListResponse
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class TaskDetailsFragment :
    BaseSdkFragment<LayoutFrgmrntTaskDetailsSdkBinding, NewTaskDetailsViewModel>(),
    TaskAssignToMeViewModel.AssignedtoMeItemViewModelListener,
    TaskClickListener, TaskTimeLineAdapter.PreviousFormListener,
    TDNavigator, DynamicAdapter.AdapterListener {

    private var ecRequest: ExecuteUpdateRequest = ExecuteUpdateRequest("", "", 0L, null, null)

    private val REQUEST_CAMERA: Int = 101
    private var qrUrl = "na"
    var resp = "na"
    var timeLineMore = false
    lateinit var foundWidgetItem: FoundWidgetItem
    var offside = 1
    private var recyclerCtaButton: RecyclerView? = null
    private var tvLabelTakeAction: TextView? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var roomId: String? = null
    private var buddyName: String? = null
    private var buddyId: String? = null
    private var categoryId: String? = null
    private var mobile: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var listBuddy = ArrayList<Buddy>()

    private var ctaActivate = false
    private var ctaMode: String = ""
    private var verificationId: String = ""
    var ctaTitle = ""
    lateinit var mNewTaskViewModel: NewTaskDetailsViewModel

    lateinit var rvDynamicForm: RecyclerView

    lateinit var rvFields: RecyclerView

    var paymentUrl = ""

    var scannerFieldName: String? = null
    var scannerFieldValue: String? = null
    private var dynamicFragment: DynamicFragment? = null
    private var ctaId: String? = null
    private var taskAction: String? = null
    private var tcfId: String? = null
    private var geofenceData: GeofenceData? = null
    private var formId: String? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var isEditable: Boolean = false
    private var isHideButton: Boolean = false

    lateinit var httpManager: HttpManager
    lateinit var preferencesHelper: PreferencesHelper
    //var addGeoFenceUtil: AddGeoFenceUtil? = null

    private lateinit var mActivityNewTaskDetailBinding: LayoutFrgmrntTaskDetailsSdkBinding

    private var task: Task? = null
    private var taskRef: Task? = null
    var highList: kotlin.collections.List<StageHistoryData> = kotlin.collections.ArrayList()

    lateinit var showDynamicFormDataAdapter: ShowDynamicFormDataAdapter

    var addGeoFenceUtil: AddGeoFenceUtil? = null

    var taskId: String? = null
    private var api: Api? = null
    private var taskResponse: TaskResponse? = null
    var listSlots: ArrayList<SlotData> = ArrayList()
    var listKeys: ArrayList<String> = ArrayList()
    private lateinit var rvSlot: RecyclerView
    private lateinit var slotImg: ImageView
    private lateinit var rvDate: RecyclerView
    private lateinit var dialogSlot: Dialog
    private lateinit var llSlots: LinearLayout
    private lateinit var ivNoData: ImageView
    private var currentLocation: GeoCoordinates? = null
    private var callToActions: CallToActions? = null
    private var ctaID: String? = null
    private var dfId: String? = null
    private var dfdId: String? = null
    private var FROM: String? = null
    private var refCall: Boolean? = false
    private val formList = ArrayList<String>()

    var slotDataResponse: SlotDataResponse = SlotDataResponse()

    private var timePosition = 0
    private var keyPosition = 0
    private var dayPosition = 0
    private var key = "0"
    private var dateFinal = ""
    private var date = ""
    private var timeFinal = ""
    private var hubIdFinal = ""
    private var hubs: List<Hub> = ArrayList()
    private var hub: Hub? = null

    private var fieldArrayList = ArrayList<String>()
    private var allowedFields = ArrayList<Field>()

    private lateinit var timeLineAdapter: TaskTimeLineAdapter

    private val TAG = "TaskDetailsFragment"

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_frgmrnt_task_details_sdk
    }


    override fun getViewModel(): NewTaskDetailsViewModel {

        val factory =
            RocketFlyer.dataManager()?.let { NewTaskDetailsViewModel.Factory(it) } // Factory
        if (factory != null) {
            mNewTaskViewModel =
                ViewModelProvider(this, factory)[NewTaskDetailsViewModel::class.java]
        }
        return mNewTaskViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNewTaskViewModel.navigator = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivityNewTaskDetailBinding = viewDataBinding

        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!
        //container = mActivityNewTaskDetailBinding.container
        timeLineAdapter = TaskTimeLineAdapter(this)
        mActivityNewTaskDetailBinding.timeLineAdapter = timeLineAdapter
        val colayout = mActivityNewTaskDetailBinding.coradinatorLL as CoordinatorLayout
        var bottomSheet = colayout.findViewById<View>(R.id.bottomSheet)
        recyclerCtaButton = bottomSheet.findViewById(R.id.recyclerCtaButton)
        tvLabelTakeAction = bottomSheet.findViewById(R.id.tvLabelTakeAction)
        sheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        sheetBehavior.isHideable = false
        sheetBehavior.isFitToContents = false
        sheetBehavior.setPeekHeight(CommonUtils.dpToPixel(activity, 80), true)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        getTaskData()
        expandCollapse()

        showDynamicFormDataAdapter = ShowDynamicFormDataAdapter(ArrayList())

        rvDynamicForm = mActivityNewTaskDetailBinding.rvDynamicFormsMini
        rvDynamicForm.layoutManager = LinearLayoutManager(baseActivity)
        rvDynamicForm.adapter = showDynamicFormDataAdapter

        //rvDynamicForm.adapter = showDynamicFormDataAdapter

        addGeoFenceUtil = AddGeoFenceUtil(baseActivity, preferencesHelper)
        tvLabelTakeAction!!.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    companion object {
        fun newInstance(taskId: String?, categoryId: String?, FROM: String?): TaskDetailsFragment? {
            val args = Bundle()
            args.putString(Extra.EXTRA_TASK_ID, taskId)
            args.putString(Extra.EXTRA_CATEGORY_ID, categoryId)
            args.putString(Extra.FROM, FROM)
            val fragment = TaskDetailsFragment()
            fragment.arguments = args
            return fragment
        }

    }


    private fun getTaskData() {
        if (arguments != null) {

            if (requireArguments().getString(Extra.EXTRA_TASK_ID) != null) {
                taskId = requireArguments().getString(Extra.EXTRA_TASK_ID)
            }
            if (requireArguments().getString(Extra.FROM) != null) {
                FROM = requireArguments().getString(Extra.FROM)
            }
            if (requireArguments().getString(Extra.EXTRA_CATEGORY_ID) != null) {
                categoryId = requireArguments().getString(Extra.EXTRA_CATEGORY_ID)
            }
            Log.d("categoryId", categoryId)
            showLoading()
            api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]
            if (api != null && ::mNewTaskViewModel.isInitialized)
                mNewTaskViewModel.getTaskById(httpManager, AcceptRejectRequest(taskId!!), api)


            if (categoryId != null) {
                val startLocationLabel =
                    CommonUtils.getAllowFieldLabelName(
                        "START_LOCATION",
                        categoryId,
                        preferencesHelper
                    )

                if (!startLocationLabel.isEmpty()) {
                    if (labelStartLocation != null)
                        labelStartLocation.text = startLocationLabel
                }
                val taskId =
                    CommonUtils.getAllowFieldLabelName("TASK_ID", categoryId, preferencesHelper)
                if (!taskId.isEmpty()) {
                    // label_id.setText(taskId)
                }
                val reffenceId = CommonUtils.getAllowFieldLabelName(
                    "REFERENCE_ID",
                    categoryId,
                    preferencesHelper
                )
                if (!reffenceId.isEmpty()) {
                    tvLabelRefferenceId.text = reffenceId
                }
                val pocLabel = CommonUtils.getAllowFieldLabelName(
                    "POINT_OF_CONTACT",
                    categoryId,
                    preferencesHelper
                )
                if (!pocLabel.isEmpty()) {
                    tvLabelPoc.text = pocLabel
                }
                val descLabel =
                    CommonUtils.getAllowFieldLabelName("DESCRIPTION", categoryId, preferencesHelper)
                if (!descLabel.isEmpty()) {
                    tvLabelDesc.text = descLabel
                }

                val endLocationName = CommonUtils.getAllowFieldLabelName(
                    "END_LOCATION",
                    categoryId,
                    preferencesHelper
                )
                if (!endLocationName.isEmpty()) {
                    labelEndLocation.text = endLocationName
                }
                val startTime =
                    CommonUtils.getAllowFieldLabelName("START_TIME", categoryId, preferencesHelper)
                if (!startTime.isEmpty()) {
                    labelScheduledAt.text = startTime
                }
                val endTime =
                    CommonUtils.getAllowFieldLabelName("END_TIME", categoryId, preferencesHelper)
                if (!endTime.isEmpty()) {
                    labelEndAt.text = endTime
                }
                val assigneeLabel =
                    CommonUtils.getAssigneeLabel(categoryId, preferencesHelper)
                if (!assigneeLabel.isEmpty()) {
                    tvClientsDetails.text = assigneeLabel
                }
                val buddyLabel =
                    CommonUtils.getBuddyLabel(categoryId, preferencesHelper)
                if (!buddyLabel.isEmpty()) {
                    tvAssignedTolable.text = buddyLabel
                }


                mActivityNewTaskDetailBinding.llQrCode.setOnClickListener {
                    if (qrUrl != "na")
                        startActivity(
                            Intent(
                                context,
                                OrderCodeActivity::class.java
                            ).putExtra("qrUrl", qrUrl)
                        )
                }

                mActivityNewTaskDetailBinding.tvTimelineMore.setOnClickListener {
                    timeLineMore = true
                    setAdapter()
                }


            }


        }
    }

    fun setMargin(dp: Int) {
        var layoutParams: RelativeLayout.LayoutParams =
            nestedScrollView.layoutParams as (RelativeLayout.LayoutParams)
        layoutParams.setMargins(0, 0, 0, CommonUtils.dpToPixel(activity, dp))
        nestedScrollView.layoutParams = layoutParams;
    }


    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        try {


            if (CommonUtils.handleResponse(callback, error, result, baseActivity)) {

                taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
                if (taskResponse != null) {

                    if (refCall == false) {

                        task = taskResponse!!.taskDetail
                        if (categoryId.isNullOrEmpty())
                            categoryId = task?.categoryId
                        val itemViewModel =
                            TaskAssignToMeViewModel(
                                task,
                                this, baseActivity, preferencesHelper, categoryId
                            )
                        mActivityNewTaskDetailBinding.data = itemViewModel
                        llMain.visibility = View.VISIBLE
                        hideLoading()
                        var isShow = CommonUtils.handleCallToActionsNew(
                            baseActivity,
                            task,
                            recyclerCtaButton,
                            this
                        )
                        if (isShow) {
                            tvLabelTakeAction!!.visibility = View.VISIBLE
                            setMargin(50)

                        } else {
                            tvLabelTakeAction!!.visibility = View.GONE
                            setMargin(0)
                        }
                        if (task!!.clientTaskId != null && task!!.clientTaskId!!.isNotEmpty()) {
                            tvTaskId.text = task!!.clientTaskId!!
                        }
                        perFormTimerReminder(task)
                        try {
                            perFormLocationReminder(task)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }

                        if (task!!.trackingState != null) {
                            //stop tracking
                            // handleTracking(task!!.trackingState!!, task!!.taskId!!)
                        }

                        if (task!!.multiReferedTask != null) {
                            if (task!!.multiReferedTask!!.size > 0) {
                                if (task!!.multiReferedTask!![0] != "") {
                                    mActivityNewTaskDetailBinding.rlReference.visibility =
                                        View.VISIBLE
                                    //getDataHere
                                    refCall = true
                                    api = TrackiSdkApplication.getApiMap()[ApiType.GET_TASK_BY_ID]
                                    if (task!!.multiReferedTask!!.size > 0) {
                                        if (task!!.multiReferedTask!![0] != null) {
                                            mNewTaskViewModel.getTaskById(
                                                httpManager,
                                                AcceptRejectRequest(task!!.multiReferedTask!![0]),
                                                api
                                            )
                                        }
                                    }
                                } else {
                                    mActivityNewTaskDetailBinding.rlReference.visibility = View.GONE
                                }
                            } else {
                                mActivityNewTaskDetailBinding.rlReference.visibility = View.GONE
                            }
                        } else {
                            mActivityNewTaskDetailBinding.rlReference.visibility = View.GONE
                        }

                        if (task!!.orderDetails != null && task!!.orderDetails!!.isNotEmpty()) {
                            cardOrders.visibility = View.VISIBLE
                            val adapter = OrderListAdapter(task!!.orderDetails)
                            rvOrderList.adapter = adapter
                            adapter.onItemClick = { item ->
                                val intent = SkuInfoPreviewActivity.newIntent(requireActivity())
                                intent.putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                                intent.putExtra(Extra.EXTRA_PRODUCT_ID, item.productId.toString())
                                intent.putExtra(
                                    Extra.EXTRA_PRODUCT_NAME,
                                    item.productName.toString()
                                )
                                startActivityForResult(
                                    intent,
                                    AppConstants.REQUEST_CODE_TAG_INVENTORY
                                )
                            }
                        } else if (task!!.products != null && task!!.products!!.isNotEmpty()) {
                            cardOrders.visibility = View.VISIBLE
                            tvInventoriesLabel.text = getString(R.string.inventory_label)
                            val adapter = ProductOrderAdapter(task!!.products)
                            rvOrderList.adapter = adapter
                        } else {
                            cardOrders.visibility = View.GONE
                        }
                        ivPinStartLocation.setOnClickListener {

                            if (task!!.source != null && task!!.source!!.location != null) {
                                CommonUtils.showLogMessage(
                                    "e",
                                    "source point map",
                                    "source start points map"
                                )
                                val geoCoordinates = task!!.source!!.location
                                CommonUtils.openGoogleMapWithOneLocation(
                                    context,
                                    geoCoordinates!!.latitude,
                                    geoCoordinates.longitude
                                )
                            }

                        }

                        Log.e("code", "check => ${task!!.encCodeUrl}")
                        if (task!!.encCodeUrl != null) {
                            qrUrl = task!!.encCodeUrl.toString()
                            GlideApp.with(baseActivity).load(qrUrl)
                                .into(mActivityNewTaskDetailBinding.ivQrCode)
                        } else {

                            mActivityNewTaskDetailBinding.llQrCode.visibility = View.GONE
                        }
                        ivPinEndLocation.setOnClickListener {

                            if (task!!.destination != null && task!!.destination!!.location != null) {
                                CommonUtils.showLogMessage(
                                    "e",
                                    "destination point map",
                                    "source start points map"
                                )
                                val geoCoordinates = task!!.destination!!.location
                                CommonUtils.openGoogleMapWithOneLocation(
                                    context,
                                    geoCoordinates!!.latitude,
                                    geoCoordinates.longitude
                                )
                            }

                        }

                        if (task!!.trackable) {
                            ivCurrentStageLocation.visibility = View.VISIBLE
//                        ivCurrentStageLocation.visibility = View.GONE
                            ivCurrentStageLocation.setOnClickListener {
                                if (FROM.equals(AppConstants.Extra.ASSIGNED_TO_ME)) {
                                    startActivity(
                                        TaskDetailActivity.Companion.newIntent(baseActivity)
                                            .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                                    );
                                } else {
                                    if (preferencesHelper.userDetail != null && preferencesHelper.userDetail.userId != null && !preferencesHelper.userDetail!!.userId!!.isEmpty()) {
                                        val navigation = Navigation()
                                        val actionConfig = ActionConfig()
                                        val userId = preferencesHelper.userDetail.userId
                                        actionConfig.actionUrl =
                                            task!!.trackingUrl + "&userId=" + userId
                                        // actionConfig.actionUrl = task!!.trackingUrl
                                        navigation.actionConfig = actionConfig
                                        navigation.title = "Tracking Details"
                                        startActivity(
                                            WebViewActivity.newIntent(baseActivity)
                                                .putExtra(
                                                    AppConstants.Extra.EXTRA_WEB_INFO,
                                                    navigation
                                                )
                                        )
                                    }
                                }
                            }
                        }
                        rlAssignedToTrack.setOnClickListener(View.OnClickListener {
                            if (FROM.equals(AppConstants.Extra.ASSIGNED_TO_ME)) {
                                startActivity(
                                    TaskDetailActivity.Companion.newIntent(baseActivity)
                                        .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                                );
                            } else {
                                if (preferencesHelper.userDetail != null && preferencesHelper.userDetail.userId != null && !preferencesHelper.userDetail!!.userId!!.isEmpty()) {
                                    val navigation = Navigation()
                                    val actionConfig = ActionConfig()
                                    val userId = preferencesHelper.userDetail.userId
                                    actionConfig.actionUrl =
                                        task!!.trackingUrl + "&userId=" + userId
                                    // actionConfig.actionUrl = task!!.trackingUrl
                                    navigation.actionConfig = actionConfig
                                    navigation.title = "Tracking Details"
                                    startActivity(
                                        WebViewActivity.newIntent(baseActivity)
                                            .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation)
                                    )
                                }
                            }
                        })

                        if (task!!.stageHistory != null && task!!.stageHistory!!.isNotEmpty()) {
                            val list = ArrayList(task!!.stageHistory!!)
                                .sortedWith(compareByDescending<StageHistoryData> { it.timeStamp }
                                )
                            val history = list[list.size - 1]

                            if (history != null) {
                                if (history.dfdId != null) {
                                    formId = history.dfdId
                                    if (formId != null && formId!!.isNotEmpty()) {
                                        var data = GetTaskDataRequest()
                                        data.dfdId = history.dfdId
                                        dynamicFormsNew = CommonUtils.getFormByFormId(formId!!)
                                        if (dynamicFormsNew != null && dynamicFormsNew!!.version != null) {
                                            data.dfVersion =
                                                Integer.valueOf(dynamicFormsNew!!.version!!)
                                        }
                                        data.taskId = taskId
                                        mNewTaskViewModel.getTaskData(httpManager, data)

                                    }
                                }
                            }
                            highList = list
                            setAdapter()


                        }

                        Log.d("timeline", timeLineAdapter.itemCount.toString())
                        if (timeLineAdapter.itemCount < 1) {
                            mActivityNewTaskDetailBinding.llTimeline.visibility = View.GONE
                        }

                    } else {

                        if (taskResponse!!.taskDetail != null) {
                            mActivityNewTaskDetailBinding.rlReference.visibility = View.VISIBLE
                            taskRef = taskResponse!!.taskDetail

                            mActivityNewTaskDetailBinding.rlReference.setOnClickListener {
                                val intent =
                                    Intent(requireContext(), NewTaskDetailsActivity::class.java)
                                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskRef!!.taskId)
                                intent.putExtra(
                                    AppConstants.Extra.EXTRA_ALLOW_SUB_TASK,
                                    taskRef!!.allowSubTask
                                )
                                intent.putExtra(
                                    AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID,
                                    taskRef!!.subCategoryIds
                                )
                                intent.putExtra(
                                    AppConstants.Extra.EXTRA_PARENT_REF_ID,
                                    taskRef!!.referenceId
                                )
                                intent.putExtra(
                                    AppConstants.Extra.EXTRA_CATEGORY_ID,
                                    taskRef!!.categoryId
                                )
                                intent.putExtra(
                                    AppConstants.Extra.FROM,
                                    AppConstants.Extra.ASSIGNED_TO_ME
                                )
                                startActivity(intent)
                            }
                        } else {
                            mActivityNewTaskDetailBinding.rlReference.visibility = View.GONE
                        }
                        refCall = false
                    }

                }

            } else {
                hideLoading()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside handleResponse(): $e")
        }
    }

    override fun handleSlotResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        Log.e("mode", "here1")
        val request = ExecuteUpdateRequest(
            taskId!!, ctaID!!,
            DateTimeUtil.getCurrentDateInMillis(), null, verificationId
        )
        ecRequest = request
        ctaMode = "Ext"
        switchCtaMode(ctaMode)
    }

    override fun handleGetTaskDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()

        if (CommonUtils.handleResponse(callback, error, result, context)) {
            val getTaskDataResponse = Gson().fromJson<GetTaskDataResponse>(
                result.toString(),
                GetTaskDataResponse::class.java
            )
            var data: List<TaskData>? = null
            if (getTaskDataResponse != null && getTaskDataResponse.successful) {

                if (getTaskDataResponse.data != null) {
                    data = getTaskDataResponse.data!!
                    getTaskDataResponse.data!!.let {
                        var list1 = ArrayList<TaskData>()
                        list1.addAll(it as ArrayList<TaskData>)
                        showDynamicFormDataAdapter.addData(list1)

                    }
                }
                if (getTaskDataResponse.dfdId != null) {
                    dfdId = getTaskDataResponse.dfdId!!
                }
            }

        }
    }

    override fun handlePaymentUrlResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()


        val response = Gson().fromJson<BaseResponse>(
            result.toString(),
            BaseResponse::class.java
        )
        Log.e("response", "$response")
        if (response.successful) {
            if (response.paymentUrl != null) {
                paymentUrl = response.paymentUrl!!
                if (paymentUrl.isNotEmpty()) {
                    val intent = Intent(context, PaymentViewActivity::class.java)
                    intent.putExtra("url", paymentUrl)
                    intent.putExtra(Extra.EXTRA_CATEGORY_ID, categoryId)
                    intent.putExtra(Extra.EXTRA_TASK_ID, taskId)
                    intent.putExtra(Extra.FROM, FROM)
                    startActivity(intent)
                }
            }
        }

    }

    override fun handleVerifyCtaOtpResponse(
        apiCallback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(apiCallback, error, result, baseActivity)) {
            if (result != null) {
                val response = Gson().fromJson("$result", BaseResponse::class.java)
                if (response.responseCode == "450") {
                    if (response.responseMsg != null) {
                        TrackiToast.Message.showShort(requireActivity(), response.responseMsg)
                    } else {
                        TrackiToast.Message.showShort(requireActivity(), "Invalid Verification ID")
                    }

                } else if (response.responseCode == "451") {
                    if (response.responseMsg != null) {
                        TrackiToast.Message.showShort(requireActivity(), response.responseMsg)
                    } else {
                        TrackiToast.Message.showShort(
                            requireActivity(),
                            "Unable To Get Verification ID"
                        )
                    }

                }
                ctaActivate = response.successful
                verificationId = response.verificationId.toString()

                if (response.successful) {
                    TrackiToast.Message.showShort(requireActivity(), response.responseMsg)

                    val listNew = preferencesHelper.verifiedCtas
                    var check = "${callToActions?.id.toString()}$taskId"
                    Log.e("CTAid1", "$check")
                    listNew.add(check)
                    preferencesHelper.verifiedCtas = listNew
                    switchCtaMode(ctaMode)
                }

            }
        }
    }

    fun setAdapter() {
        val list: List<StageHistoryData> = highList
        Log.e("ListSize", "${list.size}")
        if (timeLineMore == false) {
            mActivityNewTaskDetailBinding.tvTimelineMore.visibility = View.VISIBLE
            var tempList: ArrayList<StageHistoryData> = ArrayList()
            if (list.size < 1){
                mActivityNewTaskDetailBinding.tvTimelineMore.visibility = View.GONE
            }
            else if (list.size == 1) {
                mActivityNewTaskDetailBinding.tvTimelineMore.visibility = View.GONE
                tempList.add(list[0])
            }
            else{
                if (list.size == 2)
                    mActivityNewTaskDetailBinding.tvTimelineMore.visibility = View.GONE
                tempList.add(list[0])
                tempList.add(list[1])
            }
            val listTemp = tempList.toList()
            timeLineAdapter.addData(listTemp)
            mActivityNewTaskDetailBinding.rvFields.visibility = View.VISIBLE
        } else {
            mActivityNewTaskDetailBinding.tvTimelineMore.visibility = View.GONE
            timeLineAdapter.addData(list)
            mActivityNewTaskDetailBinding.rvFields.visibility = View.VISIBLE
        }
    }

    override fun handleSendCtaOtpResponse(
        apiCallback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(apiCallback, error, result, baseActivity)) {
            if (result != null) {
                val response = Gson().fromJson("$result", BaseResponse::class.java)
                TrackiToast.Message.showShort(requireActivity(), response.responseMsg)
                if (response.successful) {

                    showOtpDialog(response!!.mobile.toString())


                } else {
                    ctaActivate = false
                }
            }
        }
    }

    override fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        val slotDataResponse1 =
            Gson().fromJson(result.toString(), SlotDataResponse::class.java)
        val slotAdapter = SlotAdapter(requireContext())
        rvDate.adapter = slotAdapter
        Log.e("SlotsCheck", "$slotDataResponse1")
        if (slotDataResponse1.successful) {
            llSlots.visibility = View.VISIBLE
            ivNoData.visibility = View.GONE

            slotDataResponse = slotDataResponse1
            if (slotDataResponse.data != null) {
                slotAdapter.setMap(slotDataResponse.data!!)


                setSlots()
                slotAdapter.onItemClick =
                    { dateString: String, position: Int, listValues1: java.util.ArrayList<SlotData>, list: java.util.ArrayList<String> ->
                        selectDayCall(position)
                        listSlots = listValues1
                        listKeys = list
                        key = dateString
                    }
            } else {
                slotAdapter.clearAll()
            }
        } else {
            llSlots.visibility = View.GONE
            ivNoData.visibility = View.VISIBLE
            slotAdapter.clearAll()
            TrackiToast.Message.showShort(requireContext(), "No Slots Found")
        }
    }

    fun String.toDate(): Date {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(this)!!
    }

    private fun setSlots() {
        if (slotDataResponse.data!!.keys.isNotEmpty()) {
            val size = slotDataResponse.data!!.size
            if (size > dayPosition) {
                val key = slotDataResponse.data!!.keys.elementAt(dayPosition)
                dateFinal = key
                val timeSlots = slotDataResponse.data!![key]

                if (timeSlots?.slots != null && timeSlots?.slots!!.isNotEmpty()) {
                    rvSlot.visibility = View.VISIBLE
                    slotImg.visibility = View.GONE
                    val slots = timeSlots.slots as java.util.ArrayList<Slot>

                    val slotChildAdapter = SlotChildAdapter(slots, requireContext())
                    rvSlot.adapter = slotChildAdapter
                    Log.e("slotCheck", "$key and Pos = $dayPosition")
                    slotChildAdapter.onItemClick = { timeString ->
                        selectTimeCall(timeString)
                    }
                } else {
                    rvSlot.visibility = View.GONE
                    slotImg.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun selectTimeCall(timeString: String) {
        timeFinal = timeString
    }

    private fun selectDayCall(position: Int) {
        dayPosition = position
        setSlots()
    }
//done te to proceed
// image when no slots
// cta title title

    override fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, requireContext())) {

            val response: LocationListResponse =
                Gson().fromJson(result.toString(), LocationListResponse::class.java)
            if (response.successful!!) {
                Log.e("appLog1", "" + response.hubs)
                if (response.hubs != null && !response.hubs!!.isEmpty()) {
                    preferencesHelper.saveUserHubList(response.hubs)
                    dialogSlot = Dialog(requireContext())
                    dialogSlot.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialogSlot.window!!.setBackgroundDrawable(
                        ColorDrawable(
                            Color.TRANSPARENT
                        )
                    )
                    dialogSlot.setCancelable(false)
                    dialogSlot.setCanceledOnTouchOutside(true)
                    dialogSlot.setContentView(R.layout.item_dynamic_form_slot_sdk)
                    rvSlot = dialogSlot.findViewById<RecyclerView>(R.id.gv_slot)
                    slotImg = dialogSlot.findViewById(R.id.slotImage)
                    llSlots = dialogSlot.findViewById<LinearLayout>(R.id.ll_slots)
                    ivNoData = dialogSlot.findViewById<ImageView>(R.id.iv_no_data)

                    val ddHubs = dialogSlot.findViewById<Spinner>(R.id.dd_hubs)
                    val btnDone = dialogSlot.findViewById<AppCompatButton>(R.id.btn_done)
                    val btnClose = dialogSlot.findViewById<ImageView>(R.id.btnClose)
                    val ddTitle = dialogSlot.findViewById<TextView>(R.id.dd_title)
                    val title = dialogSlot.findViewById<TextView>(R.id.titlePop)
                    title.text = ctaTitle
                    title.visibility = View.VISIBLE
                    ddTitle.visibility = View.GONE
                    rvDate = dialogSlot.findViewById(R.id.rv_date)
                    rvSlot.layoutManager = GridLayoutManager(requireContext(), 3)

                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(dialogSlot.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.dimAmount = 0.8f
                    val window = dialogSlot.window
                    window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    window!!.setGravity(Gravity.CENTER)

                    dialogSlot.window!!.attributes = lp

                    dialogSlot.show()
                    timePosition = 0

                    callSlotApi(ddHubs)

                    btnDone!!.setOnClickListener {
                        if (dateFinal != "" && timeFinal != "") {
                            dialogSlot.dismiss()
                            //etSlot.setText("$dateFinal $timeFinal")
//                            hub = hubs.find { it.hubId == hubIdFinal }!!
                            val bookSlotRequest =
                                BookSlotRequest(ctaID, dateFinal, timeFinal, taskId)
                            resp = "slot"
                            Log.e("mode", "booking")
                            viewModel.bookSlots(httpManager, bookSlotRequest)
                        } else {
                            TrackiToast.Message.showShort(
                                requireContext(),
                                "Select Slot To Continue"
                            )
                        }
                    }
                    btnClose?.setOnClickListener {
                        dialogSlot.dismiss()
                    }
                } else {
                    preferencesHelper.saveUserHubList(ArrayList())
                }
            } else {
                preferencesHelper.saveUserHubList(ArrayList())
            }
        } else {
            preferencesHelper.saveUserHubList(ArrayList())
        }
    }

    private fun perFormLocationReminder(task: Task?) {
        if (task != null) {
            if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                if (task.source != null && task.source!!.location != null) {
                    var taskId = task.taskId + "#" + "Start Location"

                    addGeoFenceUtil!!.addGeofence(taskId, task.source!!.location, 500f)
                }
            }
            if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                if (task.destination != null && task.destination!!.location != null) {
                    var taskId = task.taskId + "#" + "End Location"
                    addGeoFenceUtil!!.addGeofence(taskId, task.destination!!.location, 500f)
                }
            }
        }
    }

    private fun showOtpDialog(mobile: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_verify_otp)
        val etOtp = dialog.findViewById(R.id.edOtp) as EditText
        val btnVerify = dialog.findViewById(R.id.btnVeriFy) as Button
        val btnCancel = dialog.findViewById(R.id.btnCancel) as Button

        btnVerify.setOnClickListener {
            dialog.dismiss()
            val otp = etOtp.text.toString()
            if (otp.isNotEmpty() && otp.isNotBlank()) {
                val verifyCtaOtpRequest = VerifyCtaOtpRequest(mobile, otp)
                mNewTaskViewModel.verifyCtaOtp(httpManager, verifyCtaOtpRequest)
            } else {
                TrackiToast.Message.showShort(requireContext(), "Please Enter Otp")
            }
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()

    }

    private fun perFormTimerReminder(task: Task?) {
        if (task != null) {
            var map = preferencesHelper.eventIdMap
            if (map != null && map.isNotEmpty()) {
                if (!map.containsKey(task.taskId)) {
//                    var eventId = map[task.taskId]
//                    CommonUtils.deleteEventUri(context, eventId, task.taskId, preferencesHelper)
                    if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                        var titile =
                            "Task going to start in " + preferencesHelper.timeBeforeTime + " minutes "
                        var desc = ""
                        var place = ""
                        var startDate = task.startTime
                        var needReminder = true
                        var needMainSerVice = false
                        var taskId = task.taskId
//                        if (task.clientTaskId != null)
//                            titile = task.clientTaskId!!
                        if (task.description != null)
                            desc = task.description!!
                        if (task.source != null && task.source!!.address != null)
                            place = task.source!!.address!!
                        CommonUtils.addAppointmentsToCalender(
                            baseActivity, titile, desc, place, 1, startDate, needReminder,
                            needMainSerVice, taskId, preferencesHelper
                        )
                    }
                    if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                        var titile =
                            "Task going to end in " + preferencesHelper.timeBeforeTime + " minutes "
                        var desc = ""
                        var place = ""
                        var startDate = task.endTime
                        var needReminder = true
                        var needMainSerVice = false
                        var taskId = task.taskId
//                        if (task.clientTaskId != null)
//                            titile = task.clientTaskId!!
                        if (task.description != null)
                            desc = task.description!!
                        if (task.destination != null && task.destination!!.address != null)
                            place = task.destination!!.address!!
                        CommonUtils.addAppointmentsToCalender(
                            baseActivity, titile, desc, place, 1, startDate, needReminder,
                            needMainSerVice, taskId, preferencesHelper
                        )
                    }
                }

            } else {
                if (task.currentStage != null && !task.currentStage!!.terminal!! && task.startTime > System.currentTimeMillis()) {
                    var titile =
                        "Task going to start in " + preferencesHelper.timeBeforeTime + " minutes "
                    var desc = ""
                    var place = ""
                    var startDate = task.startTime
                    var needReminder = true
                    var needMainSerVice = false
                    var taskId = task.taskId

                    if (task.description != null)
                        desc = task.description!!
                    if (task.source != null && task.source!!.address != null)
                        place = task.source!!.address!!
                    CommonUtils.addAppointmentsToCalender(
                        baseActivity, titile, desc, place, 1, startDate, needReminder,
                        needMainSerVice, taskId, preferencesHelper
                    )
                }
                if (task.currentStage != null && !task.currentStage!!.terminal!! && task.endTime > System.currentTimeMillis()) {
                    var titile =
                        "Task going to end in " + preferencesHelper.timeBeforeTime + " minutes "
                    var desc = ""
                    var place = ""
                    var startDate = task.endTime
                    var needReminder = true
                    var needMainSerVice = false
                    this.taskId = task.taskId
                    if (task.description != null)
                        desc = task.description!!
                    if (task.destination != null && task.destination!!.address != null)
                        place = task.destination!!.address!!
                    CommonUtils.addAppointmentsToCalender(
                        baseActivity, titile, desc, place, 1, startDate, needReminder,
                        needMainSerVice, taskId, preferencesHelper
                    )
                }
            }


        }

    }

    override fun expandCollapse() {
//        if (mActivityNewTaskDetailBinding.collapse.visibility == View.VISIBLE) {
//            mActivityNewTaskDetailBinding.collapse.visibility = View.GONE
//            ivTimeLineCollapse.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_plus))
//
//        } else {
//            mActivityNewTaskDetailBinding.collapse.visibility = View.VISIBLE
//            ivTimeLineCollapse.setImageDrawable(ContextCompat.getDrawable(baseActivity, R.drawable.ic_minus))
//        }

    }

    override fun onDetailsClick(task: Task?) {

    }

    override fun onChatStart(buddyId: String?, buddyName: String?) {
        if (buddyId != null && buddyId.isNotEmpty()) {
            val list = ArrayList<String>()
            list.add(buddyId!!)

//            startActivity(
//                ChatActivity.newIntent(baseActivity)
//                    .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
//                    .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
//                    .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, true)
//                    .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId)
//            )
        }

    }


    override fun onClickMapIcon(task: Task?) {


        //if current location is not available then notify user by showing toast else open google map.
        if (currentLocation != null) {
            var sLat = 0.0
            var sLng = 0.0
            var dLat = 0.0
            var dLng = 0.0
            //if status is arrived then get the source location and open g-map.
            if (task!!.status === TaskStatus.ACCEPTED) {
                if (task!!.source != null && task!!.source!!.location != null) {
                    sLat = currentLocation!!.latitude
                    sLng = currentLocation!!.longitude
                    dLat = task!!.source!!.location!!.latitude
                    dLng = task!!.source!!.location!!.longitude
                }
            } else if (task!!.status === TaskStatus.LIVE) {
                if (task!!.destination != null && task!!.destination!!.location != null) {
                    sLat = currentLocation!!.latitude
                    sLng = currentLocation!!.longitude
                    dLat = task!!.destination!!.location!!.latitude
                    dLng = task!!.destination!!.location!!.longitude
                }
            }
            if (sLat != 0.0 && sLng != 0.0 && dLat != 0.0 && dLng != 0.0) {
                //open google map activity
                CommonUtils.openGoogleMap(baseActivity, sLat, sLng, dLat, dLng)
            }
        } else {
            TrackiToast.Message.showShort(baseActivity, "No location found. Try Again")
        }
    }


    override fun onItemClick(bean: Task?) {

    }

    override fun onCallClick(mobile: String?) {
        if (mobile != null) {
            CommonUtils.openDialer(baseActivity, mobile)
        }
    }

    override fun onChatClick(buddyId: String?, name: String?) {

    }


    fun getInvAction(categoryId: String?): String? {
        var invAction: String? = null
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        if (!workFlowCategoriesList.isNullOrEmpty()) {
            for (i in workFlowCategoriesList) {
                if (i.categoryId != null && categoryId != null)
                    if (i.categoryId == categoryId) {

                        if (i.inventoryConfig != null && i.inventoryConfig!!.invAction != null && i.inventoryConfig!!.invAction!!.name.isNotEmpty())
                            invAction = i.inventoryConfig!!.invAction!!.name
                    }
            }
        }


        return invAction
    }

    fun String.getCtaInventoryConfig(): CtaInventoryConfig {
        var ctaInventoryConfig = CtaInventoryConfig()
        try {
            var jsonConverter =
                JSONConverter<CtaInventoryConfig>()
            ctaInventoryConfig = jsonConverter.jsonToObject(this, CtaInventoryConfig::class.java)
        } catch (e: JsonParseException) {
            return ctaInventoryConfig
        }
        return ctaInventoryConfig
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanActivity()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (baseActivity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openScanActivity()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA
                )
            }
        } else {
            openScanActivity()
        }
    }

    private fun openScanActivity() {
//        var scanActivity = Intent(baseActivity!!, ScanQrAndBarCodeActivity::class.java)
//        scanActivity.putExtra("from","taskdetails")
//        baseActivity.startActivityForResult(scanActivity, AppConstants.REQUEST_CODE_SCAN)
    }

    private fun perFormCtaAction() {
        Log.e("checkLog", "${callToActions!!.targetInfo!!.target}")

        if (callToActions!!.targetInfo!!.target == TRAGETINFO.PAYMENT) {
            checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "payment")
        } else if (callToActions!!.dynamicFormId != null && callToActions!!.dynamicFormId!!.isNotEmpty()) {
            checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "dynaForm")
        } else if (callToActions!!.targetInfo!!.target == TRAGETINFO.BOOK_SLOT) {
            checkCtaActivation(
                callToActions!!.enableOtp!!,
                callToActions!!,
                TRAGETINFO.BOOK_SLOT.name
            )
        } else {

            var setDirect = false
            setDirect = callToActions!!.targetInfo!!.target == TRAGETINFO.ASSIGN_EXECUTIVE
            if (setDirect) {
                try {
                    if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target === TRAGETINFO.TAG_INVENTORY) {
                        var mode =
                            checkCtaActivation(
                                callToActions!!.enableOtp!!,
                                callToActions!!,
                                "TAG_INVENTORY"
                            )
                    } else {
                        checkConditionsAndRequestAPI(null)
                    }
                } catch (e: java.lang.Exception) {
                    hideLoading()
                    Log.e(TAG, "onExecuteUpdates: ${e}")
                    e.printStackTrace()
                }
            } else {
                if (callToActions!!.targetInfo!!.target == TRAGETINFO.UNIT_INFO) {

                    var mode = checkCtaActivation(
                        callToActions!!.enableOtp!!,
                        callToActions!!,
                        "UNIT_INFO"
                    )

                } else {
                    val message = "Are you sure you want to perform ?"
                    val dialog = DoubleButtonDialog(requireContext(),
                        true,
                        null,
                        message,
                        getString(R.string.yes),
                        getString(R.string.no),
                        object : OnClickListener {
                            override fun onClickCancel() {}
                            override fun onClick() {
                                try {
                                    if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target === TRAGETINFO.TAG_INVENTORY) {
                                        checkCtaActivation(
                                            callToActions!!.enableOtp!!,
                                            callToActions!!,
                                            "TAG2"
                                        )
                                    } else {
                                        checkCtaActivation(
                                            callToActions!!.enableOtp!!,
                                            callToActions!!,
                                            "TAG3"
                                        )
                                    }
                                } catch (e: java.lang.Exception) {
                                    hideLoading()
                                    Log.e(TAG, "onExecuteUpdates: ${e}")
                                    e.printStackTrace()
                                }
                            }
                        })
                    dialog.show()
                }
            }

        }
    }

    private fun checkCtaActivation(
        checkFlag: Boolean,
        callToActions: CallToActions,
        mode: String
    ): Int {
        Log.e("modeRe", "$mode")
        offside = 1
        ctaMode = mode
        var check = "${callToActions?.id}${taskId}"
        ctaID = callToActions?.id
        Log.e("CTAid", "$check")
        var ctaCheck = false
        for (item in preferencesHelper.verifiedCtas) {
            if (item == check) {
                ctaCheck = false
                break
            } else {
                ctaCheck = true
            }
        }

        if (ctaCheck == false) {
            ctaActivate = true
            Log.e("CTAid", "verified A")
            switchCtaMode(ctaMode)

        } else {
            if (checkFlag == false) {
                Log.e("CTAid", "Not Verified")
                ctaActivate = true
                switchCtaMode(mode)
                return 2
            } else {
                Log.e("CTAid", "Verified B")
                Log.e("CTAid", callToActions.id.toString())
                Log.e("CTAid", taskId.toString())
                ctaMode = mode
                val sendCtaOtpRequest = SendCtaOtpRequest(callToActions.id.toString(), taskId)
                mNewTaskViewModel.sendCtaOtp(httpManager, sendCtaOtpRequest)
                return 1
            }
        }
        return 0
    }

    private fun switchCtaMode(mode: String) {
        Log.e("modeCheck", mode)
        when (mode) {
            "Ext" -> {
                mNewTaskViewModel.executeUpdates(httpManager, ecRequest, api!!)
            }
            TRAGETINFO.BOOK_SLOT.name -> {
                ctaTitle = callToActions!!.name.toString()
                bookSlot()
            }
            "assignExec" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                Log.e("assignExec", "${callToActions?.targetInfo?.targetValues} - Values")
                Log.e("assignExec", "check = ${callToActions!!.targetInfo!!.targetInfo}")
//                val jsonConverter: JSONConverter<SyncInfo> = JSONConverter()
//                val response: SyncInfo = jsonConverter.jsonToObject(
//                    "${callToActions!!.targetInfo!!.targetInfo}",
//                    SyncInfo::class.java
//                ) as SyncInfo
//
//                Log.e("assignExec", "check = ${response.syncInfo.toString()}")
                if (callToActions?.targetInfo?.targetValues != null) {
                    var rIds = ""
                    val intent = Intent(context, UserListNewActivity::class.java)
                    intent.putExtra("taskId", taskId)
                    val listIds = callToActions?.targetInfo?.targetValues!!
                    for (i in 0 until listIds.size) {
                        rIds += if (i > 0)
                            ",${listIds[i]}"
                        else
                            listIds[i]
                    }
                    intent.putExtra("roleIds", rIds)

                    intent.putExtra("request", ecRequest as ExecuteUpdateRequest)

                    context?.startActivity(intent)
                    requireActivity().finish()

                } else {
                    hideLoading()
                    TrackiToast.Message.showShort(context, "No Id Found Or Not Synced")
                }
            }
            "CREATE_TASK" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                val intent = NewCreateTaskActivity.newIntent(baseActivity)
                intent.putExtra(
                    Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
                    false
                )
                intent.putExtra(Extra.FROM, "taskListing")
                if (callToActions!!.categoryId != null) {
                    val dashBoardBoxItem = DashBoardBoxItem()
                    // dashBoardBoxItem.categoryId = callToActions!!.categoryId
                    if (callToActions!!.targetInfo!!.category != null)
                        dashBoardBoxItem.categoryId =
                            callToActions!!.targetInfo!!.category
                    val map = Gson().toJson(dashBoardBoxItem)
                    intent.putExtra(Extra.EXTRA_CATEGORIES, map)
                    intent.putExtra(Extra.EXTRA_PAREN_TASK_ID, task?.taskId)
                    intent.putExtra(Extra.EXTRA_PARENT_REF_ID, task?.referenceId)
                    intent.putExtra("vid", verificationId)
                }
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
            }
            "TAG3" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                offside = 2
                checkConditionsAndRequestAPI(null)
            }
            "TAG2" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                if (activity != null) {
                    val intent =
                        SelectOrderActivity.newIntent(requireActivity())
                    val dashBoardBoxItem = DashBoardBoxItem()

                    dashBoardBoxItem.categoryId = categoryId
                    intent.putExtra(
                        Extra.EXTRA_CATEGORIES,
                        Gson().toJson(dashBoardBoxItem)
                    )
                    intent.putExtra(
                        Extra.EXTRA_CTA_ID,
                        callToActions!!.id
                    )
                    intent.putExtra(
                        Extra.EXTRA_TASK_ID,
                        task!!.taskId
                    )
                    if (callToActions!!.targetInfo!!.category != null && callToActions!!.targetInfo!!.category!!.isNotEmpty())
                        intent.putExtra(
                            Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID,
                            callToActions!!.targetInfo!!.category
                        )


                    if (!callToActions!!.targetInfo!!.targetInfo.isNullOrEmpty()) {
                        var ctaInventoryConfig =
                            callToActions!!.targetInfo!!.targetInfo!!.getCtaInventoryConfig()
                        if (!ctaInventoryConfig.invAction.isNullOrEmpty()) {
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_TARGET,
                                ctaInventoryConfig.invAction
                            )
                        } else {
                            var invAction = getInvAction(categoryId)
                            if (invAction != null && invAction.isNotEmpty())
                                intent.putExtra(
                                    Extra.EXTRA_TASK_TAG_INV_TARGET,
                                    invAction
                                )
                        }
                        if (ctaInventoryConfig.dynamicPricing != null)
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING,
                                ctaInventoryConfig.dynamicPricing
                            )

                    } else {
                        var invAction = getInvAction(categoryId)
                        if (invAction != null && invAction.isNotEmpty())
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_TARGET,
                                invAction
                            )
                    }
                    startActivityForResult(
                        intent,
                        AppConstants.REQUEST_CODE_TAG_INVENTORY
                    )
                }
            }
            "UNIT_INFO" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                if (activity != null) {
                    val intent = SkuInfoActivity.newIntent(baseActivity)
                    intent.putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                    intent.putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                    startActivityForResult(
                        intent,
                        AppConstants.REQUEST_CODE_UNIT_INFO
                    )
                }
            }
            "withoutDialog" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                if (calculateCondition()) {
                    ctaActivate = false
                    startActivityForResult(
                        newIntent(baseActivity)
                            .putExtra(Extra.EXTRA_FORM_TYPE, callToActions!!.name)
                            .putExtra(Extra.EXTRA_FORM_ID, callToActions!!.dynamicFormId)
                            .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                            .putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                            .putExtra(
                                Extra.EXTRA_IS_EDITABLE,
                                callToActions!!.dynamicFormEditable
                            ),
                        AppConstants.REQUEST_CODE_DYNAMIC_FORM
                    )

                }
            }
            "payment" -> {

                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                val paymentRequest = PaymentRequest()

                paymentRequest.taskId = taskId
                paymentRequest.ctaId = callToActions!!.id.toString()

                mNewTaskViewModel.getPaymentUrl(httpManager, paymentRequest)
                ctaActivate = false
            }
            "dynaForm" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                if (calculateCondition()) {
                    foundWidgetItem = CommonUtils.getFormByFormIdContainsWidget(
                        callToActions!!.dynamicFormId,
                        DataType.SCANNER
                    )
                    if (foundWidgetItem.isPresent) {

                        if (baseActivity != null) {
                            getCameraPermission()
                        }
                    } else {
                        openDynamicFormScreen()
                    }
                }
            }
            "TAG_INVENTORY" -> {
                val request = ExecuteUpdateRequest(
                    taskId!!, ctaID!!,
                    DateTimeUtil.getCurrentDateInMillis(), null, verificationId
                )
                ecRequest = request
                if (activity != null) {
                    val intent = SelectOrderActivity.newIntent(requireActivity())
                    val dashBoardBoxItem = DashBoardBoxItem()

                    dashBoardBoxItem.categoryId = categoryId
                    intent.putExtra(
                        Extra.EXTRA_CATEGORIES,
                        Gson().toJson(dashBoardBoxItem)
                    )
                    intent.putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
                    intent.putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                    if (callToActions!!.targetInfo!!.category != null && callToActions!!.targetInfo!!.category!!.isNotEmpty())
                        intent.putExtra(
                            Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID,
                            callToActions!!.targetInfo!!.category
                        )


                    if (!callToActions!!.targetInfo!!.targetInfo.isNullOrEmpty()) {
                        var ctaInventoryConfig =
                            callToActions!!.targetInfo!!.targetInfo!!.getCtaInventoryConfig()
                        if (!ctaInventoryConfig.invAction.isNullOrEmpty()) {
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_TARGET,
                                ctaInventoryConfig.invAction
                            )
                        } else {
                            var invAction = getInvAction(categoryId)
                            if (invAction != null && invAction.isNotEmpty())
                                intent.putExtra(
                                    Extra.EXTRA_TASK_TAG_INV_TARGET,
                                    invAction
                                )
                        }
                        if (ctaInventoryConfig.dynamicPricing != null)
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING,
                                ctaInventoryConfig.dynamicPricing
                            )

                    } else {
                        var invAction = getInvAction(categoryId)
                        if (invAction != null && invAction.isNotEmpty())
                            intent.putExtra(
                                Extra.EXTRA_TASK_TAG_INV_TARGET,
                                invAction
                            )
                    }
                    startActivityForResult(
                        intent,
                        AppConstants.REQUEST_CODE_TAG_INVENTORY
                    )
                }
            }
        }
    }

    private fun openDynamicFormScreen(id: String? = null) {
        var dfIntent = DynamicFormActivity.newIntent(baseActivity)
            .putExtra(Extra.EXTRA_FORM_TYPE, callToActions!!.name)
            .putExtra(Extra.EXTRA_FORM_ID, callToActions!!.dynamicFormId)
            .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
            .putExtra(Extra.EXTRA_CTA_ID, callToActions!!.id)
            .putExtra("vid", verificationId)
            .putExtra(
                Extra.EXTRA_IS_EDITABLE,
                callToActions!!.dynamicFormEditable
            )
        if (::foundWidgetItem.isInitialized && id != null) {
            dfIntent.apply {
                putExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_NAME, foundWidgetItem.name)
            }
            dfIntent.apply {
                putExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_VALUE, id)
            }
        }
        startActivityForResult(
            dfIntent,
            AppConstants.REQUEST_CODE_DYNAMIC_FORM
        )
    }


    override fun onClickMapIcon(task: Task?, position: Int) {
    }

    override fun onCallClick(task: Task?, position: Int) {
    }

    override fun onExecuteUpdates(id: String?, task: Task?, cta: String?) {
        ctaID = id
        this.task = task
        Log.e("checkID", "$ctaID")
        Log.e(TAG, "onExecuteUpdates: $task")


        if (task!!.currentStage != null) {
            val callToActionList = task.currentStage!!.callToActions

            if (callToActionList != null && callToActionList.size > 0) {
                for (i in callToActionList.indices) {
                    if (callToActionList[i].id.equals(id, ignoreCase = true)) {
                        callToActions = callToActionList[i]
                        break
                    }
                }
            }
            if (callToActions != null) {


                if (callToActions!!.targetInfo != null && callToActions!!.targetInfo!!.target == TRAGETINFO.CREATE_TASK) {

                    checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "CREATE_TASK")

                } else {
                    perFormCtaAction()
                }


            }
        }
    }

    override fun onDetailsTaskClick(task: Task?) {

    }

    override fun onItemClick(task: Task?, position: Int) {

    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
        initSocket()
        // connectSocket(this)
    }

    /**
     * Method used to get the current location from the sdk.
     */
    private fun getCurrentLocation() {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                Log.e(TAG, "getCurrentLocation(): onSuccess: $currentLocation")
            }

            override fun onError(errorResponse: ErrorResponse) {
                Log.e(TAG, "onError: " + errorResponse.errorMessage)
            }
        })
    }

    fun hideBottomSheetFromOutSide(event: MotionEvent) {
        if (::sheetBehavior.isInitialized && sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            val outRect = Rect()
            bottomSheet.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()))
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    /**
     * This method is used to check all the conditions and if pass then
     * hit the api with cta current time and task id.
     */
    @Throws(java.lang.Exception::class)
    private fun checkConditionsAndRequestAPI(formData: com.rf.taskmodule.data.model.request.TaskData?) {
        //perform action on conditions else if conditions are null then hit api with name
        if (callToActions != null) {
            //check for all the conditions and hit API
            if (callToActions!!.conditions != null) {
                val conditionList = callToActions!!.conditions
                if (conditionList!!.size > 0) {
                    for (i in conditionList.indices) {
                        val conditionItem = conditionList[i]
                        Log.e(TAG, "checkConditionsAndRequestAPI: $conditionItem")
                        if (AppConstants.GEOFENCE == conditionItem.type) {
                            var geoCoordinates: GeoCoordinates? = GeoCoordinates()
                            val locationType =
                                conditionItem.properties!![AppConstants.LOCATION_TYPE]
                            if (locationType != null) {
                                if (locationType.equals(AppConstants.START, ignoreCase = true) &&
                                    task!!.source != null
                                ) {
                                    geoCoordinates = task!!.source!!.location
                                } else if (locationType.equals(
                                        AppConstants.END,
                                        ignoreCase = true
                                    ) &&
                                    task!!.destination != null
                                ) {
                                    geoCoordinates = task!!.destination!!.location
                                }
                            }

                            //if any of the below condition is true then show message else hit api
                            if (currentLocation == null || currentLocation!!.latitude == 0.0 || currentLocation!!.longitude == 0.0 || geoCoordinates == null || geoCoordinates.latitude == 0.0 || geoCoordinates.longitude == 0.0) {
                                conditionItem.isPassed = false
                                TrackiToast.Message.showShort(
                                    baseActivity,
                                    AppConstants.MSG_NO_LOCATION
                                )
                                return
                            }
                            var radius = conditionItem.properties!![AppConstants.RADIUS]
                            if (radius == null) {
                                radius = "200"
                            }
                            Log.e(TAG, "radius: $radius")

                            if (!CommonUtils.isPointOutSideCircle(
                                    radius.toInt(),
                                    geoCoordinates.latitude, geoCoordinates.longitude,
                                    currentLocation!!.latitude, currentLocation!!.longitude
                                )
                            ) {
                                baseActivity.openDialougShowLocationError(
                                    "Not inside the " +
                                            Objects.requireNonNull<String>(locationType)
                                                .toLowerCase() + " location"
                                )
                                //                                TrackiToast.Message.showShort(getBaseActivity(), "Not inside the " +
//                                        Objects.requireNonNull(locationType).toLowerCase() + " location");
                                conditionItem.isPassed = false
                                return
                            }
                            conditionItem.isPassed = true
                        } else if (AppConstants.TIMED.equals(
                                conditionItem.type,
                                ignoreCase = true
                            )
                        ) {
                            conditionItem.isPassed = false
                            if (conditionItem.properties != null && conditionItem.properties!!.containsKey(
                                    AppConstants.TIME
                                )
                            ) {
                                val timeInSeconds =
                                    conditionItem.properties!![AppConstants.TIME]!!.toInt()
                                if (System.currentTimeMillis() - task!!.stageUpdatedAt >= timeInSeconds * 1000) {
                                    TrackiToast.Message.showShort(
                                        baseActivity,
                                        AppConstants.MSG_TIME_PASSED
                                    )
                                    return
                                }
                                conditionItem.isPassed = true
                            }
                        }
                    }
                }
                var isAllPassed = true
                for (i in conditionList.indices) {
                    if (!conditionList[i].isPassed) {
                        isAllPassed = false
                        break
                    }
                }
                Log.e(TAG, "isAllPassed: $isAllPassed")
                if (isAllPassed) {

                    //if(formData!=null)
                    if (NetworkUtils.isConnectedFast(baseActivity))
                        manageTracking(task!!.taskId!!, formData)
                    else {
                        //  showShort(baseActivity, getString(R.string.please_check_your_internet_connection_you_are_offline_now))

                    }

                }
            } else {
                if (NetworkUtils.isConnectedFast(baseActivity))
                    manageTracking(task!!.taskId!!, formData)
                else {
                    //  showShort(baseActivity, getString(R.string.please_check_your_internet_connection_you_are_offline_now))

                }

            }
        }
    }

    fun startTracking(taskId: String) {
        TrackThat.startTracking(taskId, true)
        preferencesHelper.isTrackingLiveTrip = true
        preferencesHelper.setActiveTaskId(taskId)
        preferencesHelper.setActiveTaskCategoryId(categoryId)
        playSoundStartTracking()
    }

    fun stopTracking() {
        TrackThat.stopTracking()
        preferencesHelper.isTrackingLiveTrip = false
        preferencesHelper.saveCurrentTask(null)
        preferencesHelper.setActiveTaskId("")
        preferencesHelper.setActiveTaskCategoryId("")
    }

    fun bookSlot() {
        Log.e("slots", "ctaId - $ctaID and taskid - $taskId")
        viewModel.getUserLocations(httpManager)


    }

    private fun callSlotApi(ddHubs: Spinner) {
        var list = ArrayList<Hub>()
        var listNames = kotlin.collections.ArrayList<String>()
        viewModel.getSlotAvailability(
            httpManager,
            TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!,
            hubIdFinal,
            date,
            ctaID,
            taskId
        )
        ddHubs.visibility = View.GONE
    }

    fun handleTracking(trackingState: TrackingState, taskId: String) {
        if (trackingState == TrackingState.ENABLED) {
            if (preferencesHelper.idleTripActive) {
                TrackThat.stopTracking()
                preferencesHelper.idleTripActive = false
                playSoundStopTracking()
            }


            if (preferencesHelper.isTrackingLiveTrip) {
                if (TrackThat.getCurrentTrackingId() != null && taskId != TrackThat.getCurrentTrackingId()) {
                    stopTracking()
                    startTracking(taskId)
                }

            } else {
                startTracking(taskId)
            }
        } else if (trackingState == TrackingState.DISABLED) {
            if (preferencesHelper.isTrackingLiveTrip && TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                stopTracking()
                //   playSoundStopTracking()
                if (preferencesHelper.punchStatus) {
                    if (!preferencesHelper.idleTripActive) {
                        Log.e("AssignedFragment", "startIdealTrack=>")
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                        val myWorkRequest: WorkRequest =
                            OneTimeWorkRequest.Builder(StartIdealTrackWork::class.java)
                                .setConstraints(constraints)
                                .setInitialDelay(60, TimeUnit.SECONDS)
                                .build()
                        val workManager = WorkManager.getInstance()
                        workManager.enqueue(myWorkRequest)

                    }
                }
            }

        }

    }

    /**
     * Manage tracking of particular task on the basis of tracking enum
     * and task id.
     *
     * @param taskId   task id of the user
     * @param formData form data if not null.
     */
    private fun manageTracking(
        taskId: String,
        formData: com.rf.taskmodule.data.model.request.TaskData?
    ) {
        //start the tracking if no ongoing task is running

        Log.e("CTA", "${callToActions!!.targetInfo!!.target}")
        if (callToActions!!.tracking === Tracking.START) {
            if (preferencesHelper.idleTripActive) {
                TrackThat.stopTracking()
                preferencesHelper.idleTripActive = false
                playSoundStopTracking()
            }


//            if (TrackThat.isTracking()) {
//            if (preferencesHelper.isTrackingLiveTrip) {
//                TrackiToast.Message.showShort(baseActivity, AppConstants.MSG_ONGOING_TASK)
//                return
//            } else {
            TrackThat.startTracking(taskId, true)
            preferencesHelper.isTrackingLiveTrip = true
            preferencesHelper.setActiveTaskId(taskId)
            preferencesHelper.setActiveTaskCategoryId(categoryId)
            playSoundStartTracking()
            //}
        } else if (callToActions!!.tracking === Tracking.END &&  /*TrackThat.isTracking()*/
            preferencesHelper.isTrackingLiveTrip
        ) {
            TrackThat.stopTracking()
            preferencesHelper.isTrackingLiveTrip = false
            preferencesHelper.saveCurrentTask(null)
            preferencesHelper.setActiveTaskId("")
            preferencesHelper.setActiveTaskCategoryId("")
            playSoundStopTracking()
            if (preferencesHelper.punchStatus) {
                if (!preferencesHelper.idleTripActive) {
                    Log.e("AssignedFragment", "startIdealTrack=>")
                    val constraints = Constraints.Builder()
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build()
                    val myWorkRequest: WorkRequest =
                        OneTimeWorkRequest.Builder(StartIdealTrackWork::class.java)
                            .setConstraints(constraints)
                            .setInitialDelay(60, TimeUnit.SECONDS)
                            .build()
                    val workManager = WorkManager.getInstance()
                    workManager.enqueue(myWorkRequest)

                }
            }
        }
        showLoading()
        val api = TrackiSdkApplication.getApiMap()[ApiType.EXECUTE_UPDATE]
        val request = ExecuteUpdateRequest(
            taskId, ctaID!!,
            DateTimeUtil.getCurrentDateInMillis(), formData, verificationId
        )
        val ctaLocation = CtaLocation()
        if (currentLocation != null) {
            ctaLocation.location = currentLocation
            ctaLocation.address = CommonUtils.getAddress(
                baseActivity,
                LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            )
            request.location = ctaLocation
        }
        if (callToActions!!.dynamicFormId != null)
            request.dfdId = dfdId
        if (callToActions!!.dynamicFormId != null)
            request.dfId = callToActions!!.dynamicFormId
        if (callToActions!!.targetInfo!!.target == TRAGETINFO.ASSIGN_EXECUTIVE) {

            ecRequest = request
            var mode =
                checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "assignExec")


        } else {
            ecRequest = request
            var mode =
                checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "Ext")


        }
        dfId = null
        dfdId = null
    }

    open fun calculateCondition(): Boolean {
        var isAllPassed = true
        if (callToActions!!.conditions != null) {
            val conditionList = callToActions!!.conditions
            if (conditionList!!.size > 0) {
                for (i in conditionList!!.indices) {
                    val conditionItem = conditionList!![i]
                    Log.e(TAG, "checkConditionsAndRequestAPI: $conditionItem")
                    if (AppConstants.GEOFENCE == conditionItem.type) {
                        var geoCoordinates: GeoCoordinates? = GeoCoordinates()
                        val locationType = conditionItem.properties!![AppConstants.LOCATION_TYPE]
                        if (locationType != null) {
                            if (locationType.equals(AppConstants.START, ignoreCase = true) &&
                                task!!.source != null
                            ) {
                                geoCoordinates = task!!.source!!.location
                            } else if (locationType.equals(AppConstants.END, ignoreCase = true) &&
                                task!!.destination != null
                            ) {
                                geoCoordinates = task!!.destination!!.location
                            }
                        }
                        getCurrentLocation()
                        //if any of the below condition is true then show message else hit api
                        if (currentLocation == null || currentLocation!!.latitude == 0.0 || currentLocation!!.longitude == 0.0 || geoCoordinates == null || geoCoordinates.latitude == 0.0 || geoCoordinates.longitude == 0.0) {
                            conditionItem.isPassed = false
                            TrackiToast.Message.showShort(
                                baseActivity,
                                AppConstants.MSG_NO_LOCATION
                            )
                            return false
                        }
                        var radius = conditionItem.properties!![AppConstants.RADIUS]
                        if (radius == null) {
                            radius = "200"
                        }
                        if (!CommonUtils.isPointOutSideCircle(
                                radius.toInt(),
                                geoCoordinates.latitude, geoCoordinates.longitude,
                                currentLocation!!.latitude, currentLocation!!.longitude
                            )
                        ) {
                            baseActivity.openDialougShowLocationError(
                                "Not inside the " +
                                        Objects.requireNonNull(locationType)!!
                                            .toLowerCase() + " location"
                            )
                            conditionItem.isPassed = false
                            return false
                        }
                        //reaches here if user is inside the current location.
                        conditionItem.isPassed = true
                    } else if (AppConstants.TIMED.equals(conditionItem.type, ignoreCase = true)) {
                        conditionItem.isPassed = false
                        if (conditionItem.properties != null && conditionItem.properties!!.containsKey(
                                AppConstants.TIME
                            )
                        ) {
                            val timeInSeconds =
                                conditionItem.properties!![AppConstants.TIME]!!.toInt()
                            if (System.currentTimeMillis() - task!!.stageUpdatedAt >= timeInSeconds * 1000) {
                                TrackiToast.Message.showShort(
                                    baseActivity,
                                    AppConstants.MSG_TIME_PASSED
                                )
                                return false
                            }
                            conditionItem.isPassed = true
                        }
                    } else {
                        conditionItem.isPassed = true
                    }
                }
            }
            for (i in conditionList!!.indices) {
                if (!conditionList!![i].isPassed) {
                    isAllPassed = false
                    break
                }
            }
        }
        return isAllPassed
    }

    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, baseActivity)) {
            getTaskData()
            Log.e("execute response", "${result.toString()}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_DYNAMIC_FORM) {
            if (resultCode == Activity.RESULT_OK) {
                var taskData: com.rf.taskmodule.data.model.request.TaskData? = null
                if (data != null && data.hasExtra(AppConstants.Extra.EXTRA_FORM_MAP)) {

                    val formData: DynamicFormMainData =
                        data.getParcelableExtra(AppConstants.Extra.EXTRA_FORM_MAP)!!
                    taskData = formData.taskData
                }
                if (data != null && data.hasExtra(AppConstants.DFID)) {
                    dfId = data.getStringExtra(AppConstants.DFID)
                }
                if (data != null && data.hasExtra(AppConstants.DFDID)) {
                    dfdId = data.getStringExtra(AppConstants.DFDID)
                }

                //after filling the form check other conditions for this task.
                if (callToActions != null) {
                    try {
                        checkConditionsAndRequestAPI(taskData!!)
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, "onActivityResult: $e")
                        e.printStackTrace()
                    }
                }
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_TAG_INVENTORY) {
            if (resultCode == Activity.RESULT_OK) {
                if (callToActions != null) {
                    try {
                        checkConditionsAndRequestAPI(null)
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, "onActivityResult: $e")
                        e.printStackTrace()
                    }
                }

            }
        } else
            if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
                if (resultCode == Activity.RESULT_OK) {
                    perFormCtaActionWithoutDialog()
                }
            } else if (requestCode == AppConstants.REQUEST_CODE_SCAN) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra("id")) {
                        var id = data.getStringExtra("id")
                        openDynamicFormScreen(id)
                    } else {
                        openDynamicFormScreen()
                    }

                } else {
                    openDynamicFormScreen()
                }
            } else
                if (requestCode == AppConstants.REQUEST_CODE_UNIT_INFO) {
                    if (resultCode == Activity.RESULT_OK) {
                        Log.d(
                            "AppConstants.Extra.EXTRA_CATEGORY_ID",
                            "data!!.getStringExtra(Extra.EXTRA_CATEGORY_ID)"
                        )
                        //  Log.d("AppConstants.Extra.EXTRA_CATEGORY_ID", data!!.getStringExtra(Extra.EXTRA_CATEGORY_ID))
                        //  Log.d("AppConstants.Extra.EXTRA_TASK_ID", data.getStringExtra(Extra.EXTRA_TASK_ID))
                        getTaskData()
                    }
                }
    }

    private fun perFormCtaActionWithoutDialog() {
//        if (callToActions!!.nextStageId != null && !callToActions!!.nextStageId!!.isEmpty()) {
//            stageId = callToActions!!.nextStageId
//        }
        if (callToActions!!.dynamicFormId != null && !callToActions!!.dynamicFormId!!.isEmpty()) {
            var mode =
                checkCtaActivation(callToActions!!.enableOtp!!, callToActions!!, "withoutDialog")

        } else {
            try {
                checkConditionsAndRequestAPI(null)
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "onExecuteUpdates: $e")
                e.printStackTrace()
            }
        }
    }

    override fun openForm(dataModel: StageHistoryData) {
        if (dataModel.dfdId != null) {


            startActivityForResult(
                DynamicFormActivity.Companion.newIntent(baseActivity)
                    .putExtra(Extra.EXTRA_FORM_TYPE, dataModel.stage!!.name)
                    .putExtra(Extra.EXTRA_FORM_ID, dataModel.dfdId)
                    .putExtra(AppConstants.Extra.EXTRA_TCF_ID, dataModel.dfdId)
                    .putExtra(Extra.EXTRA_TASK_ID, task!!.taskId)
                    .putExtra(Extra.EXTRA_CTA_ID, dataModel.ctaId)
                    .putExtra(Extra.EXTRA_IS_EDITABLE, false)
                    .putExtra(AppConstants.Extra.HIDE_BUTTON, true),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM
            )
        } else {
            TrackiToast.Message.showShort(requireContext(), "form not available")
        }
    }

    private fun playSoundStartTracking() {
        if (preferencesHelper.voiceAlertsTracking) {
            if (baseActivity != null) {
                mediaPlayer = MediaPlayer.create(baseActivity, R.raw.tracking_started)
                mediaPlayer!!.start()
                Handler().postDelayed({
                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                    }
                }, 2000)
            }
        }
    }

    private fun playSoundStopTracking() {
        if (preferencesHelper.voiceAlertsTracking) {
            if (baseActivity != null) {
                mediaPlayer = MediaPlayer.create(baseActivity, R.raw.tracking_stopped)
                mediaPlayer!!.start()
                Handler().postDelayed({
                    if (mediaPlayer != null) {
                        mediaPlayer!!.stop()
                    }
                }, 2000)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mediaPlayer = null
    }

    fun openRoom() {
        if (taskId != null) {
            var connectionIds = ""
            if (preferencesHelper.userDetail != null && preferencesHelper.userDetail!!.userId != null) {
                var buddy = Buddy()
                buddy.buddyId = preferencesHelper.userDetail!!.userId
                if (listBuddy.contains(buddy)) {
                    listBuddy.remove(buddy)
                }
            }
            for (i in listBuddy?.indices!!) {
                connectionIds += listBuddy!![i].buddyId

                var p = i
                p += 1
                //if array hashMap contains next item then add comma into the string
                if (listBuddy!!.size > p) {
                    connectionIds += ","
                }
            }
        }
    }

    override fun onUploadPic(position: Int, formData: FormData?) {

    }

    override fun uploadCameraImage(adapterPosition: Int) {

    }

    override fun onProcessClick(formData: FormData?) {

    }

    override fun getDropdownItems(position: Int, target: String?, rollId: String?) {
    }

    override fun openVidCamera(
        pos: Int,
        mBinding: ItemDynamicFormVideoSdkBinding?,
        maxLength: Int
    ) {
    }

    override fun onVeriFyOtpButtonClick(formData: FormData?, mobile: String?) {

    }

    override fun openPlacePicker(position: Int, formData: FormData?) {

    }

    override fun openScanner(position: Int, formData: FormData?) {

    }

    override fun sendButtonInstance(button: Button?, isEditable: Boolean) {

    }

}