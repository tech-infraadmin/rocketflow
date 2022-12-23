package taskmodule.ui.fleetlisting;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 14/9/18
 */
public interface FleetListingNavigator extends BaseSdkNavigator {
    void onProceedClick();

//    void onRetryClick();

    void openAddFleetActivity();

    void refreshFleetList(ApiCallback apiCallback, Object result, APIError error);

    void changeFleetStatus(ApiCallback apiCallback, Object result, APIError error);
}
