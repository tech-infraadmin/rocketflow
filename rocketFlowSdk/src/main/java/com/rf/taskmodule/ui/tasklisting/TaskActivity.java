package com.rf.taskmodule.ui.tasklisting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rf.taskmodule.data.model.response.config.Buddy;
import com.rf.taskmodule.data.model.response.config.BuddyListResponse;
import com.rf.taskmodule.data.model.response.config.ChannelConfig;
import com.rf.taskmodule.data.model.response.config.ChannelSetting;
import com.rf.taskmodule.data.model.response.config.CreationMode;
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem;
import com.rf.taskmodule.data.model.response.config.Fleet;
import com.rf.taskmodule.data.model.response.config.FleetListResponse;
import com.rf.taskmodule.data.model.response.config.InventoryResponse;
import com.rf.taskmodule.data.model.response.config.MappingOn;
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.databinding.ActivityTaskSdkBinding;
import com.rf.taskmodule.factory.TaskViewModelFactory;
import com.rf.taskmodule.ui.buddylisting.BuddyListingActivity;
import com.rf.taskmodule.ui.fleetlisting.FleetListingActivity;
import com.rf.taskmodule.ui.newcreatetask.NewCreateTaskActivity;
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeFragment;
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.IhaveAssignedFragment;
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.TabDataClass;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.JSONConverter;
import com.rocketflow.sdk.RocketFlyer;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.data.model.response.config.Buddy;
import com.rf.taskmodule.data.model.response.config.BuddyListResponse;
import com.rf.taskmodule.data.model.response.config.ChannelConfig;
import com.rf.taskmodule.data.model.response.config.ChannelSetting;
import com.rf.taskmodule.data.model.response.config.CreationMode;
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem;
import com.rf.taskmodule.data.model.response.config.Fleet;
import com.rf.taskmodule.data.model.response.config.FleetListResponse;
import com.rf.taskmodule.data.model.response.config.InventoryResponse;
import com.rf.taskmodule.data.model.response.config.MappingOn;
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.factory.TaskViewModelFactory;
import com.rf.taskmodule.ui.base.BaseSdkActivity;
import com.rf.taskmodule.ui.buddylisting.BuddyListingActivity;
//import com.rf.taskmodule.ui.chat.ChatActivity;
//import com.rf.taskmodule.ui.custom.socket.WebSocketManager;
import com.rf.taskmodule.ui.fleetlisting.FleetListingActivity;
import com.rf.taskmodule.ui.newcreatetask.NewCreateTaskActivity;
import com.rf.taskmodule.ui.selectorder.SelectOrderActivity;
import com.rf.taskmodule.ui.tasklisting.assignedtome.AssignedtoMeFragment;
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.IhaveAssignedFragment;
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.TabDataClass;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.CommonUtils;
import com.rf.taskmodule.utils.JSONConverter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU;
import static com.rf.taskmodule.utils.AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU;

import com.rf.taskmodule.BR;
import com.rf.taskmodule.R;

/**
 * Created by rahul on 8/10/18
 */
