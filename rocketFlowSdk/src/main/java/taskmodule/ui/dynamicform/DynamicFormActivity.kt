package taskmodule.ui.dynamicform

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rocketflow.sdk.RocketFlyer
import taskmodule.BR
import taskmodule.R
import taskmodule.TrackiSdkApplication
import taskmodule.data.local.db.DatabaseHelper
import taskmodule.data.local.prefs.AppPreferencesHelper
import taskmodule.data.local.prefs.PreferencesHelper
import taskmodule.data.model.GeofenceData
import taskmodule.data.model.request.DynamicFormMainData
import taskmodule.data.model.response.config.*
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.databinding.ActivityDynamicFormSdkBinding
import taskmodule.ui.base.BaseSdkActivity
import taskmodule.ui.custom.ExecutorThread
import taskmodule.ui.dynamicform.dynamicfragment.DynamicFragment
import taskmodule.ui.dynamicform.dynamicfragment.FormSubmitListener
import taskmodule.utils.*
//import dagger.android.AndroidInjector
//import dagger.android.DispatchingAndroidInjector
//import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_dynamic_form_sdk.container
import kotlinx.android.synthetic.main.activity_dynamic_form_sdk.nestedScrollView
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList


/**
 * Class @DynamicFormActivity used to handle the dynamic
 * form which are present inside @config api.
 *
 * Created by rahul on 29/3/19
 */
