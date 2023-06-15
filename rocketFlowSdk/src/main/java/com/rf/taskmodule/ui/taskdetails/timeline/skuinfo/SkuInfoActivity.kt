package com.rf.taskmodule.ui.taskdetails.timeline.skuinfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rocketflow.sdk.RocketFlyer
//import com.iceteck.silicompressorr.SiliCompressor
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.TrackiSdkApplication
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.request.*
import com.rf.taskmodule.data.model.request.TaskData
import com.rf.taskmodule.data.model.response.config.*
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivitySkuInfoSdkBinding
import com.rf.taskmodule.databinding.ItemDynamicFormVideoSdkBinding
import com.rf.taskmodule.ui.addplace.HubLocation
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.common.DoubleButtonDialog
import com.rf.taskmodule.ui.common.OnClickListener
import com.rf.taskmodule.ui.custom.ExecutorThread
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicAdapter
import com.rf.taskmodule.ui.dynamicform.dynamicfragment.DynamicFragment
//import com.rf.taskmodule.ui.main.MainActivity
//import com.rf.taskmodule.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.rf.taskmodule.utils.*
import com.rf.taskmodule.utils.image_utility.Compressor
import com.rf.taskmodule.utils.image_utility.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_create_task_sdk.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class SkuInfoActivity : BaseSdkActivity<ActivitySkuInfoSdkBinding, SkuInfoViewModel>(), SkuInfoNavigator,
    DynamicAdapter.AdapterListener {

    private var imageFilePath: String? = null
    private var categoryId: String? = null
    private var target: String? = null
    private var flavourId: String? = null
    private var dynamicPricing: Boolean = false
    private var ctaId: String? = null

    var positions = -1

    private var permissionArray =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var formData: FormData
    private var position = -1
    private var vidViewposition = -1
    lateinit var formDataList: ArrayList<FormData>
    private var actualImage: File? = null
    private var compressedImage: File? = null
    private var formId: String? = null
    private var taskId: String? = null
    private var isEditable: Boolean? = true
    private var isHideButton: Boolean? = true
    private var videoFilePath: String? = null
    private var mBinding: ItemDynamicFormVideoSdkBinding? = null
    var dfdId: String = "";
    var video: File? = null
    var videoPath: String? = null
    private var isOtpApiHit = false
    var vidUri: Uri? = null

    var mainData: ArrayList<FormData>? = null

    var titleText: TextView? = null
    var percentageText: TextView? = null
    var currentStatusText: TextView? = null
    var progressBar: ProgressBar? = null
    var rlProgress: RelativeLayout? = null
    var rlSubmittingData: RelativeLayout? = null

    private var mainMap: HashMap<String, ArrayList<FormData>>? = null
    private var responseSkuInfo: HashMap<String, List<TaskInfoData>?>? = null
    var getTaskInfoResponse: GetTaskInfoResponse ? = null

    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null


    private val AUTOCOMPLETE_REQUEST_CODE = 28487

    lateinit var mSkuInfoViewModel: SkuInfoViewModel

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var httpManager: HttpManager

    lateinit var binding: ActivitySkuInfoSdkBinding

    lateinit var rvDynamicForm: RecyclerView


    lateinit var showDynamicFormDataAdapter: DynamicAdapter

    companion object {
        private val TAG = SkuInfoActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, SkuInfoActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mSkuInfoViewModel.navigator = this

        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        setToolbar(binding.toolbar, "Sub Task CTA")
        handlerThread = ExecutorThread()

        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap: Map<String, String>? = null
            val str: String = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)!!
            categoryMap = Gson().fromJson(
                str,
                object : TypeToken<HashMap<String?, String?>?>() {}.type
            )
            if (categoryMap != null && categoryMap!!.containsKey("categoryId")) {
                categoryId = categoryMap!!.get("categoryId")
                CommonUtils.showLogMessage("d", "categoryId", categoryId)
            }
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
            taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
            formId =taskId
            CommonUtils.showLogMessage("d", "taskId", taskId)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)) {
            target = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)
            CommonUtils.showLogMessage("d", "target", target)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)) {
            flavourId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)
            CommonUtils.showLogMessage("d", "flavourId", flavourId)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING)) {
            dynamicPricing =
                intent.getBooleanExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, false)
            CommonUtils.showLogMessage("d", "dynamicPricing", dynamicPricing.toString())
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID))
            ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)

        var viewProgress = binding.viewProgressNew
        titleText = viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText = viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText = viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar = viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData = viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress = viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)

        showDynamicFormDataAdapter = DynamicAdapter(ArrayList())
        showDynamicFormDataAdapter.setAdapterListener(this)
        showDynamicFormDataAdapter.setIsEditable(true,httpManager)
        rvDynamicForm = binding.rvDynamicFormsMini
        rvDynamicForm.layoutManager = LinearLayoutManager(this)
        rvDynamicForm.adapter = showDynamicFormDataAdapter

        getTaskData()
    }

    private fun getTaskData() {
        showLoading()
            mSkuInfoViewModel.getTaskData(httpManager, taskId)
        hideLoading()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_sku_info_sdk
    }

    override fun getViewModel(): SkuInfoViewModel {
        val factory = RocketFlyer.dataManager()?.let { SkuInfoViewModel.Factory(it) } // Factory
        if (factory != null) {
            mSkuInfoViewModel = ViewModelProvider(this, factory)[SkuInfoViewModel::class.java]
        }
        return mSkuInfoViewModel!!
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            Log.d("result  = ",result.toString());
            val jsonConverter: JSONConverter<SkuResponse> =
                JSONConverter()
            val response = jsonConverter.jsonToObject(result.toString(), SkuResponse::class.java)
            if (response != null) {
                if (response.successful == true) {
                    showLoading()
                    val api = TrackiSdkApplication.getApiMap()[ApiType.EXECUTE_UPDATE]
                    val request = ExecuteUpdateRequest(
                        taskId!!, ctaId!!,
                        DateTimeUtil.getCurrentDateInMillis(), TaskData()
                    )
                    mSkuInfoViewModel.executeUpdates(httpManager, request, api!!)
                } else {
                    TrackiToast.Message.showShort(this, "Something went wrong, Please Try Again.")
                }
            }
            else{
                TrackiToast.Message.showShort(this, "Something went wrong, Please Try Later.")
            }
        }
    }

    override fun handleExecutiveMapResponse(
        position: Int, callback: ApiCallback, result: Any?,
        error: APIError?
    ) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val executive =
                Gson().fromJson<ExecutiveMap>(result.toString(), ExecutiveMap::class.java)
            executive?.let {
                if (formDataList.isNotEmpty()) {
                    val formData = formDataList[position]
                    formData.apiMap = it.data
                    formDataList[position] = formData
                    showDynamicFormDataAdapter.setFormDataList(formDataList)
                    showDynamicFormDataAdapter.setIsEditable(isEditable, httpManager)
                    showDynamicFormDataAdapter.setIsHideButton(isHideButton)
                    showDynamicFormDataAdapter.setPreferencesHelper(preferencesHelper)
                    if (taskId != null)
                        showDynamicFormDataAdapter.setTaskId(taskId)
                }
            }
        }
    }

    override fun handleTaskInfoDataResponse(callback: ApiCallback, result: Any?, error: APIError?) {
       Log.d("result",result.toString())
       Log.d("error",error.toString())
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val getTaskInfoDataResponse = Gson().fromJson<GetTaskInfoResponse>(
                result.toString(),
                GetTaskInfoResponse::class.java
            )
            if (getTaskInfoDataResponse != null && getTaskInfoDataResponse.successful) {
                formDataList = ArrayList()
                responseSkuInfo = HashMap()
                getTaskInfoResponse = getTaskInfoDataResponse;
                getTaskInfoDataResponse.data!!.forEach { itemId ->
                    val formTitle = FormData()
                    formTitle.title = itemId.prodName
                    formTitle.type = DataType.LABEL
                    formDataList.add(formTitle)
                    for(i in 1..itemId.count!!) {
                        for (item in itemId.specs!!) {
                            val form = FormData()
                            form.type = item.type
                            form.formItemKey = itemId.itemId
                            form.embeddedDfId = item.id
                            form.title = item.name
                            form.name = item.name
                            form.required = item.required!!
                            if(item.required!!){
                                form.errorMessage = "please enter required fields !" + item.name
                            }
                            formDataList.add(form)
                            responseSkuInfo!![itemId.itemId!!] = itemId.specs
                        }
                     }
                }

                val form = FormData()
                form.type = DataType.BUTTON
                form.title =  "Submit"
                form.name = "Submit"
                formDataList.add(form)
                showDynamicFormDataAdapter.setFormDataList(formDataList)
                showDynamicFormDataAdapter.isSubmitButtonEnable.value = true
            }
        }
    }

    override fun handleUploadFileResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {

            if (!isOtpApiHit) {
                val fileUrlsResponse =
                    Gson().fromJson(result.toString(), FileUrlsResponse::class.java)

            } else {
                val baseResponse = Gson().fromJson(result.toString(), OtpResponse::class.java)
                if (baseResponse.successful) {
                    showDynamicFormDataAdapter.isSubmitButtonEnable.value = true
                } else {
                    baseResponse.responseMsg?.let {
                        TrackiToast.Message.showShort(this, it)
                    }

                }

            }
        }
    }

    override fun handleExecuteUpdateResponse(
        apiCallback: ApiCallback?,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(apiCallback, error, result, this)) {
            openMainActivity()
        }
    }

    fun openMainActivity() {
        val intent = Intent()
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onUploadPic(position: Int, formData: FormData?) {
        this.position = position
        this.formData = formData!!
        onPickImage()
    }

    private fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    DynamicFragment.REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(chooseImageIntent, DynamicFragment.PICK_IMAGE_FILE_ID)
    }

    @SuppressLint("CheckResult")
    fun compressImage() {
        if (actualImage == null) {
            TrackiToast.Message.showShort(this, "No image is selected")
        } else {
            Compressor(this)
                .setQuality(90)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    compressedImage = file

                    val spilt = compressedImage?.absolutePath?.split("/".toRegex())?.toTypedArray()
                    Log.e(DynamicFragment.TAG, "image mane is: ${spilt!![spilt.size - 1]}")
                    if (compressedImage != null) {
                        try {
                            Log.e(DynamicFragment.TAG, "MB: ${compressedImage!!.sizeInMb}")
                            Log.e(DynamicFragment.TAG, "KB: ${compressedImage!!.sizeInKb}")
                        } catch (e: Exception) {

                        }
                    }

                    //set the image name into the adapter
                    //formData.enteredValue = spilt[spilt.size - 1]
                    formData.enteredValue = compressedImage?.path;
                    Log.e(DynamicFragment.TAG, "path: ${compressedImage?.path}")
                    Log.e(DynamicFragment.TAG, "abspath: ${compressedImage?.absolutePath}")
                    val fileList: ArrayList<File>? = ArrayList()
                    fileList?.add(compressedImage!!)

                    formData.file = fileList
                    //notify adapter
                    showDynamicFormDataAdapter.setImage(position, formData)
                }, { throwable ->
                    throwable.printStackTrace()
                    TrackiToast.Message.showShort(this, throwable.message!!)
                })
        }
    }

    var uri: Uri? = null
    private fun openCamera() {
        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (photoIntent.resolveActivity(this.packageManager) != null) {
            val file: File
            try {
                file = createImageFile()
                if (file != null && file.exists()) {
                    uri = FileProvider.getUriForFile(
                        this.applicationContext,
                        applicationContext.packageName + ".provider", file
                    )
                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(photoIntent, DynamicFragment.CAMERA_PIC_REQUEST)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var image: File? = null
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        // Save a file: path for use with ACTION_VIEW intents
        imageFilePath = "file:" + image?.absolutePath
        return image!!
    }

    override fun uploadCameraImage(adapterPosition: Int) {
        this.positions = adapterPosition
        if (hasPermission(permissionArray))
            openCamera()
    }

    override fun onProcessClick(formData: FormData?) {
        hideKeyboard()
        //get hashMap from adapter
        val fieldList = showDynamicFormDataAdapter.formDataList
        Log.d("TAG",fieldList.size.toString())
        //traverse hashMap and check if required filed is empty then show error
        for (formData: FormData in fieldList!!) {
            if (formData.required) {
                when (formData.type) {
                    DataType.DATE_RANGE -> {
                        if (formData.maxRange == 0L || formData.minRange == 0L) {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.TIME, DataType.DATE -> {
                        if (formData.maxRange == 0L) {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.CONDITIONAL_DROPDOWN_STATIC -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.CONDITIONAL_DROPDOWN_API -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                    DataType.DROPDOWN_API -> {
                        if (formData.formItemKey == null || formData.formItemKey == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                    else -> {
                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            return
                        }
                    }
                }
            }
        }


        outer@ for (formData in fieldList) {
            if (formData.widgetData != null) {
                inner@ for (j in formData.widgetData!!.indices) {
                    val llFiledList = formData.widgetData!![j].formDataList
                    if (llFiledList != null) {
                        if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty()) {
                            var map = HashMap<String, ArrayList<FormData>>()
                            map[formData.widgetData!![j].target!!] =
                                formData.widgetData!![j].formDataList!! as ArrayList<FormData>
                            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                                val hmIterator: Iterator<*> =
                                    preferencesHelper.formDataMap.entries.iterator()
                                while (hmIterator.hasNext()) {
                                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                                    map[mapElement.key.toString()] =
                                        mapElement.value as ArrayList<FormData>
                                    CommonUtils.showLogMessage(
                                        "e",
                                        "inner map value",
                                        mapElement.value.toString()
                                    )
                                }
                            }
                            preferencesHelper.formDataMap = map
                            var jsonConverter =
                                JSONConverter<HashMap<String, ArrayList<FormData>>>()
                            var data = jsonConverter.objectToJson(map)
                            CommonUtils.showLogMessage(
                                "e",
                                "inner form data value",
                                data.toString()
                            )
                            if (checkValidation(llFiledList as ArrayList<FormData>)) {
                                return
                            }
                        }
                    }
                }
            }
        }

        if (mainMap == null) {
            mainMap = HashMap()
        }

        //submit data to activity
            var message: String = "Are you sure you want to perform ? "
            val dialog = DoubleButtonDialog(this,
                true,
                null,
                message,
                getString(R.string.yes),
                getString(R.string.no),
                object : OnClickListener {
                    override fun onClickCancel() {}
                    override fun onClick() {
                        findImages(fieldList)
                    }
                })
            dialog.show()
    }

    private fun findImages(fieldList: ArrayList<FormData>) {
        if (fieldList.isNotEmpty()) {
            mainMap?.set(formId!!, fieldList)
            val jsonConverter =
                JSONConverter<HashMap<String, ArrayList<FormData>>>()
            val data = jsonConverter.objectToJson(mainMap!!)
            CommonUtils.showLogMessage("e", "allowed field", data.toString())
        }
        mainData = ArrayList()
        for ((_, value) in mainMap!!) {
            mainData?.addAll(value)
        }
        if (mainData?.isNotEmpty()!!) {
            val hashMapFileRequest = HashMap<String, ArrayList<File>>()
            for (i in mainData?.indices!!) {
                val v = mainData!![i].file
                if (v != null && v.isNotEmpty()) {
                    if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                            AppConstants.ADD_MORE,
                            ignoreCase = true
                        )
                    ) {
                        v.removeAt(0)
                    }
                    if (mainData!![i].type == DataType.CAMERA && v.size>0&&v[v.size-1].path.equals(
                            AppConstants.ADD_MORE,
                            ignoreCase = true
                        )
                    ) {
                        v.removeAt(v.size-1)
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
                        val key = mainData!![i].uniqueID!!
                        hashMapFileRequest[key] = v
                    }
                }
                Log.e("NewCreateTaskActivity", mainData!![i].uniqueID + "<------->" + mainData!![i].enteredValue)
            }
            val jsonConverter =
                JSONConverter<HashMap<String, ArrayList<File>>>();
            Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(hashMapFileRequest))
            Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
            if (hashMapFileRequest.isNotEmpty()) {
                if (NetworkUtils.isNetworkConnected(this)) {
                    if (NetworkUtils.isConnectedFast(this)) {
                        binding.viewProgressNew.visibility = View.VISIBLE
                        CommonUtils.makeScreenDisable(this)
                        fileUploadCounter = 0
                        nestedScrollView.postDelayed(
                            { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                            200
                        )
                        count = 0
                        val thread = HandlerThread("workkker")
                        thread.start()
                        mDynamicHandler = DynamicHandler(thread.looper)
                        handlerThread?.setRequestParams(
                            mDynamicHandler!!,
                            hashMapFileRequest,
                            httpManager,
                            null
                        )
                        handlerThread?.start()
                    } else {
                        CommonUtils.showSnakbarForNetworkSettings(
                            this,
                            nestedScrollView,
                            AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                        )
                    }
                } else {
                    TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                }
            } else {
                finalApiHit(mainData!!)
            }
        }
    }

    private fun finalApiHit(mainData: ArrayList<FormData>) {
        val dataToUpload : HashMap<String,ArrayList<FormData>>  = HashMap()
        responseSkuInfo!!.forEach { item ->
            mainData.forEach {
                formItem ->
                   if(formItem.formItemKey == item.key){
                       val oldArray : ArrayList<FormData> = if (dataToUpload.containsKey(formItem.formItemKey)){
                           dataToUpload[formItem.formItemKey]!!
                       }else{
                           ArrayList();
                       }
                       oldArray.add(formItem)
                       dataToUpload[formItem.formItemKey!!] = oldArray
                   }
            }
        }
        val jsonConverterDataToUpload =
            JSONConverter<HashMap<String, ArrayList<FormData>>>()
        val dataUpload = jsonConverterDataToUpload.objectToJson(dataToUpload)
        Log.e("Final Data Filtered--->", dataUpload)

       val data = ArrayList<ProductItem>()
        getTaskInfoResponse?.data?.forEach { item ->
            val productItem = ProductItem()
            productItem.itemId = item.itemId
            productItem.prodId = item.prodId
            productItem.catId = item.catId
            productItem.flavorId = item.flavorId
            productItem.refId = item.refId
            val units: ArrayList<UnitItem> = ArrayList()

            val specs : ArrayList<FormData> = dataToUpload[item.itemId]!!

            val count = item.count
            var i = item.specs!!.size
            var old = 0
            if (i != null) {
                while (i <= item.specs?.size?.times(item.count!!)!!){
                    Log.d("Size Total ",item.specs?.size?.times(item.count!!)!!.toString())
                    Log.d("specs size old ",old.toString())
                    Log.d("specs size i ",i.toString())
                    val specification: ArrayList<SkuSpecification> = ArrayList()
                    for(it in old until i){
                        val skuSpecification = SkuSpecification()
                        skuSpecification.specId = specs.get(it).embeddedDfId
                        skuSpecification.name = specs.get(it).name
                        if (specs.get(it).type == DataType.FILE){
                            skuSpecification.value = specs.get(it).enteredValue
                        }else {
                            skuSpecification.value = specs.get(it).value
                        }
                        Log.d("skuSpecification", skuSpecification.name)
                        specification.add(skuSpecification)
                    }
                    val unitItem = UnitItem()
                    unitItem.specifications = specification
                    Log.d("specifications", unitItem.specifications!!.size.toString())
                    units.add(unitItem)
                    old = i
                    i += count!!
                }
            }

            productItem.units = units
            data.add(productItem)
        }

        val skuSpecs = SKUInfoSpecsRequest()
        skuSpecs.data = data

        val skuSpecsJson =
            JSONConverter<SKUInfoSpecsRequest>()
        val skuSpecsData = skuSpecsJson.objectToJson(skuSpecs)
        Log.e("Final Data skuSpecsData--->", skuSpecsData)
        if (NetworkUtils.isNetworkConnected(this)) {
            showLoading()
            val api = TrackiSdkApplication.getApiMap()[ApiType.SAVE_TASK_UNITS]
            mSkuInfoViewModel.uploadSkuInfoData(skuSpecs, httpManager, api)
        } else {
            hideLoading()
            TrackiToast.Message.showShort(
                this,
                getString(R.string.no_internet)
            )
        }
    }

    private fun checkValidation(fieldList: ArrayList<FormData>): Boolean {
        var isTrue = false
        outer@ for (formData in fieldList) {
            if (formData.widgetData != null) {
                inner@ for (j in formData.widgetData!!.indices) {
                    val llFiledList = formData.widgetData!![j].formDataList
                    if (llFiledList != null) {
                        if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty()) {
                            var map = HashMap<String, ArrayList<FormData>>()
                            map[formData.widgetData!![j].target!!] =
                                formData.widgetData!![j].formDataList!! as ArrayList<FormData>
                            if (preferencesHelper.formDataMap != null && preferencesHelper.formDataMap.isNotEmpty()) {
                                val hmIterator: Iterator<*> =
                                    preferencesHelper.formDataMap.entries.iterator()
                                while (hmIterator.hasNext()) {
                                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                                    map[mapElement.key.toString()] =
                                        mapElement.value as ArrayList<FormData>
                                    CommonUtils.showLogMessage(
                                        "e",
                                        "inner map value",
                                        mapElement.value.toString()
                                    )
                                }
                            }
                            preferencesHelper.formDataMap = map
                            var jsonConverter =
                                JSONConverter<HashMap<String, ArrayList<FormData>>>()
                            var data = jsonConverter.objectToJson(map)
                            CommonUtils.showLogMessage(
                                "e",
                                "inner form data value",
                                data.toString()
                            )

                        }

                    }


                }
            }
        }

        user@ for (formData: FormData in fieldList!!) {
            if (formData.required) {

                when (formData.type) {
                    DataType.DATE_RANGE -> {
                        if (formData.maxRange == 0L || formData.minRange == 0L) {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        }

                    }
                    DataType.TIME, DataType.DATE -> {
                        if (formData.maxRange == 0L) {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        }

                    }
                    DataType.CONDITIONAL_DROPDOWN_STATIC -> {


                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }


                    }
                    DataType.CONDITIONAL_DROPDOWN_API -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }

                    }
                    DataType.DROPDOWN_API -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].formDataList != null)
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                            }
                        }

                    }
                    else -> {

                        val enteredValue = formData.enteredValue
                        if (enteredValue == null || enteredValue == "") {
                            TrackiToast.Message.showShort(this, formData.errorMessage!!)
                            isTrue = true
                            break@user
                        } else {
                            isTrue = false
                        }
                        if (formData.widgetData != null) {
                            for (j in formData.widgetData!!.indices) {
                                if (formData.widgetData!![j].target != null && formData.widgetData!![j].target!!.isNotEmpty() && formData.widgetData!![j].formDataList != null) {
                                    isTrue =
                                        checkValidation(formData.widgetData!![j].formDataList as ArrayList<FormData>)
                                }
                            }
                        }


                    }
                }

            }

        }
        return isTrue

    }

    override fun getDropdownItems(position: Int, target: String, rollId: String?) {
        if (CommonUtils.containsEnum(target))
            mSkuInfoViewModel.executiveMap(position, target, httpManager, rollId)
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val videoFileName =  timeStamp + "_"
        val videoFileName = "videoFile"
        video = File.createTempFile(videoFileName, ".mp4", externalCacheDir)
        // Save a file: path for use with ACTION_VIEW intents
        videoFilePath = "file:" + video!!.getAbsolutePath()
        return video
    }

    override fun openVidCamera(pos: Int, mBinding: ItemDynamicFormVideoSdkBinding, maxlength: Int) {

        this.vidViewposition = pos;
        this.mBinding = mBinding;

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

        if (intent.resolveActivity(this.packageManager) != null) {
            val file: File?
            try {
                file = createVideoFile()


                if (file != null && file.exists()) {
                    vidUri = FileProvider.getUriForFile(
                        this.applicationContext,
                        applicationContext.packageName + ".provider", file
                    )
                    //                    uri = Uri.parse(new File(imageFilePath).toString());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, vidUri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            CameraCharacteristics.LENS_FACING_BACK
                        ) // Tested on API 24 Android version 7.0(Samsung S6)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            CameraCharacteristics.LENS_FACING_BACK
                        ) // Tested on API 27 Android version 8.0(Nexus 6P)
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                    } else {
                        intent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            1
                        ) // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    intent.putExtra("TEST", "String Extra")
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxlength)
                    startActivityForResult(intent, DynamicFragment.VIDEO_REQUEST)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onVeriFyOtpButtonClick(formData: FormData?, mobile: String?) {
        isOtpApiHit = true
        hideKeyboard()
        showLoading()
        mSkuInfoViewModel!!.verifyOtpServerRequest(
            OtpRequest(mobile!!, formData!!.enteredValue!!),
            httpManager
        )

    }

    override fun openPlacePicker(position: Int, formData: FormData?) {
        try {
            this.position = position
            // this.formData=formData!!
            val fields: List<Place.Field> =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent: Intent =
                Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        } catch (e: GooglePlayServicesRepairableException) {
            Log.e(DynamicFragment.TAG, e.message)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.e(DynamicFragment.TAG, e.message)
        }
    }

    override fun openScanner(position: Int, formData: FormData?) {
//        this.position = position
//        var scanActivity = Intent(this!!, ScanQrAndBarCodeActivity::class.java)
//        scanActivity.putExtra("from","taskdetails")
//        startActivityForResult(scanActivity, AppConstants.REQUEST_CODE_SCAN)
    }

    override fun sendButtonInstance(button: Button?, isEditable: Boolean) {
    }

    fun setVid(pos: Int, file: File, path: String) {
        showDynamicFormDataAdapter.ItemDynamicFormVideoSdkBinding = mBinding
        showDynamicFormDataAdapter.setVid(pos, file!!, path)
    }

    fun setChildVideo(pos: Int, file: File, path: String) {
        if (showDynamicFormDataAdapter.childDynamicAdapter != null) {
            showDynamicFormDataAdapter.childDynamicAdapter.ItemDynamicFormVideoSdkBinding = mBinding
            showDynamicFormDataAdapter.childDynamicAdapter.setVid(pos, file!!, path)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DynamicFragment.PICK_IMAGE_FILE_ID -> {
                actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data)
                compressImage()
            }
            DynamicFragment.CAMERA_PIC_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                        val bm = MediaStore.Images.Media.getBitmap(baseActivity.contentResolver, Uri.parse(imageFilePath))

                        val file = File(image?.path)

                        if (file.exists()) {
                            //  imageView.setImageURI(image.path?)
//                            var bm = BitmapFactory.decodeFile(imageFilePath)
                            val bm = ImagePicker.getImageResized(this, uri!!)
//                            val rotation = ImagePicker.getRotation(context, uri, true)
//                            bm = ImagePicker.rotate(bm, rotation)
                            // Code to manage the bitmap
                            actualImage = CommonUtils.convertBitmapToFile(
                                this, bm,
                                "upload_" + Calendar.getInstance().timeInMillis + ".jpg"
                            )


                            if (actualImage != null) {

                                Compressor(
                                    this
                                )
                                    .setQuality(90)
                                    .compressToFileAsFlowable(actualImage)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ fi ->
                                        compressedImage = fi

                                        showDynamicFormDataAdapter.setImg(positions, compressedImage!!)
                                    }, { throwable ->
                                        throwable.printStackTrace()
                                        TrackiToast.Message.showShort(
                                            this,
                                            throwable.message!!
                                        )
                                    })
                            } else {
                                Log.e(DynamicFragment.TAG, "Image is not captured")
                            }
                        } else {
                            Log.e(DynamicFragment.TAG, "Image is not captured")
                        }


                    } catch (c: Exception) {
                        c.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User Cancelled the action
                    TrackiToast.Message.showShort(this, "User Cancelled the action")
                }
            }
            DynamicFragment.VIDEO_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
