package taskmodule.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import taskmodule.data.DataManager;
import taskmodule.ui.tasklisting.TaskViewModel;
import taskmodule.utils.rx.AppSchedulerProvider;

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