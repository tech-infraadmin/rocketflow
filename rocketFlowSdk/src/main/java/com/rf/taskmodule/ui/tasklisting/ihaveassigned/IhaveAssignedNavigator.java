package com.rf.taskmodule.ui.tasklisting.ihaveassigned;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface IhaveAssignedNavigator extends BaseSdkNavigator {
    //    void handleCancelTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    //    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error);


}
