package com.rf.taskmodule.ui.tasklisting.ihaveassigned;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rf.taskmodule.data.model.request.AcceptRejectRequest;
import com.rf.taskmodule.data.model.request.SaveFilterData;
import com.rf.taskmodule.data.model.request.TaskRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.model.response.config.CallToActions;
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem;
import com.rf.taskmodule.data.model.response.config.Service;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.data.model.response.config.TaskListing;
import com.rf.taskmodule.data.model.response.config.TaskResponse;
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.databinding.FragmentIHaveAssignedSdkBinding;
import com.rf.taskmodule.utils.ApiType;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.BuddyInfo;
import com.rf.taskmodule.utils.DateTimeUtil;
import com.rf.taskmodule.utils.JSONConverter;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.utils.TaskStatus;
import com.rf.taskmodule.utils.TrackiToast;
import com.rocketflow.sdk.RocketFlyer;
import com.rf.taskmodule.BR;
import com.rf.taskmodule.R;
import com.rf.taskmodule.TrackiSdkApplication;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.ui.base.BaseSdkFragment;
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity;
//import com.rf.taskmodule.ui.inventory.InventoryActivity;
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity;
import com.rf.taskmodule.ui.tasklisting.PaginationListener;
import com.rf.taskmodule.ui.tasklisting.PagingData;
import com.rf.taskmodule.ui.tasklisting.StageListAdapter;
import com.rf.taskmodule.ui.tasklisting.TaskItemClickListener;
import com.rf.taskmodule.ui.tasklisting.TaskListingAdapter;
import com.rf.taskmodule.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.rf.taskmodule.ui.tasklisting.PaginationListener.PAGE_START;

/**
 * Created by rahul on 8/10/18
 */
