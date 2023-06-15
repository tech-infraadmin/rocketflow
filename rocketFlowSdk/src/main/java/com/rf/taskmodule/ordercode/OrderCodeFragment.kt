package com.rf.taskmodule.ordercode

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.FragmentOrderCodeBinding
import com.rf.taskmodule.ui.base.BaseSdkFragment
import com.rf.taskmodule.ui.custom.GlideApp
import com.rf.taskmodule.utils.Log
import com.rocketflow.sdk.RocketFlyer
import java.io.File
import java.io.FileOutputStream

class OrderCodeFragment : BaseSdkFragment<FragmentOrderCodeBinding, OrderCodeViewModel>() {

    private lateinit var viewModel: OrderCodeViewModel
    private lateinit var binding: FragmentOrderCodeBinding

    var httpManager: HttpManager? = null
    var preferencesHelper: PreferencesHelper? = null
    private var qrUrl = "na"
    private var categoryID = "na"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding = viewDataBinding
        httpManager = RocketFlyer.httpManager()!!
        preferencesHelper = RocketFlyer.preferenceHelper()!!

        qrUrl = requireArguments().getString("qrUrl").toString()
        categoryID = requireArguments().getString("categoryID").toString()

        binding.subTitle.text = "To scan and review the ${getLabelName(categoryID)} details"

        if (qrUrl != "na")
            GlideApp.with(this).load(qrUrl).into(binding.ivQrCode)

        binding.ivShare.setOnClickListener {
            val ll = binding.llOrderCode
            ll.isDrawingCacheEnabled = true
            ll.buildDrawingCache(true)
            val bitmap = Bitmap.createBitmap(ll.drawingCache)
            store(bitmap, "tempScreenshotRF.jpeg")
        }
    }

    private fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> =
            preferencesHelper!!.workFlowCategoriesList
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

    fun store(bitmap: Bitmap, fileName: String) {
        try {
            val dirPath: File? =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(dirPath, fileName)
            dirPath!!.mkdirs()
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.flush()
            fos.close()
            shareImage(file)
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("Exception1", "${exception.message}")
        }
    }

    private fun shareImage(file: File) {
        val uri = FileProvider.getUriForFile(
            requireActivity(),
            requireActivity().applicationContext.packageName + ".provider",
            file
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            startActivity(intent)
        } catch (exception: Exception) {
            Log.e("Exception", "${exception.message}")
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_order_code
    }

    override fun getViewModel(): OrderCodeViewModel {
        val factory =
            RocketFlyer.dataManager()?.let { OrderCodeViewModel.Factory(it) } // Factory
        if (factory != null) {
            viewModel = ViewModelProvider(this, factory)[OrderCodeViewModel::class.java]
        }
        return viewModel
    }

    companion object {
        fun newInstance(qrUrl: String, categoryId: String?): OrderCodeFragment? {
            val args = Bundle()
            args.putSerializable("qrUrl", qrUrl)
            args.putSerializable("categoryID", categoryId)
            val fragment = OrderCodeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}