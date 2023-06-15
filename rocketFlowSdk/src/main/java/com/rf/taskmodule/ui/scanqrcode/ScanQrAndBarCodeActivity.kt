package com.rf.taskmodule.ui.scanqrcode

//import com.rf.taskmodule.ui.productdetails.ProductDetailsActivity
//import com.rf.taskmodule.ui.userdetails.UserDetailsActivity

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.SparseArray
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.Task
import com.rf.taskmodule.data.model.response.config.TaskResponse
import com.rf.taskmodule.data.model.response.config.UpdateResponse
import com.rf.taskmodule.data.model.response.config.UserData
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityScanQrAndBarCodeSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.selectorder.CatalogProduct
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.JSONConverter
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import java.io.IOException


class ScanQrAndBarCodeActivity :
    BaseSdkActivity<ActivityScanQrAndBarCodeSdkBinding, QrCodeValueViewModel>(),
    ViewTreeObserver.OnGlobalLayoutListener, QrCodeNavigator {


    var mSurfaceView: SurfaceView? = null

    var left: View? = null

    var progressBar3: ProgressBar? = null

    var scannerBar: ImageView? = null

    var scannerLayout: LinearLayout? = null

    var mode: String? = "Task"

    var right: View? = null
    private var isClassCall = false
    private var animator: ObjectAnimator? = null
    private val REQUEST_CAMERA = 3
    lateinit var binding: ActivityScanQrAndBarCodeSdkBinding

    lateinit var qrCodeValueViewModel: QrCodeValueViewModel

    lateinit var preferencesHelper: PreferencesHelper
    lateinit var httpManager: HttpManager



    var from:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!
        CommonUtils.showLogMessage("e", "BARCODE_Value", preferencesHelper.accessId)
        CommonUtils.showLogMessage("e", "BARCODE_Value", preferencesHelper.loginToken)
        qrCodeValueViewModel.navigator = this
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Scan", MODE_PRIVATE)
        mode = sharedPreferences.getString("Scan","Task")
        setUp()

    }

    private fun setUp() {
        if(intent.hasExtra("from")){
            from=intent.getStringExtra("from")
        }
        mSurfaceView = findViewById(R.id.surface_view)
        left = findViewById(R.id.left)
        right = findViewById(R.id.right)
        scannerBar = findViewById(R.id.scannerBar)
        scannerLayout = findViewById(R.id.scannerLayout)
        getCameraPermisson()
        val vto = scannerLayout!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(this)
    }

    private fun createCameraSource() {
        val detector: BarcodeDetector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        val cameraSource: CameraSource = CameraSource.Builder(this, detector).setAutoFocusEnabled(true).setRequestedPreviewSize(1600, 1024).build()
        mSurfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScanQrAndBarCodeActivity,
                            Manifest.permission.CAMERA
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraSource.start(mSurfaceView!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodeSparseArray: SparseArray<Barcode> = detections.getDetectedItems()
                if (barcodeSparseArray.size() > 0) {
                    (getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(40)
//                    ringtone()
                    playSound()
                    val value = barcodeSparseArray.valueAt(0).displayValue
                    if (value.contains("LOGIN_")) {
                        CommonUtils.showLogMessage("e", "BARCODE_Value", value)
                        runOnUiThread {
                            if (!isClassCall) {
                                isClassCall = true
                                cameraSource.stop()
                                if (from == null) {
                                    showLoading()
                                    qrCodeValueViewModel.loginUsingQr(httpManager, value, preferencesHelper.loginToken ,preferencesHelper.accessId)
                                } else {
                                    hideLoading()
                                    val resultIntent = Intent();
                                    resultIntent.putExtra("id", value)
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }
                            }
                        }
                    } else {
                        CommonUtils.showLogMessage("e", "BARCODE_Value", value)
                        runOnUiThread {
                            if (!isClassCall) {
                                isClassCall = true
                                cameraSource.stop()
                                if (from == null) {
                                    showLoading()
                                    qrCodeValueViewModel.getQrCodeValue(httpManager, value)
                                } else {
                                    hideLoading()
                                    val resultIntent = Intent();
                                    resultIntent.putExtra("id", value)
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                }

                            }
                        }
                    }
                }
            }
        })
    }
