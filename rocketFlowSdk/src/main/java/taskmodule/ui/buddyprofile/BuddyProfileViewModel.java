package taskmodule.ui.buddyprofile;

import android.view.View;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import taskmodule.data.DataManager;
import taskmodule.data.model.response.config.Api;
import taskmodule.data.model.response.config.Buddy;
import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.data.network.HttpManager;
import taskmodule.ui.base.BaseSdkViewModel;
import taskmodule.utils.CommonUtils;
import taskmodule.utils.rx.AppSchedulerProvider;
import taskmodule.utils.rx.SchedulerProvider;

/**
 * Created by rahul on 9/10/18
 */
public class BuddyProfileViewModel extends BaseSdkViewModel<BuddyProfileNavigator> implements ApiCallback {
    private HttpManager httpManager;
    private Buddy buddy;
    private Api api;

    public BuddyProfileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }


    public boolean isViewNullOrEmpty(String string) {
        return CommonUtils.isViewNullOrEmpty(string);
    }

    public boolean isMobileValid(String mobile) {
        return CommonUtils.isMobileValid(mobile);
    }

    public void openTimePicker(View view) {
        getNavigator().openTimePicker(view);
    }

    void updateBuddyProfile(Buddy buddy, HttpManager httpManager, Api api) {
        this.httpManager = httpManager;
        this.buddy = buddy;
        this.api = api;
        hitApi();
    }

    @Override
    public void onResponse(Object result, APIError error) {
        getNavigator().handleResponse(this, result, error);
    }

    @Override
    public void hitApi() {
        getDataManager().updateBuddy(this, httpManager, buddy, api);
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
            return (T) new BuddyProfileViewModel(mDataManager, new AppSchedulerProvider());
        }
    }

}
