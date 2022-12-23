package taskmodule.data.network;

import androidx.annotation.Nullable;

import taskmodule.data.network.APIError;

/**
 * Created by Rahul Abrol on 4/6/18.
 */
public interface ApiCallback {

    void onResponse(@Nullable Object result, @Nullable APIError error);

    void hitApi();

    boolean isAvailable();

    void onNetworkErrorClose();

    void onRequestTimeOut(ApiCallback callBack);

    void onLogout();
}
