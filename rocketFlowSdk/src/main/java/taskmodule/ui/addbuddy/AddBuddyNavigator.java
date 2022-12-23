package taskmodule.ui.addbuddy;

import android.view.View;

import taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 18/9/18
 */
public interface AddBuddyNavigator extends BaseSdkNavigator {
    void validate();

    void onBackClick();

    void openTimePicker(View view);
}
