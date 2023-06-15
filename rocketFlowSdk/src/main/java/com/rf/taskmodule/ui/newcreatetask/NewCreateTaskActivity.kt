package com.rf.taskmodule.ui.newcreatetask

//import com.rf.taskmodule.ui.addcustomer.AddCustomerActivity
//import com.rf.taskmodule.ui.main.MainActivity
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.local.prefs.AppPreferencesHelper
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.BaseResponse
import com.rf.taskmodule.data.model.request.*
import com.rf.taskmodule.data.model.request.Contact
import com.rf.taskmodule.data.model.response.config.*
import com.rf.taskmodule.data.model.response.config.Target
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityNewCreateTaskSdkBinding
import com.rf.taskmodule.ui.addplace.Hub
import com.rf.taskmodule.ui.addplace.LocationListResponse
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.custom.CircleTransform
import com.rf.taskmodule.ui.custom.ExecutorThread
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.FormSubmitListener
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.SlotAdapter
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.SlotChildAdapter
import com.rf.taskmodule.ui.facility.ServicesSDKActivity
import com.rf.taskmodule.ui.newdynamicform.NewDynamicFormFragment
import com.rf.taskmodule.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity
import com.rf.taskmodule.utils.*
import com.rocketflow.sdk.RocketFlyer
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.network.TrackThatCallback
import com.trackthat.lib.models.ErrorResponse
import com.trackthat.lib.models.SuccessResponse
import com.trackthat.lib.models.TrackthatLocation
import kotlinx.android.synthetic.main.activity_new_create_task_sdk.*
import kotlinx.android.synthetic.main.item_dynamic_form_time_sdk.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList


open class NewCreateTaskActivity :
    BaseSdkActivity<ActivityNewCreateTaskSdkBinding, NewCreateTaskViewModel>(),
    NewCreateTaskNavigator, FormSubmitListener,
    BaseAutocompleteAdapter.OnItemSelectedAUtoComplete, NearHubAdapter.NearHubInterface {
    //    init {
//        System.loadLibrary("keys")
//    }

    var availType = "GEO"
    var multiReference = false
    private val SCAN_REFERENCE = 12
    private val REQUEST_CAMERA = 3
    private var snackBar: Snackbar? = null
    private lateinit var selectedUserId: String
    private lateinit var selectedUsername: String
    private var allowGeography: Boolean = false
    private var currentLocation: GeoCoordinates? = null
    private var categoryId: String? = null
    lateinit var slotAdapter: SlotAdapter
    private var parentTaskId: String? = null
    private var parentReffId: String? = null

    private lateinit var tvNearSearch: TextView

    private var selectedHubSource1 = false
    private var selectedHubSource2 = false
    private var selectedHubSource3 = false

    private var target_system_hub = Target.SOURCE

    var loggedScope = false
    var loggedScope1 = false

    private var selectedHubList1 = ArrayList<Hub>()
    private var selectedHubList2 = ArrayList<Hub>()
    private var selectedHubList3 = ArrayList<Hub>()

    private lateinit var llSlots: LinearLayout
    private lateinit var ivNoData: ImageView

    private var sourceNameA = AdvancedConfigSource.NEAR_BY_HUBS.source
    var slotDataResponse: SlotDataResponse = SlotDataResponse()
    private var timePosition = 0
    private var keyPosition = 0
    private var dayPosition = 0
    private var key = "0"
    private var dateFinal = ""
    private var date = ""
    private var timeFinal = ""
    private var hubIdFinalHubsOnly = ""
    private var hubIdFinal = ""
    private var hubIdFinalDestination = ""
    private var hubDestination: Hub? = null
    private var listRefTask: ArrayList<Task> = ArrayList()
    private var hubs: ArrayList<Hub> = ArrayList()
    private var hub: Hub? = null
    private lateinit var etSlot: EditText

    private lateinit var rvSlot: RecyclerView
    private lateinit var slotImg: ImageView
    private lateinit var rvDate: RecyclerView

    lateinit var adapter: ReferencesListAdapter

    private lateinit var dialogNearHub: Dialog
    private lateinit var nearHubAdapter: NearHubAdapter
    private lateinit var nearHubNoData: ImageView
    private lateinit var closeHubDialog: ImageView

    private lateinit var dialogSlot: Dialog

    //@Inject
    lateinit var mCreateTaskViewModel: NewCreateTaskViewModel

    //@Inject
    lateinit var mGetSuggetionViewModel: GetUserSuggestionListViewModel

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var httpManager: HttpManager

    var lookUps: List<LookUps>? = null
    lateinit var view: View
    private lateinit var startLatLng: LatLng
    private var endLatLng: LatLng? = null

    private lateinit var mActivityCreateTaskBinding: ActivityNewCreateTaskSdkBinding
    private var startTime = 0L
    private var endTime = 0L
    private var searchRequest: SearchReferenceRequest? = null

    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0
    private var startHour = 0
    private var startMinute = 0

    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0
    private var endHour = 0
    private var endMinute = 0
    private var spnCategory: Spinner? = null
    private var buddyName: String? = null
    private var selItem: String = ""//hard coded by previous developer
    private var dynamicFormToDisplay: String? = null
    private var allowedFields: String? = null
    private var allowedFieldFirst: Boolean? = null
    var requestedBy: String? = null
    var requestUserType: List<String>? = null
    var assigneeLabel: String? = null
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_new_create_task_sdk
    private var buddyId: String? = null
    private var fleetId: String? = null
    private var regionId: String? = null
    private var regionIdEnd: String? = null
    private var stateId: String? = null
    private var stateIdEnd: String? = null
    private var cityId: String? = null
    private var cityIdEnd: String? = null
    private var hubId: String? = null
    private var hubIdEnd: String? = null
    private lateinit var task: Task
    private var taskId = ""
    private var spinnerVal: String? = null
    private var createTaskRequest: CreateTaskRequest? = null
    private var mCategoryId: MutableLiveData<String> = MutableLiveData()
    private var category: WorkFlowCategories = WorkFlowCategories()
    private var mainMap: HashMap<String, ArrayList<FormData>>? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var isEditable: Boolean = true
    private var dynamicFragment: NewDynamicFormFragment? = null
    private var formList = ArrayList<String>()
    var mainData: ArrayList<FormData>? = null
    var titleText: TextView? = null
    var percentageText: TextView? = null
    var currentStatusText: TextView? = null
    var progressBar: ProgressBar? = null
    var rlProgress: RelativeLayout? = null
    var rlSubmittingData: RelativeLayout? = null

    private var categoryMap: Map<String, String>? = null
    lateinit var etScanner: AutoCompleteTextView
    lateinit var ivSearchRef: ImageView

    private lateinit var spinnerNear: Spinner
    private lateinit var spinnerLogged: Spinner
    private lateinit var spinnerAll: Spinner

    var listSlots: ArrayList<SlotData> = ArrayList()
    var listKeys: ArrayList<String> = ArrayList()
    var invIds: List<String>? = ArrayList()
    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null

    enum class FIELD {
        START_LOCATION, END_LOCATION, SELECT_BUDDY, SELECT_CLIENT, START_TIME, END_TIME, POINT_OF_CONTACT, TASK_NAME,
        TASK_TYPE, DESCRIPTION, SELECT_FLEET, SELECT_CITY, TASK_ID, REFERENCE_ID, GOOGLE_OR_MANUAL_SOURCE, GOOGLE_OR_MANUAL_DESTINATION,
        SYSTEM_LOCATION, SEARCH_REFERENCED_TASK, FIELD1, FIELD2, FIELD3, FIELD4, FIELD5, FIELD6, FIELD7, FIELD8, FIELD9, FIELD10, FIELD11,
        FIELD12, FIELD13, FIELD14, FIELD15, TIME_SLOT, SYSTEM_HUB
    }

    override fun onDestroy() {
        super.onDestroy()
        mDynamicHandler?.removeCallbacksAndMessages(null);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCreateTaskBinding = viewDataBinding
        mCreateTaskViewModel.navigator = this
        handlerThread = ExecutorThread()
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!
        val statusList: List<TaskStatus> = ArrayList()
        categoryMap = Gson().fromJson<Map<String, String>>(
            "",
            object : TypeToken<HashMap<String?, String?>?>() {}.type
        )
        searchRequest = SearchReferenceRequest()

        slotAdapter = SlotAdapter(this)

        dialogNearHub = Dialog(this)

        spinnerNear = mActivityCreateTaskBinding.spNearByHub
        spinnerLogged = mActivityCreateTaskBinding.spLoggedInHub
        spinnerAll = mActivityCreateTaskBinding.spAllHub

        mGetSuggetionViewModel = getUserSuggestionListViewModel()


        nearHubAdapter = NearHubAdapter()
        nearHubAdapter.setListener(this)

        dialogNearHub.setContentView(R.layout.layout_near_by_hub)
        tvNearSearch = dialogNearHub.findViewById(R.id.tv_loc_near)
        nearHubNoData = dialogNearHub.findViewById(R.id.no_data_hub)
        closeHubDialog = dialogNearHub.findViewById(R.id.iv_close_hub)

        adapter = ReferencesListAdapter(this, listRefTask)
        mActivityCreateTaskBinding.rvReferences.adapter = adapter

        setUp()
        getCurrentLocation()

        spinnerNear.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (selectedHubList1.size > 0) {
                    if (p2 - 1 >= 0) {
                        val item = selectedHubList1[p2 - 1]
                        hubIdFinalHubsOnly = item.hubId.toString()
                        hub = selectedHubList1[p2 - 1]
                        val location = GeoCoordinates()
                        location.latitude = item.hubLocation?.location?.latitude!!
                        location.longitude = item.hubLocation?.location?.latitude!!
                        val place: com.rf.taskmodule.data.model.response.config.Place =
                            com.rf.taskmodule.data.model.response.config.Place()

                        place.address = item.hubLocation?.address
                        place.location = location
                        place.hubId = item.hubId
                        if (target_system_hub == Target.SOURCE) {
                            createTaskRequest?.source = place
                        } else {
                            createTaskRequest?.destination = place
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        spinnerLogged.onItemSelectedListener = object : OnItemSelectedListener {


            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("logger", selectedHubList2.toString())
                if (selectedHubList2.size > 0) {
                    if (p2 - 1 >= 0) {
                        val item = selectedHubList2[p2 - 1]
                        hubIdFinalHubsOnly = item.hubId.toString()
                        hub = selectedHubList2[p2 - 1]
                        Log.d("logger", item.name)
                        val location = GeoCoordinates()
                        location.latitude = item.hubLocation?.location?.latitude!!
                        location.longitude = item.hubLocation?.location?.latitude!!
                        val place: com.rf.taskmodule.data.model.response.config.Place =
                            com.rf.taskmodule.data.model.response.config.Place()
                        place.address = item.hubLocation?.address
                        place.hubId = item.hubId
                        place.location = location
                        if (target_system_hub == Target.SOURCE) {
                            createTaskRequest?.source = place
                        } else {
                            createTaskRequest?.destination = place
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
        spinnerAll.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (selectedHubList3.size > 0) {
                    if (p2 - 1 >= 0) {
                        val item = selectedHubList3[p2 - 1]
                        hubIdFinalHubsOnly = item.hubId.toString()
                        hub = selectedHubList3[p2 - 1]
                        val location = GeoCoordinates()
                        location.latitude = item.hubLocation?.location?.latitude!!
                        location.longitude = item.hubLocation?.location?.latitude!!
                        val place: com.rf.taskmodule.data.model.response.config.Place =
                            com.rf.taskmodule.data.model.response.config.Place()
                        place.address = item.hubLocation?.address
                        place.location = location
                        place.hubId = item.hubId
                        if (target_system_hub == Target.SOURCE) {
                            createTaskRequest?.source = place
                        } else {
                            createTaskRequest?.destination = place
                        }

                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }


    override fun onAssignNowClicked() {
//        Toast.makeText(this, "click", Toast.LENGTH_LONG).show()

    }

    override fun selectDateTime(v: View) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMin = c.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

            CommonUtils.openTimePicker(this, mHour, mMin) { _, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                val formattedTime = DateTimeUtil.getFormattedTime(c.time)
                Log.e(TAG, "${c.time}")
                val cal = Calendar.getInstance()
                when (v.id) {
                    R.id.edStartDateTime -> {
                        startDay = dayOfMonth
                        startMonth = monthOfYear
                        startYear = year
                        startHour = hourOfDay
                        startMinute = minute
                        cal.set(Calendar.YEAR, startYear)
                        cal.set(Calendar.MONTH, startMonth)
                        cal.set(Calendar.DAY_OF_MONTH, startDay)
                        cal.set(Calendar.HOUR_OF_DAY, startHour)
                        cal.set(Calendar.MINUTE, startMinute)
                        cal.set(Calendar.SECOND, 0)
                        startTime = cal.timeInMillis

                        edStartDateTime.setText("$selectedDate | $formattedTime")
                    }

                    R.id.edEndDateTime -> {
                        endDay = dayOfMonth
                        endMonth = monthOfYear
                        endYear = year
                        endHour = hourOfDay
                        endMinute = minute

                        cal.set(Calendar.YEAR, endYear)
                        cal.set(Calendar.MONTH, endMonth)
                        cal.set(Calendar.DAY_OF_MONTH, endDay)
                        cal.set(Calendar.HOUR_OF_DAY, endHour)
                        cal.set(Calendar.MINUTE, endMinute)
                        cal.set(Calendar.SECOND, 0)
                        endTime = cal.timeInMillis
                        edEndDateTime.setText("$selectedDate | $formattedTime")
                    }
                }
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    override fun openMainActivity(taskId: String) {
        val intent = Intent()
        intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, buddyId)
        intent.putExtra(AppConstants.Extra.EXTRA_FLEET_ID, fleetId)
        intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        intent.putExtra("backAlpha", true)
        setResult(Activity.RESULT_OK, intent)
        val sharedPreferences = getSharedPreferences("backAlpha", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("back", true).apply()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Scan", MODE_PRIVATE)
        sharedPreferences.edit().putString("Scan", "Task").apply()
        sharedPreferences.edit().putBoolean("newTask", true).apply()
        startActivityForResult(Intent(this, ScanQrAndBarCodeActivity::class.java), SCAN_REFERENCE)
    }


    override fun onResume() {
        super.onResume();
        hideLoading()

        //Restore state here
    }

    /**
     * Method used to get the current location from the sdk.
     */
    fun getCurrentLocation(
        systemHub: Boolean? = false,
        sourceName: String? = AdvancedConfigSource.NEAR_BY_HUBS.source
    ) {
        TrackThat.getCurrentLocation(object : TrackThatCallback() {
            override fun onSuccess(successResponse: SuccessResponse) {
                val loc = successResponse.responseObject as TrackthatLocation
                currentLocation = GeoCoordinates()
                currentLocation!!.latitude = loc.latitude
                currentLocation!!.longitude = loc.longitude
                currentLatLng = LatLng(loc.latitude, loc.longitude)
                startLatLng = currentLatLng
                val placeName = CommonUtils.getAddress(this@NewCreateTaskActivity, currentLatLng)
                if (systemHub == false) {
                    edEnterStartLocation.setText(placeName)
                    edEnterEndLocation.setText(placeName)
                } else {
                    Log.d("Address", placeName)
                    mActivityCreateTaskBinding.etNearByHubLocation.setText(placeName)
                    val hubCoordinates = HubCoordinates(
                        ArrayList(), startLatLng.latitude, "",
                        startLatLng.longitude
                    )
                    sourceNameA = sourceName
                    val systemHubRequset: SystemHubRequest =
                        SystemHubRequest(categoryId, hubCoordinates, "", 5000.00, sourceName)
                    getSystemHubs(systemHubRequset)
                }

            }

            override fun onError(errorResponse: ErrorResponse) {
                currentLocation = null
            }
        })
    }

    override fun openPlaceAutoComplete(view: View, code: Int?) {
        this.view = view
        try {
            val fields: List<Place.Field> =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

            // Start the autocomplete intent.
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            startActivityForResult(intent, code!!)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(NewCreateTaskActivity.TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(NewCreateTaskActivity.TAG, e.message)
        }
    }

    override fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            finish()
        }
    }

    override fun pointOfContactClicked() {

    }

    override fun checkBuddyResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse =
                Gson().fromJson(result.toString(), BuddyListResponse::class.java)
            val list = buddyListResponse.buddies
            if (list != null && list.size > 0) {
                var buddyList: MutableList<String?> = ArrayList()
                for (data in list) {
                    buddyList.add(data.name!!)
                }

//
                var arrayAdapter = object :
                    ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, buddyList) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getDropDownView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }
                }
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnBuddy!!.adapter = arrayAdapter
                spnBuddy!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        buddyId = list[position].buddyId
                        CommonUtils.showLogMessage("e", "buddyId", buddyId);
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }


            } else {
            }
        }
    }

    override fun checkFleetResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val buddyListResponse =
                Gson().fromJson(result.toString(), FleetListResponse::class.java)
            val list = buddyListResponse.fleets
            if (list != null && list.size > 0) {
                var fleetList: MutableList<String?> = ArrayList()
                for (data in list) {
                    data.fleetName?.let {
                        fleetList.add(data.fleetName!!)
                    }

                }

//                var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fleetList);
//                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                var arrayAdapter = object :
                    ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, fleetList) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        return v
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val v = super.getDropDownView(position, convertView, parent)
                        val externalFont = Typeface.createFromAsset(
                            parent.context.assets,
                            "fonts/campton_book.ttf"
                        )
                        (v as TextView).typeface = externalFont
                        //v.setBackgroundColor(Color.GREEN);
                        return v
                    }
                }
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnFleet!!.adapter = arrayAdapter
                spnFleet!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        fleetId = list[position].fleetId
                        CommonUtils.showLogMessage("e", "fleetId", fleetId);
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

            } else {

            }
        }
    }


    fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        var lebel: String? = null

        for (i in workFlowCategoriesList) {
            if (i.categoryId != null)
                if (i.categoryId == categoryId) {
                    if (i.name != null)
                        lebel = i.name
                }
        }
        return lebel
    }

    fun getDynamicFormId(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        var id: String? = null

        for (i in workFlowCategoriesList) {
            if (i.categoryId != null)
                if (i.categoryId == categoryId) {
                    if (i.dynamicFormId != null)
                        id = i.dynamicFormId
                }
        }
        return id
    }


    fun checkIfExistes(list: List<Field>?, name: Field): Boolean {
        if (list != null) {
            for (item in list) {
                if (item.field.equals(name.field)) {
                    return true
                }
            }
        }
        return false
    }

    fun getAllowedFields(categoryId: String?): List<Field> {
        var allowedFieldsList: List<Field>? = ArrayList()
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper.workFlowCategoriesList
        if (!workFlowCategoriesList.isNullOrEmpty()) {
            for (i in workFlowCategoriesList) {
                if (i.categoryId != null && categoryId != null)
                    if (i.categoryId == categoryId) {
                        if (i.name != null)
                            selItem = i.name!!
                        if (i.channelConfig != null && i.channelConfig!!.fields != null && i.channelConfig!!.fields!!.isNotEmpty())
                            allowedFieldsList = i.channelConfig!!.fields
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.allowedFieldFirst != null)
                            this.allowedFieldFirst =
                                i.channelConfig!!.channelSetting!!.allowedFieldFirst
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.requestedBy != null)
                            this.requestedBy =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.requestedBy
                        else
                            this.requestedBy = null
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.requestUserType != null)
                            this.requestUserType =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.requestUserType
                        else
                            this.requestUserType = null
                        if (i.channelConfig != null && i.channelConfig!!.channelSetting != null && i.channelConfig!!.channelSetting!!.creationConfig != null && i.channelConfig!!.channelSetting!!.creationConfig!!.assigneeLabel != null)
                            this.assigneeLabel =
                                i.channelConfig!!.channelSetting!!.creationConfig!!.assigneeLabel
                        else
                            this.assigneeLabel = null
                    }
            }
        }


        return allowedFieldsList!!
    }

    private fun addAllToList(list: List<Field>): ArrayList<DynamicFormData> {
        val allowedFieldsList: ArrayList<DynamicFormData> = ArrayList()
        for (item in list) {
            val dynamicFormData = DynamicFormData();
            dynamicFormData.apply {
                type = item.type
                value = item.value
                label = item.label
            }
            allowedFieldsList.add(dynamicFormData)
        }
        return allowedFieldsList
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setUp() {

        etSlot = mActivityCreateTaskBinding.etVisitTime


        Places.initialize(this@NewCreateTaskActivity, googleMapKey!!)
        if (intent.hasExtra(AppConstants.Extra.EXTRA_PAREN_TASK_ID)) {
            parentTaskId = intent.getStringExtra(AppConstants.Extra.EXTRA_PAREN_TASK_ID)
            CommonUtils.showLogMessage("e", "parentTaskId", parentTaskId)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)) {
            parentReffId = intent.getStringExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)
        }
        if (intent.hasExtra("invIds")) {
            invIds = intent.getStringArrayListExtra("invIds")
        }
        enableStartLocation.setOnToggledListener { toggleableView, isChecked ->
            if (isChecked) {
                tvLableManualStartLocation.text = "Enter Location"
                edEnterManualStartLocation.visibility = View.GONE
                edEnterStartLocation.visibility = View.VISIBLE
            } else {
                tvLableManualStartLocation.text = "Enter Manual Location"
                edEnterManualStartLocation.visibility = View.VISIBLE
                edEnterStartLocation.visibility = View.GONE

            }
        }
        /*enableStartLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvLableManualStartLocation.text = "Enter Location"
                edEnterManualStartLocation.visibility = View.GONE
                edEnterStartLocation.visibility = View.VISIBLE
            } else {
                tvLableManualStartLocation.text = "Enter Manual Location"
                edEnterManualStartLocation.visibility = View.VISIBLE
                edEnterStartLocation.visibility = View.GONE

            }
        }*/
        /*enableEndLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvLableManualEndLocation.text = "Enter Location"
                edEnterManualEndLocation.visibility = View.GONE
                edEnterEndLocation.visibility = View.VISIBLE
            } else {
                tvLableManualEndLocation.text = "Enter Manual Location"
                edEnterManualEndLocation.visibility = View.VISIBLE
                edEnterEndLocation.visibility = View.GONE

            }
        }*/
        enableEndLocation.setOnToggledListener { toggleableView, isChecked ->
            if (isChecked) {
                tvLableManualEndLocation.text = "Enter Location"
                edEnterManualEndLocation.visibility = View.GONE
                edEnterEndLocation.visibility = View.VISIBLE
            } else {
                tvLableManualEndLocation.text = "Enter Manual Location"
                edEnterManualEndLocation.visibility = View.VISIBLE
                edEnterEndLocation.visibility = View.GONE

            }
        }
        var viewProgress = mActivityCreateTaskBinding.viewProgress
        titleText = viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText = viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText = viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar = viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData = viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress = viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)
        enableStartLocation.isOn = false
        enableEndLocation.isOn = false
        btnCLickSubmit.setOnClickListener {
            if (dynamicFormsNew != null) {
                if (dynamicFragment != null) {
                    dynamicFragment!!.onclickMainButton()
                } else {
                    var dynamicActionConfig = DynamicActionConfig()
                    dynamicActionConfig.action = Type.DISPOSE
                    onProcessClick(ArrayList(), dynamicActionConfig, null, null)
                }
            } else {
                var dynamicActionConfig = DynamicActionConfig()
                dynamicActionConfig.action = Type.DISPOSE
                onProcessClick(ArrayList(), dynamicActionConfig, null, null)
            }

        }

        etScanner = mActivityCreateTaskBinding.etScanner
        ivSearchRef = mActivityCreateTaskBinding.ivSearchRef
        ivSearchRef.setImageDrawable(getDrawable(R.drawable.qr_code))
        ivSearchRef.setOnClickListener {
            getCameraPermission()
        }

        mActivityCreateTaskBinding.edReference.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
            override fun beforeTextChanged(
                arg0: CharSequence?, arg1: Int, arg2: Int,
                arg3: Int
            ) {
            }

            override fun afterTextChanged(et: Editable) {
                var s: String = et.toString()
                if (s != s.toUpperCase()) {
                    s = s.toUpperCase()
                    mActivityCreateTaskBinding.edReference.setText(s)
                    mActivityCreateTaskBinding.edReference.setSelection(mActivityCreateTaskBinding.edReference.length()) //fix reverse texting
                }
            }
        })
        spnCategory = mActivityCreateTaskBinding.spnCategory
        //requestCurrentLocation(locationCallback)
        val categories: MutableList<String?> = ArrayList();
        val list: MutableList<WorkFlowCategories> = ArrayList();
        val mainList = preferencesHelper.workFlowCategoriesList
        val channelConfigMap = preferencesHelper.workFlowCategoriesListChannel
        for (i in mainList) {
            var categoryId = i.categoryId
            if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(
                    categoryId
                )
            ) {
                val channelConfig: ChannelConfig = channelConfigMap.get(categoryId)!!
                if (channelConfig.channelSetting != null) {
                    val channelSetting = channelConfig.channelSetting
                    if (channelSetting!!.allowCreation != null && channelSetting!!.allowCreation!!) {
                        if (channelSetting!!.creationMode === CreationMode.DIRECT) {
                            list.add(i)
                            categories.add(i.name!!)
                        }
                    }
                }
            }
        }

        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap: Map<String, String>? = null
            val str: String = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)!!
            categoryMap =
                Gson().fromJson(str, object : TypeToken<HashMap<String?, String?>?>() {}.type)
            if (categoryMap != null && categoryMap!!.containsKey("categoryId")) {
                categoryId = categoryMap.get("categoryId")
                if (categoryId != null) {
                    for (i in list.indices) {
                        if (list[i].categoryId == categoryId) {
                            selItem = list[i].name!!
                            spnCategory!!.setSelection(i)
                            break
                        }
                    }
                }
            }
        }


        var workFlowCategorie = WorkFlowCategories()
        for (i in mainList) {
            if (i.categoryId.equals(categoryId)) {
                workFlowCategorie = i
            }
        }

        Log.d("serviceTagging", workFlowCategorie.serviceTagging.toString())
        if (workFlowCategorie.serviceTagging == true) {
            val listCategories: ArrayList<String> = ArrayList()
            if (workFlowCategorie.serviceConfig?.taggingType == TaggingTypeService.DIRECT) {
                listCategories.add(categoryId!!)
                mActivityCreateTaskBinding.cvServices.visibility = View.VISIBLE
                mActivityCreateTaskBinding.etServices.setOnClickListener {
                    val rvUpdateServices = Intent(this, ServicesSDKActivity::class.java)
                    rvUpdateServices.putExtra(
                        AppConstants.Extra.EXTRA_CATEGORY_ID,
                        Gson().toJson(listCategories).toString()
                    )
                    startActivityForResult(rvUpdateServices, 546)
                }
            } else {
                listCategories.addAll(workFlowCategorie.serviceConfig?.referredCategories!!)
                mActivityCreateTaskBinding.cvServices.visibility = View.VISIBLE
                mActivityCreateTaskBinding.etServices.setOnClickListener {
                    val rvUpdateServices = Intent(this, ServicesSDKActivity::class.java)
                    rvUpdateServices.putExtra(
                        AppConstants.Extra.EXTRA_CATEGORY_ID,
                        Gson().toJson(listCategories).toString()
                    )
                    startActivityForResult(rvUpdateServices, 546)
                }
            }
        } else {
            mActivityCreateTaskBinding.cvServices.visibility = View.GONE
        }

        if (workFlowCategorie.taskReferencingEnabled!!) {
            Log.d("taskReferencingEnabled", workFlowCategorie.taskReferencingEnabled.toString())
            multiReference = workFlowCategorie.multiTaskReferencingEnabled!!
            Log.d("taskReferencingEnabled", multiReference.toString())
            mActivityCreateTaskBinding.cvScanner.visibility = View.VISIBLE
            mActivityCreateTaskBinding.etScanner.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().length > 2) {
                        val api1 =
                            TrackiSdkApplication.getApiMap()[ApiType.SEARCH_REFERENCE_ELIGIBLE_TASKS]!!
                        searchRequest!!.query = p0.toString()
                        searchRequest!!.catId = categoryId
                        ivSearchRef.setImageDrawable(getDrawable(R.drawable.ic_cross))
                        ivSearchRef.setOnClickListener {
                            etScanner.setText("")
                        }
                        mCreateTaskViewModel.getSearchReferenceList(
                            httpManager,
                            api1,
                            searchRequest
                        )
                    } else {
                        ivSearchRef.setOnClickListener {
                            getCameraPermission()
                        }
                        ivSearchRef.setImageDrawable(getDrawable(R.drawable.qr_code))
                    }
                }

                override fun afterTextChanged(text: Editable?) {

                }
            })
        } else {
            mActivityCreateTaskBinding.cvScanner.visibility = View.GONE
        }

        mCategoryId.observe(this, androidx.lifecycle.Observer { categoryId ->
            if (categoryId != null) {
                val llallowedFields: List<Field>? = getAllowedFields(categoryId)
                val jsonConverter =
                    JSONConverter<List<Field>>()
                val data = jsonConverter.objectToJson(llallowedFields!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())
                CommonUtils.showLogMessage("e", "allowed field", llallowedFields.toString())
                if (requestedBy != null && requestedBy.equals("OTHERS")) {
                    cardViewClientList.visibility = View.VISIBLE
                    performSearchWidgetTask()
                } else {
                    cardViewClientList.visibility = View.GONE
                }
                if (!llallowedFields.isNullOrEmpty()) {
                    var START_LOCATION = Field(field = FIELD.START_LOCATION.name)
                    if (llallowedFields.contains(START_LOCATION) || checkIfExistes(
                            llallowedFields,
                            START_LOCATION
                        )
                    ) {
                        var position = llallowedFields.indexOf(START_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilStartLocation.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilStartLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLebelStartLocation.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilStartLocation.visibility = View.GONE

                    }

                    var END_LOCATION = Field(field = FIELD.END_LOCATION.name)
                    if (llallowedFields.contains(END_LOCATION)) {
                        var position = llallowedFields.indexOf(END_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilEndLocation.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilEndLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelEndLocation.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilEndLocation.visibility = View.GONE
                    }
                    var GOOGLE_OR_MANUAL_SOURCE = Field(field = FIELD.GOOGLE_OR_MANUAL_SOURCE.name)
                    var SYSTEM_LOCATION = Field(field = FIELD.SYSTEM_LOCATION.name)
                    if (llallowedFields.contains(GOOGLE_OR_MANUAL_SOURCE)) {
                        getCurrentLocation()
                        var position = llallowedFields.indexOf(GOOGLE_OR_MANUAL_SOURCE)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.VISIBLE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                true,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualStartLocationName.text =
                                llallowedFields[position].label
                    } else if (llallowedFields.contains(SYSTEM_LOCATION)) {
                        getCurrentLocation()
                        var position = llallowedFields.indexOf(SYSTEM_LOCATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.VISIBLE
                            mActivityCreateTaskBinding.llToggleEnableStartLoc.visibility = View.GONE
                            mActivityCreateTaskBinding.tvLableManualStartLocation.visibility =
                                View.GONE
                            mActivityCreateTaskBinding.edEnterManualStartLocation.visibility =
                                View.GONE
                            mActivityCreateTaskBinding.edEnterStartLocation.visibility = View.GONE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                true,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualStartLocation.visibility =
                                View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualStartLocationName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.cardManualStartLocation.visibility = View.GONE
                    }
                    var GOOGLE_OR_MANUAL_DESTINATION =
                        Field(field = FIELD.GOOGLE_OR_MANUAL_DESTINATION.name)
                    if (llallowedFields.contains(GOOGLE_OR_MANUAL_DESTINATION)) {
                        var position = llallowedFields.indexOf(GOOGLE_OR_MANUAL_DESTINATION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.cardManualEndLocation.visibility =
                                View.VISIBLE
                            mCreateTaskViewModel.getRegionList(
                                httpManager,
                                false,
                                RegionRequest(userGeoReq = allowGeography)
                            )
                        } else {
                            mActivityCreateTaskBinding.cardManualEndLocation.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelManualEndLocationName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.cardManualEndLocation.visibility = View.GONE
                    }
                    var SELECT_BUDDY = Field(field = FIELD.SELECT_BUDDY.name)
                    if (llallowedFields.contains(SELECT_BUDDY)) {
                        var position = llallowedFields.indexOf(SELECT_BUDDY)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llbuddy.visibility = View.VISIBLE
                            mCreateTaskViewModel.checkBuddy(httpManager)

                        } else {
                            mActivityCreateTaskBinding.llbuddy.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelBuddy.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llbuddy.visibility = View.GONE
                    }
                    var SELECT_CLIENT = Field(field = FIELD.SELECT_CLIENT.name)
                    if (llallowedFields.contains(SELECT_CLIENT)) {
                        var position = llallowedFields.indexOf(SELECT_CLIENT)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llClient.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.llClient.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelClient.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llClient.visibility = View.GONE
                    }
                    var START_TIME = Field(field = FIELD.START_TIME.name)
                    if (llallowedFields.contains(START_TIME)) {
                        var position = llallowedFields.indexOf(START_TIME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val c = Calendar.getInstance()
                            startHour = c.get(Calendar.HOUR_OF_DAY)
                            startMinute = c.get(Calendar.MINUTE)
                            val startTimew = DateTimeUtil.getFormattedTime(c.time)
                            startDay = c.get(Calendar.DAY_OF_MONTH)
                            startMonth = c.get(Calendar.MONTH) + 1
                            startYear = c.get(Calendar.YEAR)
                            startTime = c.timeInMillis
                            val startDate = "$startDay-$startMonth-$startYear"
                            //set current time into the field
                            edStartDateTime.setText("$startDate | $startTimew")
                            edStartDateTime.setSelection(edStartDateTime.text.length)
                            mActivityCreateTaskBinding.tilStartDateTime.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilStartDateTime.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLebelStartDateTime.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilStartDateTime.visibility = View.GONE
                    }
                    var END_TIME = Field(field = FIELD.END_TIME.name)
                    if (llallowedFields.contains(END_TIME)) {
                        var position = llallowedFields.indexOf(END_TIME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val cal = Calendar.getInstance()
                            cal.time = Date()
                            cal.add(Calendar.HOUR_OF_DAY, 1)
                            endYear = cal.get(Calendar.YEAR)
                            endMonth = cal.get(Calendar.MONTH) + 1
                            endDay = cal.get(Calendar.DAY_OF_MONTH)
                            endHour = cal.get(Calendar.HOUR_OF_DAY)
                            endMinute = cal.get(Calendar.MINUTE)
                            endTime = cal.timeInMillis
                            val endTimew = DateTimeUtil.getFormattedTime(cal.time)
                            val endDate = "$endDay-$endMonth-$endYear"

                            //set current time into the field
                            edEndDateTime.setText("$endDate | $endTimew")
                            edEndDateTime.setSelection(edEndDateTime.text.length)
                            mActivityCreateTaskBinding.tilEndDateTime.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilEndDateTime.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelEndDateTime.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilEndDateTime.visibility = View.GONE
                    }
                    var POINT_OF_CONTACT = Field(field = FIELD.POINT_OF_CONTACT.name)
                    if (llallowedFields.contains(POINT_OF_CONTACT)) {
                        var position = llallowedFields.indexOf(POINT_OF_CONTACT)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llPOC.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.llPOC.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelPOC.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llPOC.visibility = View.GONE
                    }
                    var TASK_NAME = Field(field = FIELD.TASK_NAME.name)
                    if (llallowedFields.contains(TASK_NAME)) {
                        var position = llallowedFields.indexOf(TASK_NAME)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilTaskName.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.tilTaskName.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelTaskName.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilTaskName.visibility = View.GONE
                    }
                    var DESCRIPTION = Field(field = FIELD.DESCRIPTION.name)
                    if (llallowedFields.contains(DESCRIPTION)) {

                        var position = llallowedFields.indexOf(DESCRIPTION)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.edDescription.visibility = View.VISIBLE
                            mActivityCreateTaskBinding.tvDescription.visibility = View.VISIBLE
                            mActivityCreateTaskBinding.lldescription.visibility = View.VISIBLE

                        } else {
                            mActivityCreateTaskBinding.edDescription.visibility = View.GONE
                            mActivityCreateTaskBinding.tvDescription.visibility = View.GONE
                            mActivityCreateTaskBinding.lldescription.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvDescription.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.edDescription.visibility = View.GONE
                        mActivityCreateTaskBinding.tvDescription.visibility = View.GONE
                        mActivityCreateTaskBinding.lldescription.visibility = View.GONE
                    }
                    var SELECT_FLEET = Field(field = FIELD.SELECT_FLEET.name)
                    if (llallowedFields.contains(SELECT_FLEET)) {
                        var position = llallowedFields.indexOf(SELECT_FLEET)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llfleet.visibility = View.VISIBLE
                            mCreateTaskViewModel.checkFleet(httpManager)
                        } else {
                            mActivityCreateTaskBinding.llfleet.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelFleet.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llfleet.visibility = View.GONE
                    }
                    var SELECT_CITY = Field(field = FIELD.SELECT_CITY.name)
                    if (llallowedFields.contains(SELECT_CITY)) {
                        var position = llallowedFields.indexOf(SELECT_CITY)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.llcity.visibility = View.VISIBLE
                        } else {
                            mActivityCreateTaskBinding.llcity.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelCity.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.llcity.visibility = View.GONE
                    }
                    var TASK_ID = Field(field = FIELD.TASK_ID.name)
                    if (llallowedFields.contains(TASK_ID)) {
                        val position = llallowedFields.indexOf(TASK_ID)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {


                            mActivityCreateTaskBinding.tilTaskId.visibility = View.VISIBLE
                        } else {
                            mActivityCreateTaskBinding.tilTaskId.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelTaskId.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilTaskId.visibility = View.GONE
                    }

                    val SYSTEM_HUBS = Field(
                        field = FIELD.SYSTEM_HUB.name
                    )
                    if (llallowedFields.contains(SYSTEM_HUBS)) {
                        val position = llallowedFields.indexOf(SYSTEM_HUBS)
                        val data = llallowedFields[position]
                        val advanceConfig = data.advanceConfig
                        target_system_hub = data.advanceConfig?.target!!
                        Log.d("target_system_hub", target_system_hub.toString())

                        if (advanceConfig!!.source!! == AdvancedConfigSource.NEAR_BY_HUBS) {
                            selectedHubSource1 = true
                            Log.d("advanceConfig", "NEAR_BY_HUBS")
                            setNoHub(AdvancedConfigSource.NEAR_BY_HUBS.source!!)

                            val lp = WindowManager.LayoutParams()
                            lp.copyFrom(dialogNearHub.window?.attributes)
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                            lp.dimAmount = 0.8f
                            val window: Window? = dialogNearHub.window
                            window?.setLayout(
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT
                            )
                            window?.setGravity(Gravity.CENTER)
                            val rvNearHubs =
                                dialogNearHub.findViewById<RecyclerView>(R.id.rv_loc_near)
                            getCurrentLocation(true)
                            rvNearHubs.adapter = nearHubAdapter
                            tvNearSearch.setOnClickListener {
                                mCreateTaskViewModel.selectLocation(
                                    mActivityCreateTaskBinding.etNearByHubLocation.rootView,
                                    NEAR_REQUEST_CODE
                                )
                            }
                            closeHubDialog.setOnClickListener {
                                dialogNearHub.dismiss()
                            }

                            mActivityCreateTaskBinding.ivNearByHub.setOnClickListener {
                                if (!dialogNearHub.isShowing) {
                                    dialogNearHub.show()
                                }
                            }

                            if (position != -1 && !data.skipVisibilty) {
                                mActivityCreateTaskBinding.cvNearByHub.visibility = View.VISIBLE
                                mActivityCreateTaskBinding.labelNearByHub.text = "${data.label}"
                                loggedScope1 = false
                            } else {
                                loggedScope1 = true
                                mActivityCreateTaskBinding.cvNearByHub.visibility = View.GONE
                            }
                        } else if (advanceConfig.source!! == AdvancedConfigSource.LOGGED_IN_USER_HUBS) {
                            selectedHubSource2 = true
                            Log.d("advanceConfig", "LOGGED_IN_USER_HUBS")
                            setNoHub(AdvancedConfigSource.LOGGED_IN_USER_HUBS.source!!)

                            if (position != -1 && !data.skipVisibilty) {
                                mActivityCreateTaskBinding.cvLoggedInHub.visibility = View.VISIBLE
                                mActivityCreateTaskBinding.labelLoggedInHub.text = "${data.label}"
                                getCurrentLocation(
                                    true,
                                    AdvancedConfigSource.LOGGED_IN_USER_HUBS.source
                                )
                                loggedScope1 = true
                            } else {
                                loggedScope1 = false
                                mActivityCreateTaskBinding.cvLoggedInHub.visibility = View.GONE
                            }
                        } else if (advanceConfig.source!! == AdvancedConfigSource.ALL_HUBS) {
                            selectedHubSource3 = true
                            Log.d("advanceConfig", "ALL_HUBS")
                            setNoHub(AdvancedConfigSource.ALL_HUBS.source!!)
                            if (position != -1 && !data.skipVisibilty) {
                                mActivityCreateTaskBinding.cvAllHub.visibility = View.VISIBLE
                                mActivityCreateTaskBinding.labelAllHub.text = "${data.label}"
                                loggedScope1 = false
                                getCurrentLocation(true, AdvancedConfigSource.ALL_HUBS.source)
                            } else {
                                loggedScope1 = true
                                mActivityCreateTaskBinding.cvAllHub.visibility = View.GONE
                            }
                        }
                    }

                    //to update
                    val TIME_SLOT = Field(field = FIELD.TIME_SLOT.name)
                    if (llallowedFields.contains(TIME_SLOT)) {
                        val position = llallowedFields.indexOf(TIME_SLOT)
                        val data = llallowedFields[position]

                        mCreateTaskViewModel.getUserLocations(httpManager)

                        mActivityCreateTaskBinding.cvTimeSlot.visibility = View.VISIBLE

                        dialogSlot = Dialog(this)
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

                        val title = dialogSlot.findViewById<TextView>(R.id.titlePop)
                        val ddHubs = dialogSlot.findViewById<Spinner>(R.id.dd_hubs)
                        val ddTitle = dialogSlot.findViewById<TextView>(R.id.dd_title)
                        val btnDone = dialogSlot.findViewById<AppCompatButton>(R.id.btn_done)
                        val btnClose = dialogSlot.findViewById<ImageView>(R.id.btnClose)

                        title.text =
                            if (TIME_SLOT.label != null && TIME_SLOT.label.toString() != "" && TIME_SLOT.label.toString() != "null") TIME_SLOT.label.toString() else "Select Preferred Slot"
                        title.visibility = View.VISIBLE
                        rvDate = dialogSlot.findViewById(R.id.rv_date)
                        rvSlot.layoutManager = GridLayoutManager(this, 3)

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
                        window.setGravity(Gravity.CENTER)
                        dialogSlot.window!!.attributes = lp
                        etSlot.setOnClickListener {
                            Log.d("advanceConfig", data.advanceConfig?.source.toString())
                            if (selectedHubSource1 || selectedHubSource2 || selectedHubSource3 && data.advanceConfig?.source == AdvancedConfigSource.TASK_SCOPE_HUB_SLOTS) {
                                Log.d("advanceConfig", "TASK_SCOPE_HUB_SLOTS")
                                availType = "GEO"
                                var checkPos = false
                                var selectedPosition = 0
                                if (loggedScope1) {
                                    if (selectedHubSource2) {
                                        Log.e("logger", "2")
                                        checkPos = true
                                        loggedScope = true
                                        ///hubs.clear()
                                        hubs = selectedHubList2
                                        selectedPosition = spinnerLogged.selectedItemPosition
                                        Log.e("logger", hubs.toString())
                                    }
                                } else {
                                    if (selectedHubSource1) {
                                        Log.e("logger", "1")
                                        checkPos = true
                                        loggedScope = false
                                        //hubs.clear()
                                        hubs = selectedHubList1
                                        selectedPosition = spinnerNear.selectedItemPosition
                                    }
                                    if (!checkPos) {
                                        Log.e("logger", "3")
                                        if (selectedHubSource3) {
                                            checkPos = true
                                            loggedScope = false
                                            //hubs.clear()
                                            hubs = selectedHubList3
                                            selectedPosition = spinnerAll.selectedItemPosition
                                        }
                                    }
                                }
                                if (checkPos) {
                                    Log.e("checkPos", "in position=$selectedPosition")
                                    if (!loggedScope && selectedPosition > 0) {
                                        Log.e("checkPos", "in position=$selectedPosition")
                                        dialogSlot.show()
                                    } else if (loggedScope && selectedPosition > 0) {
                                        dialogSlot.show()
                                    } else {
                                        TrackiToast.Message.showShort(this, "Please select the hub")
                                    }
                                    timePosition = 0

                                    btnDone?.setOnClickListener {
                                        if (dateFinal != "" && timeFinal != "") {
                                            dialogSlot.dismiss()
                                            etSlot.setText("$dateFinal $timeFinal")
                                            hub = hubs.find { it.hubId == hubIdFinal }!!
                                        } else {
                                            TrackiToast.Message.showShort(
                                                this@NewCreateTaskActivity,
                                                "Select Slot To Continue"
                                            )
                                        }
                                    }
                                    btnClose?.setOnClickListener {
                                        dialogSlot.dismiss()
                                    }
                                    if (selectedPosition > 0) {
                                        callSlotApi(ddHubs, ddTitle, selectedPosition, data)
                                    }
                                } else {
                                    TrackiToast.Message.showShort(this, "No Hubs Found")
                                }
                            } else if (data.advanceConfig?.source == AdvancedConfigSource.LOGGED_IN_USER_HUBS_SLOTS) {
                                availType = "USER"
                                hubs =
                                    preferencesHelper.userHubList.toMutableList() as ArrayList<Hub>
                                Log.d("advanceConfig", "LOGGED_IN_USER_HUBS_SLOTS")
                                dialogSlot.show()
                                timePosition = 0
                                btnDone?.setOnClickListener {
                                    if (dateFinal != "" && timeFinal != "") {
                                        dialogSlot.dismiss()
                                        etSlot.setText("$dateFinal $timeFinal")
                                        hub = hubs.find { it.hubId == hubIdFinal }!!
                                    } else {
                                        TrackiToast.Message.showShort(
                                            this@NewCreateTaskActivity,
                                            "Select Slot To Continue"
                                        )
                                    }
                                }
                                btnClose?.setOnClickListener {
                                    dialogSlot.dismiss()
                                }
                                callSlotApi(ddHubs, ddTitle, 1, data)
                            } else if (data.advanceConfig?.source == AdvancedConfigSource.TASK_SCOPE_PRODUCT_SLOTS) {
                                availType = "INVENTORY"
                                hubs =
                                    preferencesHelper.userHubList.toMutableList() as ArrayList<Hub>
                                Log.d("advanceConfig", "TASK_SCOPE_PRODUCT_SLOTS")
                                dialogSlot.show()
                                timePosition = 0
                                btnDone?.setOnClickListener {
                                    if (dateFinal != "" && timeFinal != "") {
                                        dialogSlot.dismiss()
                                        etSlot.setText("$dateFinal $timeFinal")
                                        hub = hubs.find { it.hubId == hubIdFinal }!!
                                    } else {
                                        TrackiToast.Message.showShort(
                                            this@NewCreateTaskActivity,
                                            "Select Slot To Continue"
                                        )
                                    }
                                }
                                btnClose?.setOnClickListener {
                                    dialogSlot.dismiss()
                                }
                                callSlotApi(ddHubs, ddTitle, 1, data)
                            }
                        }
                    } else {
                        mActivityCreateTaskBinding.cvTimeSlot.visibility = View.GONE
                    }

                    var REFERENCE_ID = Field(field = FIELD.REFERENCE_ID.name)
                    if (llallowedFields.contains(REFERENCE_ID)) {
                        var position = llallowedFields.indexOf(REFERENCE_ID)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            mActivityCreateTaskBinding.tilReference.visibility = View.VISIBLE
                            if (parentReffId != null) {
                                mActivityCreateTaskBinding.edReference.setText(parentReffId)
                                mActivityCreateTaskBinding.edReference.isEnabled = false
                            } else {
                                if (intent.hasExtra("reffId")) {
                                    var reffId = intent.getStringExtra("reffId")
                                    mActivityCreateTaskBinding.edReference.setText(reffId)
                                    mActivityCreateTaskBinding.edReference.isEnabled = true
                                }
                            }
                        } else {
                            mActivityCreateTaskBinding.tilReference.visibility = View.GONE
                        }
                        if (position != -1 && llallowedFields[position].label != null && llallowedFields[position].label!!.isNotEmpty())
                            mActivityCreateTaskBinding.tvLabelReference.text =
                                llallowedFields[position].label
                    } else {
                        mActivityCreateTaskBinding.tilReference.visibility = View.GONE
                    }


                    //here we add other fields field1 to field15

                    //start

                    // FIELD1
                    val FIELD1 = Field(field = FIELD.FIELD1.name)
                    if (llallowedFields.contains(FIELD1)) {
                        val position = llallowedFields.indexOf(FIELD1)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field1text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield1text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield1number.text = fieldData.label
                                    mActivityCreateTaskBinding.field1number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield1spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field1spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield1spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield1spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield1radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field1radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio1Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD2
                    val FIELD2 = Field(field = FIELD.FIELD2.name)
                    if (llallowedFields.contains(FIELD2)) {
                        val position = llallowedFields.indexOf(FIELD2)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field2text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield2text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield2number.text = fieldData.label
                                    mActivityCreateTaskBinding.field2number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield2spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field2spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield2spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield2spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield2radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field2radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio2Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD3
                    val FIELD3 = Field(field = FIELD.FIELD3.name)
                    if (llallowedFields.contains(FIELD3)) {
                        val position = llallowedFields.indexOf(FIELD3)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field3text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield3text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield3number.text = fieldData.label
                                    mActivityCreateTaskBinding.field3number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield3spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field3spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield3spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield3spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield3radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field3radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio3Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD4
                    val FIELD4 = Field(field = FIELD.FIELD4.name)
                    if (llallowedFields.contains(FIELD4)) {
                        val position = llallowedFields.indexOf(FIELD4)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field4text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield4text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield4number.text = fieldData.label
                                    mActivityCreateTaskBinding.field4number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield4spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field4spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield4spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield4spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield4radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field4radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio4Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD5
                    val FIELD5 = Field(field = FIELD.FIELD5.name)
                    if (llallowedFields.contains(FIELD5)) {
                        val position = llallowedFields.indexOf(FIELD5)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field5text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield5text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield5number.text = fieldData.label
                                    mActivityCreateTaskBinding.field5number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield5spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field5spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield5spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield5spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield5radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field5radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio5Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD6
                    val FIELD6 = Field(field = FIELD.FIELD6.name)
                    if (llallowedFields.contains(FIELD6)) {
                        val position = llallowedFields.indexOf(FIELD6)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field6text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield6text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield6number.text = fieldData.label
                                    mActivityCreateTaskBinding.field6number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield6spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field6spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield6spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield6spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield6radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field6radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio6Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD7
                    val FIELD7 = Field(field = FIELD.FIELD7.name)
                    if (llallowedFields.contains(FIELD7)) {
                        val position = llallowedFields.indexOf(FIELD7)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field7text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield7text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield7number.text = fieldData.label
                                    mActivityCreateTaskBinding.field7number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield7spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field7spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield7spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield7spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield7radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field7radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio7Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }

                    // FIELD8
                    val FIELD8 = Field(field = FIELD.FIELD8.name)
                    if (llallowedFields.contains(FIELD8)) {
                        val position = llallowedFields.indexOf(FIELD8)
                        if (position != -1 && !llallowedFields[position].skipVisibilty) {
                            val fieldData = llallowedFields[position]
                            when (fieldData.type) {
                                DataType.TEXT -> {
                                    mActivityCreateTaskBinding.field8text.visibility = View.VISIBLE
                                    mActivityCreateTaskBinding.tvfield8text.text = fieldData.label
                                }

                                DataType.NUMBER -> {
                                    mActivityCreateTaskBinding.tvfield8number.text = fieldData.label
                                    mActivityCreateTaskBinding.field8number.visibility =
                                        View.VISIBLE
                                }

                                DataType.DROPDOWN -> {
                                    mActivityCreateTaskBinding.tvfield8spinner.text =
                                        fieldData.label
                                    mActivityCreateTaskBinding.field8spinner.visibility =
                                        View.VISIBLE
                                    val adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_item,
                                        fieldData.value!!.split(",").toTypedArray()
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    mActivityCreateTaskBinding.spinnerfield8spinner.adapter =
                                        adapter
                                    mActivityCreateTaskBinding.spinnerfield8spinner.setSelection(0)
                                }

                                DataType.RADIO -> {
                                    mActivityCreateTaskBinding.tvfield8radio.text = fieldData.label
                                    mActivityCreateTaskBinding.field8radio.visibility = View.VISIBLE
                                    val data = fieldData.value!!.split(",").toTypedArray()
                                    for (item in data) {
                                        val radioButton = RadioButton(this)
                                        radioButton.tag = position
                                        radioButton.setButtonDrawable(R.drawable.custom_radio_button)
                                        radioButton.id = View.generateViewId()
                                        radioButton.setPadding(
                                            radioButton.paddingLeft + 20,
                                            radioButton.paddingTop,
                                            radioButton.paddingRight,
                                            radioButton.paddingBottom
                                        )
                                        radioButton.setText(item)
                                        val params = LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f
                                        )
                                        params.setMargins(0, 0, 0, 10)
                                        radioButton.layoutParams = params
                                        mActivityCreateTaskBinding.radio8Group.addView(radioButton)
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                    //end
                }

                if (getDynamicFormId(categoryId) != null && getDynamicFormId(categoryId)!!.isNotEmpty()) {
                    Log.d("here", "Three$categoryId")
                    Log.d("here", "Three" + getDynamicFormId(categoryId))
                    dynamicFragment = NewDynamicFormFragment.getInstance(
                        getDynamicFormId(categoryId),
                        categoryId,
                        isEditable,
                        ArrayList()
                    )
                    replaceFragment(dynamicFragment!!, getDynamicFormId(categoryId))
                }
                dynamicFormsNew = CommonUtils.getFormByFormId(getDynamicFormId(categoryId))
                if (dynamicFormsNew != null && dynamicFormsNew!!.fields != null && dynamicFormsNew!!.fields!!.isNotEmpty()) {
                    try {
                        val formData: FormData? =
                            dynamicFormsNew!!.fields!!.filter { s -> s.type == DataType.BUTTON }
                                .filter { data -> data.actionConfig?.action == Type.DISPOSE || data.actionConfig?.action == Type.FORM || data.actionConfig?.action == Type.API }
                                .single()
                        formData?.let {
                            it.value?.let {
                                btnCLickSubmit.text = formData.value
                            }
                        }
                    } catch (e: java.lang.Exception) {

                    }

                } else {
                    btnCLickSubmit.text = "Create"
                }
            }
        })

        val arrayAdapter =
            object : ArrayAdapter<String?>(this, android.R.layout.simple_spinner_item, categories) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    val externalFont =
                        Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                    (v as TextView).typeface = externalFont
                    return v
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val v = super.getDropDownView(position, convertView, parent)
                    val externalFont =
                        Typeface.createFromAsset(parent.context.assets, "fonts/campton_book.ttf")
                    (v as TextView).typeface = externalFont

                    return v
                }
            }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategory!!.adapter = arrayAdapter

        if (intent.hasExtra("from")) {
            if (intent.getStringExtra("from")!!
                    .equals("taskListing") || intent.getStringExtra("from")!!.equals("dashBoard")
            ) {
                cardViewCategoryId.visibility = View.GONE
                val listCategory = preferencesHelper.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData = listCategory[position]
                            category = myCatData
                            allowGeography = myCatData.allowGeography
                        }
                    }
                }
                mCategoryId.value = categoryId!!
                setToolbar(mActivityCreateTaskBinding.toolbar, "Create " + getLabelName(categoryId))

            }
        } else {
            cardViewCategoryId.visibility = View.VISIBLE
            setToolbar(mActivityCreateTaskBinding.toolbar, "Create " + getLabelName(categoryId))

        }

        // mCategoryId.value = "0"
        spnCategory!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // val selectedItem = parent.getItemAtPosition(position).toString()
                categoryId = list[position].categoryId
                category = list[position]
                selItem = list[position].name!!
                mCategoryId.value = categoryId!!
                val listCategory = preferencesHelper.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData = listCategory[position]
                            allowGeography = myCatData.allowGeography
                        }
                    }
                }
                CommonUtils.showLogMessage("e", "categoryId", categoryId);


            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val taskActions: Array<String?>
        lookUps = TrackiSdkApplication.getLookups()
        if (lookUps != null && lookUps?.isNotEmpty()!!) {
            taskActions = Array(lookUps!!.size) { null }
//            taskActions[0] = "Select Task Type"
            for (i in lookUps?.indices!!) {
                taskActions[i] = lookUps!![i].value!!
            }
            Log.e(NewCreateTaskActivity.TAG, "array items---->> $taskActions")
            try {
                val aa = ArrayAdapter(this, R.layout.item_simple_spinner_sdk, taskActions)
                aa.setDropDownViewResource(R.layout.item_simple_spinner_dropdown)
                //spinnerTaskAction.adapter = aa
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            // spinnerTaskAction.visibility = View.GONE
        }

        val taskName = "Task created at ${
            DateTimeUtil.getFormattedTime(
                System.currentTimeMillis(),
                DateTimeUtil.DATE_TIME_FORMAT_2
            )
        }"
        edTaskName.setText(taskName)
        edTaskName.setSelection(edTaskName.text.length)



        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_EDIT_TASK)) {
                task = intent.getSerializableExtra(AppConstants.Extra.EXTRA_EDIT_TASK) as Task

                val contact = task.contact
                if (contact != null) {
                    if (contact.name != null) {
                        edContactName.setText(contact.name)
                    }
                    if (contact.mobile != null) {
                        edMobileNumber.setText(contact.mobile)
                    }
                }
                edTaskName.setText(task.taskName)
                edTaskName.setSelection(task.taskName?.length!!)

                val calendar = Calendar.getInstance()
                startTime = task.startTime
                calendar.timeInMillis = startTime

                startDay = calendar.get(Calendar.DAY_OF_MONTH)
                startMonth = calendar.get(Calendar.MONTH)
                startYear = calendar.get(Calendar.YEAR)
                startHour = calendar.get(Calendar.HOUR_OF_DAY)
                startMinute = calendar.get(Calendar.MINUTE)

                val selectedDate = "$startDay-${startMonth + 1}-$startYear"
                val formattedTime = DateTimeUtil.getFormattedTime(calendar.time!!)
                edStartDateTime.setText("$selectedDate | $formattedTime")
                edStartDateTime.setSelection("$selectedDate | $formattedTime".length)

                val cala = Calendar.getInstance()
                if (task.endTime != 0L) {
                    endTime = task.endTime
                    cala.timeInMillis = endTime
                }
                endDay = cala.get(Calendar.DAY_OF_MONTH)
                endMonth = cala.get(Calendar.MONTH)
                endYear = cala.get(Calendar.YEAR)
                endHour = cala.get(Calendar.HOUR_OF_DAY)
                endMinute = cala.get(Calendar.MINUTE)

                val selectedEndDate = "$endDay-${endMonth + 1}-$endYear"
                val formattedEndTime = DateTimeUtil.getFormattedTime(cala.time!!)
                edEndDateTime.setText("$selectedEndDate | $formattedEndTime")
                edEndDateTime.setSelection("$selectedEndDate | $formattedEndTime".length)

                edStartLocation.setText(task.source?.address)
                edStartLocation.setSelection(task.source?.address?.length!!)
                startLatLng =
                    LatLng(task.source?.location?.latitude!!, task.source?.location?.longitude!!)
                if (task.destination != null && task.destination?.location != null) {
                    endLatLng = LatLng(
                        task.destination?.location?.latitude!!,
                        task.destination?.location?.longitude!!
                    )
                    edEndLocation.setText(task.destination?.address)
                    edEndLocation.setSelection(task.destination?.address?.length!!)
                }
                if (task.description != null) {
                    edDescription.setText(task.description)
                    edDescription.setSelection(task.description?.length!!)
                }

                buddyId = task.buddyDetail?.buddyId
                fleetId = task.fleetId
                buddyName = task.buddyDetail?.name
                taskId = task.taskId!!

            }
            if (intent.hasExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) &&
                intent.getSerializableExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) != null
            ) {
                @Suppress("UNCHECKED_CAST")
                val list =
                    intent.getSerializableExtra(AppConstants.Extra.SELECTED_BUDDY_LIST_EXTRA) as ArrayList<Buddy>
                if (list.size > 0) {
                    buddyId = list[0].buddyId!!
                    buddyName = list[0].name!!
                }
            }
            if (intent.hasExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) &&
                intent.getSerializableExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) != null
            ) {
                @Suppress("UNCHECKED_CAST")
                val list =
                    intent.getSerializableExtra(AppConstants.Extra.SELECTED_FLEET_LIST_EXTRA) as ArrayList<Fleet>
                if (list.size > 0) {
                    fleetId = list[0].fleetId!!
                }
            }
        }
    }

    private fun searchRefTask(apitemp: Api) {

        Log.e("checkUrl", "${apitemp.url}")
//        mCreateTaskViewModel.getSearchReferenceList(httpManager, apitemp, searchRequest)
    }

    private fun callSlotApi(
        ddHubs: Spinner,
        ddTitle: TextView,
        selectedPosition: Int,
        data: Field
    ) {
        val list = ArrayList<Hub>()
        var selectedPos = selectedPosition - 1
        val listNames = kotlin.collections.ArrayList<String>()

        Log.e("hubs", "$hubs")

        if (hubs.isNotEmpty()) {
            if (hubs.size > 0) {
                for (hub in hubs) {
                    list.add(hub)
                    listNames.add(hub.name.toString())
                }

                val hubAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listNames)
                ddHubs.adapter = hubAdapter

                if (hubIdFinal != null) {
                    list.forEachIndexed { index, item ->
                        println("index = $index, item = $item ")
                        if (item.hubId.equals(hubIdFinal)) {
                            selectedPos = index
                        }
                    }
                }

                if (hubIdFinalDestination != null) {
                    list.forEachIndexed { index, item ->
                        println("index = $index, item = $item ")
                        if (item.hubId.equals(hubIdFinalDestination)) {
                            selectedPos = index
                        }
                    }
                }

                ddHubs.setSelection(selectedPos)
                hubIdFinal = list[selectedPos].hubId.toString()

                if (data.advanceConfig?.source == AdvancedConfigSource.TASK_SCOPE_HUB_SLOTS) {
                    ddTitle.text = "Selected Geography"
                    ddHubs.isEnabled = false
                    timePosition = selectedPos
                    mCreateTaskViewModel.getSlotAvailability(
                        httpManager,
                        TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!,
                        hubIdFinal,
                        date
                    )
                } else if (data.advanceConfig?.source == AdvancedConfigSource.LOGGED_IN_USER_HUBS_SLOTS) {
                    ddTitle.text = "Select Geography"
                    ddHubs.onItemSelectedListener = object : OnItemSelectedListener {
                        @SuppressLint("NewApi")
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            Log.e("hubs", "call GET_TIME_SLOTS")
                            timePosition = p2
                            hubIdFinalDestination = list[timePosition].hubId.toString()
                            hubDestination = list[timePosition]
                            mCreateTaskViewModel.getSlotAvailability(
                                httpManager,
                                TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!,
                                hubIdFinalDestination,
                                date
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }
                } else if (data.advanceConfig?.source == AdvancedConfigSource.TASK_SCOPE_PRODUCT_SLOTS) {
                    ddHubs.onItemSelectedListener = object : OnItemSelectedListener {
                        @SuppressLint("NewApi")
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            Log.e("hubs", "call TASK_SCOPE_PRODUCT_SLOTS")
                            ddHubs.visibility = View.GONE
                            ddTitle.visibility = View.GONE
                            timePosition = selectedPos
                            mCreateTaskViewModel.getSlotAvailability(
                                httpManager,
                                TrackiSdkApplication.getApiMap()[ApiType.GET_TIME_SLOTS]!!,
                                invIds?.get(0).toString(),
                                date
                            )
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }
                }
            } else {
                timePosition = 0
                list.add(hubs[0])
                listNames.add(hubs[0].name.toString())
                hubIdFinal = list[0].hubId.toString()
                ddHubs.visibility = View.GONE
                ddTitle.visibility = View.GONE
            }
        } else {
            llSlots.visibility = View.GONE
            ivNoData.visibility = View.VISIBLE
        }
    }

    private fun searchReferTasks() {

    }

    private fun performSearchWidgetTask() {
        if (assigneeLabel != null) {
            tvTitleSelectUser.text = assigneeLabel
            //tvLabelUser.text="Selected $assigneeLabel : "
            rlSelectedUser.visibility = View.GONE

        }
        mActivityCreateTaskBinding.etUser.setText("")
        selectedUsername = ""
        selectedUserId = ""
        mActivityCreateTaskBinding.etUser.threshold = 2
        var suggestionAdapter =
            BaseAutocompleteAdapter(this)
        suggestionAdapter.setViewModel(mGetSuggetionViewModel)
        suggestionAdapter.setHttpManager(httpManager)
        if (requestUserType != null)
            suggestionAdapter.setUserType(requestUserType!!)

        if (TrackiSdkApplication.getApiMap()[ApiType.GET_SEARCH_USER] != null) {
            val api = TrackiSdkApplication.getApiMap()[ApiType.GET_SEARCH_USER]
            suggestionAdapter.setApi(api)
        }
        mActivityCreateTaskBinding.etUser.setAdapter(suggestionAdapter)
        mActivityCreateTaskBinding.etUser.setLoadingIndicator(
            findViewById<View>(R.id.pb_loading_indicator) as ProgressBar
        )
//
    }

    override fun userSelected(dataUser: ClientData) {
        var name = ""
        if (dataUser.firstName != null && dataUser.lastName != null) {
            name = dataUser.firstName + " " + dataUser.lastName
        } else if (dataUser.firstName != null) {
            name = dataUser.firstName
        } else if (dataUser.lastName != null) {
            name = dataUser.lastName
        }
        selectedUsername = name
        if (dataUser.userId != null) {
            selectedUserId = dataUser.userId
            CommonUtils.showLogMessage("e", "selectedUserId", dataUser.userId)
        }

        mActivityCreateTaskBinding.etUser.setText("")
        var ivUser = rlSelectedUser.findViewById<ImageView>(R.id.ivUser)
        var tvName = rlSelectedUser.findViewById<TextView>(R.id.tvName)
        var tvMobile = rlSelectedUser.findViewById<TextView>(R.id.tvMobile)
        tvName.setText(name)
        if (dataUser.mobile != null)
            tvMobile.setText(dataUser.mobile)
        else
            tvMobile.setText("")
        if (dataUser.profileImg != null && dataUser.profileImg != "") {
            GlideApp.with(this)
                .asBitmap()
                .load(dataUser.profileImg)
                .apply(
                    RequestOptions()
                        .transform(CircleTransform())
                        .placeholder(R.drawable.ic_my_profile)
                )
                .error(R.drawable.ic_my_profile)
                .into(ivUser)
        } else {
            ivUser.setImageResource(R.drawable.ic_my_profile)
        }
        rlSelectedUser.visibility = View.VISIBLE
    }

    override fun handleUserNotFound() {
        openDialogUserNotFound()
    }

    private fun openDialogUserNotFound() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.dialog_user_not_found_sdk)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)

        val imageView = dialog.findViewById<View>(R.id.iv_cancel) as ImageView
        val add = dialog.findViewById<View>(R.id.btnPositive) as Button
        val noThanks = dialog.findViewById<View>(R.id.btnNegative) as Button

        dialog.window!!.attributes = lp
        imageView.setOnClickListener { dialog.dismiss() }
        noThanks.setOnClickListener { dialog.dismiss() }
        add.setOnClickListener {
            dialog.dismiss()
            addUserScreen()
        }
        if (!dialog.isShowing) dialog.show()
    }

    @SuppressLint("SuspiciousIndentation")
    fun addUserScreen() {
        val intent = Intent("com.rocketflow.sdk.CreateCustomer")
        if (!requestUserType.isNullOrEmpty())
            intent.putStringArrayListExtra(
                AppConstants.REQUESTED_USER_TYPES,
                requestUserType as java.util.ArrayList<String>?
            )
        sendBroadcast(intent)

    }


    @SuppressLint("SuspiciousIndentation")
    private fun replaceFragment(fragment: Fragment, fragmentName: String?) {
        val formName = CommonUtils.getFormByFormId(fragmentName)
        CommonUtils.showLogMessage("e", "fragmentName ", fragmentName)
        if (formName?.name != null) {
            setToolbar(mActivityCreateTaskBinding.toolbar, "Create " + getLabelName(categoryId))
        } else {
//            mActivityCreateTaskBinding?.toolbar?.title = ""
            setToolbar(mActivityCreateTaskBinding.toolbar, "Create " + getLabelName(categoryId))
        }
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        if (allowedFieldFirst != null && !allowedFieldFirst!!) {
            CommonUtils.showLogMessage("e", "allowedFieldFirst", "false");
            container.visibility = View.GONE
            ft.replace(R.id.container_second, fragment, fragmentName)
            container_second.visibility = View.VISIBLE
            if (formName == null) {
                container_second.visibility = View.GONE
            }
        } else {
            CommonUtils.showLogMessage("e", "allowedFieldFirst", "true");
            container_second.visibility = View.GONE
            ft.replace(R.id.container, fragment, fragmentName)
            container.visibility = View.VISIBLE
            if (formName == null) {
                container.visibility = View.GONE
            }
        }
        ft.commit()
    }

    private fun getSystemHubs(request: SystemHubRequest) {
        mCreateTaskViewModel.getSystemHubs(httpManager, request)
    }

    private var services: ArrayList<Service>? = ArrayList()
    private var serviceIds: ArrayList<String> = ArrayList()
    private var serviceNames: ArrayList<String> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    if (view.id == R.id.edStartLocation) {
                        startLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edStartLocation.setText(place.name)
                    } else if (view.id == R.id.edEndLocation) {
                        endLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEndLocation.setText(place.name)
                    } else if (view.id == R.id.edEnterStartLocation) {
                        startLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEnterStartLocation.setText(place.name)
                    } else if (view.id == R.id.edEnterEndLocation) {
                        endLatLng = place.latLng!!
                        mActivityCreateTaskBinding.edEnterEndLocation.setText(place.name)
                    }
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(this, status.statusMessage!!)
                }

                Activity.RESULT_CANCELED -> // The user canceled the operation.
                    TrackiToast.Message.showShort(this, "operation cancelled.")
            }
        }

        if (requestCode == NEAR_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.e(TAG, "Place: " + place.name + ", " + place.id)
                    mActivityCreateTaskBinding.etNearByHubLocation.setText(place.address)
                    tvNearSearch.text = place.address
                    val hubCoordinates = HubCoordinates(
                        ArrayList(), place.latLng?.latitude, "",
                        place.latLng?.longitude
                    )
                    val systemHubRequset = SystemHubRequest(
                        categoryId,
                        hubCoordinates,
                        "",
                        5000.00,
                        AdvancedConfigSource.NEAR_BY_HUBS.source
                    )
                    val location = GeoCoordinates()
                    location.latitude = place.latLng?.latitude!!
                    location.longitude = place.latLng?.longitude!!
                    val place1: com.rf.taskmodule.data.model.response.config.Place =
                        com.rf.taskmodule.data.model.response.config.Place()
                    place1.address = place.address
                    place1.location = location
                    createTaskRequest?.source = place1
                    sourceNameA = AdvancedConfigSource.NEAR_BY_HUBS.source
                    getSystemHubs(systemHubRequset)
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(TAG, status.statusMessage)
                    TrackiToast.Message.showShort(this, status.statusMessage!!)
                }

                Activity.RESULT_CANCELED -> // The user canceled the operation.
                    TrackiToast.Message.showShort(this, "operation cancelled.")
            }
        }

        if (requestCode == SCAN_REFERENCE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val gson = Gson()
                    val task = gson.fromJson(data!!.getStringExtra("result"), Task::class.java)
                    if (task.clientTaskId != null) {
                        if (!multiReference) {
                            listRefTask.clear()
                        }
                        listRefTask.add(task)
                        adapter.notifyDataSetChanged()
                        mActivityCreateTaskBinding.etScanner.setText(task.clientTaskId)
                    }
                }
            }
        }

        if (requestCode == 546) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = data!!.getStringExtra("serviceIds")
                    val listType = object : TypeToken<ArrayList<Service?>?>() {}.type
                    services = Gson().fromJson(result, listType)
                    services?.forEach { item ->
                        serviceIds.add(item.id!!)
                        serviceNames.add(item.name!!)
                    }
                    createTaskRequest?.serviceIds = serviceIds
                    mActivityCreateTaskBinding.etServices.setText(
                        serviceNames.joinToString(
                            separator = ", "
                        )
                    )
                    Log.d("serviceIds", services.toString())
                }
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<BaseResponse> =
                JSONConverter()
            val response: BaseResponse =
                jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            if (response.taskId != null)
                openMainActivity(response.taskId!!)
        }
    }

    private fun setHubs(
        finalSource: String,
        arrayList: ArrayList<String>,
        listHubs: ArrayList<Hub>
    ) {
        if (arrayList.isNotEmpty()) {
            val spAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            Log.e("hubsModeDS", "$listHubs")
            when (finalSource) {
                AdvancedConfigSource.NEAR_BY_HUBS.source -> {
                    selectedHubSource1 = true
                    selectedHubList1.clear()
                    selectedHubList1.addAll(listHubs)
                    nearHubAdapter.clearItems()
                    nearHubAdapter.addItems(listHubs)
                    spinnerNear.adapter = spAdapter
                    if (listHubs.isNotEmpty()) {
                        nearHubNoData.visibility = View.GONE
                    } else {
                        nearHubNoData.visibility = View.VISIBLE
                    }
                }

                AdvancedConfigSource.LOGGED_IN_USER_HUBS.source -> {
                    selectedHubSource2 = true
                    selectedHubList2.clear()
                    selectedHubList2.addAll(listHubs)
                    spinnerLogged.adapter = spAdapter
                    Log.d("logger", "clear data$listHubs")
                }

                AdvancedConfigSource.ALL_HUBS.source -> {
                    selectedHubSource3 = true
                    selectedHubList3.clear()
                    selectedHubList3.addAll(listHubs)
                    spinnerAll.adapter = spAdapter
                }

            }
        }
    }

    override fun handleSystemHubs(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        Log.e("hubMode", "${result.toString()}")
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val systemHubResponse =
                Gson().fromJson(result.toString(), SystemHubResponse::class.java)
            if (systemHubResponse.successful == true) {
                if (systemHubResponse.data != null) {
                    if (systemHubResponse.data!!.isNotEmpty()) {
                        val arrayListTemp = systemHubResponse.data!!
                        val arrayList = ArrayList<String>()
                        arrayList.add("Select Hub")
                        arrayListTemp.forEach { arrayList.add(it.name.toString()) }
                        setHubs(sourceNameA!!, arrayList, arrayListTemp)
                    } else {
                        setNoHub(sourceNameA!!)
                    }
                } else {
                    setNoHub(sourceNameA!!)
                }
            } else {
                setNoHub(sourceNameA!!)
            }
        }

    }

    private fun setNoHub(sourceB: String) {
        Log.e("hubMode", "0 $sourceNameA")
        val arrayList = ArrayList<String>()
        arrayList.add("No Hub Found")
        setHubs(sourceB, arrayList, ArrayList())
    }

    override fun handleTaskResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {

            val taskListing =
                Gson().fromJson(
                    result.toString(),
                    SearchRefResponse::class.java
                )
            if (taskListing.data != null) {
                val list =
                    taskListing.data


                if (list != null) {

                    setupAutoCompleteTextView(list)
                }

            }
        }
    }

    override fun handleTDResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this@NewCreateTaskActivity)) {
            var taskResponse1 = Gson().fromJson("$result", TaskResponse::class.java)
            if (taskResponse1 != null) {
                if (taskResponse1.taskDetail != null) {
                    val taskResponse = taskResponse1.taskDetail
                    if (!multiReference) {
                        listRefTask.clear()
                    }
                    listRefTask.add(taskResponse!!)
                    mActivityCreateTaskBinding.etScanner.setText("")
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun setupAutoCompleteTextView(list: Map<String, String>) {
        val listTemp = kotlin.collections.ArrayList<String>()
        val listTemp1 = kotlin.collections.ArrayList<String>()
        for (item in list) {
            listTemp.add(item.value)
            listTemp1.add(item.key)
        }
        Log.e("checkList", "${list.size}")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listTemp)
        etScanner.setAdapter(adapter)
        etScanner.setOnItemClickListener { adapterView, view, pos, l ->
            mCreateTaskViewModel.getTaskDetails(httpManager, listTemp1[pos])
        }
    }


    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                return
            }
            for (location in locationResult.locations) {
                if (location != null) {
                    removeLocationUpdates(this)
                    startLatLng = LatLng(location.latitude, location.longitude)
                    endLatLng = LatLng(location.latitude, location.longitude)
                    var stringAddress =
                        CommonUtils.getAddress(this@NewCreateTaskActivity, startLatLng)
                    stringAddress?.let {
                        mActivityCreateTaskBinding.edStartLocation.setText(it)
                        mActivityCreateTaskBinding.edEnterStartLocation.setText(it)
                        mActivityCreateTaskBinding.edEnterEndLocation.setText(it)
                        mActivityCreateTaskBinding.edEndLocation.setText(it)
                    }


//
                }
            }
        }
    }


    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 23487
        private const val NEAR_REQUEST_CODE = 23488
        private val TAG = NewCreateTaskActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, NewCreateTaskActivity::class.java)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onProcessClick(
        list: ArrayList<FormData>,
        dynamicActionConfig: DynamicActionConfig?,
        currentFormId: String?,
        dfdid: String?
    ) {

        Log.e(TAG, "dynamicActionConfig----> ${dynamicActionConfig} ")
        Log.e(TAG, "currentFormId----> ${currentFormId} ")
        Log.e(TAG, "currentFormId----> ${list} ")

        var startAddress = ""
        var endAddress = ""


        if (cardManualStartLocation.visibility == View.GONE) {
            startAddress = edStartLocation.text.toString().trim()
        } else {
            if (edEnterManualStartLocation.visibility == View.VISIBLE)
                startAddress = edEnterManualStartLocation.text.toString().trim()
            else {
                startAddress = edEnterStartLocation.text.toString().trim()
            }
        }

        if (mActivityCreateTaskBinding.cvNearByHub.visibility == View.VISIBLE) {
            startAddress = mActivityCreateTaskBinding.etNearByHubLocation.text.toString().trim()
            Log.e("Address", "Address ----> $startAddress ")
        }

        if (cardManualEndLocation.visibility == View.GONE) {
            endAddress = edEndLocation.text.toString().trim()
        } else {
            if (edEnterManualEndLocation.visibility == View.VISIBLE)
                endAddress = edEnterManualEndLocation.text.toString().trim()
            else {
                endAddress = edEnterEndLocation.text.toString().trim()
            }
        }

//        val buddy = spnBuddy.selectedItem.toString().trim()
//        val client = spnClient.selectedItem.toString().trim()
        val startDateTime = edStartDateTime.text.toString().trim()
        val endDateTime = edEndDateTime.text.toString().trim()
        val pocName = edContactName.text.toString().trim()
        val pocMobile = edMobileNumber.text.toString().trim()
        val taskName = edTaskName.text.toString().trim()
        val referenceId = edReference.text.toString().trim()

        //    field1
        var field1 = ""
        var field2 = ""
        var field3 = ""
        var field4 = ""
        var field5 = ""
        var field6 = ""
        var field7 = ""
        var field8 = ""

        // field1
        if (field1text.visibility == View.VISIBLE) {
            field1 = etfield1text.text.toString().trim()
        } else if (field1spinner.visibility == View.VISIBLE) {
            field1 = spinnerfield1spinner.selectedItem.toString().trim()
        } else if (field1number.visibility == View.VISIBLE) {
            field1 = etfield1number.text.toString().trim()
        } else if (field1radio.visibility == View.VISIBLE) {

        }

        // field2
        if (field2text.visibility == View.VISIBLE) {
            field2 = etfield2text.text.toString().trim()
        } else if (field2spinner.visibility == View.VISIBLE) {
            field2 = spinnerfield2spinner.selectedItem.toString().trim()
        } else if (field2number.visibility == View.VISIBLE) {
            field2 = etfield2number.text.toString().trim()
        } else if (field2radio.visibility == View.VISIBLE) {

        }

        // field3
        if (field3text.visibility == View.VISIBLE) {
            field3 = etfield3text.text.toString().trim()
        } else if (field3spinner.visibility == View.VISIBLE) {
            field3 = spinnerfield3spinner.selectedItem.toString().trim()
        } else if (field3number.visibility == View.VISIBLE) {
            field3 = etfield3number.text.toString().trim()
        } else if (field3radio.visibility == View.VISIBLE) {

        }

        // field4
        if (field4text.visibility == View.VISIBLE) {
            field4 = etfield4text.text.toString().trim()
        } else if (field4spinner.visibility == View.VISIBLE) {
            field4 = spinnerfield4spinner.selectedItem.toString().trim()
        } else if (field4number.visibility == View.VISIBLE) {
            field4 = etfield4number.text.toString().trim()
        } else if (field4radio.visibility == View.VISIBLE) {

        }

        // field5
        if (field5text.visibility == View.VISIBLE) {
            field5 = etfield5text.text.toString().trim()
        } else if (field5spinner.visibility == View.VISIBLE) {
            field5 = spinnerfield5spinner.selectedItem.toString().trim()
        } else if (field5number.visibility == View.VISIBLE) {
            field5 = etfield5number.text.toString().trim()
        } else if (field5radio.visibility == View.VISIBLE) {

        }

        // field6
        if (field6text.visibility == View.VISIBLE) {
            field6 = etfield6text.text.toString().trim()
        } else if (field6spinner.visibility == View.VISIBLE) {
            field6 = spinnerfield6spinner.selectedItem.toString().trim()
        } else if (field6number.visibility == View.VISIBLE) {
            field6 = etfield6number.text.toString().trim()
        } else if (field6radio.visibility == View.VISIBLE) {

        }

        // field7
        if (field7text.visibility == View.VISIBLE) {
            field7 = etfield7text.text.toString().trim()
        } else if (field7spinner.visibility == View.VISIBLE) {
            field7 = spinnerfield7spinner.selectedItem.toString().trim()
        } else if (field7number.visibility == View.VISIBLE) {
            field7 = etfield7number.text.toString().trim()
        } else if (field7radio.visibility == View.VISIBLE) {

        }

        // field8
        if (field8text.visibility == View.VISIBLE) {
            field8 = etfield8text.text.toString().trim()
        } else if (field8spinner.visibility == View.VISIBLE) {
            field8 = spinnerfield8spinner.selectedItem.toString().trim()
        } else if (field7number.visibility == View.VISIBLE) {
            field8 = etfield8number.text.toString().trim()
        } else if (field8radio.visibility == View.VISIBLE) {

        }

        taskId = edTaskId.text.toString().trim()
        val description = edDescription.text.toString().trim()
//        val fleet = spnFleet.selectedItem.toString().trim()
        var GOOGLE_OR_MANUAL_SOURCE = CommonUtils.getTaskAllowedField(
            categoryId,
            "GOOGLE_OR_MANUAL_SOURCE",
            preferencesHelper
        )
        var GOOGLE_OR_MANUAL_DESTINATION = CommonUtils.getTaskAllowedField(
            categoryId,
            "GOOGLE_OR_MANUAL_DESTINATION",
            preferencesHelper
        )
        var SYSTEM_LOCATION =
            CommonUtils.getTaskAllowedField(categoryId, "SYSTEM_LOCATION", preferencesHelper)
//        val city = spnCity.selectedItem.toString().trim()

        if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && regionId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location Region" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && stateId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location State" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && cityId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location City" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_SOURCE != null && GOOGLE_OR_MANUAL_SOURCE.visible && GOOGLE_OR_MANUAL_SOURCE.required && hubId == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_SOURCE!!.errMsg.isNullOrBlank()) "Please Select Start Location Hub" else GOOGLE_OR_MANUAL_SOURCE!!.errMsg
            )

        } else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && regionId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location Region" else SYSTEM_LOCATION!!.errMsg
            )

        } else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && stateId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location State" else SYSTEM_LOCATION!!.errMsg
            )

        } else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && cityId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location City" else SYSTEM_LOCATION!!.errMsg
            )

        } else if (SYSTEM_LOCATION != null && SYSTEM_LOCATION.visible && SYSTEM_LOCATION.required && hubId == null) {
            TrackiToast.Message.showShort(
                this,
                if (SYSTEM_LOCATION!!.errMsg.isNullOrBlank()) "Please Select Start Location Hub" else SYSTEM_LOCATION!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && regionIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location Region" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && stateIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location State" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && cityIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location City" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        } else if (GOOGLE_OR_MANUAL_DESTINATION != null && GOOGLE_OR_MANUAL_DESTINATION.visible && GOOGLE_OR_MANUAL_DESTINATION.required && hubIdEnd == null) {
            TrackiToast.Message.showShort(
                this,
                if (GOOGLE_OR_MANUAL_DESTINATION!!.errMsg.isNullOrBlank()) "Please Select End Location Hub" else GOOGLE_OR_MANUAL_DESTINATION!!.errMsg
            )

        }
        /* if (cardManualStartLocation.visibility == View.VISIBLE && regionId == null) {
             TrackiToast.Message.showShort(this, "Please Select Start Location Region")

         } else if (cardManualStartLocation.visibility == View.VISIBLE && stateId == null) {
             TrackiToast.Message.showShort(this, "Please Select Start Location State")
         } else if (cardManualStartLocation.visibility == View.VISIBLE && cityId == null) {
             TrackiToast.Message.showShort(this, "Please Select Start Location City")

         } else if (cardManualStartLocation.visibility == View.VISIBLE && hubId == null) {
             TrackiToast.Message.showShort(this, "Please Select Start Location Hub")

         }*//* else if (cardManualStartLocation.visibility == View.VISIBLE && startAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"Start location field cannot be empty")
        }*/ /*else if (cardManualEndLocation.visibility == View.VISIBLE && regionIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location  Region")
        } else if (cardManualEndLocation.visibility == View.VISIBLE && stateIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location State")
        } else if (cardManualEndLocation.visibility == View.VISIBLE && cityIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location City")

        } else if (cardManualEndLocation.visibility == View.VISIBLE && hubIdEnd == null) {
            TrackiToast.Message.showShort(this, "Please Select End Location Hub")
        }*/ /*else if (cardManualEndLocation.visibility == View.VISIBLE && endAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"End Location field cannot be empty")
        } else if (tilStartLocation.visibility == View.VISIBLE && startAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"Start location field cannot be empty")
        } */ else if (tilTaskId.visibility == View.VISIBLE && taskId.isEmpty()) {
            TrackiToast.Message.showShort(this, "Task Id field cannot be empty")
        }/* else if (tilEndLocation.visibility == View.VISIBLE && endAddress.isEmpty()) {
            TrackiToast.Message.showShort(this,"End Location field cannot be empty")
        } */ else if (llbuddy.visibility == View.VISIBLE && spnBuddy.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please Select Your Buddy")
        } else if (llbuddy.visibility == View.VISIBLE && spnBuddy.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Your Buddy")
        } else if (llClient.visibility == View.VISIBLE && spnClient.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please Select Client")
        } else if (llClient.visibility == View.VISIBLE && spnClient.selectedItem.toString()
                .trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Client")
        } else if (cardViewClientList.visibility == View.VISIBLE && !::selectedUserId.isInitialized) {
            TrackiToast.Message.showShort(this, "Please Select Requested By")
        } else if (cardViewClientList.visibility == View.VISIBLE && selectedUserId.toString()
                .trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please Select Requested By")
        } else if (tilStartDateTime.visibility == View.VISIBLE && startDateTime.isEmpty()) {
            TrackiToast.Message.showShort(this, "Start Date & Time field cannot be empty")
        }/* else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && (startYear == endYear && startMonth == endMonth && startDay == endDay) || (startYear == endYear && startMonth == endMonth && endHour < startHour)) {
            Toast.makeText(this, getString(R.string.end_time_cannot_be_less_than_start_time), Toast.LENGTH_SHORT).show()

        } else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && (startYear == endYear && startMonth == endMonth && startDay == endDay) || (startYear == endYear && startMonth == endMonth && endHour == startHour && endMinute <= startMinute)) {
            Toast.makeText(this, getString(R.string.end_time_cannot_be_less_than_start_time), Toast.LENGTH_SHORT).show()

        } else if ((tilEndDateTime.visibility == View.VISIBLE && tilStartDateTime.visibility == View.VISIBLE) && (endYear <= startYear && endMonth <= startMonth && endDay < startDay)) {
            Toast.makeText(this, getString(R.string.no_past_date), Toast.LENGTH_SHORT).show()

        }*/ else if (tilEndDateTime.visibility == View.VISIBLE && endDateTime.isEmpty()) {
            TrackiToast.Message.showShort(this, "End Date & Time field cannot be empty")
        } else if ((tilStartDateTime.visibility == View.VISIBLE && tilEndDateTime.visibility == View.VISIBLE) && startTime > endTime) {
            TrackiToast.Message.showShort(
                this,
                getString(R.string.end_time_cannot_be_less_than_start_time)
            )
        } else if (llPOC.visibility == View.VISIBLE && pocName.isEmpty()) {
            TrackiToast.Message.showShort(this, "Contact Person Name field cannot be empty")
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                "Contact Person mobile number field cannot be empty"
            )
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.length < 10) {
            TrackiToast.Message.showShort(this, "Mobile Number cannot be less than 10 digit")
        } else if (llPOC.visibility == View.VISIBLE && pocMobile.length < 10) {
            TrackiToast.Message.showShort(this, "Mobile Number cannot be less than 10 digit")
        } else if (tilTaskName.visibility == View.VISIBLE && taskName.isEmpty()) {
            TrackiToast.Message.showShort(this, "Task Name cannot be empty")
        } else if (tilReference.visibility == View.VISIBLE && referenceId.isEmpty()) {
            TrackiToast.Message.showShort(this, "Reference Id cannot be empty")
        } else if (lldescription.visibility == View.VISIBLE && description.isEmpty()) {
            TrackiToast.Message.showShort(this, "Description cannot be empty")
        } else if (llfleet.visibility == View.VISIBLE && spnFleet.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please select your fleet")
        } else if (llfleet.visibility == View.VISIBLE && spnFleet.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please select your fleet")
        } else if (llcity.visibility == View.VISIBLE && spnCity.selectedItem == null) {
            TrackiToast.Message.showShort(this, "Please select your city")
        } else if (llcity.visibility == View.VISIBLE && spnCity.selectedItem.toString().trim()
                .isEmpty()
        ) {
            TrackiToast.Message.showShort(this, "Please select your city")
        } else {
            val startLoc = GeoCoordinates()
            if (this::startLatLng.isInitialized) {
                startLoc.latitude = startLatLng.latitude
                startLoc.longitude = startLatLng.longitude
            }

            var source: com.rf.taskmodule.data.model.response.config.Place? =
                com.rf.taskmodule.data.model.response.config.Place()
            if (createTaskRequest?.destination != null)
                source = createTaskRequest?.source

            val isCategoryContainsStartLocation = CommonUtils.isCategoryContainsFiled(
                FIELD.START_LOCATION.name,
                categoryId,
                preferencesHelper
            )
            val isCategoryContainsEndLocation = CommonUtils.isCategoryContainsFiled(
                FIELD.END_LOCATION.name,
                categoryId,
                preferencesHelper
            )
            if (tilStartLocation.visibility == View.VISIBLE || cardManualStartLocation.visibility == View.VISIBLE || isCategoryContainsStartLocation) {
                if (llToggleEnableStartLoc.visibility == View.VISIBLE || isCategoryContainsStartLocation) {
                    source!!.address = startAddress
                    source!!.location = startLoc
                }
            }
            if (cardManualStartLocation.visibility == View.VISIBLE) {
                source!!.regionId = regionId
                source.stateId = stateId
                source.cityId = cityId
                source.hubId = hubId
                source.manualLocation = !enableStartLocation.isOn
                if (source.manualLocation || llToggleEnableStartLoc.visibility == View.GONE)
                    source.location = null

            }

            var destination: com.rf.taskmodule.data.model.response.config.Place? =
                com.rf.taskmodule.data.model.response.config.Place()

            if (createTaskRequest?.destination != null)
                destination = createTaskRequest?.destination

            if (endLatLng != null && endLatLng?.latitude != 0.0 && endLatLng?.longitude != 0.0) {
                val endLoc = GeoCoordinates()
                endLoc.latitude = endLatLng?.latitude!!
                endLoc.longitude = endLatLng?.longitude!!
                destination!!.address = endAddress
                destination.location = endLoc
                if (cardManualEndLocation.visibility == View.VISIBLE) {
                    destination.regionId = regionIdEnd
                    destination.hubId = hubIdEnd
                    destination.stateId = stateIdEnd
                    destination.cityId = cityIdEnd
                    destination.manualLocation = !enableEndLocation.isOn
                    if (destination.manualLocation)
                        destination.location = null

                }

            } else {
                val endLoc = GeoCoordinates()
                endLoc.latitude = 0.0
                endLoc.longitude = 0.0
                destination!!.address = endAddress
                destination!!.location = endLoc
                if (cardManualEndLocation.visibility == View.VISIBLE) {
                    destination!!.regionId = regionIdEnd
                    destination!!.hubId = hubIdEnd
                    destination!!.stateId = stateIdEnd
                    destination!!.cityId = cityIdEnd
                    destination!!.manualLocation = !enableEndLocation.isOn
                    if (destination!!.manualLocation)
                        destination.location = null

                }

            }

            buddyId = if (buddyId != null) buddyId!! else ""
            fleetId = if (fleetId != null) fleetId!! else ""
            var name = preferencesHelper.userDetail?.name
            if (name == null) {
                name = ""
            }
            if (mActivityCreateTaskBinding.tilEndDateTime.visibility == View.GONE) {
                endTime = 0
            }
            if (mActivityCreateTaskBinding.tilEndLocation.visibility == View.GONE && !isCategoryContainsEndLocation && cardManualEndLocation.visibility == View.GONE) {
                destination = null
            }
            if (tilStartLocation.visibility == View.GONE && !isCategoryContainsStartLocation && cardManualStartLocation.visibility == View.GONE) {
                source = null
            }

            createTaskRequest = CreateTaskRequest(
                false,
                buddyId!!,
                fleetId!!,
                name,
                description,
                destination,
                source,
                startTime,
                taskName,
                taskId,
                endTime,
                Contact(pocName, pocMobile),
                spinnerVal,
                field1,
                field2,
                field3,
                field4,
                field5,
                field6,
                field7,
                field8,
                serviceIds
            )

            if (listRefTask.isNotEmpty()) {
                var multiReferedTask: ArrayList<String> = ArrayList()
                listRefTask.forEach { item ->
                    multiReferedTask.add(item.taskId.toString())
                }
                createTaskRequest!!.multiReferedTask = multiReferedTask
            }

            if (intent.hasExtra("directMapping")) {
                createTaskRequest!!.directMapping =
                    intent.getBooleanExtra("directMapping", false)
            }
            if (intent.hasExtra("invIds")) {
                createTaskRequest!!.invIds = intent.getStringArrayListExtra("invIds")
            }
            if (intent.hasExtra("from")) {
                if (intent.getStringExtra("from").equals("taskListing")) {
                    if (parentReffId != null) {
                        createTaskRequest!!.referenceId = parentReffId

                    } else {
                        createTaskRequest!!.referenceId = referenceId
                    }
                    if (parentTaskId != null) {
                        createTaskRequest!!.parentTaskId = parentTaskId
                    }

                } else if (intent.getStringExtra("from").equals("dashBoard")) {
                    createTaskRequest!!.referenceId = referenceId
                }
            } else {
                createTaskRequest!!.referenceId = referenceId
            }
            if (::selectedUserId.isInitialized && selectedUserId.isNotEmpty())
                createTaskRequest!!.requestBy = selectedUserId
            if (categoryId != null)
                createTaskRequest!!.categoryId = categoryId
            hideKeyboard()
            //showLoading()
            val json = JSONConverter<CreateTaskRequest>()
                .objectToJson(createTaskRequest)
            CommonUtils.showLogMessage("e", "jsondata", json)

            var bundle = Bundle();
            bundle.putSerializable("CREATE_TASK_REQUEST", createTaskRequest);
            Log.e(TAG, "config type----> ${dynamicActionConfig?.action} ")
            if (mainMap == null) {
                mainMap = HashMap()
            }
            //add form data after validation into the map
            if (list.isNotEmpty()) {
                mainMap?.set(currentFormId!!, list)
                val jsonConverter =
                    JSONConverter<HashMap<String, ArrayList<FormData>>>()
                val data = jsonConverter.objectToJson(mainMap!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())


//                if (mainMap == null) {
//                    mainMap = HashMap()
//                }
//                //add form data after validation into the map
//                mainMap?.set(currentFormId!!, list)
            }
            var jsonConverter =
                JSONConverter<ArrayList<FormData>>();
            Log.e(TAG, jsonConverter.objectToJson(list))
            // replace a new fragment to the container
            if (dynamicActionConfig?.action == Type.FORM) {
                showLoading()
                Log.d("Here", "one")
                //make the back button visible if user click on next form
                hideAllAllowedFields()
                dynamicFragment = NewDynamicFormFragment.getInstance(
                    dynamicActionConfig.target!!,
                    taskId,
                    isEditable,
                    ArrayList()
                )
                replaceFragment(
                    dynamicFragment!!, dynamicActionConfig.target!!
                )
                hideLoading()
            } else if (dynamicActionConfig?.action == Type.API) {
                mainData = ArrayList()
                Log.d("Here", "two")
                //add all the values of map to the hashMap and go to all
                // elements to get the required data
                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }
                //if form data hashMap is not empty then we get all the image file
                // and upload the data to the server and get the respective urls in string format.
                if (mainData?.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {
                            //remove last element only if type is camera else
                            // SIGNATURE OR FILE always contains single image
                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //  Log.e("path",v[i].path)
                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA && v.size > 0 && v[v.size - 1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //  Log.e("path",v[i].path)
                                v.removeAt(v.size - 1)
                            }
                            if (v.isNotEmpty()) {
                                var listIterator = v.listIterator()
                                while (listIterator.hasNext()) {
                                    if (!listIterator.next().exists()) {
                                        listIterator.remove()
                                    }
                                }
                            }
                            if (v.isNotEmpty()) {
                                val key = mainData!![i].name!!
                                hashMapFileRequest[key] = v
                            }

                        }
                        Log.e(
                            "NewCreateTaskActivity", mainData!![i].name + "<------->"
                                    + mainData!![i].enteredValue
                        )
                    }
                    val jsonConverter = JSONConverter<HashMap<String, ArrayList<File>>>();
                    Log.e(
                        DynamicFormActivity.TAG,
                        jsonConverter.objectToJson(hashMapFileRequest)
                    )
                    Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {
                        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
                            if (NetworkUtils.isConnectedFast(this@NewCreateTaskActivity)) {
                                mActivityCreateTaskBinding.btnCLickSubmit.visibility = View.GONE
                                mActivityCreateTaskBinding.viewProgress.visibility =
                                    View.VISIBLE
                                CommonUtils.makeScreenDisable(this)
                                fileUploadCounter = 0
                                nestedScrollView.postDelayed(
                                    { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                                    200
                                )
                                count = 0
                                val thread = HandlerThread("workkker")
                                thread.start()
                                //start a handler
                                mDynamicHandler =
                                    DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                                handlerThread?.setRequestParams(
                                    mDynamicHandler!!,
                                    hashMapFileRequest,
                                    httpManager,
                                    null
                                )
                                //start thread
                                handlerThread?.start()
                            } else {
                                CommonUtils.showSnakbarForNetworkSettings(
                                    this@NewCreateTaskActivity,
                                    nestedScrollView,
                                    AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                                )
                            }

                        } else {
                            // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                        }
                    } else {
                        finalApiHit()
                    }
                }
            } else if (dynamicActionConfig?.action == Type.DISPOSE) {
                Log.d("Here", "three")
                mainData = ArrayList()
                //add all the values of map to the hashMap and go to all
                // elements to get the required data
                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }
                //if form data hashMap is not empty then we get all the image file
                // and upload the data to the server and get the respective urls in string format.
                if (mainData != null && mainData!!.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {
                            //remove last element only if type is camera else
                            // SIGNATURE OR FILE always contains single image
                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                //Log.e("path", v[i].path)
                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA && v.size > 0 && v[v.size - 1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                               // Log.e("path", v[i].path)
                                v.removeAt(v.size - 1)
                            }
                            if (v.isNotEmpty()) {
                                var listIterator = v.listIterator()
                                while (listIterator.hasNext()) {
                                    if (!listIterator.next().exists()) {
                                        listIterator.remove();
                                    }
                                }
                            }



                            if (v.isNotEmpty()) {
                                val key = mainData!![i].name!!
                                hashMapFileRequest[key] = v
                                Log.e(DynamicFormActivity.TAG, "file to upload $key =>$v")
                            }
                        }
                        Log.e(
                            "NewCreateTaskActivity", mainData!![i].name + "<------->"
                                    + mainData!![i].enteredValue
                        )
                    }
                    val jsonConverter = JSONConverter<HashMap<String, ArrayList<File>>>();
                    Log.e(
                        DynamicFormActivity.TAG,
                        jsonConverter.objectToJson(hashMapFileRequest)
                    )
                    Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {


//                        val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
//                        mCreateTaskViewModel.uploadFileList(hashMapFileRequest, httpManager, api, false)
                        //uplo
                        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {

                            if (NetworkUtils.isConnectedFast(this@NewCreateTaskActivity)) {
                                count = 0
                                Log.e(TAG, "worker thread open")
                                // showLoading()
                                mActivityCreateTaskBinding.btnCLickSubmit.visibility =
                                    View.GONE
                                mActivityCreateTaskBinding.viewProgress.visibility =
                                    View.VISIBLE
                                CommonUtils.makeScreenDisable(this)
                                nestedScrollView.postDelayed(
                                    { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                                    200
                                )
                                fileUploadCounter = 0
                                val thread = HandlerThread("workkker")
                                thread.start()
                                //start a handler
                                mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                                handlerThread?.setRequestParams(
                                    mDynamicHandler!!,
                                    hashMapFileRequest,
                                    httpManager,
                                    null
                                )
                                //start thread
                                handlerThread?.start()
                            } else {
                                CommonUtils.showSnakbarForNetworkSettings(
                                    this@NewCreateTaskActivity,
                                    nestedScrollView,
                                    AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                                )
                            }

                        } else {
                            // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                        }
                    } else {
                        showLoading()
                        perFormCreateTask()
                    }
                } else {
                    showLoading()
                    perFormCreateTask()
                }

            }
        }
    }

    private fun hideAllAllowedFields() {
        ll_allowed_fields.visibility = View.GONE
    }

    private fun perFormCreateTask() {
        if (dynamicFormsNew != null) {
            val dynamicFormMainData = CommonUtils.createFormData(
                mainData, selItem, taskId!!, dynamicFormsNew!!.formId,
                dynamicFormsNew!!.version
            )
            Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

            val taskData = dynamicFormMainData!!.taskData
            createTaskRequest!!.taskData = taskData
            createTaskRequest!!.dfId = getDynamicFormId(categoryId)
        }
        if (categoryId != null)
            createTaskRequest!!.categoryId = categoryId

        if (dateFinal != "" && timeFinal != "") {
            if (invIds!!.size > 0) {
                createTaskRequest?.timeSlot = TimeSlot(
                    date = dateFinal,
                    slot = timeFinal,
                    entityId = invIds!![0],
                    availType = availType
                )
            } else {
                createTaskRequest?.timeSlot =
                    TimeSlot(date = dateFinal, slot = timeFinal, availType = availType)
            }
        }

        if (hubIdFinalHubsOnly != "") {
            val place = com.rf.taskmodule.data.model.response.config.Place()
            place.regionId = hub?.regionId
            place.address = hub?.hubLocation?.address
            place.cityId = hub?.cityId
            place.hubId = hub?.hubId
            place.stateId = hub?.stateId

            val location = GeoCoordinates()
            location.latitude = hub?.hubLocation?.location?.latitude!!
            location.longitude = hub?.hubLocation?.location?.longitude!!

            place.location = location

            if (target_system_hub == Target.SOURCE) {
                if (createTaskRequest?.source?.address.isNullOrEmpty()) {
                    createTaskRequest?.source = place
                }
            } else {
                if (createTaskRequest?.destination?.address.isNullOrEmpty()) {
                    createTaskRequest?.destination = place
                }
            }
        }

        if (hubIdFinal != "") {
            val place = com.rf.taskmodule.data.model.response.config.Place()
            place.regionId = hub?.regionId
            place.address = hub?.hubLocation?.address
            place.cityId = hub?.cityId
            place.hubId = hub?.hubId
            place.stateId = hub?.stateId

            val location = GeoCoordinates()
            location.latitude = hub?.hubLocation?.location?.latitude!!
            location.longitude = hub?.hubLocation?.location?.longitude!!

            place.location = location

            if (target_system_hub == Target.SOURCE) {
                if (createTaskRequest?.source?.address.isNullOrEmpty()) {
                    createTaskRequest?.source = place
                }
            } else {
                if (createTaskRequest?.destination?.address.isNullOrEmpty()) {
                    createTaskRequest?.destination = place
                }
            }
        }

        if (hubIdFinalDestination != "") {
            val place = com.rf.taskmodule.data.model.response.config.Place()
            place.regionId = hubDestination?.regionId
            place.address = hubDestination?.hubLocation?.address
            place.cityId = hubDestination?.cityId
            place.hubId = hubDestination?.hubId
            place.stateId = hubDestination?.stateId

            val location = GeoCoordinates()
            location.latitude = hubDestination?.hubLocation?.location?.latitude!!
            location.longitude = hubDestination?.hubLocation?.location?.longitude!!
            place.location = location

            if (createTaskRequest?.destination?.address.isNullOrEmpty()) {
                createTaskRequest?.destination = place
            }
        }

        val jsonConverter =
            JSONConverter<CreateTaskRequest>()
        val json = jsonConverter.objectToJson(createTaskRequest)
        CommonUtils.showLogMessage("e", "task data", json)
        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
            val apiType = ApiType.CREATE_TASK
            val api = TrackiSdkApplication.getApiMap()[apiType]
            mCreateTaskViewModel.createTaskApi(httpManager, createTaskRequest!!, api!!)
        } else {
            hideLoading()
            TrackiToast.Message.showShort(
                this@NewCreateTaskActivity,
                getString(R.string.no_internet)
            )
        }

    }

    private fun finalApiHit() {

        var dynamicFormMainData = CommonUtils.createFormData(
            mainData, selItem, taskId!!, dynamicFormsNew!!.formId,
            dynamicFormsNew!!.version
        )
        Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

        if (NetworkUtils.isNetworkConnected(this@NewCreateTaskActivity)) {
            showLoading()
            val api = TrackiSdkApplication.getApiMap()[ApiType.UPDATE_TASK_DATA]
            mCreateTaskViewModel.uploadTaskData(dynamicFormMainData, httpManager, api)
        } else {
            hideLoading()
            TrackiToast.Message.showShort(
                this@NewCreateTaskActivity,
                getString(R.string.no_internet)
            )
        }
    }

    override fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@NewCreateTaskActivity)) {
            val fileUrlsResponse =
                Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> =
                            java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        formData.enteredValue = commaSeperatedString
                    }

                }
                finalApiHit()
            }
        } else {
            finalApiHit()
        }

    }

    override fun upLoadFileDisposeApiResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this@NewCreateTaskActivity)) {
            val fileUrlsResponse =
                Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> =
                            java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        formData.enteredValue = commaSeperatedString
                    }

                }
                perFormCreateTask()
            }
        } else {
            perFormCreateTask()
        }
    }

    override fun handleRegionListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            tvLabelSelectStartArea.visibility = View.GONE
                            spnCategoryStartArea.visibility = View.GONE
                            cvspnCategoryStartArea.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartArea)
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartArea)
                            regionId = list[0].id
                            CommonUtils.showLogMessage("e", "regionId", regionId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartState.adapter = null
                            stateId = null
                            mCreateTaskViewModel.getStateList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )
                        } else {
                            spnCategoryStartArea.visibility = View.VISIBLE
                            cvspnCategoryStartArea.visibility = View.VISIBLE
                            tvLabelSelectStartArea.visibility = View.VISIBLE
                            spnCategoryStartArea!!.adapter = arrayAdapter
                            spnCategoryStartArea!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        regionId = list[position].id
                                        CommonUtils.showLogMessage("e", "regionId", regionId);
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartState.adapter = null
                                        stateId = null
                                        mCreateTaskViewModel.getStateList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }


                    } else {
                        if (list.size == 1) {
                            spnCategoryEndArea.visibility = View.GONE
                            cvspnCategoryEndArea.visibility = View.GONE
                            tvLabelSelectEndArea.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndArea)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndArea)
                            regionIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "regionIdEnd", regionIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            spnCategoryEndState.adapter = null
                            stateIdEnd = null
                            mCreateTaskViewModel.getStateList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )
                        } else {
                            spnCategoryEndArea.visibility = View.VISIBLE
                            cvspnCategoryEndArea.visibility = View.VISIBLE
                            tvLabelSelectEndArea.visibility = View.VISIBLE
                            spnCategoryEndArea!!.adapter = arrayAdapter
                            spnCategoryEndArea!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        regionIdEnd = list[position].id
                                        CommonUtils.showLogMessage(
                                            "e",
                                            "regionIdEnd",
                                            regionIdEnd
                                        );
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        spnCategoryEndState.adapter = null
                                        stateIdEnd = null
                                        mCreateTaskViewModel.getStateList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    cardManualEndLocation.visibility = View.GONE
                }
            }
        }

    }

    override fun handleStateListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartState.visibility = View.GONE
                            cvspnCategoryStartState.visibility = View.GONE
                            tvLabelSelectStartState.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartState)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartState)
                            stateId = list[0].id
                            CommonUtils.showLogMessage("e", "stateId", stateId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.stateId = stateId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartCity.adapter = null
                            cityId = null
                            mCreateTaskViewModel.getCityList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )

                        } else {
                            spnCategoryStartState.visibility = View.VISIBLE
                            cvspnCategoryStartState.visibility = View.VISIBLE
                            tvLabelSelectStartState.visibility = View.VISIBLE
                            spnCategoryStartState!!.adapter = arrayAdapter
                            spnCategoryStartState!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        stateId = list[position].id
                                        CommonUtils.showLogMessage("e", "stateId", stateId);
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.stateId = stateId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartCity.adapter = null
                                        cityId = null
                                        mCreateTaskViewModel.getCityList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndState.visibility = View.GONE
                            cvspnCategoryEndState.visibility = View.GONE
                            tvLabelSelectEndState.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndState)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndState)
                            stateIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "stateIdEnd", stateIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            getUserManualLocationData.stateId = stateIdEnd
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryEndCity.adapter = null
                            cityIdEnd = null
                            mCreateTaskViewModel.getCityList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )
                        } else {
                            spnCategoryEndState.visibility = View.VISIBLE
                            cvspnCategoryEndState.visibility = View.VISIBLE
                            tvLabelSelectEndState.visibility = View.VISIBLE
                            spnCategoryEndState!!.adapter = arrayAdapter
                            spnCategoryEndState!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        stateIdEnd = list[position].id
                                        CommonUtils.showLogMessage(
                                            "e",
                                            "stateIdEnd",
                                            stateIdEnd
                                        );
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        getUserManualLocationData.stateId = stateIdEnd
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryEndCity.adapter = null
                                        cityIdEnd = null
                                        mCreateTaskViewModel.getCityList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    // cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }
            }
        }

    }

    override fun handleCityListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartCity.visibility = View.GONE
                            cvspnCategoryStartCity.visibility = View.GONE
                            tvLabelSelectStartCity.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryStartCity)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectStartCity)
                            cityId = list[0].id
                            CommonUtils.showLogMessage("e", "cityId", cityId);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionId
                            getUserManualLocationData.stateId = stateId
                            getUserManualLocationData.cityId = cityId
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryStartHub.adapter = null
                            hubId = null
                            mCreateTaskViewModel.getHubList(
                                httpManager,
                                getUserManualLocationData,
                                true
                            )
                        } else {
                            spnCategoryStartCity.visibility = View.VISIBLE
                            cvspnCategoryStartCity.visibility = View.VISIBLE
                            tvLabelSelectStartCity.visibility = View.VISIBLE
                            spnCategoryStartCity!!.adapter = arrayAdapter
                            spnCategoryStartCity!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        cityId = list[position].id
                                        CommonUtils.showLogMessage("e", "cityId", cityId);
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionId
                                        getUserManualLocationData.stateId = stateId
                                        getUserManualLocationData.cityId = cityId
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryStartHub.adapter = null
                                        hubId = null
                                        mCreateTaskViewModel.getHubList(
                                            httpManager,
                                            getUserManualLocationData,
                                            true
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndCity.visibility = View.GONE
                            cvspnCategoryEndCity.visibility = View.GONE
                            tvLabelSelectEndCity.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndCity)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndCity)
                            cityIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "cityIdEnd", cityIdEnd);
                            var getUserManualLocationData = GetManualLocationRequest()
                            getUserManualLocationData.regionId = regionIdEnd
                            getUserManualLocationData.stateId = stateIdEnd
                            getUserManualLocationData.cityId = cityIdEnd
                            getUserManualLocationData.userGeoReq = allowGeography
                            spnCategoryEndHub.adapter = null
                            hubIdEnd = null
                            mCreateTaskViewModel.getHubList(
                                httpManager,
                                getUserManualLocationData,
                                false
                            )

                        } else {
                            spnCategoryEndCity.visibility = View.VISIBLE
                            cvspnCategoryEndCity.visibility = View.VISIBLE
                            tvLabelSelectEndCity.visibility = View.VISIBLE
                            spnCategoryEndCity!!.adapter = arrayAdapter
                            spnCategoryEndCity!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        cityIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "cityIdEnd", cityIdEnd);
                                        var getUserManualLocationData =
                                            GetManualLocationRequest()
                                        getUserManualLocationData.regionId = regionIdEnd
                                        getUserManualLocationData.stateId = stateIdEnd
                                        getUserManualLocationData.cityId = cityIdEnd
                                        getUserManualLocationData.userGeoReq = allowGeography
                                        spnCategoryEndHub.adapter = null
                                        hubIdEnd = null
                                        mCreateTaskViewModel.getHubList(
                                            httpManager,
                                            getUserManualLocationData,
                                            false
                                        )

                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }

            }
        } else {
            if (isStart) {
//                CommonUtils.hideViewWithAnimation(spnCategoryStartCity)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectStartHubs)
//                CommonUtils.hideViewWithAnimation(spnCategoryStartHub)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectStartCity)
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1
                    && spnCategoryStartState.adapter != null && spnCategoryStartState.adapter.count > 1
                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    //  cardManualStartLocation.visibility=View.GONE
                }

                spnCategoryStartCity.visibility = View.GONE
                cvspnCategoryStartCity.visibility = View.GONE
                tvLabelSelectStartHubs.visibility = View.GONE
                spnCategoryStartHub.visibility = View.GONE
                cvspnCategoryStartHub.visibility = View.GONE
                tvLabelSelectStartCity.visibility = View.GONE
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                    && spnCategoryEndState.adapter != null && spnCategoryEndState.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    //  cardManualEndLocation.visibility = View.GONE
                }
                spnCategoryEndCity.visibility = View.GONE
                cvspnCategoryEndCity.visibility = View.GONE
                tvLabelSelectEndHubs.visibility = View.GONE
                spnCategoryEndHub.visibility = View.GONE
                cvspnCategoryEndHub.visibility = View.GONE
                tvLabelSelectEndCity.visibility = View.GONE
