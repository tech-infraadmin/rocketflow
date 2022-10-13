package com.tracki.ordercode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.rocketflow.sdk.RocketFlyer
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityOrderCodeBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.custom.GlideApp
import com.tracki.utils.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class OrderCodeActivity : BaseActivity<ActivityOrderCodeBinding, OrderCodeViewModel>(),
    OrderCodeNavigator {

    var orderCodeViewModel: OrderCodeViewModel? = null

    var httpManager: HttpManager? = null
    var preferencesHelper: PreferencesHelper? = null

    lateinit var binding: ActivityOrderCodeBinding

    lateinit var imagePath: File

    var qrUrl = "na"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        orderCodeViewModel!!.navigator = this

        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        qrUrl = intent.getStringExtra("qrUrl").toString()


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        if (qrUrl != "na")
            GlideApp.with(this).load(qrUrl).into(binding.ivQrCode)
        else
            onBackPressed()

        binding.ivShare.setOnClickListener {
            val ll = binding.llOrderCode
            ll.isDrawingCacheEnabled = true
            ll.buildDrawingCache(true)
            val bitmap = Bitmap.createBitmap(ll.drawingCache)

            store(bitmap,"tempScreenshotRF.jpeg")
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_code
    }


    override fun getViewModel(): OrderCodeViewModel {
        val factory = RocketFlyer.dataManager()?.let { OrderCodeViewModel.Factory(it) } // Factory
        if (factory != null) {
            orderCodeViewModel = ViewModelProvider(this, factory)[OrderCodeViewModel::class.java]
        }
        return orderCodeViewModel!!
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onBackClick() {

    }

    fun store(bitmap: Bitmap, fileName: String){

        try {
            val dirPath: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(dirPath, fileName)
            dirPath!!.mkdirs()

            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.flush()
            fos.close()

            shareImage(file)

        }
        catch (exception: Exception){
            exception.printStackTrace()
            Log.e("Exception1", "${exception.message}")
        }
    }

    private fun shareImage(file: File){
        val uri = FileProvider.getUriForFile(this,applicationContext.packageName + ".provider",file)
        val intent = Intent()

        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM,uri)

        try {
            startActivity(intent)
        }
        catch (exception: Exception){
            Log.e("Exception","${exception.message}")
        }
    }

}