//                        val bm = MediaStore.Images.Media.getBitmap(baseActivity.contentResolver, Uri.parse(imageFilePath))

                        val file = File(video?.path)
                        videoPath = video?.path
                        if (file.exists()) {
                            var thread = Thread(Runnable {

                                if (showDynamicFormDataAdapter.formDataList[vidViewposition] != null && showDynamicFormDataAdapter.formDataList[vidViewposition].type == DataType.VIDEO) {
//                                    var filePath = SiliCompressor.with(this)
//                                        .compressVideo(file.path, file.parent)
                                    var filePath = file.path

                                    showDynamicFormDataAdapter.formDataList[vidViewposition].enteredValue = filePath
                                    if (file.exists()) {
                                        if (file.delete()) {
                                            Log.e("file Deleted :", file!!.path!!)
                                        } else {
                                            Log.e("file not Deleted :", file!!.path!!.toString())
                                        }
                                    }
                                    runOnUiThread(Runnable {
                                        setVid(vidViewposition, video!!, filePath!!)
                                        //  adapter.notifyDataSetChanged()
                                    })
                                    if (video != null && video!!.exists()) {
                                        video!!.delete()
                                    }
                                }

                                for (formData in showDynamicFormDataAdapter.formDataList) {
                                    if (formData.widgetData != null) {
                                        for (j in formData.widgetData!!.indices) {
                                            val llFormData = formData.widgetData!![j].formDataList
                                            if (llFormData != null) {
                                                for (data in llFormData) {
                                                    if (data.type == DataType.VIDEO) {
                                                        var filePath = file.path
//                                                            SiliCompressor.with(this)
//                                                                .compressVideo(
//                                                                    file.path,
//                                                                    file.parent
//                                                                )
                                                        // data.enteredValue=file.path
                                                        data.enteredValue = filePath
                                                        if (file.exists()) {
                                                            if (file.delete()) {
                                                                Log.e("file Deleted :", file!!.path)
                                                            } else {
                                                                Log.e(
                                                                    "file not Deleted :",
                                                                    file!!.path
                                                                )
                                                            }
                                                        }
                                                        runOnUiThread(Runnable {
                                                            setChildVideo(
                                                                vidViewposition,
                                                                video!!,
                                                                filePath!!
                                                            )
                                                            //  adapter.notifyDataSetChanged()
                                                        })
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                            thread.start()
                        }
                    } catch (c: Exception) {
                        c.printStackTrace()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User Cancelled the action
                    TrackiToast.Message.showShort(this, "User Cancelled the action")
                }
            }
            AUTOCOMPLETE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.i(DynamicFragment.TAG, "Place: " + place.name + ", " + place.id)
                    var latLong = place.latLng
                    var location =
                        com.rf.taskmodule.ui.addplace.Location(latLong!!.latitude, latLong.longitude)
                    location.locationId = place.id!!
                    var hubLocation = com.rf.taskmodule.ui.addplace.HubLocation(location, 0.0F)
                    hubLocation.address = place.name
                    var jsonConverter =
                        JSONConverter<HubLocation>()
                    //adapter.formDataList[position].enteredValue = jsonConverter.objectToJson(hubLocation)
                    showDynamicFormDataAdapter.formDataList[position].enteredValue = place.name
                    CommonUtils.showLogMessage(
                        "e",
                        "enterdvalue",
                        showDynamicFormDataAdapter.formDataList[position].enteredValue
                    )
                    showDynamicFormDataAdapter.formDataList[position].value =
                        showDynamicFormDataAdapter.formDataList[position].enteredValue
                    showDynamicFormDataAdapter.formDataList[position].value = place.name
                    showDynamicFormDataAdapter.notifyItemChanged(position)

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i(DynamicFragment.TAG, status.statusMessage)
                    TrackiToast.Message.showShort(this, status.statusMessage!!)

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    TrackiToast.Message.showShort(this, "operation cancelled.")
                }

            }
            AppConstants.REQUEST_CODE_SCAN->{
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra("id")) {
                        var id=data.getStringExtra("id")
                        showDynamicFormDataAdapter.formDataList[position].enteredValue = id
                        CommonUtils.showLogMessage(
                            "e",
                            "enterdvalue",
                            showDynamicFormDataAdapter.formDataList[position].enteredValue
                        )
                        showDynamicFormDataAdapter.formDataList[position].value = id
                        showDynamicFormDataAdapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    var count = 0
    var fileUploadCounter = 0

    inner class DynamicHandler(looper: Looper) :
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
                        for (i in mainData?.indices!!) {
                            try {
                                if (mainData!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(mainData!![i].uniqueID)!!) {
                                        Log.e("Upload Form List--->", mainData!![i].uniqueID!!)
                                        mainData!![i].enteredValue =
                                            CommonUtils.getCommaSeparatedList(
                                                CommonUtils.stringListHashMap[mainData!![i].uniqueID])
                                        Log.e("Upload Form List--->", mainData!![i].enteredValue!!)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //assign empty object to map again
                        CommonUtils.stringListHashMap = ConcurrentHashMap()
                        finalApiHit(mainData!!)
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                    handlerThread?.interrupt()
                    hideLoading()
                    CommonUtils.stringListHashMap = ConcurrentHashMap()
                        finalApiHit(mainData!!)
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    runOnUiThread {
                        binding.viewProgressNew.visibility = View.GONE
                        CommonUtils.makeScreenClickable(this@SkuInfoActivity)
                    }
                    count++
                    fileUploadCounter = 0
                    TrackiToast.Message.showShort(this@SkuInfoActivity,
                        AppConstants.UNABLE_TO_PROCESS_REQUEST)
                    handlerThread?.interrupt()
                    hideLoading()
                }
            }
            }
        }
    }
}