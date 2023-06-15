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
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rf.taskmodule.BR
import com.rf.taskmodule.R
import com.rf.taskmodule.data.local.prefs.PreferencesHelper
import com.rf.taskmodule.data.model.response.config.ConfigResponse
import com.rf.taskmodule.data.model.response.config.Navigation
import com.rf.taskmodule.data.network.APIError
import com.rf.taskmodule.data.network.ApiCallback
import com.rf.taskmodule.data.network.HttpManager
import com.rf.taskmodule.databinding.ActivityWebviewSdkBinding
import com.rf.taskmodule.ui.base.BaseSdkActivity
import com.rf.taskmodule.utils.AppConstants
import com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_WEB_INFO
import com.rf.taskmodule.utils.CommonUtils
import com.rf.taskmodule.utils.Log
import com.rf.taskmodule.utils.NextScreen
import com.rocketflow.sdk.RocketFlyer


/**
 * Created by rahul on 22/10/18
 */
class WebViewActivity : BaseSdkActivity<ActivityWebviewSdkBinding, WebViewModel>(), WebViewNavigator
{

    lateinit var httpManager: HttpManager
    var renewal = false

    lateinit var mWebViewModel: WebViewModel

    lateinit var prefsHelper: PreferencesHelper

    private var mediaPlayer: MediaPlayer? = null
    private var mActivityWebviewBinding: ActivityWebviewSdkBinding? = null

    private var accessId: String? = null
    private var loginToken: String? = null

    private var handshakeUrl: String? = null
    private var webviewUrl: String? = null
    private var redirectionScreen: NextScreen? = null
    private var mobile: String? = null
    private lateinit var webView: WebView
    private lateinit var contentProgressBar: ContentLoadingProgressBar
    private lateinit var context: Context
    private var navigation: Navigation? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_webview_sdk

    override fun getViewModel(): WebViewModel {
        val factory = RocketFlyer.dataManager()?.let { WebViewModel.Factory(it) } // Factory
        if (factory != null) {
            mWebViewModel = ViewModelProvider(this, factory)[WebViewModel::class.java]
        }
        return mWebViewModel!!
    }

    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityWebviewBinding!!.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityWebviewBinding = viewDataBinding
        mWebViewModel.navigator = this
        httpManager = RocketFlyer.httpManager()!!
        prefsHelper = RocketFlyer.preferenceHelper()!!
        setToolbar(mActivityWebviewBinding!!.toolbar,"Merchant  Signup")

        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("WebView", "Error Inside OnCreate(): $e")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Throws(Exception::class)
    private fun init() {
        renewal = false
        if (intent.hasExtra(EXTRA_WEB_INFO)) {
            navigation = intent?.getSerializableExtra(EXTRA_WEB_INFO) as Navigation
        }
        if (intent.hasExtra("webviewUrl")){
            webviewUrl = intent.getStringExtra("webviewUrl")
        }

        if (intent.hasExtra("payUrl")){
            webviewUrl = intent.getStringExtra("payUrl")
            setToolbar(mActivityWebviewBinding!!.toolbar,"Renewal")
            renewal = true
        }

        if (intent.hasExtra("mobile")){
            mobile = intent.getStringExtra("mobile")
        }

        if (intent.hasExtra("handshakeUrl")){
            handshakeUrl = intent.getStringExtra("handshakeUrl")
        }
        if (intent.hasExtra("redirectionScreen")){
            redirectionScreen = intent.getSerializableExtra("redirectionScreen") as NextScreen
        }
        // set toolbar
        setToolbar(mActivityWebviewBinding?.toolbar, navigation?.title)

        webView = mActivityWebviewBinding?.webView!!
        contentProgressBar = mActivityWebviewBinding?.contentProgressBar!!
        contentProgressBar.visibility = View.GONE
        context = this

        webView.webViewClient = TrackiWebViewClient()
        val setting = webView.settings
        setting.domStorageEnabled = true
        setting.javaScriptEnabled = true

//        setting.setSupportMultipleWindows(true)
//        setting.javaScriptCanOpenWindowsAutomatically=true
//        setting.allowFileAccess = true
//        setting.setAppCacheEnabled(true)
//        setting.domStorageEnabled=true
//        setting.cacheMode=WebSettings.LOAD_DEFAULT
//        setting.loadWithOverviewMode = true
//        setting.useWideViewPort = true
//        setting.allowContentAccess = true

        if (webviewUrl != null){
            val headers = CommonUtils.buildWebViewHeader(this@WebViewActivity)
            Log.e("headers","$headers")
            Log.e("urlNew","$webviewUrl")
            webView.loadUrl(webviewUrl!!,headers)
        }
        else {
            loadUrl(webView)
        }
    }

