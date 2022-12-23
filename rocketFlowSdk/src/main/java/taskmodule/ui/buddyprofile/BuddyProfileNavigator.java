package taskmodule.ui.buddyprofile;

import android.view.View;

import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface BuddyProfileNavigator extends BaseSdkNavigator {
    void validate();

    void openTimePicker(View view);

    void openAddFleet();
}
