package taskmodule.ui.tasklisting.ihaveassigned;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface IhaveAssignedNavigator extends BaseSdkNavigator {
    //    void handleCancelTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    //    void handleEndTaskResponse(ApiCallback apiCallback, Object result, APIError error);
    void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error);


}
