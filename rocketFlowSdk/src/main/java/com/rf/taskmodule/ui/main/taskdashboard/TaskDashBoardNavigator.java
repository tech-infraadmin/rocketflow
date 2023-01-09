package com.rf.taskmodule.ui.main.taskdashboard;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by Vikas Kesharvani on 23/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
interface TaskDashBoardNavigator extends BaseSdkNavigator {

    void handleDashboardResponse(ApiCallback apiCallback, Object result, APIError error);
    void handleStatusResponse(ApiCallback apiCallback, Object result, APIError error);
    void handleInsightsResponse(ApiCallback apiCallback, Object result, APIError error);
}
