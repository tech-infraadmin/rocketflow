package taskmodule.ui.buddylisting;

import taskmodule.data.network.APIError;
import taskmodule.data.network.ApiCallback;
import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 14/9/18
 */
public interface BuddyListingNavigator extends BaseSdkNavigator {
    void onSubmitClick();

//    void onRetryClick();

    void openAddBuddyActivity();

    void refreshBuddyList(ApiCallback apiCallback, Object result, APIError error);
}
