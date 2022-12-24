package com.rf.taskmodule.ui.addbuddy;

import android.view.View;

import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;
import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.request.AddBuddyRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.ui.base.BaseSdkViewModel;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;
import com.rf.taskmodule.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 18/9/18
 */
public class AddBuddyViewModel extends BaseSdkViewModel<AddBuddyNavigator> implements ApiCallback {
    ObservableArrayList<String> vehicleList = new ObservableArrayList<>();
    private HttpManager httpManager;
    private AddBuddyRequest addBuddyRequest;
    private Api api;

    AddBuddyViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public ObservableArrayList<String> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(ObservableArrayList<String> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public void onFabClick() {
        getNavigator().validate();
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }

    public boolean isViewNullOrEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

  /*  public boolean isEmailValid(String email) {
        return !CommonUtils.isViewNullOrEmpty(email) && CommonUtils.isEmailValid(email);
    }*/

    public boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    public void openTimePicker(View view) {
        getNavigator().openTimePicker(view);
    }

    public void addBuddyInvite(HttpManager httpManager, AddBuddyRequest addBuddyRequest, Api api) {
        this.httpManager = httpManager;
        this.addBuddyRequest = addBuddyRequest;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().addBuddyInvitation(this, httpManager, addBuddyRequest, api);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onNetworkErrorClose() {

    }

    @Override
    public void onRequestTimeOut(ApiCallback callBack) {
        getNavigator().showTimeOutMessage(callBack);
    }

    @Override
    public void onLogout() {

    }

    static class Factory implements ViewModelProvider.Factory {
        private final DataManager mDataManager;

        Factory(DataManager mDataManager) {
            this.mDataManager = mDataManager;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AddBuddyViewModel(mDataManager, new AppSchedulerProvider());
        }
    }
}
