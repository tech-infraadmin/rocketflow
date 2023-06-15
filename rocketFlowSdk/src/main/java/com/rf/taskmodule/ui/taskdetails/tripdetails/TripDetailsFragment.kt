package com.rf.taskmodule.ui.taskdetails.tripdetails

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.request.AcceptRejectRequest
import com.rf.taskmodule.data.model.response.config.ActionConfig
import com.rf.taskmodule.data.model.response.config.Api
import com.rf.taskmodule.data.model.response.config.DynamicFormData
import com.rf.taskmodule.data.model.response.config.Event
import com.rf.taskmodule.data.model.response.config.GeoCoordinates
import com.rf.taskmodule.data.model.response.config.Navigation
import com.rf.taskmodule.data.model.response.config.Place
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.model.response.config.TaskResponse
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.FragmentTripDetailsBinding
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.ui.custom.EventDialogFragment
import com.rf.taskmodule.ui.taskdetails.AlertEvent
import com.rf.taskmodule.ui.taskdetails.TaskAlterEventAdapter
import com.rf.taskmodule.ui.taskdetails.TaskDetailActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.DateTimeUtil
import com.rf.taskmodule.utils.JSONConverter
import com.rf.taskmodule.utils.Log
import com.rf.taskmodule.utils.TaskStatus
import com.rf.taskmodule.utils.TrackiToast
import com.rocketflow.sdk.RocketFlyer
import com.trackthat.lib.TrackThat
import com.trackthat.lib.internal.util.EventType
import com.trackthat.lib.internal.util.TripState
import com.trackthat.lib.locationobserver.LocationObserver
import com.trackthat.lib.models.Events
import com.trackthat.lib.models.LocationData
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.ivVendor
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.llAlert
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.ratingBar
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.rl_chat
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.tvMobile
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.tvName
import kotlinx.android.synthetic.main.bottom_trip_sheet_task_detail_sdk.tvNumberAlert
import kotlinx.android.synthetic.main.fragment_trip_details.llSync
import kotlinx.android.synthetic.main.fragment_trip_details.progressBar
import kotlinx.android.synthetic.main.fragment_trip_details.rlSyncing
import kotlinx.android.synthetic.main.fragment_trip_details.tvSynProgress
import kotlinx.android.synthetic.main.fragment_trip_details.tvSyncStatus
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