//                CommonUtils.hideViewWithAnimation(spnCategoryEndCity)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectEndHubs)
//                CommonUtils.hideViewWithAnimation(spnCategoryEndHub)
//                CommonUtils.hideViewWithAnimation(tvLabelSelectEndCity)
            }
        }//spnCategoryStartState


    }

    override fun handleHubListResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        isStart: Boolean
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            executive?.data?.let {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = it.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()

                    list.add(buddy)
                }
                if (list.size > 0) {
                    var buddyList: MutableList<String?> = java.util.ArrayList()
                    for (data in list) {
                        buddyList.add(data.name!!)
                    }
                    var arrayAdapter = object : ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        buddyList
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            return v
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val v = super.getDropDownView(position, convertView, parent)
                            val externalFont = Typeface.createFromAsset(
                                parent.context.assets,
                                "fonts/campton_book.ttf"
                            )
                            (v as TextView).typeface = externalFont
                            //v.setBackgroundColor(Color.GREEN);
                            return v
                        }
                    }
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    if (isStart) {
                        if (list.size == 1) {
                            spnCategoryStartHub.visibility = View.GONE
                            cvspnCategoryStartHub.visibility = View.GONE
                            tvLabelSelectStartHubs.visibility = View.GONE
                            hubId = list[0].id
                            //cardManualStartLocation.visibility = View.GONE
                            CommonUtils.showLogMessage("e", "hubId", hubId);
                        } else {
                            spnCategoryStartHub.visibility = View.VISIBLE
                            cvspnCategoryStartHub.visibility = View.VISIBLE
                            tvLabelSelectStartHubs.visibility = View.VISIBLE
                            spnCategoryStartHub!!.adapter = arrayAdapter
                            spnCategoryStartHub!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        hubId = list[position].id
                                        CommonUtils.showLogMessage("e", "hubId", hubId);


                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    } else {
                        if (list.size == 1) {
                            spnCategoryEndHub.visibility = View.GONE
                            cvspnCategoryEndHub.visibility = View.GONE
                            tvLabelSelectEndHubs.visibility = View.GONE
//                            CommonUtils.hideViewWithAnimation(spnCategoryEndHub)
//                            CommonUtils.hideViewWithAnimation(tvLabelSelectEndHubs)
//                            CommonUtils.hideViewWithAnimation(cardManualEndLocation)
                            hubIdEnd = list[0].id
                            CommonUtils.showLogMessage("e", "hubIdEnd", hubIdEnd);
                        } else {
                            spnCategoryEndHub.visibility = View.VISIBLE
                            cvspnCategoryEndHub.visibility = View.VISIBLE
                            tvLabelSelectEndHubs.visibility = View.VISIBLE
                            cardManualEndLocation.visibility = View.VISIBLE
                            spnCategoryEndHub!!.adapter = arrayAdapter
                            spnCategoryEndHub!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val selectedItem =
                                            parent.getItemAtPosition(position).toString()
                                        hubIdEnd = list[position].id
                                        CommonUtils.showLogMessage("e", "hubIdEnd", hubIdEnd);


                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }
                        }

                    }


                } else {
                }
            }
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1
                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    // cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }

            }

        } else {
            if (isStart) {
                if (spnCategoryStartArea.adapter != null && spnCategoryStartArea.adapter.count > 1

                ) {
                    cardManualStartLocation.visibility = View.VISIBLE
                } else {
                    //   cardManualStartLocation.visibility = View.GONE
                }
            } else {
                if (spnCategoryEndArea.adapter != null && spnCategoryEndArea.adapter.count > 1
                ) {
                    cardManualEndLocation.visibility = View.VISIBLE
                } else {
                    // cardManualEndLocation.visibility = View.GONE
                }

            }

        }
    }

    override fun getSlotDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        val slotDataResponse1 =
            Gson().fromJson(result.toString(), SlotDataResponse::class.java)

        rvDate.adapter = slotAdapter
        slotAdapter.clearAll()
        if (slotDataResponse1.successful) {
            llSlots.visibility = View.VISIBLE
            ivNoData.visibility = View.GONE

            slotDataResponse = slotDataResponse1
            if (slotDataResponse.data != null) {
                slotAdapter.setMap(slotDataResponse.data!!)
                slotAdapter.onItemClick =
                    { dateString: String, position: Int, listValues1: ArrayList<SlotData>, list: ArrayList<String> ->
                        selectDayCall(position)
                        listSlots = listValues1
                        listKeys = list
                        key = dateString
                    }
                setSlots()
            }
        } else {
            llSlots.visibility = View.GONE
            ivNoData.visibility = View.VISIBLE
            slotAdapter.clearAll()
            TrackiToast.Message.showShort(this, "No Slots Found")
        }
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
                    val slots = timeSlots.slots as ArrayList<Slot>

                    val slotChildAdapter = SlotChildAdapter(slots, this)
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

    override fun handleMyPlaceResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<*> =
                JSONConverter<Any?>()
            val response: LocationListResponse =
                Gson().fromJson(result.toString(), LocationListResponse::class.java)
            if (response.successful!!) {
                Log.e("appLog1", "" + response.hubs)
                if (response.hubs != null && !response.hubs!!.isEmpty()) {
                    preferencesHelper.saveUserHubList(response.hubs)
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


    var count = 0
    var fileUploadCounter = 0

    inner class DynamicHandler(looper: Looper, var actionConfig: DynamicActionConfig) :
        Handler(looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                2 -> {
                    var obj = message.obj as HandlerObject
                    fileUploadCounter += obj.chunkSize
                    var progressUploadText = "${fileUploadCounter}/${obj.totalSize}"
                    var percentage = ((fileUploadCounter * 100 / obj.totalSize))
                    Log.e(TAG, "progressUploadText=> $progressUploadText")
                    Log.e(TAG, "percentage=> $percentage")
                    runOnUiThread {
                        progressBar?.progress = percentage
                        percentageText!!.text = "$percentage %"
                        currentStatusText!!.text = progressUploadText
                    }
                }
                /*For Success */0 -> {

                if (CommonUtils.stringListHashMap.isNotEmpty()) {

                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    runOnUiThread {
                        rlProgress?.visibility = View.GONE
                        rlSubmittingData!!.visibility = View.VISIBLE

                    }

                    if (mainData?.isNotEmpty()!!) {
                        val finalMap = java.util.HashMap<String, String>()
                        for (i in mainData?.indices!!) {
                            try {
                                if (mainData!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(mainData!![i].name)!!) {
                                        //Log.e("Upload Form List--->", mainData!![i].name!!)
                                        mainData!![i].enteredValue =
                                            CommonUtils.getCommaSeparatedList(
                                                CommonUtils.stringListHashMap[mainData!![i].name]
                                            )
                                    }
                                    finalMap[mainData!![i].name!!] =
                                        mainData!![i].enteredValue!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //assign empty object to map again

                        CommonUtils.stringListHashMap = ConcurrentHashMap()
                        if (actionConfig?.action == Type.API)
                            finalApiHit()
                        else if (actionConfig?.action == Type.DISPOSE) {
                            perFormCreateTask()
                        }
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                    handlerThread?.interrupt()
                    hideLoading()
                    CommonUtils.stringListHashMap = ConcurrentHashMap()
                    if (actionConfig?.action == Type.API)
                        finalApiHit()
                    else if (actionConfig?.action == Type.DISPOSE) {
                        perFormCreateTask()
                    }
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    runOnUiThread {
                        mActivityCreateTaskBinding.btnCLickSubmit.visibility = View.VISIBLE
                        mActivityCreateTaskBinding.viewProgress.visibility = View.GONE
                        CommonUtils.makeScreenClickable(this@NewCreateTaskActivity)
                    }
                    count++

                    fileUploadCounter = 0
                    // This is where you do your work in the UI thread.
                    // Your worker tells you in the message what to do.
                    /*
                                        if (CommonUtils.errorString != null) {
                                            showDialogCancel(
                                                CommonUtils.errorString
                                            ) { dialog: DialogInterface?, which: Int ->
                                                when (which) {
                                                    DialogInterface.BUTTON_POSITIVE -> {
                                                        finish()
                                                        CommonUtils.errorString=null
                                                    }
                                                    DialogInterface.BUTTON_NEGATIVE ->
                                                    {
                                                        CommonUtils.errorString=null
                                                        finish()
                                                    }// proceed with logic by disabling the related features or quit the app.

                                                }
                                            }

                                        }
                    */
                    TrackiToast.Message.showShort(
                        this@NewCreateTaskActivity,
                        AppConstants.UNABLE_TO_PROCESS_REQUEST
                    )
                    //after getting error form the thread we interrupt the thread
                    handlerThread?.interrupt()
                    hideLoading()
                }
            }
            }
        }
    }


    override fun networkAvailable() {
        if (snackBar != null)
            snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            mActivityCreateTaskBinding.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    override fun getViewModel(): NewCreateTaskViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { NewCreateTaskViewModel.Factory(it) } // Factory
        if (factory != null) {
            mCreateTaskViewModel =
                ViewModelProvider(this, factory)[NewCreateTaskViewModel::class.java]
        }
        return mCreateTaskViewModel!!
    }


    private fun getUserSuggestionListViewModel(): GetUserSuggestionListViewModel {
        val factory =
            RocketFlyer.dataManager()
                ?.let { GetUserSuggestionListViewModel.Factory(it) } // Factory
        if (factory != null) {
            mGetSuggetionViewModel =
                ViewModelProvider(this, factory)[GetUserSuggestionListViewModel::class.java]
        }
        return mGetSuggetionViewModel
    }

    override fun addNearHubItems(hub: Hub) {
        dialogNearHub.dismiss()
        spinnerNear.setSelection((selectedHubList1.indexOf(hub) + 1))
    }

    override fun openPlaceOnMaps(hub: Hub) {
        val geoUri =
            "http://maps.google.com/maps?q=loc:${hub.hubLocation?.location?.latitude},${hub.hubLocation?.location?.longitude} (${hub.name})"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(intent)
    }
}