public class TaskActivity extends BaseSdkActivity<ActivityTaskSdkBinding, TaskViewModel>
        implements TaskNavigator, View.OnClickListener, IhaveAssignedFragment.setIHaveChatListener, AssignedtoMeFragment.setAssignToMeChatListener {

   // @Inject
    //DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;
    TaskViewModel mTaskViewModel;
    TaskPagerAdapter mPagerAdapter;
    PreferencesHelper preferencesHelper;
    HttpManager httpManager;


    private boolean isTagInventory = false;
    private boolean showMerchantTab = false;
    private ActivityTaskSdkBinding mActivityTaskSdkBinding;
    private FloatingActionButton ivCreateTask;
    private String categoryMap;
    private String buddyId;
    private String buddyName;
    private Map<String, String> mMapCategory;
    private int connectionCounter = 0;
    private String categoryId;
    private String LOADBY;
    private long fromDate;
    private long toDate;
    private boolean userGeoReq = true;
    private Snackbar snackBar;

    public static Intent newIntent(Context context) {
        return new Intent(context, TaskActivity.class);
    }

    private List<TabDataClass> fragments = new ArrayList<>();

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_task_sdk;
    }

    @Override
    public TaskViewModel getViewModel() {
        return mTaskViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTaskSdkBinding = getViewDataBinding();
        httpManager = RocketFlyer.Companion.httpManager();
        preferencesHelper = RocketFlyer.Companion.preferenceHelper();
        mPagerAdapter = new TaskPagerAdapter(getSupportFragmentManager());
        mTaskViewModel  = new ViewModelProvider(this, new TaskViewModelFactory(RocketFlyer.Companion.dataManager())).get(TaskViewModel.class);
        mTaskViewModel.setNavigator(this);

        mActivityTaskSdkBinding.coordinatorLayout.setVisibility(View.VISIBLE);
        setUp();


//        if(preferencesHelper!=null && preferencesHelper.getLoginToken()!=null
//                && !preferencesHelper.getLoginToken().isEmpty()) {
//            hitConfig();
//        }else{
//            String sdkClintId = preferencesHelper!=null
//                    && preferencesHelper.getSDKClientID()!=null
//                    && !preferencesHelper.getSDKClientID().isEmpty()?preferencesHelper.getSDKClientID():"sahshahsss";
//            hitLoginSDKToken(sdkClintId);
//        }
    }

