package taskmodule.ui.addfleet;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 6/9/18
 */
public interface AddFleetNavigator extends BaseSdkNavigator {
    void validateViews();

    void uploadImage();

    void handleFleetPicResponse(ApiCallback apiCallback, Object result, APIError error);
}
