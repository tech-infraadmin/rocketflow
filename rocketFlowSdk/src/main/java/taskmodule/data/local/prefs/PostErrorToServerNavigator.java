package taskmodule.data.local.prefs;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

public interface PostErrorToServerNavigator extends BaseSdkNavigator {



    void handlePostErrorResponse(ApiCallback apiCallback, Object result, APIError error);


}

