package taskmodule.ui.buddylisting;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import taskmodule.data.DataManager;
import taskmodule.data.model.request.BuddiesRequest;
import taskmodule.data.model.request.DeleteBuddyRequest;
import taskmodule.data.model.response.config.Api;
import taskmodule.data.model.response.config.Buddy;
import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.data.network.HttpManager;
import taskmodule.ui.base.BaseSdkViewModel;
import taskmodule.utils.rx.AppSchedulerProvider;
import taskmodule.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by rahul on 14/9/18
 */
public class BuddyListingViewModel extends BaseSdkViewModel<BuddyListingNavigator> {

    final ObservableList<Buddy> buddyObservableArrayList = new ObservableArrayList<>();

    // Create a LiveData with a List of @Buddy
    private MutableLiveData<List<Buddy>> buddyListLiveData;
    private HttpManager httpManager;
    private Api apiUrl;


    BuddyListingViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    MutableLiveData<List<Buddy>> getBuddyListLiveData() {
        if (buddyListLiveData == null) {
            buddyListLiveData = new MutableLiveData<>();
        }
        return buddyListLiveData;
    }

    void addItemsToList(@NonNull List<Buddy> driverList) {
        buddyObservableArrayList.clear();
        buddyObservableArrayList.addAll(driverList);
    }

    ObservableList<Buddy> getBuddyObservableArrayList() {
        return buddyObservableArrayList;
    }

    public void onProceedClick() {
        getNavigator().onSubmitClick();
    }

    public void onFabClick() {
        getNavigator().openAddBuddyActivity();
    }

    void fetchBuddyList(BuddiesRequest buddyRequest, HttpManager httpManager, Api apiUrl) {
        this.httpManager = httpManager;
        this.apiUrl = apiUrl;
        //createBuddyList(selection);
        new GetBuddyListing(buddyRequest).hitApi();
    }

    void deleteBuddy(HttpManager httpManager, DeleteBuddyRequest buddyRequest, Api api) {
        this.httpManager = httpManager;
        this.apiUrl = api;
        new DeleteBuddy(buddyRequest).hitApi();
    }

    class DeleteBuddy implements ApiCallback {
        private DeleteBuddyRequest buddyRequest;

        DeleteBuddy(DeleteBuddyRequest buddyRequest) {
            this.buddyRequest = buddyRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().refreshBuddyList(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().deleteBuddy(this, httpManager, apiUrl, buddyRequest);
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

    class GetBuddyListing implements ApiCallback {
        private BuddiesRequest buddyRequest;

        GetBuddyListing(BuddiesRequest buddyRequest) {
            this.buddyRequest = buddyRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().buddyListing(this, httpManager, apiUrl, buddyRequest);
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
            return (T) new BuddyListingViewModel(mDataManager, new AppSchedulerProvider());
        }
    }
}
