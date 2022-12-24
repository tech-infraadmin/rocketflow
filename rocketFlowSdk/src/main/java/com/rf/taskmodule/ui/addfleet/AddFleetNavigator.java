package com.rf.taskmodule.ui.addfleet;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 6/9/18
 */
public interface AddFleetNavigator extends BaseSdkNavigator {
    void validateViews();

    void uploadImage();

    void handleFleetPicResponse(ApiCallback apiCallback, Object result, APIError error);
}
