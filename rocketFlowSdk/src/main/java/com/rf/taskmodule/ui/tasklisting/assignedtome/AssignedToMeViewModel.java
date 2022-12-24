package com.rf.taskmodule.ui.tasklisting.assignedtome;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.data.model.request.ExecuteUpdateRequest;
import com.rf.taskmodule.data.model.request.TaskRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.ui.base.BaseSdkViewModel;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;
import com.rf.taskmodule.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by rahul abrol on 05/10/18.
 */

public class AssignedToMeViewModel extends BaseSdkViewModel<AssignedtoMeNavigator> {

    public final ObservableList<Task> taskObservableArrayList = new ObservableArrayList<>();

    private MutableLiveData<List<Task>> taskListLiveData;
    private HttpManager httpManager;
    private Api apiUrl;

    public AssignedToMeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }

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
            if(getDataManager()!=null)
            getDataManager().getTasksList(this, httpManager, taskRequest, apiUrl);
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



    void executeUpdates(HttpManager httpManager, ExecuteUpdateRequest request, Api api) {
        this.httpManager = httpManager;
        this.apiUrl = api;
        new ExecuteUpdateTask(request).hitApi();
    }

    class ExecuteUpdateTask implements ApiCallback {

        ExecuteUpdateRequest request;

        ExecuteUpdateTask(ExecuteUpdateRequest request) {
            this.request = request;
        }

        @Override
        public void onResponse(Object result, APIError error) {
            getNavigator().handleExecuteUpdateResponse(this, result, error);
        }

        @Override
        public void hitApi() {
            getDataManager().executeUpdateTask(this, httpManager, request, apiUrl);
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
            return (T) new AssignedToMeViewModel(mDataManager, new AppSchedulerProvider());
        }
    }


}
