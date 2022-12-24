package com.rf.taskmodule.ui.tasklisting;

import com.rf.taskmodule.data.model.response.config.Task;

public interface TaskItemClickListener {
    void onItemClick(Task task, int position);

    void onCallClick(String mobile);

    void onDetailsTaskClick(Task task);

}