public class IhaveAssignedFragment extends BaseSdkFragment<FragmentIHaveAssignedSdkBinding, IhaveAssignedViewModel>
        implements IhaveAssignedNavigator, TaskItemClickListener, SwipeRefreshLayout.OnRefreshListener, StageListAdapter.DashBoardListener, View.OnClickListener {

    private static final String TAG = IhaveAssignedFragment.class.getSimpleName();

    int pos = 0;
    Task clickedTask;

    TaskListingAdapter mIhaveAssignedAdapter;
    LinearLayoutManager mLayoutManager;
    HttpManager httpManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    PreferencesHelper preferencesHelper;

    private CardView cvStages;
    private FragmentIHaveAssignedSdkBinding mFragmentIHaveAssignedSdkBinding;
    private Api api;
    private TaskRequest buddyRequest;
    private IhaveAssignedViewModel mIhaveAssignedViewModel;

    private Map<String, String> categoryMap;
    private CallToActions callToActions = null;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private setIHaveChatListener mListener;
    private long fromDate;
    private long toDate;
    private Chip tvFromDate;
    private CardView cardFromDate;
    private ImageButton mButtonSubmit;
    private EditText etSearch;
    private Date today;
    private String stageId = null;
    private String categoryId = null;
    private MediaPlayer mediaPlayer = null;
    LinkedHashMap<String, String> mStageNameMap = null;
    private String regionId;
    private List<String> hubIds;
    private String stateId;
    private String cityId;
    private String hubIdStr;
    private boolean isMerchantTab;
    private boolean userGeoReq = false;

    public static IhaveAssignedFragment newInstance(String value, long fromDate, long toDate, boolean isMerchantTab, boolean geoReq, String categoryName) {
        Bundle args = new Bundle();
        args.putString(AppConstants.Extra.EXTRA_CATEGORIES, value);
        args.putLong(AppConstants.Extra.FROM_DATE, fromDate);
        args.putLong(AppConstants.Extra.FROM_TO, toDate);
        args.putBoolean(AppConstants.Extra.GEO_FILTER, geoReq);
        args.putBoolean(AppConstants.Extra.IS_MERCHANT_TAB, isMerchantTab);
        args.putString(AppConstants.Extra.EXTRA_CATEGORIES_NAME, categoryName);
        IhaveAssignedFragment fragment = new IhaveAssignedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rvIhaveAssigned;
    private RecyclerView rvStages;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_i_have_assigned_sdk;
    }

    @Override
    public IhaveAssignedViewModel getViewModel() {

        IhaveAssignedViewModel.Factory factory = new IhaveAssignedViewModel.Factory(RocketFlyer.Companion.dataManager());
        mIhaveAssignedViewModel = ViewModelProviders.of(this, factory).get(IhaveAssignedViewModel.class);
        return mIhaveAssignedViewModel;
    }

    String categoryName = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        httpManager = RocketFlyer.Companion.httpManager();
        preferencesHelper = RocketFlyer.Companion.preferenceHelper();


        mIhaveAssignedAdapter = new TaskListingAdapter(new ArrayList<>(), RocketFlyer.Companion.preferenceHelper());

        mIhaveAssignedViewModel.setNavigator(this);
        mIhaveAssignedAdapter.setListener(this);


        List<TaskStatus> statusList = new ArrayList<>();


        if (getArguments() != null) {
            String str = getArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES);
            categoryName = getArguments().getString(AppConstants.Extra.EXTRA_CATEGORIES_NAME);
            Log.e("EXTRA_CATEGORIES", "12 " + str);
            isMerchantTab = getArguments().getBoolean(AppConstants.Extra.IS_MERCHANT_TAB, false);
            userGeoReq = getArguments().getBoolean(AppConstants.Extra.GEO_FILTER, false);
            Log.e("userGeoReq", "" + userGeoReq);
            categoryMap = new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {
            }.getType());
            if (categoryMap != null && categoryMap.containsKey("categoryId"))
                categoryId = categoryMap.get("categoryId");
            fromDate = getArguments().getLong(AppConstants.Extra.FROM_DATE);
            toDate = getArguments().getLong(AppConstants.Extra.FROM_TO);
            //  showHideFilter(categoryId);
            // CommonUtils.showLogMessage("e","categoryId",categoryMap.get("categoryId"));
        }
        if (isMerchantTab)
            buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_TO_MERCHANT, categoryMap);
        else {
            buddyRequest = new TaskRequest(statusList, BuddyInfo.ASSIGNED_BY_ME, categoryMap);
        }
        buddyRequest.setFrom(fromDate);
        buddyRequest.setTo(toDate);
        buddyRequest.setUserGeoReq(userGeoReq);
        if ((preferencesHelper.getFilterMap() != null)) {
            if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                if (saveFilterData != null) {
                    buddyRequest.setRegionId(saveFilterData.getRegionId());
                    buddyRequest.setStateId(saveFilterData.getStateId());
                    buddyRequest.setCityId(saveFilterData.getCityId());
                    hubIdStr = saveFilterData.getHubId();
                    if (hubIdStr != null && !hubIdStr.isEmpty()) {
                        hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                    }
                    buddyRequest.setHubIds(hubIds);
                }

            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentIHaveAssignedSdkBinding = getViewDataBinding();
        mIhaveAssignedAdapter.setAssignedToTask(false);
        setUp();
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));
        subscribeToLiveData();
        perFormStageTask(categoryMap);
    }

    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        super.setUserVisibleHint(isUserVisible);
        // when fragment visible to user and view is not null then enter here.
        if (isUserVisible && getRootView() != null) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (clickedTask == null) {
            if (!getUserVisibleHint()) {
                return;
            }
            isLastPage = false;
            currentPage = PAGE_START;
            PagingData pagingData = new PagingData();
            pagingData.setDatalimit(10);
            pagingData.setPageOffset(0);
            pagingData.setPageIndex(currentPage);
            buddyRequest.setPaginationData(pagingData);
            buddyRequest.setUserGeoReq(userGeoReq);
            if ((preferencesHelper.getFilterMap() != null)) {
                if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                    SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                    if (saveFilterData != null) {
                        buddyRequest.setRegionId(saveFilterData.getRegionId());
                        buddyRequest.setStateId(saveFilterData.getStateId());
                        buddyRequest.setCityId(saveFilterData.getCityId());
                        hubIdStr = saveFilterData.getHubId();
                        if (hubIdStr != null && !hubIdStr.isEmpty()) {
                            hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                        }
                        buddyRequest.setHubIds(hubIds);
                        buddyRequest.setUserGeoReq(userGeoReq);
                    }
                }
            }

            if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_BY_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.GONE);
                mIhaveAssignedAdapter.clearItems();
                showLoading();
                buddyRequest.setUserGeoReq(userGeoReq);
                mFragmentIHaveAssignedSdkBinding.shimmerViewContainer.setVisibility(View.VISIBLE);
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            }
        } else {
            api = TrackiSdkApplication.getApiMap().get(ApiType.GET_TASK_BY_ID);

            if (clickedTask != null) {
                Log.d(TAG, clickedTask.getTaskId());
                mIhaveAssignedViewModel.getTaskById(httpManager, new AcceptRejectRequest(clickedTask.getTaskId()), api);
            }
        }
    }

    private void setUp() {
        mSwipeRefreshLayout = mFragmentIHaveAssignedSdkBinding.swipeContainer;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        rvStages = mFragmentIHaveAssignedSdkBinding.rvStages;
        cvStages = mFragmentIHaveAssignedSdkBinding.cardstages;
        cardFromDate = mFragmentIHaveAssignedSdkBinding.cardFromDate;
        tvFromDate = mFragmentIHaveAssignedSdkBinding.tvFromDate;
        cardFromDate.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);
        etSearch = mFragmentIHaveAssignedSdkBinding.etSearch;
        cardFromDate = mFragmentIHaveAssignedSdkBinding.cardFromDate;

        if (categoryMap != null && categoryMap.containsKey("categoryId")) {
            String label = CommonUtils.getReffranceLabel(preferencesHelper, categoryMap.get("categoryId"));
            //  mIhaveAssignedAdapter.setReffLabel(label);
            mIhaveAssignedAdapter.setCategoryId(categoryMap.get("categoryId"));
            CommonUtils.showLogMessage("e", "label=>", "=>" + label);
            if (label.length() > 9)
                label = label.substring(0, 8) + "...";
            etSearch.setHint(label);
        }
        mButtonSubmit = mFragmentIHaveAssignedSdkBinding.btnSubmit;
        cardFromDate.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Log.i(TAG, "Enter pressed");
                String refrenceId = etSearch.getText().toString().trim();
                buddyRequest.setUserGeoReq(userGeoReq);
                if (!refrenceId.isEmpty()) {
                    buddyRequest.setReferenceId(refrenceId);
                } else {
                    buddyRequest.setReferenceId(null);
                }
            }
            return false;
        });
    }

    private void perFormStageTask(Map<String, String> categoryMap) {
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();
        categoryId = null;
        stageId = null;
        if (categoryMap != null && categoryMap.containsKey("categoryId"))
            categoryId = categoryMap.get("categoryId");
        if (categoryMap != null && categoryMap.containsKey("stageId"))
            stageId = categoryMap.get("stageId");
        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    LinkedHashMap<String, String> stageNameMap = myCatData.getStageNameMap();

                    if (stageNameMap == null) {
                        stageNameMap = new LinkedHashMap<>();
                    }
                    mStageNameMap = myCatData.getStageNameMap();
                    Iterator hmIterator = stageNameMap.entrySet().iterator();
                    ArrayList<DashBoardBoxItem> list = new ArrayList<>();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry) hmIterator.next();
                        DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                        dashBoardBoxItem.setCategoryId(categoryId);
                        dashBoardBoxItem.setStageId(mapElement.getKey().toString());
                        dashBoardBoxItem.setStageName(mapElement.getValue().toString());
                        if (stageId != null && stageId.equals(mapElement.getKey().toString())) {
                            dashBoardBoxItem.setSelected(true);
                            mFragmentIHaveAssignedSdkBinding.selectedStageChip.setText(mapElement.getValue().toString());
                        }
                        list.add(dashBoardBoxItem);
                    }
                    JSONConverter jsonConverter = new JSONConverter();
                    CommonUtils.showLogMessage("e", "whole gson", jsonConverter.objectToJson(list));

                    if (list.size() == 1) {
                        cvStages.setVisibility(View.GONE);
                        onItemClick(list.get(0));
                    } else {
                        if (stageId == null) {
                            if (!list.isEmpty()) {
                                list.get(0).setSelected(true);
                                stageId = list.get(0).getStageId();
                            }
                        }
                        StageListAdapter adapter = new StageListAdapter(list);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isSelected()) {
                                rvStages.smoothScrollToPosition(i);
                                break;
                            }

                        }
                        rvStages.setAdapter(adapter);

                        adapter.setListener(this);
                    }

                }

            }
        }
    }

    private void setRecyclerView() {
        if (rvIhaveAssigned == null) {
            rvIhaveAssigned = mFragmentIHaveAssignedSdkBinding.rvIhaveAssigned;
            mLayoutManager = new LinearLayoutManager(this.getActivity());
            mLayoutManager.setOrientation(RecyclerView.VERTICAL);
            rvIhaveAssigned.setLayoutManager(mLayoutManager);
            rvIhaveAssigned.setItemAnimator(new DefaultItemAnimator());
            rvIhaveAssigned.setAdapter(mIhaveAssignedAdapter);
        }
        if (rvIhaveAssigned != null)
            rvIhaveAssigned.setVisibility(View.VISIBLE);
        rvIhaveAssigned.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                Log.e("userGeoReq", userGeoReq + " - check");
                buddyRequest.setUserGeoReq(userGeoReq);
                showLoading();
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);

                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_BY_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.VISIBLE);
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset((currentPage - 1) * 10);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                buddyRequest.setUserGeoReq(userGeoReq);
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        mIhaveAssignedViewModel.addItemsToList(mIhaveAssignedViewModel.getTaskListLiveData().getValue());
        mIhaveAssignedAdapter.addItems(mIhaveAssignedViewModel.getBuddyObservableArrayList());