class TripDetailsFragment : BaseSdkFragment<FragmentTripDetailsBinding, TripDetailsViewModel>(),
    OnMapReadyCallback, LocationObserver,
    GoogleMap.OnMarkerClickListener, Runnable, TaskAlterEventAdapter.OnAlertClick,
    View.OnClickListener,
    TripDetailsNavigator {

    //View
    private var dialogAllAlert: Dialog? = null
    private var eventList: ArrayList<AlertEvent>? = null
    private var name: String? = null
    private var buddyId: String? = null

    lateinit var httpManager: HttpManager
    lateinit var preferencesHelper: PreferencesHelper

    private var distinationMarker = false

    private var mobile: String? = null
    private var eventDialog: EventDialogFragment? = null
    private val tag = TaskDetailActivity::class.java.simpleName
    private var api: Api? = null
    private var polyLine: Polyline? = null
    private var movingMarker: Marker? = null
    private lateinit var shake: Animation
    private var endTaskData: List<DynamicFormData>? = null
    private var startTaskData: List<DynamicFormData>? = null

    private lateinit var toolbarTitle: TextView

    private lateinit var tvSOS: TextView
    private lateinit var cardViewTaskDetail: CardView

    private lateinit var tvDistance: TextView
    private lateinit var tvAvgSpeed: TextView
    private lateinit var tvDiverName: TextView
    private lateinit var tvDriverShortCode: TextView
    private lateinit var tvShortCode: TextView
    private lateinit var tvTaskAssignee: TextView
    private lateinit var tvTaskCreatedDate: TextView
    private lateinit var tvTaskEndLocation: TextView
    private lateinit var tvTaskEndTime: TextView
    private lateinit var tvTaskName: TextView
    private lateinit var tvTaskStartLocation: TextView
    private lateinit var tvTaskStartTime: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvFleetDetail: TextView
    private lateinit var ivShare: ImageView
    private lateinit var buttonDetail: Button
    private lateinit var bottomSheetTimeline: View

    private lateinit var mMap: GoogleMap
    private lateinit var viewAutoStart: TextView
    private var mapFragment: SupportMapFragment? = null

    private var osObject: ArrayList<Any?>? = null
    private var ostObject: ArrayList<Any?>? = null
    private var hcObject: ArrayList<Any?>? = null
    private var hbObject: ArrayList<Any?>? = null
    private var haObject: ArrayList<Any?>? = null
    private var mapList = ArrayList<Any?>()
    private var hashMap: HashMap<String, List<DynamicFormData>>? = null
    private var isCameraZoomEventDone = false
    private var locationSynHandler: Handler? = null
    var mStopHandler = false
    private var snackBar: Snackbar? = null
    var taskId: String? = null

    companion object {
        fun newInstance(taskResponse: String, webView: Boolean): TripDetailsFragment {
            val args = Bundle()
            args.putSerializable("taskResponse", taskResponse)
            args.putBoolean("webView", webView)
            val fragment = TripDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    //webview
    private var taskResponse: TaskResponse? = null
    private lateinit var webView: WebView

    private lateinit var viewModel: TripDetailsViewModel
    private lateinit var fragmentTripDetailsBinding: FragmentTripDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireArguments().containsKey("taskResponse")) {
            taskResponse = Gson().fromJson(requireArguments().getString("taskResponse"), TaskResponse::class.java)
            Log.d("task_response_trip", taskResponse?.taskDetail?.assigneeDetail?.name ?: "")
            taskId = taskResponse?.taskDetail?.taskId
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentTripDetailsBinding = viewDataBinding
        viewModel.navigator = this
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        if (requireArguments().containsKey("webView")) {
            Log.d("task_response_trip", requireArguments().getBoolean("webView").toString())
            if (requireArguments().getBoolean("webView")) {
                Log.d("task_response_trip","webView")
                loadWebView()
            } else {
                Log.d("task_response_trip","View")
                loadView()
            }
        }
    }

    private fun loadWebView() {

        webView = fragmentTripDetailsBinding.webView
        webView.visibility = View.VISIBLE
        webView.webViewClient = WebViewClient()
        val setting = webView.settings
        setting.domStorageEnabled = true
        setting.javaScriptEnabled = true

        val navigation = Navigation()
        val actionConfig = ActionConfig()
        val userId = preferencesHelper.userDetail.userId
        actionConfig.actionUrl = taskResponse?.taskDetail!!.trackingUrl + "&userId=" + userId
        navigation.actionConfig = actionConfig
        navigation.title = "Tracking Details"
        Log.d("task_response_trip", navigation.actionConfig?.actionUrl!!)
        webView.loadUrl(
            navigation.actionConfig?.actionUrl!!,
            CommonUtils.buildWebViewHeader(activity)
        )
    }

    private fun loadView() {
        fragmentTripDetailsBinding.llMain.visibility = View.VISIBLE
        setUp()
        buttonDetail = fragmentTripDetailsBinding.bottomSheetTrip.buttonDetail

        locationSynHandler = Handler()
        val runnable: Runnable = object : Runnable {
            override fun run() {
                // do your stuff - don't create a new runnable here!
                updateLocationPercentageTask()
                if (!mStopHandler) {
                    locationSynHandler!!.postDelayed(this, 100)
                }
            }
        }
        locationSynHandler!!.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler?.removeCallbacks(this)
            handler = null
            viewModel.onDestroy()
            if (locationSynHandler != null) {
                mStopHandler = true
                locationSynHandler!!.removeCallbacks(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside onDestroy(): $e")
        }
    }

    private fun setUp() {
        try {
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment?.getMapAsync(this)

            llAlert.setOnClickListener(this)

            bottomSheetTimeline = fragmentTripDetailsBinding.bottomSheetTimeline
            toolbarTitle = fragmentTripDetailsBinding.toolbarTitleNew

            val rootView = fragmentTripDetailsBinding.root
            tvSOS = rootView.findViewById(R.id.tvSOS)
            cardViewTaskDetail = rootView.findViewById(R.id.cardViewTaskDetail)

            viewAutoStart = rootView.findViewById(R.id.viewAutoStart)
            tvDistance = rootView.findViewById(R.id.tvDistance)
            tvTime = rootView.findViewById(R.id.tvTime)
            tvAvgSpeed = rootView.findViewById(R.id.tvAvgSpeed)
            tvDriverShortCode = rootView.findViewById(R.id.tvDriverShortCode)
            tvDiverName = rootView.findViewById(R.id.tvDiverName)
            tvShortCode = rootView.findViewById(R.id.tvShortCode)
            tvTaskAssignee = rootView.findViewById(R.id.tvTaskAssignee)
            tvTaskCreatedDate = rootView.findViewById(R.id.tvTaskCreatedDate)
            tvTaskName = rootView.findViewById(R.id.tvTaskName)
            tvTaskStartTime = rootView.findViewById(R.id.tvTaskStartTime)
            tvTaskEndTime = rootView.findViewById(R.id.tvTaskEndTime)
            tvTaskStartLocation = rootView.findViewById(R.id.tvTaskStartLocation)
            tvTaskEndLocation = rootView.findViewById(R.id.tvTaskEndLocation)
            tvFleetDetail = rootView.findViewById(R.id.tvFleetDetail)
            ivShare = rootView.findViewById(R.id.ivShare)

            toolbarTitle.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside setUp(): ${e.message}")
        }
    }

    private fun updateLocationPercentageTask() {
        if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
            var tripStatsCurrent = TrackThat.getCurrentTripStatistics(taskId)
            var jsonConverter =
                JSONConverter<com.trackthat.lib.models.TripsStatistics>()
            var jsonConverter2 =
                JSONConverter<com.rf.taskmodule.data.model.response.config.TripsStatistics>()
            var tripStatsStr = jsonConverter.objectToJson(tripStatsCurrent)
            CommonUtils.showLogMessage(
                "e",
                "trip_data",
                tripStatsCurrent.toString()
            )
            var tripStats: com.rf.taskmodule.data.model.response.config.TripsStatistics =
                jsonConverter2.jsonToObject(
                    tripStatsStr,
                    com.rf.taskmodule.data.model.response.config.TripsStatistics::class.java
                )
            setStats(
                tripStats.distanceInMeter / 1000,
                tripStats.tripDurationInMinute,
                tripStats.maxSpeed
            )
            setEvents(tripStats)
            if (!TrackThat.checkTripIsComplete(taskId!!)) {
                progressBar.visibility = View.GONE
                llSync.visibility = View.VISIBLE
                rlSyncing.visibility = View.GONE
                tvSyncStatus.visibility = View.VISIBLE
                tvSyncStatus.text = "Tracking.."
            } else {
                llSync.visibility = View.VISIBLE
                if (TrackThat.getSyncPercentageOfTrip(taskId!!) != null && TrackThat.getSyncPercentageOfTrip(taskId!!) < 100.0) {
                    rlSyncing.visibility = View.VISIBLE
                    tvSyncStatus.visibility = View.VISIBLE
                    tvSyncStatus.text = "Syncing.."
                    progressBar.progress = TrackThat.getSyncPercentageOfTrip(taskId!!).toInt()
                    tvSynProgress.text = "${progressBar.progress}%"
                } else {
                    rlSyncing.visibility = View.VISIBLE
                    tvSyncStatus.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    tvSyncStatus.text = "Synced"
                    if (TrackThat.getSyncPercentageOfTrip(taskId!!) != null) {
                        progressBar.progress = TrackThat.getSyncPercentageOfTrip(taskId!!).toInt()
                        tvSynProgress.text = "${progressBar.progress}%"
                    } else {
                        progressBar.progress = 100
                        tvSynProgress.text = "${progressBar.progress}%"
                    }
                    if (locationSynHandler != null) {
                        mStopHandler = true
                        locationSynHandler!!.removeCallbacks(this)
                    }
                }
            }
            // }

        }
    }

    private fun setEvents(tripStats: com.rf.taskmodule.data.model.response.config.TripsStatistics) {
        try {
            var overSpeed = 0
            var overStop = 0
            var harshAcceleration = 0
            var harshBraking = 0
            var harshCornering = 0
            var actionAirPlane = 0
            var mobileDataOff = 0
            var phoneUsage = 0
            var shutDown = 0
            var dateTimeChange = 0
            var geoIn = 0
            var geoOut = 0
            var logout = 0
            var forcepuncOut = 0
            var locationOff = 0
            if (tripStats.events != null && tripStats.events!!.isNotEmpty()) {
                if (tripStats.events?.containsKey("OVER_SPEEDING")!!) {
                    overSpeed = tripStats.events!!["OVER_SPEEDING"]!!
                }
                if (tripStats.events!!.containsKey("OVER_STOPPING")) {
                    overStop = tripStats.events!!["OVER_STOPPING"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_ACCELERATION")) {
                    harshAcceleration = tripStats.events!!["HARSH_ACCELERATION"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_BREAKING")) {
                    harshBraking = tripStats.events!!["HARSH_BREAKING"]!!
                }
                if (tripStats.events!!.containsKey("HARSH_CORNERING")) {
                    harshCornering = tripStats.events!!["HARSH_CORNERING"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_AIRPLANE_MODE")) {
                    actionAirPlane = tripStats.events!!["ACTION_AIRPLANE_MODE"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_MANUAL_MOBILE_DATA_OFF")) {
                    mobileDataOff = tripStats.events!!["ACTION_MANUAL_MOBILE_DATA_OFF"]!!
                }
                if (tripStats.events!!.containsKey("PHONE_USAGE")) {
                    phoneUsage = tripStats.events!!["PHONE_USAGE"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_SHUTDOWN_GRACEFULLY")) {
                    shutDown = tripStats.events!!["ACTION_SHUTDOWN_GRACEFULLY"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_DATE_TIME_CHANGE")) {
                    dateTimeChange = tripStats.events!!["ACTION_DATE_TIME_CHANGE"]!!
                }
                if (tripStats.events!!.containsKey("GEOFENCE_IN")) {
                    geoIn = tripStats.events!!["GEOFENCE_IN"]!!
                }
                if (tripStats.events!!.containsKey("GEOFENCE_OUT")) {
                    geoOut = tripStats.events!!["GEOFENCE_OUT"]!!
                }
                if (tripStats.events!!.containsKey("LOGOUT")) {
                    logout = tripStats.events!!["LOGOUT"]!!
                }
                if (tripStats.events!!.containsKey("FORCE_PUNCH_OUT")) {
                    forcepuncOut = tripStats.events!!["FORCE_PUNCH_OUT"]!!
                }
                if (tripStats.events!!.containsKey("ACTION_LOCATION_CHANGE")) {
                    locationOff = tripStats.events!!["ACTION_LOCATION_CHANGE"]!!
                }


                val count =
                    overSpeed + overStop + harshAcceleration + harshBraking + harshCornering + actionAirPlane + mobileDataOff + phoneUsage + shutDown + dateTimeChange + geoIn + geoOut + logout + forcepuncOut + locationOff


                if (count > 0) {
                    tvNumberAlert.text = "$count"
                    //  tvTotalCount.visibility = View.VISIBLE
                    tvNumberAlert.visibility = View.VISIBLE
                } else {
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside setEvents(): $e")
        }
    }

    private fun setStats(distance: Double, timed: Int, maxSpeed: Int) {
        try {
            val speed = ((maxSpeed * 3600) / 1000)
            val distance3digits: Double = String.format("%.3f", distance).toDouble()
            val distance2digits: Double = String.format("%.2f", distance3digits).toDouble()

//            val speed3digits: Double = String.format("%.3f", speed).toDouble()
//            val speed2digits: Double = String.format("%.2f", speed3digits).toDouble()

            val hours = timed / 60 //since both are ints, you get an int
            val minutes = timed % 60

            tvDistance.text = "$distance2digits KMS"
            tvAvgSpeed.text = "$speed KMPH"

            if (hours != 0 && minutes != 0) {
                tvTime.text = "$hours HRS $minutes MINS"
            } else if (hours == 0 && minutes != 0) {
                tvTime.text = "$minutes MINS"
            } else if (hours != 0 && minutes == 0) {
                tvTime.text = "$hours HRS"
            } else {
                tvTime.text = "0 MINS"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside setStats(): $e")
        }
    }

    private fun setMarkers(taskDetail: Task?) {
        try {
            var isMarker = false
            val builder = LatLngBounds.Builder()
            if (taskDetail != null && taskDetail!!.source != null && taskDetail!!.source!!.location != null) {
                var loc = taskDetail!!.source!!.location
                val sourceLat = loc!!.latitude
                val sourceLng = loc!!.longitude
                val srcAddress = taskDetail!!.source!!.address!!
                val srcLatLng = LatLng(sourceLat, sourceLng)

                val srcMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(srcLatLng)
                        .title(srcAddress)
                        .icon(
                            CommonUtils.bitmapDescriptorFromVector(
                                activity,
                                R.drawable.source_marker
                            )
                        )
                )
                srcMarker?.tag = TaskDetailActivity.MARKER_TAG
                builder.include(srcMarker.position)
                isMarker = true
            }
            if (!distinationMarker) {
                Log.d("distinationMarker","distinationMarker 1")
                if (taskDetail != null && taskDetail.destination != null && taskDetail!!.destination!!.location != null) {
                    Log.d("distinationMarker","distinationMarker 2")
                    val loc = taskDetail.destination!!.location
                    val destLat = loc!!.latitude
                    val destLng = loc.longitude
                    val destAddress = taskDetail.destination!!.address!!
                    val destLatLng = LatLng(destLat, destLng)
                    Log.d("distinationMarker", "isMarker $destLatLng $")
                    val destMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(destLatLng)
                            .title(destAddress)
                            .icon(
                                CommonUtils.bitmapDescriptorFromVector(
                                    activity,
                                    R.drawable.destination_marker
                                )
                            )
                    )
                    Log.d("distinationMarker", "isMarker $destLatLng $")
                    destMarker?.tag = TaskDetailActivity.MARKER_TAG
                    builder.include(destMarker.position)
                    isMarker = true
                    distinationMarker = true;
                }

            }

            Log.d("distinationMarker", "isMarker $distinationMarker $isCameraZoomEventDone")
            //check if marker is added into the builder
            if (isMarker && !isCameraZoomEventDone) {
                Log.d("distinationMarker","distinationMarker 3")
                val padding = 250 //offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                mMap.animateCamera(cu)
            }
            taskDetail?.let {


            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside setMarkers(): $e")
        }
    }

    private var lastTimeStamp = 0L

    private fun setLiveTripData() {
        try {
            val locationList = viewModel.getLiveTrip(lastTimeStamp)
            if (!locationList.isNullOrEmpty()) {
                Log.e(tag, "inside setLiveTripData():  location size : ${locationList.size}")
                lastTimeStamp = locationList[locationList.size - 1].time
                if (polyLine == null) {
                    polyLine = mMap.addPolyline(viewModel.drawPolyline(locationList))
                } else {
                    val polylineList = polyLine!!.points
                    polyLine?.points = viewModel.updatePolylineList(polylineList, locationList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside setLiveTripData(): $e")
        }
    }

    private fun updateCameraBearing(bearing: Float, latLng: LatLng) {
        try {
            val camPos = CameraPosition.Builder(
                mMap.cameraPosition
            )
                .bearing(bearing)
                .target(latLng)
                .zoom(14.toFloat())
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos))
        } catch (e: Exception) {
            e.printStackTrace()

            Log.e(TaskDetailActivity.TAG, "Exception Inside updateCameraBearing(): $e")
        }
    }



    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_trip_details
    }

    override fun getViewModel(): TripDetailsViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { TripDetailsViewModel.Factory(it) } // Factory
        if (factory != null) {
            viewModel = ViewModelProvider(this, factory)[TripDetailsViewModel::class.java]
        }
        return viewModel
    }

    override fun onMapReady(mapboxMap: GoogleMap?) {
        Log.d("onMapReady","onMapReady")
        if (mapboxMap != null) {
            Log.d("onMapReady","mapboxMap")
            mMap = mapboxMap
        }
        CommonUtils.changeGoogleLogoPosition(
            mapFragment!!,
            resources.getDimension(R.dimen.dimen_120).toInt(),
            mMap
        )
        mMap.setOnMarkerClickListener(this)
        Log.d("taskId 1",taskId)

        if (TrackThat.checkIfTripIdIsValid(taskId!!)) {
            Log.d("taskId 2",taskId)
            if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                Log.d("taskId 3", taskId)
                getLiveEvent()
            }
        }

        setTaskDetails()
    }

    private fun toRadians(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    private fun calculateDistance(latLng: List<LocationData>): Double {
        var distance = 0.0;
        for (i in latLng.indices) {
            if (i >= latLng.size - 1)
                break
            else {
                distance += calculateDistance(
                    latLng[i].geoCoordinates.latitude,
                    latLng[i].geoCoordinates.longitude,
                    latLng[i + 1].geoCoordinates.latitude,
                    latLng.get(i + 1).geoCoordinates.longitude
                )
            }
        }

        return distance
    }

    private fun calculateDistance(
        initialLat: Double, initialLong: Double,
        finalLat: Double, finalLong: Double
    ): Double {
        val R = 6371 // km
        val dLat = toRadians(finalLat - initialLat)
        val dLon = toRadians(finalLong - initialLong)
        val lat1 = toRadians(initialLat)
        val lat2 = toRadians(finalLat)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val dd = String.format("%.4f", R * c)
        val d = dd.toDouble()
        return d * 1000
    }

    private fun getLiveEvent() {
        if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
            CommonUtils.showLogMessage(
                "e",
                "getLiveEvent call",
                "" + TrackThat.checkTripIsComplete(taskId!!)
            )
            //  TrackiToast.Message.showShort(this,"getLiveEvent call")

            if (!TrackThat.checkTripIsComplete(taskId!!)) {
                val eventList = TrackThat.getEvent(taskId!!)
                if (eventList != null && !eventList.isEmpty()) {
                    var tripStats = com.rf.taskmodule.data.model.response.config.TripsStatistics()
                    var task = Task()
                    var secondTime: Long = 0
                    var detail: List<LocationData>? = TrackThat.getTripLocationById(taskId!!)

                    var builder = LatLngBounds.builder()
                    if (detail != null && detail.isNotEmpty()) {
                        var sortedLocationList = detail.sortedBy { data -> data.time }
                        var maxSpeedEvent = sortedLocationList.maxByOrNull { data -> data.speed }
                        if (maxSpeedEvent != null)
                            tripStats.maxSpeed =
                                CommonUtils.metersPerSecToKmPerHr(maxSpeedEvent.speed)
                        if (sortedLocationList.size > 1) {

                            tripStats.distance = calculateDistance(sortedLocationList)
                            tripStats.distanceInMeter =
                                calculateDistance(sortedLocationList) / 1000.toDouble()
                        }

                        val polyLine = viewModel.extractSdkPolyline(detail!!)
                        if (this::mMap.isInitialized)
                            mMap.addPolyline(polyLine)
                        //   isCameraZoomEventDone=false;

                        var initlatlong = LatLng(
                            detail[0].geoCoordinates?.latitude!!,
                            detail[0].geoCoordinates?.longitude!!
                        )
                        builder.include(initlatlong)
                        var startPlace = Place()
                        startPlace.address = CommonUtils.getAddress(
                            activity,
                            LatLng(
                                detail[0].geoCoordinates?.latitude!!,
                                detail[0].geoCoordinates?.longitude!!
                            )
                        )
                        var startGetCoards = GeoCoordinates()
                        startGetCoards.latitude = detail[0].geoCoordinates?.latitude!!
                        startGetCoards.longitude = detail[0].geoCoordinates?.longitude!!
                        startPlace.location = startGetCoards
                        task.source = startPlace
                        var finnallatlong = LatLng(
                            detail[detail.size - 1].geoCoordinates?.latitude!!,
                            detail[detail.size - 1].geoCoordinates?.longitude!!
                        )
                        builder.include(finnallatlong)
                        var endPlace = Place()
                        endPlace.address = CommonUtils.getAddress(
                            activity,
                            LatLng(
                                detail[detail.size - 1].geoCoordinates?.latitude!!,
                                detail[detail.size - 1].geoCoordinates?.longitude!!
                            )
                        )
                        var endGetCoards = GeoCoordinates()
                        endGetCoards.latitude = detail[detail.size - 1].geoCoordinates?.latitude!!
                        endGetCoards.longitude = detail[detail.size - 1].geoCoordinates?.longitude!!
                        endPlace.location = endGetCoards
                        task.destination = endPlace
                        try {
                            val padding = 250 //offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                            if (this::mMap.isInitialized)
                                mMap.animateCamera(cu)
                        } catch (e: java.lang.Exception) {

                        }

                        isCameraZoomEventDone = true;
                        setMarkers(taskDetail = task)


                    }
                    if (eventList != null && eventList.isNotEmpty()) {
                        var anotherList =
                            eventList.filter { data -> data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name }
                                .distinctBy { data -> data.startCoordinates.latitude }
                                .distinctBy { data -> data.startCoordinates.longitude }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            eventList.removeIf(Predicate { data ->
                                data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name
                            })
                        } else {
                            var locationChangeList =
                                eventList.filter { data -> data.eventType.name == EventType.ACTION_LOCATION_CHANGE.name }
                            eventList.removeAll(locationChangeList)
                        }

                        if (anotherList.isNotEmpty())
                            eventList.addAll(anotherList)

                        if (preferencesHelper.onTripOverStopping != null) {

                            var allOverStoppingList =
                                eventList.filter { data -> data.eventType.name == EventType.OVER_STOPPING.name }
                                    .distinctBy { data -> data.startCoordinates.latitude }
                                    .distinctBy { data -> data.startCoordinates.longitude }
                            if (allOverStoppingList.isNotEmpty()) {
                                var overStopping: MutableList<Events> = ArrayList()
                                var overstoppingConfig = preferencesHelper.onTripOverStopping
                                for (event in allOverStoppingList) {
                                    var diff = event.endTime - event.startTime
                                    var overStoppingMinute =
                                        TimeUnit.MILLISECONDS.toMinutes(diff).toInt()
                                    var isPointOutSide = CommonUtils.isPointOutSideCircle(
                                        overstoppingConfig.distanceinMeter,
                                        event.startCoordinates.latitude,
                                        event.startCoordinates.longitude,
                                        event.endCoordinates.latitude,
                                        event.endCoordinates.longitude
                                    )
                                    if (overStoppingMinute >= overstoppingConfig.timeInMinute && !isPointOutSide) {
                                        overStopping.add(event)
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    eventList.removeIf(Predicate { data ->
                                        data.eventType.name == EventType.OVER_STOPPING.name
                                    })
                                } else {
                                    var overStoppingList =
                                        eventList.filter { data -> data.eventType.name == EventType.OVER_STOPPING.name }
                                    eventList.removeAll(overStoppingList)
                                }
                                if (overStopping.isNotEmpty()) {
                                    eventList.addAll(overStopping)
                                }
                            }

                        }
                        var sortedList = eventList.sortedBy { data -> data.startTime }
                        if (preferencesHelper.isTrackingLiveTrip) {
                            secondTime = System.currentTimeMillis()
                        } else {
                            var allEventList: MutableList<Events>? = TrackThat.getEvent(taskId!!)
                            if (allEventList != null) {

                                var completedEventList =
                                    allEventList.filter { data -> data.tripState.name == TripState.COMPLETED.name }
                                var latestCompleted =
                                    completedEventList.sortedByDescending { data -> data.startTime }
                                var jsonConvertor =
                                    JSONConverter<List<Events>>()
                                var json = jsonConvertor.objectToJson(latestCompleted);
                                CommonUtils.showLogMessage("e", "event list ", "" + json)
                                if (latestCompleted.isNotEmpty())
                                    secondTime = latestCompleted[0].startTime
                                else {

                                    if (preferencesHelper.lastPunchOutTime != 0L)
                                        secondTime = preferencesHelper.lastPunchOutTime
                                    else {
                                        var calculateStartTime =
                                            allEventList.sortedByDescending { data -> data.startTime }
                                        secondTime = calculateStartTime[0].startTime
                                    }
                                }
                            }

                        }
                        var firstTime = sortedList[0].startTime
                        var diff = (secondTime - firstTime)

                        tripStats.tripDurationInMinute =
                            TimeUnit.MILLISECONDS.toMinutes(diff).toInt();
                        // fabLayoutAlerts.visibility = View.VISIBLE
                        // setStats(tripStats.distanceInMeter, tripStats.tripDurationInMinute, tripStats.maxSpeed)
                        llAlert.visibility = View.VISIBLE

                        var listEvent = HashMap<String, Int>()
                        for (event in sortedList) {
                            if (listEvent.containsKey(event.eventType.name)) {
                                listEvent[event.eventType.name] =
                                    listEvent[event.eventType.name]!! + 1
                            } else {
                                listEvent.put(event.eventType.name, 1)
                            }
                            listEvent[event.eventType.name]
                        }

                        tripStats.events = listEvent
                    }


                    var listevent = ArrayList<Event>()
                    for (eventData in eventList) {
                        var event = Event()
                        event.eventStartTime = eventData.startTime
                        event.eventEndTime = eventData.endTime
                        event.eventName = eventData.eventType.name
                        var eventStartLocation = GeoCoordinates();
                        eventStartLocation.longitude = eventData.startCoordinates.longitude
                        eventStartLocation.latitude = eventData.startCoordinates.latitude
                        event.from = eventStartLocation
                        var eventEndLocation = GeoCoordinates();
                        eventEndLocation.longitude = eventData.endCoordinates.longitude
                        eventEndLocation.latitude = eventData.endCoordinates.latitude
                        event.to = eventEndLocation
                        event.speed = eventData.speed
                        event.tripId = taskId
                        listevent.add(event)

                    }

                    viewModel.isolateEvents(listevent, requireActivity(), preferencesHelper)
                    setEvents(tripStats)
                }

            }
        }
    }

    override fun showEventDialog(showDialog: Boolean, eventName: String) {
        try {
            if (showDialog) {
                if (eventDialog != null) {
                    eventDialog!!.dismiss()
                }
                eventDialog = EventDialogFragment.getInstance(eventName)
                eventDialog!!.show(requireActivity().supportFragmentManager, "dialog")
            } else {
                TrackiToast.Message.showLong(activity, eventName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside showEventDialog(): $e")
        }
    }

    override fun onLocationChange(location: Location?) {
        try {
            getLiveEvent()
            setLiveTripData()
            if (location != null) {
                Log.e(
                    tag,
                    "inside requestCurrentLocation() currentLocation: ${location.latitude}, ${location.longitude}"
                )
                val latLng = LatLng(location.latitude, location.longitude)
                //add moving marker here
                if (movingMarker == null) {
                    movingMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .icon(
                                CommonUtils.bitmapDescriptorFromVector(
                                    activity,
                                    R.drawable.ic_location_marker
                                )
                            )
                    )
                } else {
                    movingMarker!!.position = latLng
                }
                //   updateCameraBearing(location.bearing, latLng)
            }
        } catch (e: Exception) {
            Log.e(tag, "Inside Current Location ${e.message}")
        }
    }

    override fun isolateEventResponse(
        osObject: ArrayList<Any?>,
        ostObject: ArrayList<Any?>,
        hcObject: ArrayList<Any?>,
        hbObject: ArrayList<Any?>,
        haObject: ArrayList<Any?>
    ) {
        this.osObject = osObject
        this.ostObject = ostObject
        this.hcObject = hcObject
        this.hbObject = hbObject
        this.haObject = haObject
    }

    override fun allEventList(list: ArrayList<AlertEvent>) {
        eventList = list
    }

    override fun onCallClick() {
        if (mobile != null) {
            CommonUtils.openDialer(activity, mobile)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

        try {
            hideLoading()
            if (CommonUtils.handleResponse(callback, error, result, activity)) {
                taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
                setTaskDetails()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside handleResponse(): $e")
        }
    }

    private var handler: Handler? = null

    private fun setTaskDetails() {
        //  try {
        val detail = taskResponse?.taskDetail
        Log.d("assignedToDetails", detail?.assignedToDetails.toString())
        if (detail != null) {
            if (detail.assigneeDetail != null && detail.assignedToDetails?.get(0)?.profileImage != null) {
                Glide.with(this).load(detail.assignedToDetails?.get(0)?.profileImage)
                    .placeholder(R.drawable.ic_social_media).apply(
                    RequestOptions().circleCrop()
                ).error(R.drawable.ic_social_media)
                    .into(ivVendor)
            } else if (detail.assigneeDetail != null && detail.assigneeDetail!!.profileImage != null) {
                Glide.with(this).load(detail.assigneeDetail!!.profileImage)
                    .placeholder(R.drawable.ic_social_media).apply(
                    RequestOptions().circleCrop()
                ).error(R.drawable.ic_social_media)
                    .into(ivVendor)
            }
            if (detail.assigneeDetail != null && detail.assigneeDetail!!.name != null)
                tvName.text = detail.assignedToDetails?.get(0)?.name ?: detail.assigneeDetail!!.name
            if (detail.assigneeDetail != null && detail.assigneeDetail!!.name != null)
                tvName.text = detail.assignedToDetails?.get(0)?.name ?: detail.assigneeDetail!!.name
            if (detail.assigneeDetail != null && detail.assigneeDetail!!.mobile != null) {
                tvMobile.text =
                    detail.assignedToDetails?.get(0)?.mobile ?: detail.assigneeDetail!!.mobile
                mobile = detail.assignedToDetails?.get(0)?.mobile ?: detail.assigneeDetail!!.mobile
            }
            if (detail.assigneeDetail != null) {
                ratingBar.rating = 4.0f
            }
            if (preferencesHelper.userDetail != null && preferencesHelper.userDetail!!.userId != null && detail!!.assigneeDetail != null && detail.assigneeDetail!!.buddyId != null) {
                if (preferencesHelper.userDetail!!.userId.equals(detail.assigneeDetail!!.buddyId)) {
                    rl_chat.visibility = View.GONE
                } else {
                    rl_chat.visibility = View.VISIBLE
                }
            }
            rl_chat.setOnClickListener(View.OnClickListener {
                buddyId = detail!!.assigneeDetail!!.buddyId;
                name = detail!!.assigneeDetail!!.name;
                try {
//                    if (webSocketManager != null && webSocketManager.isConnected) {
//                        showLoading()
//                        webSocketManager.connectPacket(buddyId)
//                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

            })


            viewModel.setContact(detail)
            if (detail.autoCreated) {
                viewAutoStart.text = getString(R.string.autostart)
//                    viewAutoStart.visibility = View.VISIBLE
            } else {
                viewAutoStart.text = "Assigned By: ${detail.assigneeDetail?.name}"
//                    viewAutoStart.visibility = View.GONE
            }

            if (detail.fleetDetail != null) {
                tvFleetDetail.text =
                    "Fleet: ${detail.fleetDetail?.fleetName} | ${detail.fleetDetail?.inventoryId}"
                tvFleetDetail.visibility = View.VISIBLE
            } else {
                tvFleetDetail.visibility = View.GONE
            }
            buttonDetail.visibility = View.GONE
            this.startTaskData = null
            this.endTaskData = null

            hashMap = HashMap()
            if (detail.taskData != null) {
                for (i in detail.taskData?.indices!!) {
                    val data = detail.taskData!![i]
                    hashMap!![data.ctaId!!] = data.taskData!!
                }
            }


            if (hashMap!!.isNotEmpty()) {
                buttonDetail.visibility = View.VISIBLE
            }

            tvSOS.visibility = View.GONE
            if (TrackThat.isTracking())
                ivShare.visibility = View.VISIBLE
            else {
                ivShare.visibility = View.GONE
            }

            var distance = 0.0
            var maxSpeed = 0
            var timed = 0
            ivShare.setOnClickListener {
//                startActivity(ShareTripActivity.newIntent(this)
//                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId))
            }
            llAlert.visibility = View.VISIBLE
            if (handler == null) {
                handler = Handler()
                handler?.post(this)
            }

            detail.tripData.let { tData ->
                tData?.locations.let { loc ->
                    loc?.let { l ->
                        val polyLine = viewModel.extractPolyline(l)
                        mMap.addPolyline(polyLine)
                        //  isCameraZoomEventDone=false;
                        if (l.isNotEmpty()) {
                            var builder = LatLngBounds.builder();
                            var initlatlong = LatLng(
                                l[0].geoCoordinates?.latitude!!,
                                l[0].geoCoordinates?.longitude!!
                            )
                            builder.include(initlatlong)
                            var finnallatlong = LatLng(
                                l[l.size - 1].geoCoordinates?.latitude!!,
                                l[l.size - 1].geoCoordinates?.longitude!!
                            )
                            builder.include(finnallatlong)
                            val padding = 250 //offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding)
                            mMap.animateCamera(cu)
                            isCameraZoomEventDone = true
                        }

                    }
                }

            }

            setMarkers(detail)
            detail.tripStatistics?.let { stats ->
                stats?.distanceInMeter?.run { distance = stats.distanceInMeter / 1000 }
                stats?.maxSpeed?.run { maxSpeed = stats.maxSpeed }
                stats?.tripDurationInMinute?.run { timed = stats.tripDurationInMinute }
                stats?.let {
                    val eventList = TrackThat.getEvent(taskId!!)
                    if (eventList != null && eventList.isEmpty()) {
                        useLocalDbEvent()
                    } else {
                        //  performMakeList(stats.events!!)
                        if (detail.tripData!!.events != null && !detail.tripData!!.events!!.isEmpty())
                            viewModel.isolateEvents(
                                detail.tripData!!.events,
                                requireActivity(),
                                preferencesHelper
                            )
                        else if (stats.events != null)
                            performMakeList(stats.events!!)

                        setEvents(stats)
                    }
                }
            }


            val time = DateTimeUtil.getParsedDate(detail.createdAt) + " at " +
                    DateTimeUtil.getParsedTime(detail.createdAt)
            setStats(distance, timed, maxSpeed)

            if (detail.buddyDetail?.name == null) {
                tvDiverName.text = "${detail.assigneeDetail?.name}"
            } else {
                tvDiverName.text = "${detail.buddyDetail?.name}"
            }

            if (detail.buddyDetail?.shortCode == null) {
                tvDriverShortCode.text = "${detail.assigneeDetail?.shortCode}"
            } else {
                tvDriverShortCode.text = "${detail.buddyDetail?.shortCode}"
            }
            tvShortCode.text = "${detail.assigneeDetail?.shortCode}"
            val mess: String = detail.assigneeDetail?.name + " has assign you a task"
            tvTaskAssignee.text = mess
            tvTaskCreatedDate.text = time
            val s = "Task Name: ${detail.taskName}"
            tvTaskName.text = CommonUtils.setCustomFontTypeSpan(
                activity,
                s,
                12,
                s.length,
                R.font.campton_semi_bold
            )
            tvTaskStartTime.text = time
            tvTaskEndTime.text = "${DateTimeUtil.getParsedDate(detail.endTime)} at" + " ${
                DateTimeUtil.getParsedTime(detail.endTime)
            }"
            tvTaskStartLocation.text = detail.source?.address
            tvTaskEndLocation.text = detail.destination?.address

            toolbarTitle.text = time



            if (taskResponse != null &&
                detail.status == TaskStatus.LIVE &&
                (taskResponse?.taskDetail?.taskId!! == TrackThat.getCurrentTrackingId())
            ) {
                Log.i(tag, "My trip (${taskResponse?.taskDetail?.taskId}) is: LIVE")
                setLiveTripData()
                viewModel.registerLocationObserver()
            } else {
                Log.i(tag, "TripId (${taskResponse?.taskDetail?.taskId}) is: LIVE")
            }
//            if(detail.currentStage!=null){
//                if(detail.currentStage!!.terminal!!){
//                    val snappedPoints: List<LatLng> = ArrayList()
//                    val sourcePoints = ArrayList<LatLng>()
//                    if(detail.tripData!!.locations!=null){
//                        sourcePoints.add(0,LatLng(detail.tripData!!.locations!![0].geoCoordinates?.latitude!!, detail.tripData!!.locations!![0].geoCoordinates?.longitude!!))
//                        var jump=0
//                        var size =detail.tripData!!.locations!!.size
//                        if(size<100){
//                            jump=1
//                        }else{
//                            jump=2
//                        }
//                        for(i in detail.tripData!!.locations!!.indices step jump){
//                            var points=detail.tripData!!.locations!![i]
//                            sourcePoints.add(LatLng(points.geoCoordinates?.latitude!!, points.geoCoordinates?.longitude!!))
//                        }
//                        sourcePoints.add(sourcePoints.lastIndex,LatLng(detail.tripData!!.locations!![detail.tripData!!.locations!!.lastIndex].geoCoordinates?.latitude!!, detail.tripData!!.locations!![detail.tripData!!.locations!!.lastIndex].geoCoordinates?.longitude!!))
//
//                    }
//
//                    GetSnappedPointsAsyncTask().execute(sourcePoints, null, snappedPoints)
//                }
//            }
            //check if payout eligible flag is true then show payment details
            if (detail.payoutEligible && detail.taskStateUpdated && detail.status == TaskStatus.COMPLETED) {
                viewModel.isPayoutEligible.set(detail.payoutEligible)
                detail.taskstats?.let { stats ->
                    viewModel.extraKm.set("${stats.distance} " + AppConstants.KM)
                    viewModel.waitingTime.set("${stats.waitingtime} " + AppConstants.MIN)
                    viewModel.overtime.set("${stats.tripDuration} " + AppConstants.MIN)
                }
                detail.driverPayoutBreakUps?.let { driverPayout ->
                    viewModel.estimatedEarnings.set(
                        AppConstants.ESTIMATE_EARNINGS + " " +
                                AppConstants.INR + " " + driverPayout.totalPayout
                    )
                    viewModel.nightCharges.set("${driverPayout.nightCharge}")
                    viewModel.baseFare.set("${driverPayout.basefare}")
                    viewModel.extraKmCharges.set("${driverPayout.extraKmCharge}")
                    viewModel.waitingTimeCharges.set("${driverPayout.waitingCharge}")
                    viewModel.overtimeCharges.set("${driverPayout.overtimeCharge}")
                    viewModel.nightCharges.set("${driverPayout.nightCharge}")
                    viewModel.totalEarning.set("${driverPayout.totalPayout}")

                }
            }
        }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e(TAG, "Exception Inside setTaskDetail(): $e")
//        }
    }

    private fun performMakeList(events: HashMap<String, Int>) {
        eventList = ArrayList();
        var context = requireActivity()
        val hmIterator: Iterator<*> = events!!.entries.iterator()
        while (hmIterator.hasNext()) {
            val mapElement = hmIterator.next() as Map.Entry<*, *>
            var alertEvent = AlertEvent()
            alertEvent.name = mapElement.key.toString()
            if (alertEvent.name == "OVER_STOPPING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_over_stopping)


            }
            if (alertEvent.name == "HARSH_CORNERING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_harsh_cornering)

            }
            if (alertEvent.name == "HARSH_BREAKING") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_harsh_braking)

            }
            if (alertEvent.name == "HARSH_ACCELERATION") {
                alertEvent.icon =
                    ContextCompat.getDrawable(context, R.drawable.ic_harsh_acceleration)

            }
            if (alertEvent.name == "ACTION_AIRPLANE_MODE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_airplane_mode)

            }
            if (alertEvent.name == "OUT_OF_NETWORK") {
                alertEvent.icon =
                    ContextCompat.getDrawable(context, R.drawable.ic_network_connection_off)

            }

            if (alertEvent.name == "ACTION_MANUAL_MOBILE_DATA_OFF") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_internet)

            }

            if (alertEvent.name == "ACTION_DATE_TIME_CHANGE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_date_time_change)

            }
            if (alertEvent.name == "GEOFENCE_IN") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_geo_in)

            }
            if (alertEvent.name == "GEOFENCE_OUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_geo_in)

            }
            if (alertEvent.name == "LOGOUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_force_logout)

            }
            if (alertEvent.name == "FORCE_PUNCH_OUT") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_exit)

            }
            if (alertEvent.name == "ACTION_SHUTDOWN_GRACEFULLY") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_shutdown)

            }
            if (alertEvent.name == "PHONE_USAGE") {
                alertEvent.icon = ContextCompat.getDrawable(context, R.drawable.ic_phone_usage)

            }
            if (alertEvent.name == "ACTION_LOCATION_CHANGE") {
                alertEvent.icon =
                    ContextCompat.getDrawable(context, R.drawable.ic_location_off_small)

            }
            if (alertEvent.name != "NONE")
                eventList!!.add(alertEvent)
        }

    }

    private fun useLocalDbEvent() {
        val eventList = TrackThat.getEvent(taskId!!)
        var tripStats = com.rf.taskmodule.data.model.response.config.TripsStatistics()
        var listevent = ArrayList<Event>()
        var listEvent = HashMap<String, Int>()
        for (event in eventList) {
            if (listEvent.containsKey(event.eventType.name)) {
                listEvent[event.eventType.name] = listEvent[event.eventType.name]!! + 1
            } else {
                listEvent.put(event.eventType.name, 1)
            }
            listEvent[event.eventType.name]
        }

        tripStats.events = listEvent
        for (eventData in eventList) {
            var event = Event()
            event.eventStartTime = eventData.startTime
            event.eventEndTime = eventData.endTime
            event.eventName = eventData.eventType.name
            var eventStartLocation = GeoCoordinates();
            eventStartLocation.longitude = eventData.startCoordinates.longitude
            eventStartLocation.latitude = eventData.startCoordinates.latitude
            event.from = eventStartLocation
            var eventEndLocation = GeoCoordinates();
            eventEndLocation.longitude = eventData.endCoordinates.longitude
            eventEndLocation.latitude = eventData.endCoordinates.latitude
            event.to = eventEndLocation
            event.speed = eventData.speed
            event.tripId = taskId
            listevent.add(event)

        }
        viewModel.isolateEvents(listevent, requireActivity(), preferencesHelper)
        setEvents(tripStats)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return if (marker != null && marker.tag != null) {
            // Retrieve the data from the marker.
            val markerTag = marker.tag as String

            markerTag != TaskDetailActivity.MARKER_TAG
        } else {
            true
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    override fun run() {
        try {
            if (handler != null) {
                //   fabAlerts.startAnimation(shake)
                if (taskId != null) {
                    if (TrackThat.getCurrentTrackingId() != null && taskId == TrackThat.getCurrentTrackingId()) {
                        getLiveEvent()

                    } else
                        viewModel.getTaskById(httpManager, AcceptRejectRequest(taskId!!), api)
                }
                handler?.postDelayed(this, 60000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception Inside run(): $e")
        }
    }

    override fun alert(alertEvent: AlertEvent) {
        if (dialogAllAlert != null)
            dialogAllAlert?.dismiss()
        if (alertEvent.eventData != null)
            showPolyLineAndMarkers(alertEvent.eventData!!)
    }

    private fun showPolyLineAndMarkers(polylineAndMarkerObj: ArrayList<Any?>) {
        try {
            clearMapList()

//            for (aPolylineAndMarkerObj in polylineAndMarkerObj) {
            drawOnMap(polylineAndMarkerObj)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TaskDetailActivity.TAG, "Exception inside showPolyLineAndMarkers(): $e")
        }
    }

    @Throws(NullPointerException::class)
    fun drawOnMap(objects: ArrayList<Any?>) {
        if (null != objects[0]) {
            val polylineMap = objects[0] as Map<Int, PolylineOptions>

            for (value in polylineMap.entries) {
                mapList.add(mMap.addPolyline(value.value))
            }
        }
        if (null != objects[1]) {
            val listMarkers = objects[1] as List<MarkerOptions>

            for (i in listMarkers.indices) {
                mapList.add(mMap.addMarker(listMarkers[i]))
            }
        }

    }

    private fun clearMapList() {
        if (mapList.size > 0) {
            for (i in mapList.indices) {
                val item = mapList[i]
                if (item is Polyline) {
                    item.remove()
                } else if (item is Marker) {
                    item.remove()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.llAlert -> {
                if (eventList != null && eventList!!.isNotEmpty()) {
                    openDialogShowEvent(eventList!!)
                }
            }
        }
    }

    open fun openDialogShowEvent(list: ArrayList<AlertEvent>) {
        var adapter = TaskAlterEventAdapter(requireActivity(), ArrayList())
        if (dialogAllAlert == null) {
            var cellwidthWillbe = 0
            dialogAllAlert = Dialog(requireActivity())
            dialogAllAlert!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogAllAlert?.window!!.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            dialogAllAlert!!.setContentView(R.layout.layout_pop_alert_sdk)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialogAllAlert?.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.dimAmount = 0.8f
            val window = dialogAllAlert!!.window
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window!!.setGravity(Gravity.CENTER)
            val textViewCancel = dialogAllAlert!!.findViewById<TextView>(R.id.cancelText)
            val recyclerViewEvents =
                dialogAllAlert!!.findViewById<RecyclerView>(R.id.recyclerViewEvents)
            val rlRv = dialogAllAlert!!.findViewById<RelativeLayout>(R.id.rlRv)

            val viewTreeObserver: ViewTreeObserver = rlRv.getViewTreeObserver()
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerViewEvents.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    val width: Int = recyclerViewEvents.getMeasuredWidth()
                    val height: Int = recyclerViewEvents.getMeasuredHeight()
                    cellwidthWillbe = width / 4
                    adapter.addList(list)
                    adapter.setWidthWillBe(cellwidthWillbe)
                    // recyclerViewEvents.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    recyclerViewEvents.adapter = adapter
                }
            })

            dialogAllAlert?.window!!.attributes = lp
            textViewCancel.setOnClickListener {
                dialogAllAlert!!.dismiss()
            }
        } else {
            adapter.addList(list)
        }
        if (!dialogAllAlert!!.isShowing)
            dialogAllAlert!!.show()
    }


}