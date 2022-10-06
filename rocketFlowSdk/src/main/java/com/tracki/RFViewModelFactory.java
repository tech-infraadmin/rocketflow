package com.tracki;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tracki.data.DataManager;
import com.tracki.ui.base.BaseViewModel;
import com.tracki.utils.rx.SchedulerProvider;

public class RFViewModelFactory implements ViewModelProvider.Factory {
    private final DataManager mDataManager;
    private final SchedulerProvider mSchedulerProvider;


    public RFViewModelFactory(DataManager dataManager, SchedulerProvider schedulerProvider) {
        mDataManager = dataManager;
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new BaseViewModel<>(mDataManager, mSchedulerProvider);
    }
}
