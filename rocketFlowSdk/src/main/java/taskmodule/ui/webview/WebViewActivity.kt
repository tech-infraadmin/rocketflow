package taskmodule.ui.webview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rocketflow.sdk.RocketFlyer
import taskmodule.BR
//import taskmodule.Config
import taskmodule.R
import taskmodule.data.local.prefs.PreferencesHelper
import taskmodule.data.model.response.config.ConfigResponse
import taskmodule.data.model.response.config.Navigation
import taskmodule.data.network.APIError
import taskmodule.data.network.ApiCallback
import taskmodule.data.network.HttpManager
import taskmodule.databinding.ActivityWebviewSdkBinding
import taskmodule.ui.base.BaseSdkActivity
import taskmodule.utils.AppConstants
import taskmodule.utils.AppConstants.Extra.EXTRA_WEB_INFO
import taskmodule.utils.CommonUtils
import taskmodule.utils.Log
import taskmodule.utils.NextScreen
import javax.inject.Inject


/**
 * Created by rahul on 22/10/18
 */
class WebViewActivity : BaseSdkActivity<ActivityWebviewSdkBinding, WebViewModel>(), WebViewNavigator
{

    lateinit var httpManager: HttpManager

    lateinit var mWebViewModel: WebViewModel

    lateinit var prefsHelper: PreferencesHelper

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
        if (intent.hasExtra(EXTRA_WEB_INFO)) {
            navigation = intent?.getSerializableExtra(EXTRA_WEB_INFO) as Navigation
        }
        if (intent.hasExtra("webviewUrl")){
            webviewUrl = intent.getStringExtra("webviewUrl")
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
}