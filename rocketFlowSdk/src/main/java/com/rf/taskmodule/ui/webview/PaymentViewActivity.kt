package com.rf.taskmodule.ui.webview

//import com.rf.taskmodule.ui.Config
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rocketflow.sdk.RocketFlyer
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.databinding.ActivityPaymentWebviewSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log


class PaymentViewActivity : BaseSdkActivity<ActivityPaymentWebviewSdkBinding, PaymentViewModel>(),
    PaymentNavigator {


    lateinit var mWebViewModel: PaymentViewModel
    //lateinit var preferencesHelper: PreferencesHelper

    private var mediaPlayer: MediaPlayer? = null


    private var mActivityWebviewBinding: ActivityPaymentWebviewSdkBinding? = null

    private lateinit var webView: WebView
    private lateinit var contentProgressBar: ContentLoadingProgressBar
    private lateinit var context: Context

    private lateinit var pageUrl: String

    private var categoryId = ""
    private var taskId = ""
    private var FROM = ""

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_payment_webview_sdk
    override fun getViewModel(): PaymentViewModel {
        val factory = RocketFlyer.dataManager()?.let { PaymentViewModel.Factory(it) } // Factory
        if (factory != null) {
            mWebViewModel = ViewModelProvider(this, factory)[PaymentViewModel::class.java]
        }
        return mWebViewModel
    }
    private var snackBar: Snackbar? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            mActivityWebviewBinding!!.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityWebviewBinding = viewDataBinding
        mWebViewModel.navigator = this

        init()

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Throws(Exception::class)
    private fun init() {


        taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID).toString()

        FROM = intent.getStringExtra(AppConstants.Extra.FROM).toString()

        categoryId = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID).toString()


        webView = mActivityWebviewBinding?.webView!!
        contentProgressBar = mActivityWebviewBinding?.contentProgressBar!!
        contentProgressBar.visibility = View.GONE
        context = this

        webView.webViewClient = TrackiWebViewClient()
        val setting = webView.settings
        setting.domStorageEnabled = true
        setting.javaScriptEnabled = true

        if (intent.hasExtra("url")) {
            pageUrl = intent!!.getStringExtra("url").toString()

            loadUrl(webView)
        }
    }

    private fun loadUrl(webView: WebView) {
        webView.loadUrl(
            pageUrl,
            CommonUtils.buildDeviceHeader(this@PaymentViewActivity)
        )
        Log.e("payment url", "$pageUrl")
    }

    @SuppressLint("NewApi")
    fun openSFDialog(success: Boolean, context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_payment_sdk)
        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)

        val image = dialog.findViewById<ImageView>(R.id.iv_status)
        val proceed = dialog.findViewById<AppCompatButton>(R.id.btn_proceed)
        val text = dialog.findViewById<TextView>(R.id.tv_payment)
        val desc = dialog.findViewById<TextView>(R.id.tv_payment_desc)

        text.visibility = View.VISIBLE
        proceed.visibility = View.VISIBLE
        image.visibility = View.VISIBLE
        if (success) {
            playSound()
            text.text = "Payment Successful"
            desc.text = "Congratulations, Your Transaction Was Successful"
            text.setTextColor(context.getColor(R.color.green_google_button))
            image.setImageDrawable(context.getDrawable(R.drawable.ic_green_tick))
        } else {
            playSound()
            text.text = "Payment Unsuccessful"
            desc.text = "Your Transaction was unsuccessful. Please try again"
            text.setTextColor(context.getColor(R.color.red_dark5))
            image.setImageDrawable(context.getDrawable(R.drawable.i_cross_red))
        }

        proceed.setOnClickListener {
            val intentNew = Intent(context,NewTaskDetailsActivity::class.java)
            intentNew.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID,categoryId)
            intentNew.putExtra(AppConstants.Extra.EXTRA_TASK_ID,taskId)
            intentNew.putExtra(AppConstants.Extra.FROM,FROM)
            startActivity(intentNew)
            finish()
        }
        if (!dialog.isShowing) dialog.show()
    }

    private fun playSound() {


        mediaPlayer = MediaPlayer.create(context, R.raw.beep)
        mediaPlayer!!.start()
        Handler().postDelayed({
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
        }, 2000)


    }

    inner class TrackiWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            contentProgressBar.visibility = View.VISIBLE
//            https://uat.rocketflyer.in/signin
            Log.e("urlDivert","$url")
            val urlFailure = "https://uat.rocketflyer.in/webView/TASK_DETAILS/$taskId/FAILURE"
            val urlFailure1 = "https://uat.rocketflyer.in/webView/TASK_DETAILS/null/FAILURE"
            val urlSuccess = "https://uat.rocketflyer.in/webView/TASK_DETAILS/$taskId/SUCCESS"
            val urlSuccess1 = "https://uat.rocketflyer.in/webView/TASK_DETAILS/null/SUCCESS"
            Log.e("urlDivertNew","$urlFailure")
            if (url == urlFailure || url == urlFailure1) {
                webView.stopLoading()
                openSFDialog(false,context)
            }
            else if (url == urlSuccess || url == urlSuccess1) {
                webView.stopLoading()
                openSFDialog(true,context)
            }


        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            contentProgressBar.visibility = View.GONE
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
//            super.onReceivedSslError(view, handler, error)
            val builder = AlertDialog.Builder(this@PaymentViewActivity)
            builder.setMessage(R.string.notification_error_ssl_cert_invalid)
            builder.setPositiveButton(AppConstants.CONTINUE) { _, _ -> handler!!.proceed() }
            builder.setNegativeButton(AppConstants.CANCEL) { _, _ -> handler!!.cancel() }
            val dialog = builder.create()
            dialog.show()
        }
    }
}