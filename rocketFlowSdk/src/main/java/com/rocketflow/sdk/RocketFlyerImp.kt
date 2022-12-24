package com.rocketflow.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.gson.Gson
import com.rocketflow.sdk.util.RFLog
import com.rf.taskmodule.data.model.response.config.ConfigResponse
import com.rf.taskmodule.data.model.response.config.SDKToken
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories
import com.rf.taskmodule.data.network.APIError
import java.lang.ref.WeakReference

internal class RocketFlyerImp(
    context: Context
) : IRocketFlyer {
    private val contextRef = WeakReference(context.applicationContext)

    override fun initialize(sdkInitToken: String) {

        contextRef.get()?.let {
            if (sdkInitToken.isEmpty()) throw Exception("Token cannot be null")
            RFLog.d("Token value : $sdkInitToken")
            RocketFlyerBuilder.getPrefInstance()?.sdkClientID = sdkInitToken
            val pref = RocketFlyerBuilder.getPrefInstance()
            if(pref?.loginToken != null && pref.loginToken.isNotEmpty()) {
                hitConfig();
            }else{
                hitLoginSDKToken(sdkInitToken)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun start(processId: String, startActivity: Boolean) {

        contextRef.get()?.let {

            try{

                val listCategory: List<WorkFlowCategories> =
                    RocketFlyerBuilder.getDataManagerInstance()?.workFlowCategoriesList ?: ArrayList()

                val pref = RocketFlyerBuilder.getPrefInstance()

                if(pref==null
                    || pref.loginToken==null
                    || pref.loginToken.isEmpty()
                    || listCategory.isEmpty()){

                    /*showToast(it,"Call init before start method")*/
                    return
                }

                val configResponse = Gson().fromJson(
                    pref.configResponse.toString(),
                    ConfigResponse::class.java
                )

                com.rf.taskmodule.utils.CommonUtils.saveConfigDetails(
                    it,
                    configResponse,
                    RocketFlyer.preferenceHelper(),
                    "1")

                if (startActivity) {
                    val intent = com.rf.taskmodule.ui.main.MainSDKActivity.newIntent(it)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
            }catch (e : Exception){
                showToast(it, "Call init before start method :$e")
            }


//            if (processId.isEmpty()) throw Exception("ProcessId cannot be null")
//            RFLog.d("ProcessId : $processId")
//
//            val intent = TaskActivity.newIntent(it)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, processId)
//            it.startActivity(intent);



//            intent.putExtra(
//                AppConstants.Extra.EXTRA_CATEGORIES,
//                Gson().toJson(response)
//            )
//            if (categoryName != null) intent.putExtra(AppConstants.Extra.TITLE, categoryName)
//            intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, response.getStageId())
//            intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate)
//            intent.putExtra(AppConstants.Extra.FROM_TO, toDate)
//            intent.putExtra(AppConstants.Extra.LOAD_BY, LOADBY)
//            intent.putExtra(AppConstants.Extra.GEO_FILTER, userGeoReq)
            //ActivitCocontextRef.get().startActivityForResult(intent)

        }
    }

    /*class ItemOnClickListener implements View.OnClickListener {
        private View _parent;

        public ItemOnClickListener(ViewGroup parent) {
            _parent = parent;
        }

        @Override
        public void onClick(View view) {
            //.......
            // close the dropdown
            View root = _parent.getRootView();
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        }
    }*/

    override fun terminate() {
        contextRef.get()?.let {
            RFLog.d("Terminate")
            RocketFlyerBuilder.getPrefInstance()?.loginToken = "";
            //showToast(it,"Terminate");
        }
    }


    private fun showToast(context: Context, str : String){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show()
    }

    private fun hitLoginSDKToken(sdkClintId: String) {

        RocketFlyerBuilder.getDataManagerInstance()
            ?.getSDKLoginToken(sdkClintId,RocketFlyerBuilder.getHttpManagerInstance(), object :
                com.rf.taskmodule.data.network.ApiCallback {
            override fun hitApi() {
                //SyncCallback.super.hitApi();z
                RFLog.d("hitLoginSDKToken : hitApi")
            }

            override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
                //SyncCallback.super.onRequestTimeOut(callBack);
                RFLog.d("hitLoginSDKToken : onRequestTimeOut")
            }

            override fun onLogout() {
                RFLog.d("hitLoginSDKToken : onLogout")
            }

            override fun onNetworkErrorClose() {
                RFLog.d("hitLoginSDKToken : onNetworkErrorClose")
            }

            override fun onResponse(result: Any?, error: APIError?) {
                if(result==null) return
                RFLog.d("hitLoginSDKToken : onResponse")
                contextRef.get()?.let {
                    if (com.rf.taskmodule.utils.CommonUtils.handleResponse(this, error, result, it)) {
                        val gson = Gson()
                        val token = gson.fromJson(result.toString(), SDKToken::class.java)
                        if (token != null) {
                            com.rf.taskmodule.utils.CommonUtils.saveSDKAccessToken(token, RocketFlyer.preferenceHelper())
                        }
                        hitConfig()
                    } else {
                        //CommonUtils.showPermanentSnackBar(findViewById(R.id.rlMain), AppConstants.ALERT_TRY_AGAIN,mSplashViewModel);
                    }
                }

            }

            override fun isAvailable(): Boolean {
                return true
            }
        })
    }

    private fun hitConfig() {
        RocketFlyerBuilder.getDataManagerInstance()
            ?.getConfig(RocketFlyerBuilder.getHttpManagerInstance(), object :
                com.rf.taskmodule.data.network.ApiCallback {
                override fun hitApi() {
                    //SyncCallback.super.hitApi();
                }

                override fun onRequestTimeOut(callBack: com.rf.taskmodule.data.network.ApiCallback) {
                    //SyncCallback.super.onRequestTimeOut(callBack);
                }

                override fun onLogout() {

                }

                override fun onNetworkErrorClose() {

                }

                override fun onResponse(result: Any?, error: APIError?) {
                    if (result == null) return
                    contextRef.get()?.let {
                        if (com.rf.taskmodule.utils.CommonUtils.handleResponse(this, error, result, it)) {
                            val gson = Gson()
                            val configResponse = gson.fromJson(
                                result.toString(),
                                ConfigResponse::class.java
                            )
                            com.rf.taskmodule.utils.CommonUtils.saveConfigDetails(
                                it,
                                configResponse,
                                RocketFlyer.preferenceHelper(),
                                "1"
                            )
                        } else {
                            //CommonUtils.showPermanentSnackBar(findViewById(R.id.rlMain), AppConstants.ALERT_TRY_AGAIN,mSplashViewModel);
                        }
                    }

                }

                override fun isAvailable(): Boolean {
                    return true
                }
                                                    },"")
    }

}