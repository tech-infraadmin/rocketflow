package com.rf.taskmodule.ui.addfleet;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.request.AddFleetRequest;
import com.rf.taskmodule.data.model.request.UpdateFileRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;
import com.rf.taskmodule.utils.rx.SchedulerProvider;
import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.request.AddFleetRequest;
import com.rf.taskmodule.data.model.request.UpdateFileRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.ui.base.BaseSdkViewModel;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 6/9/18
 */
public class AddFleetActivityViewModel extends BaseSdkViewModel<AddFleetNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Api api;
    private AddFleetRequest addFleetRequest;
    private UpdateFileRequest updateFileRequest;

    AddFleetActivityViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    public void onUploadClick() {
        getNavigator().uploadImage();
    }

    public void onSubmitClick() {
        getNavigator().validateViews();
    }

    public boolean isViewEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

    public void addFleet(HttpManager httpManager, AddFleetRequest addFleetRequest, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.addFleetRequest = addFleetRequest;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().addFleet(this, httpManager, addFleetRequest, api);
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

    void uploadFleetPic(UpdateFileRequest file, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.api = api;
        this.updateFileRequest = file;
        new UpdateProfilePic().hitApi();
    }

    class UpdateProfilePic implements ApiCallback {
        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleFleetPicResponse(UpdateProfilePic.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().updateProfilePic(UpdateProfilePic.this, httpManager, updateFileRequest, api);
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
    }

    static class Factory implements ViewModelProvider.Factory {
        private final DataManager mDataManager;

        Factory(DataManager mDataManager) {
            this.mDataManager = mDataManager;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AddFleetActivityViewModel(mDataManager, new AppSchedulerProvider());
        }
    }
}
