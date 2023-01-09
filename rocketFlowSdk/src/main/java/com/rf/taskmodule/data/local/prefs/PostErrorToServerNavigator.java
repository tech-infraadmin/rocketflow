package com.rf.taskmodule.data.local.prefs;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

public interface PostErrorToServerNavigator extends BaseSdkNavigator {



    void handlePostErrorResponse(ApiCallback apiCallback, Object result, APIError error);


}

