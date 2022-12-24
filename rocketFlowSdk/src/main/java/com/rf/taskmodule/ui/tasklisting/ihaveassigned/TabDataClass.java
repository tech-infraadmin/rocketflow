package com.rf.taskmodule.ui.tasklisting.ihaveassigned;

import com.rf.taskmodule.ui.base.BaseSdkFragment;
import com.rf.taskmodule.ui.base.BaseSdkFragment;

/**
 * Created by Vikas Kesharvani on 08/07/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
public class TabDataClass {
    BaseSdkFragment fragment;
    String title;

    public BaseSdkFragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }

    public TabDataClass(BaseSdkFragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }
}
