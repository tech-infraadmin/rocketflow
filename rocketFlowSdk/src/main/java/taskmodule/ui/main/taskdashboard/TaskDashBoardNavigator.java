package taskmodule.ui.main.taskdashboard;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

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
