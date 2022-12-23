package taskmodule.ordercode;

import android.view.View;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import taskmodule.data.DataManager;
import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.data.network.HttpManager;
import taskmodule.ui.base.BaseSdkViewModel;
import taskmodule.utils.CommonUtils;
import taskmodule.utils.rx.AppSchedulerProvider;
import taskmodule.utils.rx.SchedulerProvider;


public class OrderCodeViewModel extends BaseSdkViewModel<OrderCodeNavigator> implements ApiCallback {
    private HttpManager httpManager;

    OrderCodeViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);

    }


    public void onBackClick() {
        getNavigator().onBackClick();
    }


    public void onButtonClick(View view) {
        CommonUtils.preventTwoClick(view);
    }


    @Override
    public void onResponse(Object result, @Nullable APIError error) {

    }

    @Override
    public void hitApi() {

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
            return (T) new OrderCodeViewModel(mDataManager, new AppSchedulerProvider());
        }
    }


}
