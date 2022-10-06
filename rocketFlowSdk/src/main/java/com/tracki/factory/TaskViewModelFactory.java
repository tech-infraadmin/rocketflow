package com.tracki.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tracki.data.DataManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.ui.tasklisting.TaskViewModel;
import com.tracki.utils.rx.AppSchedulerProvider;
import com.tracki.utils.rx.SchedulerProvider;

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