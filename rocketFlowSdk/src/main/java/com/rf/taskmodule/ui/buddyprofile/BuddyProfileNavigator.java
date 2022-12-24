package com.rf.taskmodule.ui.buddyprofile;

import android.view.View;

import com.rf.taskmodule.ui.base.BaseSdkNavigator;

import com.rf.taskmodule.ui.base.BaseSdkNavigator;

/**
 * Created by rahul on 9/10/18
 */
public interface BuddyProfileNavigator extends BaseSdkNavigator {
    void validate();

    void openTimePicker(View view);

    void openAddFleet();
}