//    private void hitLoginSDKToken(String sdkClintId){
//        showLoading();
//        mTaskViewModel.sdkToken(sdkClintId, RocketFlyer.Companion.httpManager(), new SyncCallback() {
//            @Override
//            public void hitApi() {
//                //SyncCallback.super.hitApi();z
//            }
//
//            @Override
//            public void onRequestTimeOut(@NonNull ApiCallback callBack) {
//                //SyncCallback.super.onRequestTimeOut(callBack);
//                hideLoading();
//            }
//
//            @Override
//            public void onLogout() {
//                hideLoading();
//            }
//
//            @Override
//            public void onNetworkErrorClose() {
//                hideLoading();
//            }
//
//            @Override
//            public void onResponse(Object result, @Nullable APIError error) {
//                hideLoading();
//                if (CommonUtils.handleResponse(this, error, result, TaskActivity.this)) {
//                    if (result == null) {
//                        return;
//                    }
//                    Gson gson = new Gson();
//                    SDKToken token = gson.fromJson(String.valueOf(result), SDKToken.class);
//                    if(token!=null){
//                        CommonUtils.saveSDKAccessToken( token, preferencesHelper);
//                    }
//                     hitConfig();
//
//                }else{
//                    //CommonUtils.showPermanentSnackBar(findViewById(R.id.rlMain), AppConstants.ALERT_TRY_AGAIN,mSplashViewModel);
//                }
//            }
//
//            @Override
//            public boolean isAvailable() {
//                return true;
//            }
//        });
//    }
//
//    private void hitConfig(){
//        showLoading();
//        mTaskViewModel.hitConfigApi(RocketFlyer.Companion.httpManager(), new SyncCallback() {
//            @Override
//            public void hitApi() {
//                //SyncCallback.super.hitApi();
//            }
//
//            @Override
//            public void onRequestTimeOut(@NonNull ApiCallback callBack) {
//                //SyncCallback.super.onRequestTimeOut(callBack);
//                hideLoading();
//            }
//
//            @Override
//            public void onLogout() {
//                hideLoading();
//            }
//
//            @Override
//            public void onNetworkErrorClose() {
//                hideLoading();
//            }
//
//            @Override
//            public void onResponse(Object result, @Nullable APIError error) {
//                hideLoading();
//                if (CommonUtils.handleResponse(this, error, result, TaskActivity.this)) {
//                    if (result == null) {
//                        return;
//                    }
//                    Gson gson = new Gson();
//                    ConfigResponse configResponse = gson.fromJson(String.valueOf(result), ConfigResponse.class);
//
//                    CommonUtils.saveConfigDetails(TaskActivity.this, configResponse, preferencesHelper, "1");
//                    mActivityTaskSdkBinding.coordinatorLayout.setVisibility(View.VISIBLE);
//                    setUp();
//
//                }else{
//                    //CommonUtils.showPermanentSnackBar(findViewById(R.id.rlMain), AppConstants.ALERT_TRY_AGAIN,mSplashViewModel);
//                }
//            }
//
//            @Override
//            public boolean isAvailable() {
//                return true;
//            }
//        });
//    }

    @SuppressLint("RestrictedApi")
    private void setUp() {
        if (getIntent() != null) {
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
                categoryMap = getIntent().getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES);
                CommonUtils.showLogMessage("e", "categoryMap", categoryMap);
                mMapCategory = new Gson().fromJson(categoryMap, new TypeToken<HashMap<String, String>>() {
                }.getType());
            }
            if (getIntent().hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
//                viewPager.setCurrentItem(1);
            }
            if (getIntent().hasExtra(AppConstants.Extra.FROM_DATE)) {
                fromDate = getIntent().getLongExtra(AppConstants.Extra.FROM_DATE, 0);
            }else{
                fromDate = fromDate();
            }
            if (getIntent().hasExtra(AppConstants.Extra.FROM_TO)) {
                toDate = getIntent().getLongExtra(AppConstants.Extra.FROM_TO, 0);
            }else{
                toDate = toDateDateAdd24Hour();
            }
            if (getIntent().hasExtra(AppConstants.Extra.LOAD_BY)) {
                LOADBY = getIntent().getStringExtra(AppConstants.Extra.LOAD_BY);
            }
            if (getIntent().hasExtra(AppConstants.Extra.GEO_FILTER)){
                userGeoReq = getIntent().getBooleanExtra(AppConstants.Extra.GEO_FILTER,false);
            }
        }
        preferencesHelper.setIsFleetAndBuddyShow(false);
        if (getIntent().hasExtra(AppConstants.Extra.TITLE)) {
            setToolbar(mActivityTaskSdkBinding.toolbar, getIntent().getStringExtra(AppConstants.Extra.TITLE));
        } else {
            setToolbar(mActivityTaskSdkBinding.toolbar, getString(R.string.tasks));
        }

        ivCreateTask = mActivityTaskSdkBinding.ivCreateTask;
        ivCreateTask.setOnClickListener(this);
        ViewPager viewPager = mActivityTaskSdkBinding.vpTask;
        TabLayout tabLayout = mActivityTaskSdkBinding.tabLayout;

        if(mMapCategory==null){
            mMapCategory = new HashMap<>();
            categoryMap = "{\"categoryId\":\""+getIntent().getStringExtra(AppConstants.Extra.EXTRA_STAGEID)+"\"}";

            mMapCategory.put("categoryId",getIntent().getStringExtra(AppConstants.Extra.EXTRA_STAGEID));
        }

        if (mMapCategory != null && mMapCategory.containsKey("categoryId")) {
            categoryId = mMapCategory.get("categoryId");
            List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();
            if(listCategory==null){
                WorkFlowCategories workFlowCategories = new WorkFlowCategories();
                workFlowCategories.setCategoryId(categoryId);
                listCategory = new ArrayList<>();
                listCategory.add(workFlowCategories);
            }

            preferencesHelper.saveWorkFlowCategoriesList(listCategory);

            if (categoryId != null) {
                WorkFlowCategories workFlowCategories = new WorkFlowCategories();
                workFlowCategories.setCategoryId(categoryId);
                if (listCategory!=null && listCategory.contains(workFlowCategories)) {
                    int position = listCategory.indexOf(workFlowCategories);
                    if (position != -1) {
                        WorkFlowCategories myCatData = listCategory.get(position);
                        if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getMappingOn() == MappingOn.CREATION) {
                            isTagInventory = true;
                        } else {
                            isTagInventory = false;
                        }
                        if(myCatData.getShowMerchantTasks()){
                            showMerchantTab=true;
                            if(LOADBY==null){
                                LOADBY = "ASSIGNED_TO_MERCHANT";
                            }
                        }else{
                            showMerchantTab=false;
                            if(LOADBY==null){
                                LOADBY = "ASSIGNED_TO_ME";
                            }

                        }
                    }
                }

            }
            Map<String, ChannelConfig> channelConfigMap = preferencesHelper.getWorkFlowCategoriesListChannel();

            if(channelConfigMap==null){
                channelConfigMap = new HashMap<>();

                ChannelSetting setting = new ChannelSetting();
//                setting.setAllowCreation(true);
//                setting.setTaskExecution(true);
//                setting.setCreationTitle("Demo");
//                setting.setExecutionTitle("DEmo");
//                setting.setCreationMode(CreationMode.DIRECT);


                ChannelConfig config = new ChannelConfig();
                config.setChannelSetting(setting);
                channelConfigMap.put(categoryId,config);
                preferencesHelper.saveWorkFlowCategoriesChannel(channelConfigMap);
            }

            if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(categoryId)) {
                ChannelConfig channelConfig = channelConfigMap.get(categoryId);
                if (channelConfig != null && channelConfig.getChannelSetting() != null) {
                    ChannelSetting channelSetting = channelConfig.getChannelSetting();
                    if (showMerchantTab) {
                        String merchantTabLabel="Request BY Merchant";
                        if (channelSetting.getMerchantTaskLabel() != null && !channelSetting.getMerchantTaskLabel().isEmpty())
                            merchantTabLabel=channelSetting.getMerchantTaskLabel();
                        fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,true,userGeoReq), merchantTabLabel));

                        mPagerAdapter.setFragments(fragments);
                        // tabLayout.getTabAt(0).select();
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        viewPager.setCurrentItem(0, true);

                    }
                    if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                            && channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution()) {
                        if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty())
                            fragments.add(new TabDataClass(AssignedtoMeFragment.newInstance(categoryMap, fromDate, toDate, null, null,userGeoReq), channelSetting.getExecutionTitle()));
                        if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty())
                            fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,false,userGeoReq), channelSetting.getCreationTitle()));
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        tabLayout.setVisibility(View.VISIBLE);
                        mPagerAdapter.setFragments(fragments);
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()) {
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(2, true);
                                }

                            }else{
                                if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(0, true);
                                } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }


                        }
                    } else if (channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution() && (channelSetting.getAllowCreation() == null || !channelSetting.getAllowCreation())) {
                        if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty())
                            fragments.add(new TabDataClass(AssignedtoMeFragment.newInstance(categoryMap, fromDate, toDate, null, null,userGeoReq), channelSetting.getExecutionTitle()));
                        ivCreateTask.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        mPagerAdapter.setFragments(fragments);
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()){
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_TO_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }else{
                                viewPager.setCurrentItem(0, true);
                            }
                        }


                    } else if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                            && (channelSetting.getTaskExecution() == null || !channelSetting.getTaskExecution())) {
                        if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty())
                            fragments.add(new TabDataClass(IhaveAssignedFragment.newInstance(categoryMap, fromDate, toDate,false,userGeoReq), channelSetting.getCreationTitle()));
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        tabLayout.setVisibility(View.GONE);
                        mPagerAdapter.setFragments(fragments);
                        // tabLayout.getTabAt(0).select();
                        viewPager.setAdapter(mPagerAdapter);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        if (LOADBY != null && !LOADBY.isEmpty()){
                            if(showMerchantTab){
                                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                                    viewPager.setCurrentItem(0, true);
                                }
                                else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                                    viewPager.setCurrentItem(1, true);
                                }
                            }else{
                                viewPager.setCurrentItem(0, true);
                            }
                        }

                    }

                }


            }
            if (fragments.size() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
            } else {
                tabLayout.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivCreateTask) {
//            if(preferencesHelper.getIsTrackingLiveTrip()&&categoryId.equals(preferencesHelper.getActiveTaskCategoryId())){
//                TrackiToast.Message.showShort(this, AppConstants.MSG_ONGOING_TASK_SAME_CATGEORY);
//            }else {
                if (isTagInventory) {
                    Intent intent = SelectOrderActivity.Companion.newIntent(this);
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                            new Gson().toJson(dashBoardBoxItem));
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

                } else {
                    if (preferencesHelper.getIsFleetAndBuddySHow()) {
                        showLoading();
                        mTaskViewModel.checkBuddy(httpManager);
                    } else {
                        Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                        intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                        intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                        if (categoryMap != null)
                            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
                    }
                }
            //}

        }
    }

    @Override
    public void checkBuddyResponse(ApiCallback callback,
                                   Object result,
                                   APIError error) {
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            BuddyListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), BuddyListResponse.class);
            List<Buddy> list = buddyListResponse.getBuddies();
            if (list != null && list.size() > 0) {
                //categoryId
                hideLoading();
                Intent intent = BuddyListingActivity.newIntent(this);
                intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent,
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                mTaskViewModel.checkFleet(RocketFlyer.Companion.httpManager());
            }
        }
    }

    @Override
    public void checkFleetResponse(ApiCallback callback,
                                   Object result,
                                   APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            FleetListResponse buddyListResponse = new Gson().fromJson(String.valueOf(result), FleetListResponse.class);
            List<Fleet> list = buddyListResponse.getFleets();
            if (list != null && list.size() > 0) {
                //categoryId
                Intent intent = FleetListingActivity.newIntent(this);
                intent.putExtra(AppConstants.Extra.EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent,
                        AppConstants.REQUEST_CODE_CREATE_TASK);
            } else {
                Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

            }
        }
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    @Override
    public void chatAssignClick(String buddyId, String buddyname) {
        //initSocket();
        //connectSocket(this);
//        try {
//            ++connectionCounter;
//            if (webSocketManager != null && webSocketManager.isConnected()) {
//                showLoading();
//                webSocketManager.connectPacket(buddyId);
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
        /*finally {
            if(connectionCounter>=3){
                Toast.makeText(this, "Connection not established please try after some time", Toast.LENGTH_SHORT).show();
            }
        }*/

        this.buddyId = buddyId;
        this.buddyName = buddyname;
//        ArrayList list = new ArrayList<String>();
//         list.add(buddyId);
//        webSocketManager.openCreateRoom(list, null, true, 10);
    }

    @Override
    public void chatIHaveClick(String buddyId, String buddyname) {

        //initSocket();
        //connectSocket(this);
//        try {
//            ++connectionCounter;
//            if (webSocketManager != null && webSocketManager.isConnected()) {
//                showLoading();
//                webSocketManager.connectPacket(buddyId);
//            }
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
        /*finally {
            Log.e("tag",connectionCounter+"");
            if(connectionCounter>=3){
                Toast.makeText(this, "Connection not established please try after some time", Toast.LENGTH_SHORT).show();
            }
        }*/


        this.buddyId = buddyId;
        this.buddyName = buddyname;
        /*ArrayList list = new ArrayList<String>();
        list.add(buddyId);
        webSocketManager.openCreateRoom(list, null, true, 10);*/
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof AssignedtoMeFragment) {
            ((AssignedtoMeFragment) fragment).setListenerClick(this);
        } else if (fragment instanceof IhaveAssignedFragment) {
            ((IhaveAssignedFragment) fragment).setListenerClick(this);
        }
    }

