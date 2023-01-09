package com.rf.taskmodule.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rf.taskmodule.data.DataManager;
import com.rf.taskmodule.ui.tasklisting.TaskViewModel;
import com.rf.taskmodule.utils.rx.AppSchedulerProvider;

public class TaskViewModelFactory implements ViewModelProvider.Factory {
    private final DataManager mDataManager;


    public TaskViewModelFactory(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TaskViewModel(mDataManager, new AppSchedulerProvider());
    }
}