class DynamicFormActivity : BaseSdkActivity<ActivityDynamicFormSdkBinding, DynamicFormViewModel>(),
    DynamicFormNavigator, FormSubmitListener {
//    init {
//        System.loadLibrary("keys")
//    }

    private var snackBar: Snackbar?=null
    private var tcfId: String? = null
    private var mDfdId: String? = null
    private var showToolbar: Boolean = true //it is used for main activity


    lateinit var httpManager: HttpManager
    lateinit var preferencesHelper: PreferencesHelper

    lateinit var mDynamicFormViewModel: DynamicFormViewModel
    private var mActivityDynamicFormSdkBinding: ActivityDynamicFormSdkBinding? = null
    private lateinit var dBHelper: DatabaseHelper

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_dynamic_form_sdk
    override fun getViewModel(): DynamicFormViewModel {
        val factory = RocketFlyer.dataManager()?.let { DynamicFormViewModel.Factory(it) } // Factory
        if (factory != null) {
            mDynamicFormViewModel = ViewModelProvider(this, factory)[DynamicFormViewModel::class.java]
        }
        return mDynamicFormViewModel!!
    }
    private var mainMap: HashMap<String, ArrayList<FormData>>? = null
    var taskId: String? = ""
    private var taskAction: String? = null
    private var ivBack: ImageView? = null
    private var currentOpenedFragment: String? = null
    private var geofenceData: GeofenceData? = null

    private var ctaId: String? = null
    private var dynamicFormsNew: DynamicFormsNew? = null
    private var isEditable: Boolean = true
    private var isHideButton: Boolean = false
    private var dynamicFragment: DynamicFragment? = null


    var formId: String? = null
    var scannerFieldName: String? = null
    var scannerFieldValue: String? = null
    var mainData: ArrayList<FormData>? = null

    public var toolbar: androidx.appcompat.widget.Toolbar? = null


    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null
    var titleText: TextView?=null
    var percentageText: TextView?=null
    var currentStatusText: TextView?=null
    var progressBar: ProgressBar?=null
    var rlProgress: RelativeLayout?=null
    var rlSubmittingData: RelativeLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityDynamicFormSdkBinding = viewDataBinding
        mDynamicFormViewModel.navigator = this

        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        dBHelper = DatabaseHelper.getInstance(this@DynamicFormActivity)
        handlerThread = ExecutorThread()
        toolbar = mActivityDynamicFormSdkBinding!!.toolbar

        //set toolbar title here
        setToolbar(toolbar!!, /*${formType?.name} */"Form")

        ivBack = mActivityDynamicFormSdkBinding?.ivBack
//        loading = mActivityDynamicFormSdkBinding?.loading
//        ivForward = mActivityDynamicFormSdkBinding?.ivForward

        ivBack?.setOnClickListener {
            onBackPressed()
        }

        if (intent != null) {
            val intent = intent
            if (intent.hasExtra(AppConstants.Extra.EXTRA_FORM_TYPE)) {
                taskAction = intent.getStringExtra(AppConstants.Extra.EXTRA_FORM_TYPE)
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_FORM_ID)) {
                formId = intent.getStringExtra(AppConstants.Extra.EXTRA_FORM_ID)
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
            }

            if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID)) {
                ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID);
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_TCF_ID)) {
                tcfId = intent.getStringExtra(AppConstants.Extra.EXTRA_TCF_ID);
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_DATA)) {
                geofenceData = intent.getSerializableExtra(AppConstants.Extra.EXTRA_DATA) as GeofenceData
            }

            if (intent.hasExtra(AppConstants.Extra.EXTRA_IS_EDITABLE)) {
                isEditable = intent.getBooleanExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, true)
            }
            if (intent.hasExtra(AppConstants.Extra.HIDE_BUTTON)) {
                isHideButton = intent.getBooleanExtra(AppConstants.Extra.HIDE_BUTTON, false)
            }
            //it is used for main activity.
            if (intent.hasExtra(AppConstants.Extra.EXTRA_SHOW_TOOLBAR)) {
                showToolbar = intent.getBooleanExtra(AppConstants.Extra.EXTRA_SHOW_TOOLBAR, true)
                if (!showToolbar) {
                    if (supportActionBar != null) {
                        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    }
                }
            }

            if (intent.hasExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_NAME)) {
                scannerFieldName = intent.getStringExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_NAME)
            }
            if (intent.hasExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_VALUE)) {
                scannerFieldValue = intent.getStringExtra(AppConstants.Extra.EXTRA_SCANNER_FIELD_VALUE)
            }
            dynamicFragment = DynamicFragment.getInstance(formId!!, taskId!!, isEditable, tcfId, isHideButton,scannerFieldName,scannerFieldValue, ArrayList())
            if(dynamicFragment!=null)
                replaceFragment(dynamicFragment!!, formId)

        }


        dynamicFormsNew = CommonUtils.getFormByFormId(formId!!)
        var viewProgress=mActivityDynamicFormSdkBinding!!.viewProgress
        titleText=viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText=viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText=viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar=viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData=viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress=viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)

    }

    override fun onResume() {
        super.onResume()
        Places.initialize(this@DynamicFormActivity, getGoogleMapKey()!!)
    }


    private val formList = ArrayList<String>()

    /**
     * Method used to add fragment and show it to user.
     *
     * @param fragment fragment that needs to be added/replaced.
     */
    private fun replaceFragment(fragment: Fragment, fragmentName: String?) {
        val formName = CommonUtils.getFormByFormId(fragmentName)
        CommonUtils.showLogMessage("e", "formid", fragmentName);
        if (formName?.name != null) {
            mActivityDynamicFormSdkBinding?.toolbar?.title = formName.name
        } else {
            mActivityDynamicFormSdkBinding?.toolbar?.title = ""
        }
        //if form already added into the hashMap the remove the id from the hashMap and update the position again
        if (formList.isNotEmpty()) {
            for (i in formList.indices) {
                if (formList[i] == fragmentName) {
                    formList.removeAt(i)
                }
            }
        }
        if(fragmentName!=null)
            formList.add(fragmentName)
        if (formList.size > 1) {
            ivBack?.visibility = View.VISIBLE
        }
        Log.e(TAG, "Fragment name----> ${formList.size} <---> $fragmentName")
        val manager = supportFragmentManager

        val ft = manager.beginTransaction()
        ft.replace(R.id.container, fragment, fragmentName)
        ft.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dynamicFragment != null) {
            dynamicFragment!!.onActivityResult(requestCode, resultCode, data);
        }
    }

    override fun onBackPressed() {
        super.onBackPressed();

    }


    /**
     * Method used to get the entered data of to be processed and send to server.
     */
    override fun onProcessClick(list: ArrayList<FormData>, dynamicActionConfig: DynamicActionConfig?,
                                currentFormId: String?, dfdid: String?) {
        currentOpenedFragment = currentFormId
        Log.e(TAG, "Current Opened Fragment----> $currentOpenedFragment")
        Log.e(TAG, "config type----> ${dynamicActionConfig?.action} ")
        var jsonConverter = JSONConverter<ArrayList<FormData>>();
        Log.e(TAG, jsonConverter.objectToJson(list))
        mDfdId = dfdid
        if (mainMap == null) {
            mainMap = java.util.HashMap()
        }

        if (list.isNotEmpty())
            mainMap?.set(currentFormId!!, list)
        // replace a new fragment to the container
        if (dynamicActionConfig?.action == Type.FORM) {
            showLoading()
            //make the back button visible if user click on next form
            replaceFragment(DynamicFragment.getInstance(dynamicActionConfig.target!!, taskId!!, isEditable, tcfId, isHideButton,null,null, ArrayList()), dynamicActionConfig.target!!)
            hideLoading()
        } else if (dynamicActionConfig?.action == Type.API) {
            mainData = ArrayList()
            //add all the values of map to the hashMap and go to all
            // elements to get the required data
            for ((_, value) in mainMap!!) {
                mainData?.addAll(value)
            }
            //if form data hashMap is not empty then we get all the image file
            // and upload the data to the server and get the respective urls in string format.
            if (mainData?.isNotEmpty()!!) {
                val hashMapFileRequest = java.util.HashMap<String, ArrayList<File>>()
                for (i in mainData?.indices!!) {

                    val v = mainData!![i].file
                    if (v != null && v.isNotEmpty()) {
                        //remove last element only if type is camera else
                        // SIGNATURE OR FILE always contains single image
                        if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(AppConstants.ADD_MORE, ignoreCase = true)) {
                            v.removeAt(0)
                        }
                        if (mainData!![i].type == DataType.CAMERA && v.size>0&&v[v.size-1].path.equals(
                                AppConstants.ADD_MORE,
                                ignoreCase = true
                            )
                        ) {
                            //  Log.e("path",v[i].path)
                            v.removeAt(v.size-1)
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
                        }

                    }
                    Log.e("DynamicFormActivity", mainData!![i].name + "<------->"
                            + mainData!![i].enteredValue)
                }

                if (hashMapFileRequest.isNotEmpty()) {

//                    val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
//                    mDynamicFormViewModel.uploadFileList(hashMapFileRequest, httpManager, api,true)
                    if (NetworkUtils.isNetworkConnected(this@DynamicFormActivity)) {
                        if (NetworkUtils.isConnectedFast(this@DynamicFormActivity)) {
//                            showLoading()
                            count = 0
//                            mActivityCreateTaskBinding.btnCLick.visibility=View.GONE
                            mActivityDynamicFormSdkBinding!!.viewProgress.visibility=View.VISIBLE
                            CommonUtils.makeScreenDisable(this)
                            dynamicFragment!!.sendDataToServer(true)
                            fileUploadCounter=0
                            nestedScrollView.postDelayed({ nestedScrollView.fullScroll(View.FOCUS_DOWN) },200)
                            val thread = HandlerThread("workkker")
                            thread.start()
                            //start a handler
                            mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                            handlerThread?.setRequestParams(mDynamicHandler!!, hashMapFileRequest, httpManager, null)
                            //start thread
                            try {

                                handlerThread?.start()
                            } catch (e: IllegalThreadStateException) {

                            }
                        } else {
                            CommonUtils.showSnakbarForNetworkSettings(this@DynamicFormActivity, container, AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE)
                        }

                    } else {
                        //TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                    }

                    //uplode task
                } else {
                    finalApiHit()
                }
            }
        } else if (dynamicActionConfig?.action == Type.DISPOSE) {

            mainData = ArrayList()
            //add all the values of map to the hashMap and go to all
            // elements to get the required data
            for ((_, value) in mainMap!!) {
                mainData?.addAll(value)
            }
            //if form data hashMap is not empty then we get all the image file
            // and upload the data to the server and get the respective urls in string format.
            if (mainData?.isNotEmpty()!!) {
                val hashMapFileRequest = java.util.HashMap<String, ArrayList<File>>()
                for (i in mainData?.indices!!) {

                    val v = mainData!![i].file
                    if (v != null && v.isNotEmpty()) {
                        //remove last element only if type is camera else
                        // SIGNATURE OR FILE always contains single image
                        if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(AppConstants.ADD_MORE, ignoreCase = true)) {
                            // Log.e("path",v[i].path)
                            v.removeAt(0)
                        }
                        if (mainData!![i].type == DataType.CAMERA &&v.size>0&& v[v.size-1].path.equals(
                                AppConstants.ADD_MORE,
                                ignoreCase = true
                            )
                        ) {
                            //  Log.e("path",v[i].path)
                            v.removeAt(v.size-1)
                        }
                        if (v.isNotEmpty()) {
                            val key = mainData!![i].name!!
                            hashMapFileRequest[key] = v
                        }
                    }
                    Log.e("DynamicFormActivity", mainData!![i].name + "<------->"
                            + mainData!![i].enteredValue)
                }

                if (hashMapFileRequest.isNotEmpty()) {


//                    val api = TrackiApplication.getApiMap()[ApiType.UPLOAD_FILE]
//                    mDynamicFormViewModel.uploadFileList(hashMapFileRequest, httpManager, api,false)
                    if (NetworkUtils.isNetworkConnected(this@DynamicFormActivity)) {
                        if (NetworkUtils.isConnectedFast(this@DynamicFormActivity)) {
                            // showLoading()
                            mActivityDynamicFormSdkBinding!!.viewProgress.visibility=View.VISIBLE
                            CommonUtils.makeScreenDisable(this)
                            dynamicFragment!!.sendDataToServer(true)
                            fileUploadCounter=0
                            nestedScrollView.postDelayed({ nestedScrollView.fullScroll(View.FOCUS_DOWN) },200)
                            count = 0
                            val thread = HandlerThread("workkker")
                            thread.start()
                            //start a handler
                            mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                            handlerThread?.setRequestParams(mDynamicHandler!!, hashMapFileRequest, httpManager, null)
                            //start thread
                            try {
                                handlerThread?.start()
                            } catch (e: IllegalThreadStateException) {

                            }
                        } else {
                            CommonUtils.showSnakbarForNetworkSettings(this@DynamicFormActivity, container, AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE)
                        }

                    } else {
                        // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                    }

                } else {
                    perFormCreateTask()
                }
            }

        }
    }

    private fun perFormCreateTask() {
        var dynamicFormMainData = CommonUtils.createFormData(mainData, ctaId, taskId!!, dynamicFormsNew!!.formId,
            dynamicFormsNew!!.version)
        Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")
        var apiType = ApiType.CREATE_TASK
        var api = TrackiSdkApplication.getApiMap()[apiType]
        var taskData = dynamicFormMainData!!.taskData
        val intent = Intent()
        intent.putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, taskAction)
        intent.putExtra(AppConstants.DFID, formId)
        if (!isEditable)
            intent.putExtra(AppConstants.DFDID, mDfdId)
        intent.putExtra(AppConstants.Extra.EXTRA_FORM_MAP, dynamicFormMainData)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    private fun finalApiHit() {
        showLoading()
        var dynamicFormMainData = CommonUtils.createFormData(mainData, ctaId, taskId!!, dynamicFormsNew!!.formId,
            dynamicFormsNew!!.version)
        Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")
        var jsonConverter = JSONConverter<DynamicFormMainData>()
        var request = jsonConverter.objectToJson(dynamicFormMainData);
        CommonUtils.showLogMessage("e", "data", request)

        val api = TrackiSdkApplication.getApiMap()[ApiType.UPDATE_TASK_DATA]
        mDynamicFormViewModel.uploadTaskData(dynamicFormMainData, httpManager, api)
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@DynamicFormActivity)) {

            var resp: UploadTaskDataResponse = Gson().fromJson(result.toString(), UploadTaskDataResponse::class.java)

            var dfdId: String = resp.id!!

            val intent = Intent()
            intent.putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, taskAction)
            if (isEditable) {
                intent.putExtra(AppConstants.DFDID, dfdId)
            } else {
                intent.putExtra(AppConstants.DFDID, mDfdId)
            }
            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                preferencesHelper.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            var dynamicFormMainData = CommonUtils.createFormData(mainData, ctaId, taskId!!, dynamicFormsNew!!.formId,
                dynamicFormsNew!!.version)
            intent.putExtra(AppConstants.DFID, formId)
            intent.putExtra(AppConstants.Extra.EXTRA_FORM_MAP, dynamicFormMainData)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            if (geofenceData != null) {
                dBHelper.updateSyncStatus(0, geofenceData?.geofenceId, geofenceData?.id!!, geofenceData?.trackingId)
            }
        }

    }

    override fun upLoadFileApiResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this@DynamicFormActivity)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> = java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        if (formData.value != null && formData.value!!.isNotEmpty()) {
                            var urlList: List<String> = formData.value!!.split("\\s*,\\s*")
                            if (urlList.isNotEmpty()) {
                                formData.enteredValue = formData.value + ", " + commaSeperatedString
                            } else {
                                formData.enteredValue = commaSeperatedString
                            }
                        } else {
                            formData.enteredValue = commaSeperatedString
                        }
                    }

                }
                finalApiHit()
            }
        } else {
            finalApiHit()
        }

    }

    override fun upLoadFileDisposeApiResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this@DynamicFormActivity)) {
            val fileUrlsResponse = Gson().fromJson(result.toString(), FileUrlsResponse::class.java)
            val fileResponseMap = fileUrlsResponse.filesUrl
            if (mainData != null) {
                for (i in mainData!!.indices) {
                    var formData = mainData!![i]
                    if (fileResponseMap!!.containsKey(formData.name)) {
                        var fileUrlList: java.util.ArrayList<String> = java.util.ArrayList<String>()
                        fileUrlList = fileResponseMap[formData.name]!!
                        val commaSeperatedString = fileUrlList.joinToString { it -> it }
                        if (formData.value != null && formData.value!!.isNotEmpty()) {
                            var urlList: List<String> = formData.value!!.split("\\s*,\\s*")
                            if (urlList.isNotEmpty()) {
                                formData.enteredValue = formData.value + ", " + commaSeperatedString
                            } else {
                                formData.enteredValue = commaSeperatedString
                            }
                        } else {
                            formData.enteredValue = commaSeperatedString
                        }
                    }

                }
                perFormCreateTask()
            }
        } else {
            perFormCreateTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainMap = null
    }

    companion object {
        const val TAG = "DynamicFormActivity"
        fun newIntent(context: Context) = Intent(context, DynamicFormActivity::class.java)
    }

    var count = 0
    var fileUploadCounter=0
    inner class DynamicHandler(looper: Looper, var actionConfig: DynamicActionConfig) : Handler(looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                2 -> {
                    var obj = message.obj as HandlerObject
                    fileUploadCounter += obj.chunkSize
                    var progressUploadText = "${fileUploadCounter}/${obj.totalSize}"
                    var percentage = ((fileUploadCounter * 100 / obj.totalSize))
                    Log.e(TAG, "progressUploadText=> " + progressUploadText)
                    Log.e(TAG, "percentage=> " + percentage.toString())
                    runOnUiThread {
                        progressBar!!.progress = percentage
                        percentageText!!.text = "$percentage %"
                        currentStatusText!!.text = progressUploadText

                    }


                }
                /*For Success */0 -> {
                if (CommonUtils.stringListHashMap.isNotEmpty()) {
                    runOnUiThread {
                        rlProgress!!.visibility=View.GONE
                        rlSubmittingData!!.visibility=View.VISIBLE

                    }
                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    if (mainData?.isNotEmpty()!!) {
                        val finalMap = java.util.HashMap<String, String>()
                        for (i in mainData?.indices!!) {
                            try {
                                if (mainData!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(mainData!![i].name)!!) {
                                        //Log.e("Upload Form List--->", mainData!![i].name!!)
                                        mainData!![i].enteredValue = CommonUtils.getCommaSeparatedList(CommonUtils.stringListHashMap[mainData!![i].name])
                                    }
                                    finalMap[mainData!![i].name!!] = mainData!![i].enteredValue!!
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
                    hideLoading()
                    try {
                        handlerThread?.interrupt()
                    } catch (e: InterruptedException) {
                    }


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
                    count++
                    runOnUiThread {
                        mActivityDynamicFormSdkBinding!!.viewProgress.visibility = View.GONE
                        CommonUtils.makeScreenClickable(this@DynamicFormActivity)
                        dynamicFragment!!.sendDataToServer(false)
                    }

                    /*if (CommonUtils.errorString != null) {
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

                    }*/
                    // This is where you do your work in the UI thread.
                    // Your worker tells you in the message what to do.
                    TrackiToast.Message.showShort(this@DynamicFormActivity,AppConstants.UNABLE_TO_PROCESS_REQUEST)
                    //after getting error form the thread we interrupt the thread
                    hideLoading()
                    try {
                        handlerThread?.interrupt()
                    } catch (e: InterruptedException) {
                    }


                }
            }
            }
        }
    }
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityDynamicFormSdkBinding!!.llMain, getString(R.string.please_check_your_internet_connection))
    }

}
