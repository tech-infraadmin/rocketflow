package com.rf.taskmodule.ui.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.rf.taskmodule.data.local.prefs.AppPreferencesHelper;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.buddylisting.BuddyListingActivity;
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity;
import com.rf.taskmodule.ui.fleetlisting.FleetListingActivity;
import com.rf.taskmodule.ui.geofencing.GeofenceUtil;
import com.rf.taskmodule.ui.main.filter.BuddyFilterActivity;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.ErrorScreenHelper;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.utils.NetworkStateReceiver;
import com.rf.taskmodule.utils.NetworkStateReceiverListener;
import com.rf.taskmodule.utils.NetworkUtils;
import com.rf.taskmodule.utils.TrackiToast;
import com.rocketflow.sdk.RocketFlyer;
import com.rf.taskmodule.R;
import com.rf.taskmodule.data.local.prefs.AppPreferencesHelper;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.buddylisting.BuddyListingActivity;
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity;
import com.rf.taskmodule.ui.fleetlisting.FleetListingActivity;
import com.rf.taskmodule.ui.geofencing.GeofenceUtil;
import com.rf.taskmodule.ui.main.filter.BuddyFilterActivity;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.ErrorScreenHelper;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.utils.NetworkStateReceiver;
import com.rf.taskmodule.utils.NetworkStateReceiverListener;
import com.rf.taskmodule.utils.NetworkUtils;
import com.rf.taskmodule.utils.TrackiToast;
import com.trackthat.lib.TrackThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU;
import static com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.rf.taskmodule.utils.AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE;
import static com.rf.taskmodule.utils.ErrorScreenHelper.LOCATION_SERVICE_ERROR;

//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;

/**
 * Created by rahul on 07/07/17.
 */

public abstract class BaseSdkActivity<T extends ViewDataBinding, V extends BaseSdkViewModel<?>> extends AppCompatActivity
        implements BaseSdkFragment.Callback , NetworkStateReceiverListener {

    private final String TAG = BaseSdkActivity.class.getName();

    public LatLng currentLatLng;
    //@Inject
    //public AnalyticsHelper analyticsHelper;
    public GeofenceUtil geofenceUtil;
    // protected WebSocketManager webSocketManager = null;
    // private SensorManager mSensorManager;
    private Dialog locationDialog;
    //@Inject
    //PreferencesHelper sharedPreferences;
    // TODO
    // this can probably depend on isLoading variable of BaseViewModel,
    // since its going to be common for all the activities
    private ProgressDialog mProgressDialog;
    private T mViewDataBinding;
    private V mViewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private ErrorScreenHelper mScreenHelper;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private NetworkStateReceiver networkStateReceiver;


//    private final SensorEventListener mSensorListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            mAccelLast = mAccelCurrent;
//            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
//            float delta = mAccelCurrent - mAccelLast;
//            mAccel = mAccel * 0.9f + delta;
//            if (mAccel > 12) {
//                if (mScreenHelper == null) {
//                    mScreenHelper = new ErrorScreenHelper(BaseActivity.this);
//                }
//                if(!mScreenHelper.dialog.isShowing())
//                    mScreenHelper.changeAccessId(sharedPreferences);
//               // Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//
//
//    };


    /**
     * Init socket if null
     */
   /* protected void initSocket() {
        if (webSocketManager == null) {
            String userId = sharedPreferences.getUserDetail().getUserId();
            String t = sharedPreferences.getLoginToken();
            String url = sharedPreferences.getChatUrl();

            Log.e(TAG, "initSocket() called");
            //initialize socket
            webSocketManager = WebSocketManager.getInstance(url, userId, t);
            if (webSocketManager == null)
                Log.e(TAG, "webSocketManager is Null");
        }
    }*/

    /**
     * Check if socket is connected the hit connection with callback
     *
     * @param socketListener listener for socket events
     */
//    protected void connectSocket(@NonNull WebSocketManager.SocketListener socketListener) {
//        if (webSocketManager != null) {
//            webSocketManager.addListener(socketListener);
//            Log.e(TAG, "connectSocket() called");
//        }
//    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public String getGoogleMapKey(){
        return "AIzaSyATO_5mNZJ8h6V64L6eHeZfiVjk63803ec";
    }
    PreferencesHelper preferencesHelper;

    static int THEME_BLUE = R.style.AppTheme;
    static int THEME_GREEN = R.style.AppThemeGreen;
    static int THEME_RED = R.style.AppThemeRed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection();
