package com.rf.taskmodule.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.utils.AnalyticsHelper;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.data.network.ApiCallback;
//import com.rf.taskmodule.ui.login.LoginActivity;
import com.rf.taskmodule.utils.AnalyticsHelper;
import com.rf.taskmodule.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rahul.
 */

public abstract class BaseSdkFragment<T extends ViewDataBinding, V extends BaseSdkViewModel<?>> extends Fragment {

    private final String TAG = BaseSdkFragment.class.getName();


    public AnalyticsHelper analyticsHelper;
    private BaseSdkActivity mActivity;
    private View mRootView;
    private T mViewDataBinding;
    private V mViewModel;
    //protected WebSocketManager webSocketManager = null;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseSdkActivity) {
            BaseSdkActivity activity = (BaseSdkActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection();
        super.onCreate(savedInstanceState);
        mViewModel = getViewModel();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();
        return mRootView;
    }

    public View getRootView() {
        return mRootView;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();
    }

    public BaseSdkActivity getBaseActivity() {
        return mActivity;
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
//            Intent intent = LoginActivity.newIntent(getBaseActivity());
//            intent.putExtra(AppConstants.Extra.EXTRA_LOGOUT, true);
//            mActivity.openActivityOnTokenExpire(intent);
        }
    }

    private void performDependencyInjection() {
        //AndroidSupportInjection.inject(this);
    }

    public void showTimeOutMessage(@NonNull ApiCallback callBack) {
        getBaseActivity().showTimeOutMessage(callBack);
    }

    public void showLoading() {
        hideLoading();
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }
    /**
     * Init socket if null
     */
    protected void initSocket() {
//        if (webSocketManager == null) {
//            String userId = mActivity.sharedPreferences.getUserDetail().getUserId();
//            String t = mActivity.sharedPreferences.getLoginToken();
//            String url = mActivity.sharedPreferences.getChatUrl();
//
//            Log.e(TAG, "initSocket() called");
//            //initialize socket
//            webSocketManager = WebSocketManager.getInstance(url, userId, t);
//            if(webSocketManager==null)
//                Log.e(TAG, "webSocketManager is Null");
//        }
    }
    /**
     * Check if socket is connected the hit connection with callback
     *
     * //@param socketListener listener for socket events
     */
   // protected void connectSocket(@NonNull WebSocketManager.SocketListener socketListener) {
//        if (webSocketManager != null) {
//            webSocketManager.addListener(socketListener);
//            Log.e(TAG, "connectSocket() called");
//        }
    //}
    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    protected void showDialogOK(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getBaseActivity())
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", onClickListener)
                .create()
                .show();
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ActivityCompat.checkSelfPermission(getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            String[] permi = new String[listPermissionsNeeded.size()];
            permi = listPermissionsNeeded.toArray(permi);
            requestPermissions(permi, AppConstants.PERMISSIONS_REQUEST_CODE_MULTIPLE);
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (webSocketManager != null) {
//            webSocketManager.removeListener();
//        }

    }
    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