//    @Override
//    public void onSocketResponse(int eventName, BaseModel baseModel) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
////               Log.e("TaskActivity", "onSocketResponse() called");
////               Log.e("TaskActivity", "eventName =>" + eventName);
////               Log.e("TaskActivity", "baseModel =>" + baseModel);
//                hideLoading();
//                if (eventName == 4) {
//                    OpenCreateRoomModel openCreateRoomModel = (OpenCreateRoomModel) baseModel;
//                    String roomId = openCreateRoomModel.getRoomId();
//                    ArrayList list = new ArrayList<String>();
//                    list.add(buddyId);
//                    startActivity(ChatActivity.Companion.newIntent(TaskActivity.this)
//                            .putExtra(AppConstants.Extra.EXTRA_SELECTED_BUDDY, list)
//                            .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, buddyName)
//                            .putExtra(AppConstants.Extra.EXTRA_IS_CREATE_ROOM, false)
//                            .putExtra(AppConstants.Extra.EXTRA_ROOM_ID, roomId));
//                } else if (eventName == 1) {
//                    connectionCounter = 0;
//                    //connection hashMap
//                    ArrayList connectionInfoList = new ArrayList<ConnectionInfo>();
//                    //buddy message hashMap
//                    ArrayList buddyList = new ArrayList<Buddy>();
//                    Map map = new HashMap<String, String>();
//                    ConnectionResponse connectionResponse = (ConnectionResponse) baseModel;
//
//                    ArrayList list = new ArrayList<String>();
//                    list.add(buddyId);
//                    webSocketManager.openCreateRoom(list, null, true, 10);
//
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onOpen() {
////        ArrayList list = new ArrayList<String>();
////        list.add(buddyId);
////        webSocketManager.openCreateRoom(list, null, true, 10);
//    }
//
//    @Override
//    public void closed() {
//        webSocketManager = null;
//        Log.e("close call", "close call");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (webSocketManager != null) {
//            webSocketManager.disconnect();
//            webSocketManager = null;
//        }

    }

    @Override
    protected void onResume() {
        //initSocket();
        //connectSocket(this);
        super.onResume();

    }

    @Override
    public void checkInventory(@Nullable ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        JSONConverter jsonConverter = new JSONConverter();
        InventoryResponse response = (InventoryResponse) jsonConverter.jsonToObject(result.toString(), InventoryResponse.class);
        if (response.getSuccessful()) {
            Intent intent = SelectOrderActivity.Companion.newIntent(this);
            DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
            dashBoardBoxItem.setCategoryId(categoryId);
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES,
                    new Gson().toJson(dashBoardBoxItem));
            startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

        } else {
            if (preferencesHelper.getIsFleetAndBuddySHow()) {
                showLoading();
                mTaskViewModel.checkBuddy(httpManager);
            } else {
                // Intent intent=CreateTaskActivity.Companion.newIntent(this);
                Intent intent = NewCreateTaskActivity.Companion.newIntent(this);
                intent.putExtra(AppConstants.Extra.FROM, "taskListing");
                intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                if (categoryMap != null)
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, categoryMap);
                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
            }
        }
    }
    @Override
    public void networkAvailable() {
        if(snackBar!=null)
            snackBar.dismiss();

    }

    @Override
    public void networkUnavailable() {
//        RelativeLayout rlMain=mActivityMainBinding.mainView.findViewById(R.id.rlMain);
        snackBar= CommonUtils.showNetWorkConnectionIssue(  mActivityTaskSdkBinding.coordinatorLayout,getString(R.string.please_check_your_internet_connection));
    }


    private long toDateDateAdd24Hour() {
        final Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime().getTime();
    }


    private long fromDate(){
        final Calendar cal = Calendar.getInstance();
        if (preferencesHelper.getDefDateRange() == 0) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else {
            cal.add(Calendar.DAY_OF_MONTH, -1 * preferencesHelper.getDefDateRange());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        }
       return cal.getTime().getTime();
    }
}