//        /*TODO test here for navigation bar color and navigation button color*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this instanceof BuddyFilterActivity ||
                    this instanceof DynamicFormActivity) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            } else if (this instanceof BuddyListingActivity ||
                    this instanceof FleetListingActivity) {
                if (getIntent().hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU) ||
                        getIntent().hasExtra(AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU) ||
                        getIntent().hasExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU)) {
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
                }
            }
        }
        super.onCreate(savedInstanceState);
        preferencesHelper = RocketFlyer.Companion.preferenceHelper();
        setTheme(preferencesHelper.getCurrentTheme());

        performDataBinding();
        geofenceUtil = new GeofenceUtil(BaseSdkActivity.this);
        if (geofenceUtil.getGeofencesAdded()) {
            Log.e(TAG, "----> true");
        } else {
            Log.e(TAG, "----> false");
        }
        mobileShakeTask();
        //registerReceiver();
    }

    private void mobileShakeTask() {
//        if(BuildConfig.BUILD_TYPE!="release" /*&& BuildConfig.BUILD_TYPE!="uat"*/) {
//            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//            Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                    SensorManager.SENSOR_DELAY_NORMAL);
//            mAccel = 10f;
//            mAccelCurrent = SensorManager.GRAVITY_EARTH;
//            mAccelLast = SensorManager.GRAVITY_EARTH;
//        }
    }

    private void unRegisterShakeListener() {
//        if (BuildConfig.BUILD_TYPE!="release" /*&& BuildConfig.BUILD_TYPE!="uat"*/) {
//            if (mSensorListener != null)
//                mSensorManager.unregisterListener(mSensorListener);
//        }
    }

    private void registerShakeListener() {
//        if (BuildConfig.BUILD_TYPE!="release"/*&& BuildConfig.BUILD_TYPE!="uat"*/) {
//            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }

    }


    private BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    //location is enabled
                    if (mScreenHelper != null) {
                        if (mScreenHelper.dialog != null && mScreenHelper.dialog.isShowing())
                            mScreenHelper.dialog.dismiss();
                    }
                } else {
                    if (mScreenHelper == null) {
                        mScreenHelper = new ErrorScreenHelper(BaseSdkActivity.this);
                    }
                    mScreenHelper.displayLocation(ErrorScreenHelper.LOCATION_SERVICE_ERROR);
                    //location is disabled
                }
            }
        }
    };

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(2000);
//        locationRequest.setSmallestDisplacement(20f);
    }

    @SuppressLint("MissingPermission")
    public void requestCurrentLocation(LocationCallback locationCallback) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void removeLocationUpdates(LocationCallback locationCallback) {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void performDependencyInjection() {
        //AndroidInjection.inject(this);
    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            String[] permi = new String[listPermissionsNeeded.size()];
            permi = listPermissionsNeeded.toArray(permi);
            ActivityCompat.requestPermissions(this, permi, AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE);
            return false;
        }
        return true;
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkMultiplePermission(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public void openActivityOnTokenExpire(Intent intent) {
        //stop tracking
        if (TrackThat.isTracking()) {
            TrackThat.stopTracking();
        }
//        Intent intent = LoginActivity.newIntent(this);
        setFlags(intent);
        startActivity(intent);
        finish();
    }

    protected void setFlags(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    public void showLoading() {
        hideLoading();
        if (isNetworkConnected()) {
            if (!isFinishing())
                mProgressDialog = CommonUtils.showLoadingDialog(this);
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= 21) {
                    supportFinishAfterTransition();
                }
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setToolbar(Toolbar mToolbar, String title) {
        if (title != null) {
            mToolbar.setTitle(title);
            //mToolbar.setBackgroundColor(Color.parseColor("#3581F3"));
            mToolbar.setTitleTextColor(Color.WHITE);
            mToolbar.setTitleTextAppearance(this, R.style.CamptonBookTextAppearance);
            // centerToolbarTitle(mToolbar);
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    protected void setToolbarHideBackArrow(Toolbar mToolbar, String title) {
        if (title != null) {
            mToolbar.setTitle("    "+title);
            mToolbar.setTitleTextColor(Color.BLACK);
            mToolbar.setTitleTextAppearance(this, R.style.CamptonBookTextAppearance);
            // centerToolbarTitle(mToolbar);
        }
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }
    protected void centerToolbarTitle(@NonNull final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER_HORIZONTAL);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
            //also you can use titleView for changing font: titleView.setTypeface(Typeface);
        }
    }

    public void showTimeOutMessage(@NonNull ApiCallback callback) {
        TrackiToast.Message.showShort(this, AppConstants.MSG_REQUEST_TIME_OUT_TYR_AGAIN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideLoading();

        //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public void invalidToken(Intent intent) {
        //clear all data from preferences and open login activity
        Objects.requireNonNull(RocketFlyer.Companion.preferenceHelper()).clear(AppPreferencesHelper.PreferencesKeys.NOT_ALL);
        openActivityOnTokenExpire(intent);
    }
    public void invalidLoginTokenOnly() {
        if(RocketFlyer.Companion.preferenceHelper()!=null) {
            Objects.requireNonNull(RocketFlyer.Companion.preferenceHelper()).setLoginToken(null);
            Objects.requireNonNull(RocketFlyer.Companion.preferenceHelper()).setAccessId(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start listening geofence enter and exit alerts.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TrackThat.ACTION_GEOFENCE_ENTER);
        intentFilter.addAction(TrackThat.ACTION_GEOFENCE_EXIT);
        /*//if user is on main activity of task activity or create task activity then
        if (this instanceof MainActivity || this instanceof TaskActivity || this instanceof CreateTaskActivity) {
            //      Show battery optimization dialog if user disable this feature.
            CommonUtils.showBatteryOptimizationDialog(this);
        }*/
        if (Objects.requireNonNull(RocketFlyer.Companion.preferenceHelper()).getLocationRequired()) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            registerReceiver(locationSwitchStateReceiver, intentFilter2);
        }
        registerShakeListener();
        registerReceiver();
    }

    public void openDialougShowLocationError(final String message) {
        if (locationDialog == null) {
            locationDialog = new Dialog(this);
            CommonUtils.showLogMessage("e", "call", "call");
            locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            locationDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(
                            Color.TRANSPARENT));
            locationDialog.setContentView(R.layout.layout_not_in_location_sdk);
            locationDialog.setCancelable(true);
            locationDialog.setCanceledOnTouchOutside(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(locationDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.dimAmount = 0.8f;
            Window window = locationDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            ImageView ivCancel = locationDialog.findViewById(R.id.ivCancel);
            ImageView ivMain = locationDialog.findViewById(R.id.ivMain);
            TextView textView = locationDialog.findViewById(R.id.tvMessage);
            textView.setText(message);

            ivMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationDialog.cancel();
                    locationDialog = null;
                }
            });
            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationDialog.cancel();
                    locationDialog = null;
                }
            });
        }

        if (!locationDialog.isShowing() && !isFinishing())
            locationDialog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (webSocketManager != null) {
//            webSocketManager.removeListener();
//        }
        //unregister when complete
        if (Objects.requireNonNull(RocketFlyer.Companion.preferenceHelper()).getLocationRequired() && locationSwitchStateReceiver != null)
            unregisterReceiver(locationSwitchStateReceiver);
        unRegisterShakeListener();
        unRegisterReceiver();
    }

    public void showDialogOK(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", onClickListener)
                .create()
                .show();
    }

    public void showDialogCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", onClickListener)
                .create()
                .show();
    }


    public void registerReceiver(){
        networkStateReceiver =new  NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        registerReceiver(
                networkStateReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
    }
    public void unRegisterReceiver(){
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unRegisterReceiver();
    }
}