/*
    fun ringtone() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/

    override fun onGlobalLayout() {
        scannerLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            scannerLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
        } else {
            scannerLayout!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

        val destination = scannerLayout!!.y +
                scannerLayout!!.height

        animator = ObjectAnimator.ofFloat(
            scannerBar, "translationY",
            scannerLayout!!.y,
            destination
        )

        animator!!.setRepeatMode(ValueAnimator.REVERSE)
        animator!!.setRepeatCount(ValueAnimator.INFINITE)
        animator!!.setInterpolator(AccelerateDecelerateInterpolator())
        animator!!.setDuration(3000)
        animator!!.start()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScanQrAndBarCodeActivity::class.java)
        }
    }

    fun getCameraPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                createCameraSource()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ), REQUEST_CAMERA
                )
            }
        } else {
            createCameraSource()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createCameraSource()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_scan_qr_and_bar_code_sdk
    }

    override fun getViewModel(): QrCodeValueViewModel {
        val factory = RocketFlyer.dataManager()?.let { QrCodeValueViewModel.Factory(it) } // Factory
        if (factory != null) {
            qrCodeValueViewModel = ViewModelProvider(this, factory)[QrCodeValueViewModel::class.java]
        }
        return qrCodeValueViewModel!!
    }

    override fun handleQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            val jsonConverter: JSONConverter<QrCodeResponse> =
                JSONConverter()
            val response: QrCodeResponse = jsonConverter.jsonToObject(
                result.toString(),
                QrCodeResponse::class.java
            ) as QrCodeResponse

            callScanDetailsApi(response)
        } else {
            hideLoading()
            finish()
        }
    }

    override fun handleLoginQrCodeResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            hideLoading()
            playSoundSuccess()
            Log.d("Login",result.toString())
        } else {
            playSoundFailed()
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            hideLoading()
            finish()
        }
    }

    private fun playSoundSuccess() {
        mediaPlayer = MediaPlayer.create(this, R.raw.success)
        mediaPlayer!!.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                val resultIntent = Intent()
                resultIntent.putExtra("QRLogin", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }, 200)
    }
    private fun playSoundFailed() {
        mediaPlayer = MediaPlayer.create(this, R.raw.errorsound)
        mediaPlayer!!.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
        }, 200)
    }
    private fun callScanDetailsApi(response: QrCodeResponse) {
        response.type?.let { type ->
            when (type) {
                QrCodeValueType.TASK -> {
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getTaskDetails(httpManager, response.id)
                    } else {
                        hideLoading()
                    }

                }
                QrCodeValueType.PRODUCT -> {
//                        hideLoading()
//                        openProductDetailsActivity(response.id)
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getProductDetails(httpManager,response.id)
                    } else {
                        hideLoading()
                    }

                }
                QrCodeValueType.USER -> {
                    if (!response.id.isNullOrBlank()) {
                        qrCodeValueViewModel.getUserDetail(httpManager, response.id)
                    } else {
                        hideLoading()
                    }

                }
            }
        }

    }

    private var mediaPlayer: MediaPlayer? = null
    private fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer!!.start()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
        }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer = null
    }


    override fun handleUserDetailsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            val jsonConverter: JSONConverter<UpdateResponse> =
                JSONConverter()
            var response: UpdateResponse = jsonConverter.jsonToObject(
                result.toString(),
                UpdateResponse::class.java
            ) as UpdateResponse
            if (response.data != null) {
                var data = response.data
                var userData = UserData()
                if (!data!!.firstName.isNullOrBlank()) {
                    userData.firstName = data!!.firstName
                }
                if (!data!!.userId.isNullOrBlank()) {
                    userData.userId = data!!.userId
                }
                if (!data.middleName.isNullOrBlank()) {
                    userData.middleName = data!!.middleName
                }
                if (!data.lastName.isNullOrBlank()) {
                    userData.lastName = data!!.lastName
                }
                if (!data.mobile.isNullOrBlank()) {
                    userData.mobile = data!!.mobile
                }
                if (!data.email.isNullOrBlank()) {
                    userData.email = data!!.email
                }
                if (!data.fatherName.isNullOrBlank()) {
                    userData.fatherName = data!!.fatherName
                }
                if (!data.motherName.isNullOrBlank()) {
                    userData.motherName = data!!.motherName
                }
                if (!data.dateOfBirth.isNullOrBlank()) {
                    userData.dateOfBirth = data!!.dateOfBirth
                }
                if (!data.dateOfJoining.isNullOrBlank()) {
                    userData.dateOfJoining = data!!.dateOfJoining
                }
                if (!data.roleId.isNullOrBlank()) {
                    userData.roleId = data!!.roleId
                }
                if (!data.roleName.isNullOrBlank()) {
                    userData.roleName = data!!.roleName
                }
                openUserDetailsActivity(userData)

            }
        }else {
            finish()
        }
    }

    override fun handleProductDetailsResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?,
        pid: String?
    ) {
        hideLoading()
        Log.d("openProductDetailsActivity",pid);
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            openProductDetailsActivity(pid)
        }else {
            finish()
        }
    }

    private fun openProductDetailsActivity(id: String?) {
        val product = CatalogProduct()
        product.pid = id
        val gson = Gson()
        val json = gson.toJson(product)
        startActivity(Intent("com.tracki.ui.productdetails.ProductDetailsActivity").apply {
            // you can add values(if any) to pass to the next class or avoid using `.apply`
            putExtra("dataSDK", json)
        })
        finish()
    }

    private fun openUserDetailsActivity(data: UserData) {
        val gson = Gson()
        val json = gson.toJson(data)
        startActivity(Intent("com.tracki.useraddresslist.UserAddressListActivity").apply {
            // you can add values(if any) to pass to the next class or avoid using `.apply`
            putExtra("userDataSDK", json)
            putExtra("from", AppConstants.EMPLOYEES)
            putExtra("action", "view")
        })
        finish()
    }

    private fun openTaskDetailsActivity(task: Task?) {
        if (task != null) {
            val sharedPreferences: SharedPreferences =
                getSharedPreferences("Scan", MODE_PRIVATE)

            if (sharedPreferences.getBoolean("newTask",false) == true){
                val returnIntent = Intent()
                val gson = Gson()
                val json = gson.toJson(task)
                returnIntent.putExtra("result", json)
                setResult(RESULT_OK, returnIntent)
                sharedPreferences.edit().putBoolean("newTask",false).apply()
                finish()
            }
            else{
                val intent = Intent(this@ScanQrAndBarCodeActivity, NewTaskDetailsActivity::class.java)
                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.taskId)
                intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.allowSubTask)
                intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.subCategoryIds)
                intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.referenceId)
                intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, task.categoryId)
                intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ScanQrAndBarCodeActivity)) {
            var taskResponse = Gson().fromJson("$result", TaskResponse::class.java)
            taskResponse.taskDetail?.let { task ->
                openTaskDetailsActivity(task)

            }

        }else {
            finish()
        }
    }


}