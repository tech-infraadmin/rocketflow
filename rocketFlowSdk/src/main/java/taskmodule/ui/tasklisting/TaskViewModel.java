package taskmodule.ui.tasklisting;

import taskmodule.TrackiSdkApplication;
import taskmodule.data.DataManager;
import taskmodule.data.model.request.BuddiesRequest;
import taskmodule.data.model.request.FleetRequest;
import taskmodule.data.model.request.InventoryRequest;
import taskmodule.data.model.response.config.Api;
import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.data.network.HttpManager;
import taskmodule.ui.base.BaseSdkViewModel;
import taskmodule.utils.ApiType;
import taskmodule.utils.BuddyInfo;
import taskmodule.utils.BuddyStatus;
import taskmodule.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 8/10/18
 */
public class TaskViewModel extends BaseSdkViewModel<TaskNavigator> {
    private HttpManager httpManager;

    public TaskViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

    void checkBuddy(HttpManager httpManager) {
        this.httpManager = httpManager;
        new BuddyListingAPI().hitApi();
    }

    void checkFleet(HttpManager httpManager) {
        this.httpManager = httpManager;
        new FleetListingAPI().hitApi();
    }

    public void sdkToken(String sdkClintId, HttpManager httpManager,ApiCallback callback){
        this.httpManager = httpManager;
        if(getDataManager()!=null) {
            getDataManager().getSDKLoginToken(sdkClintId,this.httpManager, callback);
        }
    }

    private class BuddyListingAPI implements ApiCallback {
        private BuddiesRequest buddyRequest;
        private Api api;

        BuddyListingAPI() {
            List<BuddyStatus> statusList = new ArrayList<>();
            statusList.add(BuddyStatus.ON_TRIP);
            statusList.add(BuddyStatus.OFFLINE);
            statusList.add(BuddyStatus.IDLE);
            buddyRequest = new BuddiesRequest(statusList, BuddyInfo.TRACKED_BY_ME, true);
            api = TrackiSdkApplication.getApiMap().get(ApiType.BUDDIES);
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkBuddyResponse(BuddyListingAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().buddyListing(BuddyListingAPI.this, httpManager, api, buddyRequest);
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

    private class FleetListingAPI implements ApiCallback {

        FleetRequest fleetRequest = new FleetRequest("ALL");
        Api api = TrackiSdkApplication.getApiMap().get(ApiType.FLEETS);

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkFleetResponse(FleetListingAPI.this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().fleetListing(FleetListingAPI.this, httpManager, fleetRequest, api);
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

    void checkInventoryData(HttpManager httpManager, InventoryRequest request) {
        this.httpManager = httpManager;
        new CheckInventory(request).hitApi();
    }

    class CheckInventory implements ApiCallback {
        InventoryRequest data;

        public CheckInventory(InventoryRequest data) {
            this.data = data;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().checkInventory(this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiSdkApplication.getApiMap().get(ApiType.GET_TASK_INVENTORIES);

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
}