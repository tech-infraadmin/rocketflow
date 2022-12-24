package com.rf.taskmodule.ui.tasklisting;

import com.rf.taskmodule.data.model.response.config.Task;

import com.rf.taskmodule.data.model.response.config.Task;

public interface TaskClickListener {

    void onItemClick(Task task, int position);

    void onClickMapIcon(Task task, int position);

    void onCallClick(Task task, int position);

    void onExecuteUpdates(String name, Task task, String cta);

    void onDetailsTaskClick(Task task);

     void onChatClick(String buddyId,String name);

}
