package com.rf.taskmodule.ui.main.taskdashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rf.taskmodule.TrackiSdkApplication;
import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.TrackiSdkApplication;
import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.request.StatusRequest;
import com.rf.taskmodule.data.model.request.TaskRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.ui.base.BaseSdkViewModel;
import com.rf.taskmodule.utils.ApiType;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;
import com.rf.taskmodule.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class TaskDashBoardViewModel extends BaseSdkViewModel<TaskDashBoardNavigator> {
    private static final String TAG = "MainViewModel";
    private HttpManager httpManager;

    public final ObservableList<Task> taskObservableArrayList = new ObservableArrayList<>();

    private MutableLiveData<List<Task>> taskListLiveData;
    private Api apiUrl;
    private DataManager dataManager;

    public MutableLiveData<List<Task>> getTaskListLiveData() {
        if (taskListLiveData == null) {
            taskListLiveData = new MutableLiveData<>();
        }
        return taskListLiveData;
    }

    void addItemsToList(@NonNull List<Task> driverList) {
        taskObservableArrayList.clear();
        taskObservableArrayList.addAll(driverList);
    }

    ObservableList<Task> getBuddyObservableArrayList() {
        return taskObservableArrayList;
    }

    public TaskDashBoardViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        this.dataManager = dataManager;
    }


    class DashBoardRequest {
        String categoryId;
        Long from;
        Long to;
        String loadBy;
        String userId;
        List<String> hubIds;
        String regionId;
        String cityId;
        String stateId;
        String placeId;

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        Long date;

        public void setDate(Long date) {
            this.date = date;
        }

        boolean userGeoReq;

        public boolean isUserGeoReq() {
            return userGeoReq;
        }

        public void setUserGeoReq(boolean userGeoReq) {
            this.userGeoReq = userGeoReq;
        }

        public List<String> getHubIds() {
            return hubIds;
        }

        public void setHubIds(List<String> hubIds) {
            this.hubIds = hubIds;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        public String getCityId() {
            return cityId;
        }

        public void setCityId(String cityId) {
            this.cityId = cityId;
        }

        public String getStateId() {
            return stateId;
        }

        public void setStateId(String stateId) {
            this.stateId = stateId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public Long getFrom() {
            return from;
        }

        public void setFrom(Long from) {
            this.from = from;
        }

        public Long getTo() {
            return to;
        }

        public void setTo(Long to) {
            this.to = to;
        }

        public String getLoadBy() {
            return loadBy;
        }

        public void setLoadBy(String loadBy) {
            this.loadBy = loadBy;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    void dashboardApi(HttpManager httpManager, String categoryId, Long from, Long to, String userId, String LOADBY, List<String> hubIds, String regionId, String stateId, String cityId, boolean userGeoReq,String placeId) {
        this.httpManager = httpManager;
        DashBoardRequest dashBoardRequest = new DashBoardRequest();
        dashBoardRequest.setCategoryId(categoryId);
        dashBoardRequest.setFrom(from);
        dashBoardRequest.setTo(to);
        dashBoardRequest.setRegionId(regionId);
        dashBoardRequest.setStateId(stateId);
        dashBoardRequest.setCityId(cityId);
        dashBoardRequest.setHubIds(hubIds);
        dashBoardRequest.setPlaceId(placeId);
        dashBoardRequest.setUserGeoReq(userGeoReq);
        dashBoardRequest.setDate(System.currentTimeMillis());
        //dashBoardRequest.setLoadBy("ASSIGNED_TO_ME");
        if(LOADBY!=null&&!LOADBY.isEmpty()) {
            dashBoardRequest.setLoadBy(LOADBY);
        }
        dashBoardRequest.setUserId(userId);
        new DashboardAPI(dashBoardRequest).hitApi();
    }

    private class StatusAPI implements ApiCallback {

        private StatusRequest statusRequest;

        StatusAPI(StatusRequest statusRequest){
            this.statusRequest = statusRequest;
        }

        @Override
        public void onResponse(Object result, @Nullable APIError error) {
            if(getNavigator()!=null) {
                Log.d("Bloacks", "Dashboard Response Called Model");
                getNavigator().handleStatusResponse(StatusAPI.this, result, error);
            }
        }

        @Override
        public void hitApi() {
            if(TrackiSdkApplication.getApiMap().containsKey(ApiType.CHANGE_STATUS)) {
                Api api = TrackiSdkApplication.getApiMap().get(ApiType.CHANGE_STATUS);
                if(dataManager!=null)
                    dataManager.callDashboardApi(StatusAPI.this, httpManager, api, statusRequest);
            }
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

    public void changeStatus(HttpManager httpManager, StatusRequest statusRequest){
        this.httpManager = httpManager;
        new StatusAPI(statusRequest).hitApi();
    }


    private class DashboardAPI implements ApiCallback {
        private DashBoardRequest dashboardRequest;

        public DashboardAPI(DashBoardRequest dashboardRequest) {
            this.dashboardRequest = dashboardRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null) {
                Log.d("Bloacks", "Dashboard Response Called Model");
                getNavigator().handleDashboardResponse(DashboardAPI.this, result, error);
            }
        }

        @Override
        public void hitApi() {
            Log.d("Bloacks", "Dashboard Request Called Model");
            if(TrackiSdkApplication.getApiMap().containsKey(ApiType.DASHBOARD_TASKS)) {
                Api api = TrackiSdkApplication.getApiMap().get(ApiType.DASHBOARD_TASKS);
                if(dataManager!=null)
                    dataManager.callDashboardApi(DashboardAPI.this, httpManager, api, dashboardRequest);
            }
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

    void getInsights(HttpManager httpManager) {
        this.httpManager = httpManager;
        new GetInsights().hitApi();
    }

    private class GetInsights implements ApiCallback {

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleInsightsResponse(GetInsights.this, result, error);
        }

        @Override
        public void hitApi() {
            Api api = TrackiSdkApplication.getApiMap().get(ApiType.GET_INSIGHTS);
            if(api!=null) {
                if(dataManager!=null)
                    dataManager.getInsights(GetInsights.this, httpManager, api);
            }
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
            if(getNavigator()!=null)
                getNavigator().showTimeOutMessage(callBack);
        }

        @Override
        public void onLogout() {

        }
    }

    public void getTaskList(HttpManager httpManager, Api api, TaskRequest buddyRequest) {
        this.httpManager = httpManager;
        this.apiUrl = api;
        new TaskList(buddyRequest).hitApi();
    }

    class TaskList implements ApiCallback {
        TaskRequest taskRequest;
        public TaskList(TaskRequest taskRequest) {
            this.taskRequest=taskRequest;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            if(getNavigator()!=null)
                getNavigator().handleResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            if(dataManager!=null)
                dataManager.getTasksList(this, httpManager, taskRequest, apiUrl);
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
            if(getNavigator()!=null)
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
            return (T) new TaskDashBoardViewModel(mDataManager, new AppSchedulerProvider());
        }
    }


}
