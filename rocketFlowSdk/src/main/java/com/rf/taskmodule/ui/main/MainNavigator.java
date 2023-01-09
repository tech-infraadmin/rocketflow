package com.rf.taskmodule.ui.main;

import com.rf.taskmodule.data.model.request.PunchInOut;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;

import com.rf.taskmodule.data.model.request.PunchInOut;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by rahul on 6/9/18
 */
public interface MainNavigator extends BaseSdkNavigator {
    void checkBuddyResponse(ApiCallback callback, Object result, APIError error);

    void checkFleetResponse(ApiCallback callback, Object result, APIError error);

    void onSuccessfulLogout(ApiCallback apiCallback, Object result, APIError error);



    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);

    void handlePunchInOutResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                  @Nullable APIError error, PunchInOut event);

    void handleOnlineOfflineResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                     @Nullable APIError error);
    void handleGetTaskData(@NotNull ApiCallback apiCallback, @Nullable Object result,
                           @Nullable APIError error);

    void checkInventoryResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                                @Nullable APIError error);


    void handleUnsubscribeResponse(ApiCallback apiCallback, Object result, APIError error);


    void handleMyPlaceResponse(@NotNull ApiCallback apiCallback, @Nullable Object result,
                           @Nullable APIError error);



//    void setCurrentLocationMarker(Location location);
}
