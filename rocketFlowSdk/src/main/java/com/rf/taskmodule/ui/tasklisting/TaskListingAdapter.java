package com.rf.taskmodule.ui.tasklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.rf.taskmodule.data.model.response.config.ActionConfig;
import com.rf.taskmodule.data.model.response.config.Navigation;
import com.rf.taskmodule.data.model.response.config.Service;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeEmptyItemViewModel;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.data.model.response.config.ActionConfig;
import com.rf.taskmodule.data.model.response.config.Navigation;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.databinding.ItemAssignedToMeEmptyViewSdkBinding;
import com.rf.taskmodule.databinding.ItemRowTaskListSdkBinding;
import com.rf.taskmodule.ui.base.BaseSdkViewHolder;
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeEmptyItemViewModel;
import com.rf.taskmodule.ui.webview.WebViewActivity;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class TaskListingAdapter extends RecyclerView.Adapter<BaseSdkViewHolder> {
    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Task> mResponseList;
    private TaskItemClickListener mListener;
    private Context context;
    private PreferencesHelper preferencesHelper;
    private String categoryId = null;
    private boolean assignedToTask;


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setAssignedToTask(boolean assignedToTask) {
        this.assignedToTask = assignedToTask;
    }


    public TaskListingAdapter(List<Task> responseList, PreferencesHelper preferencesHelper) {
        this.mResponseList = responseList;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public int getItemCount() {
        if (mResponseList != null && mResponseList.size() > 0) {
            return mResponseList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mResponseList != null && !mResponseList.isEmpty()) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseSdkViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseSdkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                ItemRowTaskListSdkBinding itemAssignedToMeViewBinding = ItemRowTaskListSdkBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new TaskViewHolder(itemAssignedToMeViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemAssignedToMeEmptyViewSdkBinding emptyViewBinding = ItemAssignedToMeEmptyViewSdkBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Task> stringList) {
        //  clearItems();

        mResponseList.addAll(stringList);
        Set<Task> set = new TreeSet<>(new TaskComparator());
        set.addAll(mResponseList);
        mResponseList.clear();
        mResponseList.addAll(set);
        notifyDataSetChanged();
    }

    public Task getTaskByTaskId(String taskId) {
        if (mResponseList != null && !mResponseList.isEmpty()) {
            Task reqTask = new Task();
            reqTask.setTaskId(taskId);
            if (mResponseList.contains(reqTask)) {
                int position = mResponseList.indexOf(reqTask);
                if (position != -1) {
                    return mResponseList.get(position);
                }
            }
        }
        return null;
    }

    public List<Task> getList() {
        return mResponseList;
    }

    public void clearItems() {
        mResponseList.clear();
        notifyDataSetChanged();
    }

    public void setListener(TaskItemClickListener listener) {
        this.mListener = listener;
    }

    public void updateTask(int pos, Task taskDetail) {
        Log.d("updateTask", taskDetail.getCurrentStage().getName());
        Log.d("updateTask", pos + "");
        try{
            mResponseList.set(pos, taskDetail);
            notifyItemChanged(pos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class TaskViewHolder extends BaseSdkViewHolder implements AssignTaskViewModel
            .TaskItemViewModelListener {
        private ItemRowTaskListSdkBinding mBinding;

        TaskViewHolder(ItemRowTaskListSdkBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Task task = mResponseList.get(position);

            if (task.getServiceIds() != null) {
                if (task.getServiceIds().size() > 0) {
                    mBinding.llServices.setVisibility(View.VISIBLE);
                    ArrayList<Service> services = new ArrayList<>(preferencesHelper.getServices());
                    ArrayList<Service> finalServices = new ArrayList<>();
                    for (int i = 0; i < task.getServiceIds().size(); i++) {
                        for (int j = 0; j < services.size(); j++) {
                            if (task.getServiceIds().get(i).equals(services.get(j).getId())) {
                                finalServices.add(services.get(j));
                            }
                        }
                    }

                    RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    mBinding.recyclerViewServices.setLayoutManager(manager);
                    TaskServicesListAdapter taskServicesListAdapter = new TaskServicesListAdapter(finalServices);
                    mBinding.recyclerViewServices.setAdapter(taskServicesListAdapter);
                } else {
                    mBinding.llServices.setVisibility(View.GONE);
                }
            } else {
                mBinding.llServices.setVisibility(View.GONE);
            }

            AssignTaskViewModel itemViewModel = new AssignTaskViewModel(task, this, context, preferencesHelper, categoryId, assignedToTask);
            mBinding.setViewModel(itemViewModel);
            if (task.getTrackingUrl() != null) {
                mBinding.imageTracking.setVisibility(View.VISIBLE);
                Log.d("Tacking Url", task.getTrackingUrl());
            } else {
                mBinding.imageTracking.setVisibility(View.GONE);
            }
            //  itemView.setOnClickListener(view -> mListener.onItemClick(task, position));

            mBinding.imageTracking.setOnClickListener(view -> {
                if (task.getTrackingUrl() != null) {
                    Navigation navigation = new Navigation();
                    ActionConfig actionConfig = new ActionConfig();
                    actionConfig.setActionUrl(task.getTrackingUrl());
                    navigation.setActionConfig(actionConfig);
                    navigation.setTitle("Tracking Details");
                    context.startActivity(WebViewActivity.Companion.newIntent(context)
                            .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation));
                }
            });
        }

        @Override
        public void onItemClick(Task bean) {
            Log.d("updateTask", getAdapterPosition() + "");
            mListener.onItemClick(bean, getAdapterPosition());
        }

        @Override
        public void onCallClick(String mobile) {
            mListener.onCallClick(mobile);
        }

        @Override
        public void onDetailsClick(Task task) {
            mListener.onDetailsTaskClick(task);

        }
    }

    public class EmptyViewHolder extends BaseSdkViewHolder implements
            AssignedtoMeEmptyItemViewModel.AssignedtoMeEmptyItemViewModelListener {

        private ItemAssignedToMeEmptyViewSdkBinding mBinding;

        EmptyViewHolder(ItemAssignedToMeEmptyViewSdkBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AssignedtoMeEmptyItemViewModel emptyItemViewModel = new AssignedtoMeEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
            //     mListener.onRetryClick();
        }
    }

}