//        if (mIhaveAssignedAdapter != null) {
//            if (!preferencesHelper.getActiveTaskIdId().isEmpty())
//                preferencesHelper.saveCurrentTask(mIhaveAssignedAdapter.getTaskByTaskId(preferencesHelper.getActiveTaskIdId()));
//        }
    }

    private void subscribeToLiveData() {
        mIhaveAssignedViewModel.getTaskListLiveData().observe(getBaseActivity(), list -> {
            if (list != null) {
                mIhaveAssignedViewModel.addItemsToList(list);
            }
        });
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result,
                               @Nullable APIError error) {
        hideLoading();
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mFragmentIHaveAssignedSdkBinding.shimmerViewContainer.setVisibility(View.GONE);
        if (getBaseActivity() != null) {
            if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
                getBaseActivity().runOnUiThread(() -> {
                    TaskListing taskListing = new Gson().fromJson(String.valueOf(result), TaskListing.class);
                    List<Task> list = taskListing.getTasks();
                    mIhaveAssignedViewModel.getTaskListLiveData().setValue(list);
                    setRecyclerView();

                    if (mIhaveAssignedAdapter.getItemCount() > 0) {
                        mFragmentIHaveAssignedSdkBinding.noDataLayout.setVisibility(View.GONE);
                    } else {
                        mFragmentIHaveAssignedSdkBinding.noDataLayout.setVisibility(View.VISIBLE);
                        mFragmentIHaveAssignedSdkBinding.tvMessage.setText("Seems,you don't have any task under " + categoryName);
                    }

                    CommonUtils.showLogMessage("e", "adapter total_count =>",
                            "" + mIhaveAssignedAdapter.getItemCount());
                    CommonUtils.showLogMessage("e", "adapter total_count =>",
                            "" + categoryMap.toString());
                    CommonUtils.showLogMessage("e", "fetch total_count =>",
                            "" + taskListing.getPaginationData().getDataCount());
                    if (taskListing.getPaginationData().getDataCount() == mIhaveAssignedAdapter.getItemCount()) {
                        isLastPage = true;
                    }
                });
            } else {
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        isLastPage = false;
        currentPage = PAGE_START;
        PagingData pagingData = new PagingData();
        pagingData.setDatalimit(10);
        pagingData.setPageOffset(0);
        pagingData.setPageIndex(currentPage);
        buddyRequest.setPaginationData(pagingData);
        buddyRequest.setUserGeoReq(userGeoReq);
        api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
        if (api != null) {
            api.setAppendWithKey("ASSIGNED_BY_ME");
        }
        if (rvIhaveAssigned != null)
            rvIhaveAssigned.setVisibility(View.GONE);
        mIhaveAssignedAdapter.clearItems();
        showLoading();
        mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
    }

    @Override
    public void onItemClick(Task task, int position) {
        pos = position;
        clickedTask = task;
        Intent intent = new Intent(getBaseActivity(), NewTaskDetailsActivity.class);
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId());
        intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.getAllowSubTask());
        intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.getSubCategoryIds());
        intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.getReferenceId());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_BY_ME);
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        startActivity(intent);
    }

    @Override
    public void onCallClick(String mobile) {
        CommonUtils.openDialer(getBaseActivity(), mobile);
    }


    @Override
    public void onDetailsTaskClick(Task task) {
        startActivityForResult(
                DynamicFormActivity.Companion.newIntent(getBaseActivity())
                        /*.putExtra(AppConstants.Extra.EXTRA_FORM_TYPE, callToActions.getName())*/
                        .putExtra(AppConstants.Extra.EXTRA_FORM_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId())
                        .putExtra(AppConstants.Extra.EXTRA_TCF_ID, task.getTcfId())
                        .putExtra(AppConstants.Extra.HIDE_BUTTON, true)
                        /* .putExtra(AppConstants.Extra.EXTRA_CTA_ID, callToActions.getId())*/
                        .putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false),
                AppConstants.REQUEST_CODE_DYNAMIC_FORM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_FILTER_USER) {
            if (resultCode == RESULT_OK) {
                if ((preferencesHelper.getFilterMap() != null)) {
                    if (preferencesHelper.getFilterMap().containsKey(categoryId)) {
                        SaveFilterData saveFilterData = preferencesHelper.getFilterMap().get(categoryId);
                        if (saveFilterData != null) {
                            buddyRequest.setRegionId(saveFilterData.getRegionId());
                            buddyRequest.setStateId(saveFilterData.getStateId());
                            buddyRequest.setCityId(saveFilterData.getCityId());
                            hubIdStr = saveFilterData.getHubId();
                            if (hubIdStr != null && !hubIdStr.isEmpty()) {
                                hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                            }
                            buddyRequest.setHubIds(hubIds);
                            buddyRequest.setUserGeoReq(userGeoReq);
                        }

                    }
                }
                isLastPage = false;
                currentPage = PAGE_START;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                buddyRequest.setUserGeoReq(userGeoReq);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_TO_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.GONE);
                mIhaveAssignedAdapter.clearItems();
                showLoading();
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            } else {
                regionId = null;
                hubIds = null;
                stateId = null;
                cityId = null;
                buddyRequest.setRegionId(regionId);
                buddyRequest.setHubIds(hubIds);
                buddyRequest.setStateId(stateId);
                buddyRequest.setCityId(cityId);
                buddyRequest.setUserGeoReq(userGeoReq);
                isLastPage = false;
                currentPage = PAGE_START;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey("ASSIGNED_BY_ME");
                }
                if (rvIhaveAssigned != null)
                    rvIhaveAssigned.setVisibility(View.GONE);
                mIhaveAssignedAdapter.clearItems();
                showLoading();
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            }
        }

    }


    @Override
    public void handleExecuteUpdateResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();

    }

    @Override
    public void handleGetTaskDataResponse(ApiCallback callback, Object result, APIError error) {
        if (getBaseActivity() != null) {
            if (CommonUtils.handleResponse(callback, error, result, getBaseActivity())) {
                TaskResponse taskData = new Gson().fromJson(String.valueOf(result), TaskResponse.class);
                if (taskData != null) {
                    Log.d(TAG, taskData.getTaskDetail().getTaskId());
                    mIhaveAssignedAdapter.updateTask(pos, taskData.getTaskDetail());
                }
            }
        }
    }


    public void setListenerClick(setIHaveChatListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onItemClick(@NotNull DashBoardBoxItem response) {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String tvDate = tvFromDate.getText().toString().trim();
            api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
            if (!tvDate.isEmpty()) {
                if (mIhaveAssignedAdapter.getList() != null) {
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);


                    mIhaveAssignedAdapter.clearItems();
                } else {
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                }
                showLoading();
                Log.d("selected", response.getStageId());
                Log.d("selected", response.getStageName());
                buddyRequest.setStageId(response.getStageId());
                buddyRequest.setUserGeoReq(userGeoReq);
                mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
            } else {
                TrackiToast.Message.showShort(requireContext(), "Please select to date");
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvFromDate || id == R.id.cardFromDate) {
            //  openDatePicker(tvFromDate);
        } else if (id == R.id.btnSubmit) {
            hitApiAndGetTask();
        }
    }

    private void hitApiAndGetTask() {
        if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
            String refrenceId = etSearch.getText().toString().trim();
            String tvTodate = tvFromDate.getText().toString().trim();
            if (!tvTodate.isEmpty()) {
                if (!refrenceId.isEmpty()) {
                    buddyRequest.setReferenceId(refrenceId);
                    buddyRequest.setUserGeoReq(userGeoReq);
                    mIhaveAssignedAdapter.clearItems();
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                    showLoading();
                    mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
                } else {
                    buddyRequest.setReferenceId(null);
                    buddyRequest.setUserGeoReq(userGeoReq);
                    if (rvIhaveAssigned != null)
                        rvIhaveAssigned.setVisibility(View.GONE);
                    mIhaveAssignedAdapter.clearItems();
                    showLoading();
                    mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
                }
            } else {
                TrackiToast.Message.showShort(requireContext(), "Please select to date");
            }
        }


    }

    public interface setIHaveChatListener {
        void chatIHaveClick(String buddyId, String buddyname);

    }

    private void openDatePicker(TextView dateChange) {
        final Calendar c = Calendar.getInstance();
        if (fromDate != 0) {
            c.setTimeInMillis(fromDate);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        today = c.getTime();
        long minTime = 0L;
        final Calendar calMinTIme = Calendar.getInstance();
        if (preferencesHelper.getMaxPastDaysAllowed() != 0)
            calMinTIme.add(Calendar.DAY_OF_MONTH, -1 * preferencesHelper.getMaxPastDaysAllowed());
        calMinTIme.set(Calendar.HOUR_OF_DAY, 0);
        calMinTIme.set(Calendar.MINUTE, 0);
        calMinTIme.set(Calendar.SECOND, 0);
        minTime = calMinTIme.getTimeInMillis();
        if (getBaseActivity() != null) {
            CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                    mDay, minTime, 0, (view, year, monthOfYear, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        CommonUtils.openDatePicker(getBaseActivity(), mYear, mMonth,
                                mDay, calendar.getTimeInMillis(), 0, (view_, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {
                                    //fromDate = calendar.getTimeInMillis();
                                    fromDateDialog = calendar.getTimeInMillis();
                                    Calendar calEnd = Calendar.getInstance();
                                    calEnd.set(Calendar.YEAR, yearEnd);
                                    calEnd.set(Calendar.MONTH, monthOfYearEnd);
                                    calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
                                    calEnd.set(Calendar.HOUR_OF_DAY, 23);
                                    calEnd.set(Calendar.MINUTE, 59);
                                    calEnd.set(Calendar.SECOND, 0);
                                    //toDate = calEnd.getTimeInMillis();
                                    toDateDialog = calEnd.getTimeInMillis();
                                    Log.d("selected", DateTimeUtil.getParsedDateApply(fromDate));
                                    Log.d("selected", DateTimeUtil.getParsedDateApply(toDate));
                                    dateChange.setText(DateTimeUtil.getParsedDate(fromDateDialog) + " - " + DateTimeUtil.getParsedDate(toDateDialog));
                                });

                    });
        }
    }

    private void showHideFilter(String categoryId) {
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();

        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    setHasOptionsMenu(myCatData.getAllowGeography());
                }
            }
        }
    }

    private void playSoundStartTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.tracking_started);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }
    }

    private void playSoundStopTracking() {
        if (preferencesHelper.getVoiceAlertsTracking()) {
            if (getBaseActivity() != null) {
                mediaPlayer = MediaPlayer.create(getBaseActivity(), R.raw.tracking_stopped);
                mediaPlayer.start();
                new Handler().postDelayed(() -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mediaPlayer = null;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_task, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        EditText txtSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        txtSearch.setHint(getResources().getString(R.string.search_hint));
        txtSearch.setHintTextColor(Color.LTGRAY);
        txtSearch.setTextColor(Color.WHITE);
        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
        ViewCompat.setBackgroundTintList(txtSearch, colorStateList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            txtSearch.setTextCursorDrawable(R.drawable.cursor);
            txtSearch.getTextCursorDrawable().setTint(ContextCompat.getColor(requireActivity(), R.color.white));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("search", "SearchOnQueryTextSubmit: " + query);
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();

                String refrenceId = query.trim();
                buddyRequest.setUserGeoReq(userGeoReq);
                if (!refrenceId.isEmpty()) {
                    buddyRequest.setReferenceId(refrenceId);
                } else {
                    buddyRequest.setReferenceId(null);
                }

                hitApiAndGetTask();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("search", "SearchOnQueryTextSubmit: " + s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showBottomSheetDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    int selectedRange = R.id.chip_custom;
    ArrayList<String> serviceIds = new ArrayList();
    TextView dateChange;

    private void showBottomSheetDialog() {

        WorkFlowCategories workFlowCategories = new WorkFlowCategories();

        List<WorkFlowCategories> workFlowCategoriesList = preferencesHelper.getWorkFlowCategoriesList();
        for (int i = 0; i < workFlowCategoriesList.size(); i++) {
            if (workFlowCategoriesList.get(i).getCategoryId().equals(categoryId)) {
                workFlowCategories = workFlowCategoriesList.get(i);
            }
        }

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tvService = bottomSheetDialog.findViewById(R.id.text_service);
        ChipGroup taskServices = bottomSheetDialog.findViewById(R.id.chip_group_filter_services);

        if (workFlowCategories.getServiceConfig() != null) {
            tvService.setVisibility(View.VISIBLE);
            tvService.setText(workFlowCategories.getServiceConfig().getLabel());
            addTaskServicesChips(taskServices);
        } else {
            tvService.setVisibility(View.GONE);
        }

        ChipGroup dateGroup = bottomSheetDialog.findViewById(R.id.chip_group_filter_date);
        dateGroup.check(selectedRange);

        ChipGroup taskGroup = bottomSheetDialog.findViewById(R.id.chip_group_filter_task);
        addTaskListChips(taskGroup);

        dateChange = bottomSheetDialog.findViewById(R.id.tvFromDateDialog);
        String tvDate = tvFromDate.getText().toString().trim();
        dateChange.setText(tvDate);
        dateChange.setOnClickListener(view -> openDatePicker(dateChange));

        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
        Button apply = bottomSheetDialog.findViewById(R.id.apply_button);
        apply.setOnClickListener(view -> {
            int selected = taskGroup.getCheckedChipId();
            Chip chip = taskGroup.findViewById(selected);
            List<Integer> selectedServices = taskServices.getCheckedChipIds();
            serviceIds.clear();
            for (int i = 0; i < selectedServices.size(); i++) {
                Chip chipService = taskServices.findViewById(selectedServices.get(i));
                serviceIds.add(chipService.getTag().toString());
            }

            if (fromDateDialog != 0) {
                fromDate = fromDateDialog;
            }
            if (toDateDialog != 0) {
                toDate = toDateDialog;
            }

            Log.d("selected here", DateTimeUtil.getParsedDateApply(fromDate));
            Log.d("selected here ", DateTimeUtil.getParsedDateApply(toDate));

            if (stageId != null) {
                stageId = chip.getTag().toString();
            }
            if (getBaseActivity() != null && getBaseActivity().isNetworkConnected()) {
                String tvDate1 = tvFromDate.getText().toString().trim();
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
                if (!tvDate1.isEmpty()) {
                    if (mIhaveAssignedAdapter.getList() != null) {
                        if (rvIhaveAssigned != null)
                            rvIhaveAssigned.setVisibility(View.GONE);
                        mIhaveAssignedAdapter.clearItems();
                    } else {
                        if (rvIhaveAssigned != null)
                            rvIhaveAssigned.setVisibility(View.GONE);
                    }
                    showLoading();
                    buddyRequest.setStageId(chip.getTag().toString());
                    buddyRequest.setUserGeoReq(userGeoReq);
                    buddyRequest.setFrom(fromDate);
                    buddyRequest.setTo(toDate);
                    buddyRequest.setServiceIds(serviceIds);
                    mIhaveAssignedViewModel.getTaskList(httpManager, api, buddyRequest);
                    mFragmentIHaveAssignedSdkBinding.selectedStageChip.setText(chip.getText().toString());
                    mFragmentIHaveAssignedSdkBinding.tvFromDate.setText(dateChange.getText().toString());
                } else {
                    TrackiToast.Message.showShort(requireContext(), "Please select to date");
                }
            }
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
        sedateListener(dateGroup);
    }

    private void addTaskServicesChips(ChipGroup taskServices) {
        List<Service> listCategory = preferencesHelper.getServices();
        if (listCategory != null) {
            for (int i = 0; i < listCategory.size(); i++) {
                Service myCatData = listCategory.get(i);
                if (Boolean.TRUE.equals(myCatData.getSelected())) {
                    Chip mChip1 = (Chip) this.getLayoutInflater().inflate(R.layout.layout_chip, null, false);
                    mChip1.setText(myCatData.getName());
                    mChip1.setTag(myCatData.getId());
                    mChip1.setId(View.generateViewId());
                    taskServices.addView(mChip1);
                    if (serviceIds.contains(myCatData.getId())) {
                        taskServices.check(mChip1.getId());
                    }
                }
            }
        }
    }

    long fromDateDialog = fromDate;
    long toDateDialog = toDate;

    private void sedateListener(ChipGroup dateGroup) {
        dateGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedRange = checkedId;
            if (checkedId == R.id.chip_today) {
                fromDateDialog = atStartOfDay(Calendar.getInstance().getTime()).getTime();
                toDateDialog = atEndOfDay(Calendar.getInstance().getTime()).getTime();
                dateChange.setText(DateTimeUtil.getParsedDate(fromDateDialog) + " - " + DateTimeUtil.getParsedDate(toDateDialog));
            } else if (checkedId == R.id.chip_yesterday) {
                Date yesterdayDate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
                fromDateDialog = atStartOfDay(yesterdayDate).getTime();
                toDateDialog = atEndOfDay(yesterdayDate).getTime();
                dateChange.setText(DateTimeUtil.getParsedDate(fromDateDialog) + " - " + DateTimeUtil.getParsedDate(toDateDialog));
            } else if (checkedId == R.id.chip_last_week) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                c.add(Calendar.DATE, -1 * 7);
                ArrayList<Date> listDate = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    listDate.add(c.getTime());
                }
                Date startDate = listDate.get(0);
                Date endDate = listDate.get(6);
                fromDateDialog = atStartOfDay(startDate).getTime();
                toDateDialog = atEndOfDay(endDate).getTime();
                dateChange.setText(DateTimeUtil.getParsedDate(fromDateDialog) + " - " + DateTimeUtil.getParsedDate(toDateDialog));
            } else if (checkedId == R.id.chip_last_month) {
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                fromDateDialog = atStartOfDay(firstDateOfPreviousMonth).getTime();
                toDateDialog = atEndOfDay(lastDateOfPreviousMonth).getTime();
                dateChange.setText(DateTimeUtil.getParsedDate(fromDateDialog) + " - " + DateTimeUtil.getParsedDate(toDateDialog));
            } else if (checkedId == R.id.chip_custom) {
                openDatePicker(dateChange);
            }
        });
    }

    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private void addTaskListChips(ChipGroup taskGroup) {
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();
        WorkFlowCategories workFlowCategories = new WorkFlowCategories();
        workFlowCategories.setCategoryId(categoryId);
        if (listCategory.contains(workFlowCategories)) {
            int position = listCategory.indexOf(workFlowCategories);
            if (position != -1) {
                WorkFlowCategories myCatData = listCategory.get(position);
                LinkedHashMap<String, String> stageNameMap = myCatData.getStageNameMap();
                Iterator hmIterator = stageNameMap.entrySet().iterator();
                while (hmIterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry) hmIterator.next();
                    Chip mChip1 = (Chip) this.getLayoutInflater().inflate(R.layout.layout_chip, null, false);
                    mChip1.setText(mapElement.getValue().toString());
                    mChip1.setTag(mapElement.getKey().toString());
                    mChip1.setId(View.generateViewId());
                    taskGroup.addView(mChip1);
                    Log.d("selected - stageId ", stageId);
                    if (stageId != null && stageId.equals(mapElement.getKey().toString())) {
                        Log.d("selected - stageId ", stageId);
                        taskGroup.check(mChip1.getId());
                    }
                }
            }
        }
    }
}