    private fun loadUrl(webView: WebView) {
//        navigation?.actionConfig?.actionUrl.let {

        if (navigation?.actionConfig?.actionUrl != null) {
            CommonUtils.showLogMessage("e","url",navigation?.actionConfig?.actionUrl)
            webView.loadUrl(navigation?.actionConfig?.actionUrl!!,
                CommonUtils.buildWebViewHeader(this@WebViewActivity))
        }
//        }
    }



    /**
     * Show internet dialog
     */
    private fun showInternetDialog() {
        AlertDialog.Builder(context)
            .setMessage(AppConstants.ALERT_NO_CONNECTION)
            .setTitle(AppConstants.CONNECTION_ERROR)
            .setCancelable(false)
            .setPositiveButton(AppConstants.RETRY) { dialog, _ ->
                dialog.dismiss()
                //    callBack.hitApi();
            }.setNegativeButton(AppConstants.CLOSE) { dialog, _ ->
                dialog.dismiss()
                //callBack.onNetworkErrorClose();
            }
            .show()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, WebViewActivity::class.java)
    }

    /**
     * Class used to handle the urls
     */
    inner class TrackiWebViewClient : WebViewClient() {
        /*@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Log.e("headers","${request?.requestHeaders}")
            return null
        }*/
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            contentProgressBar.visibility = View.VISIBLE
            if (url != null && handshakeUrl != null) {
                val url1 = url.split("/")
                val urla = "${url1[0]}//${url1[2]}"
                val hUrl = handshakeUrl!!.split("/")
                val hUrla = "${hUrl[0]}//${hUrl[2]}"
                if (urla == hUrla) {
                    accessId = url1[4]
                    loginToken = url1[3]
                    prefsHelper.accessId = accessId
                    prefsHelper.loginToken = loginToken
                    Log.e("urlNew","accessId => $accessId \n token => $loginToken")
                    mWebViewModel.getConfig(httpManager, "")
                    view!!.stopLoading()
                }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            contentProgressBar.visibility = View.GONE
            if (renewal){
                Log.e("urlSet","setfull = $url")
                val webUrl = url
                val web1 = webUrl?.split("/")
                if (web1 != null) {
                    if (web1.size>6){
                        val web2 = web1[7]
                        Log.e("urlSet","full url = $webUrl endpoint = $web2")
                        if (web2 == "SUCCESS"){
                            openSFDialog(true,this@WebViewActivity)
                        }
                        else if(web2 == "FAILURE"){
                            openSFDialog(false,this@WebViewActivity)
                        }
                    }

                }


            }

        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//            super.onReceivedSslError(view, handler, error)
            val builder = AlertDialog.Builder(this@WebViewActivity)
            builder.setMessage(R.string.notification_error_ssl_cert_invalid)
            builder.setPositiveButton(AppConstants.CONTINUE) { _, _ -> handler!!.proceed() }
            builder.setNegativeButton(AppConstants.CANCEL) { _, _ -> handler!!.cancel() }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {

            if (result != null) {
                val gson = Gson()
                val configResponse = gson.fromJson(result.toString(), ConfigResponse::class.java)
                configResponse.refreshConfig = true
                CommonUtils.saveConfigDetails(this@WebViewActivity, configResponse, prefsHelper, "2", mobile)
                prefsHelper.loginToken = loginToken
                prefsHelper.accessId = accessId
                if (configResponse.appConfig != null) {
                    CommonUtils.otpgoToNext(this, redirectionScreen, mobile)

                } else {
                    CommonUtils.otpgoToNext(this, redirectionScreen, mobile)
                }
            }
        }
    }

    fun openSFDialog(success: Boolean, context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_payment_sdk)
        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
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
            desc.text = "Congratulations, Your Transaction Was Successful. Continue and restart your app for the changes to apply."
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

            finish()
            finishAffinity()
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
}