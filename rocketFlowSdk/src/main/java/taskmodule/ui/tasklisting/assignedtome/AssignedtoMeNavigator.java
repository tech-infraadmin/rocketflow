package taskmodule.ui.tasklisting.assignedtome;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul abrol on 05/10/18.
 */

public interface AssignedtoMeNavigator extends BaseSdkNavigator {

//    void acceptTaskResponse(ApiCallback apiCallback, Object result, APIError error);
//
//    void handleStartTaskResponse(ApiCallback apiCallback, Object result, APIError error);
//
//    void handleCancelTaskResponse(ApiCallback apiCallback, Object result, APIError error);

//    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);

//    void handleArriveReachTaskResponse(ApiCallback apiCallback, Object result, APIError error);

    void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error);


}
