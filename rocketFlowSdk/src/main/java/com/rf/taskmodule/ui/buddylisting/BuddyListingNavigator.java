package com.rf.taskmodule.ui.buddylisting;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 14/9/18
 */
public interface BuddyListingNavigator extends BaseSdkNavigator {
    void onSubmitClick();

//    void onRetryClick();

    void openAddBuddyActivity();

    void refreshBuddyList(ApiCallback apiCallback, Object result, APIError error);
}
