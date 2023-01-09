package com.rf.taskmodule.ui.main.taskdashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rf.taskmodule.data.local.prefs.AppPreferencesHelper;
import com.rf.taskmodule.data.model.BaseResponse;
import com.rf.taskmodule.data.model.request.SaveFilterData;
import com.rf.taskmodule.data.model.request.StatusRequest;
import com.rf.taskmodule.data.model.request.TaskRequest;
import com.rf.taskmodule.data.model.response.config.Api;
import com.rf.taskmodule.data.model.response.config.Category;
import com.rf.taskmodule.data.model.response.config.ChannelConfig;
import com.rf.taskmodule.data.model.response.config.ChannelSetting;
import com.rf.taskmodule.data.model.response.config.CreationMode;
import com.rf.taskmodule.data.model.response.config.DashBoardBoxItem;
import com.rf.taskmodule.data.model.response.config.DashBoardMapResponse;
import com.rf.taskmodule.data.model.response.config.GeoCoordinates;
import com.rf.taskmodule.data.model.response.config.InsightResponse;
import com.rf.taskmodule.data.model.response.config.MappingOn;
import com.rf.taskmodule.data.model.response.config.ProfileInfo;
import com.rf.taskmodule.data.model.response.config.ProjectCategories;
import com.rf.taskmodule.data.model.response.config.ServiceDescr;
import com.rf.taskmodule.data.model.response.config.StatusCoordinates;
import com.rf.taskmodule.data.model.response.config.StatusLocation;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.data.model.response.config.TaskListing;
import com.rf.taskmodule.data.model.response.config.WorkFlowCategories;
import com.rf.taskmodule.data.network.APIError;
import com.rf.taskmodule.data.network.ApiCallback;
import com.rf.taskmodule.data.network.HttpManager;
import com.rf.taskmodule.databinding.DashboardBinding;
import com.rf.taskmodule.ui.addplace.Hub;
import com.rf.taskmodule.ui.base.BaseSdkFragment;
import com.rf.taskmodule.ui.dynamicform.DynamicFormActivity;
import com.rf.taskmodule.ui.main.DashBoardStageCountAdapter;
import com.rf.taskmodule.ui.main.InsightAdapter;
import com.rf.taskmodule.ui.main.filter.TaskFilterActivity;
import com.rf.taskmodule.ui.newcreatetask.NewCreateTaskActivity;
import com.rf.taskmodule.ui.scanqrcode.ScanQrAndBarCodeActivity;
import com.rf.taskmodule.ui.selectorder.SelectOrderActivity;
import com.rf.taskmodule.ui.taskdetails.NewTaskDetailsActivity;
import com.rf.taskmodule.ui.tasklisting.PaginationListener;
import com.rf.taskmodule.ui.tasklisting.PagingData;
import com.rf.taskmodule.ui.tasklisting.TaskActivity;
import com.rf.taskmodule.ui.tasklisting.TaskItemClickListener;
import com.rf.taskmodule.ui.tasklisting.TaskListingAdapter;
import com.rf.taskmodule.ui.tasklisting.ihaveassigned.TabDataClass;
import com.rf.taskmodule.utils.ApiType;
import com.rf.taskmodule.utils.AppConstants;
import com.rf.taskmodule.utils.BuddyInfo;
import com.rf.taskmodule.utils.DateTimeUtil;
import com.rf.taskmodule.utils.JSONConverter;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.utils.NetworkUtils;
import com.rf.taskmodule.utils.TaskStatus;
import com.rf.taskmodule.utils.TrackiToast;
import com.rf.taskmodule.utils.toggle.widget.LabeledSwitch;
import com.rocketflow.sdk.RocketFlyer;

import com.rf.taskmodule.BR;
import com.rf.taskmodule.R;
import com.rf.taskmodule.TrackiSdkApplication;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.ui.custom.GlideApp;
import com.rf.taskmodule.utils.CommonUtils;

import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TaskDashBoardFragment extends BaseSdkFragment<DashboardBinding, TaskDashBoardViewModel> implements TaskDashBoardNavigator, View.OnClickListener, DashBoardStageCountAdapter.DashBoardListener, TaskItemClickListener {
    private static final String TAG = "TaskDashBoardFragment";
    private static final int REQUEST_CAMERA = 3;
    TaskDashBoardViewModel mMainViewModel;
    PreferencesHelper preferencesHelper;
    HttpManager httpManager;

    private static final int REQUEST_READ_STORAGE = 4;

    DashboardBinding taskStageDashBoardBinding;

    TaskPagerNewAdapter mPagerAdapter;

    private BottomSheetBehavior<View> sheetBehavior;
    private View bottomSheet;

    private GeoCoordinates currentLocation;

    LinkedHashMap<String, String> stageNameMap = null;
    private RecyclerView gridRecyclerView;
    // private RecyclerView gridAttendance;
    private boolean userGeoReq;
    private long fromDate;
    private long toDate;
    private ImageView ivScanQrCode;
    private ImageView ivFilter;
    private ImageView ivNavigationIcon;
    private Date today;
    private AppCompatSpinner taskCategorySpinner;
    private String categoryId;
    private TextView tvFromDate;
    private TextView tvName;
    private TabLayout tabLayout;
    private CardView cardViewTab;
    private String categoryName;
    private int cellwidthWillbe = 0;
    private int InsightscellwidthWillbe = 0;
    private FloatingActionButton ivCreateTask;
    private String regionId;
    private List<String> hubIds;
    private String stateId;
    private String invAction;
    private String cityId;
    private String hubIdStr;
    private boolean isTagInventory = false;
    private String LOADBY = "ASSIGNED_TO_ME";
    private LinearLayout cardSpinner;
    private String hubId;
    private AppCompatSpinner locationSpinner;
    private CardView cardLocation;
    private boolean showMerchantTab;
    private TextView tvProcessState;
    private ChannelSetting channelSetting;
    private ArrayList<Boolean> catGeo;
    private HashMap<String, Boolean> catGeoMap;
    private ArrayList<String> catIds;
    private ProfileInfo userDetail;
    private List<TabDataClass> fragments = new ArrayList<>();


    private ImageView close;

    private LabeledSwitch switchToggle;

    private String dfdId = null;
    private int currentPage = PaginationListener.PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private CardView cardFromDate;
    private ImageButton mButtonSubmit;
    private EditText etSearch;
    private TaskRequest buddyRequest;
    private Api api;
    private RecyclerView rvAssignedToMe;
    private Map<String, String> categoryMap;
    private Integer toggleCount = 0;

    LinearLayoutManager mLayoutManager;

    TaskListingAdapter mAssignedtoMeAdapter;

    private BroadcastReceiver refreshTaskListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (getActivity() != null && NetworkUtils.isNetworkConnected(getActivity())) {
                isLastPage = false;
                PagingData pagingData = new PagingData();
                pagingData.setDatalimit(10);
                pagingData.setPageOffset(0);
                pagingData.setPageIndex(currentPage);
                buddyRequest.setPaginationData(pagingData);
                buddyRequest.setFrom(fromDate);
                buddyRequest.setTo(toDate);
                buddyRequest.setUserGeoReq(userGeoReq);
                if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
                    buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_TO_MERCHANT);
                } else if (LOADBY.equals("ASSIGNED_TO_ME")) {
                    buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_TO_ME);
                } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
                    buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_BY_ME);
                } else if (LOADBY.equals("LOCATION")) {
                    buddyRequest.setLoadBy(BuddyInfo.LOCATION);
                }
                //buddyRequest.setCategoryId(categoryMap.get("categoryId"));
                api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
                if (api != null) {
                    api.setAppendWithKey(LOADBY);
                }
                mAssignedtoMeAdapter.clearItems();
                Log.d("getTaskList", "getTaskList 1");
                mMainViewModel.getTaskList(httpManager, api, buddyRequest);
            }
        }
    };

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_task_stage_dash_board_sdk;
    }

    public static TaskDashBoardFragment newInstance(Context context) {
        Bundle args = new Bundle();
        TaskDashBoardFragment fragment = new TaskDashBoardFragment();
        fragment.setListener((NavigationClickFromTaskDashBoard) context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public TaskDashBoardViewModel getViewModel() {
        TaskDashBoardViewModel.Factory factory = new TaskDashBoardViewModel.Factory(RocketFlyer.Companion.dataManager());
        mMainViewModel = ViewModelProviders.of(this, factory).get(TaskDashBoardViewModel.class);
        return mMainViewModel;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesHelper = RocketFlyer.Companion.preferenceHelper();
        httpManager = RocketFlyer.Companion.httpManager();
        mAssignedtoMeAdapter = new TaskListingAdapter(new ArrayList<>(), preferencesHelper);
        mAssignedtoMeAdapter.setListener(this);
        mAssignedtoMeAdapter.setAssignedToTask(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMainViewModel.setNavigator(this);
        taskStageDashBoardBinding = getViewDataBinding();

        setUp();

        populateCategorySpinner();

        switchToggle = taskStageDashBoardBinding.toolLayoutDashboard.findViewById(R.id.switch_toggle_dash);
        setToggle(switchToggle);



        toggleCount = 2;

        Log.d("officeOnline", preferencesHelper.officeOnline() + "");

        Log.e("toggleFeature", "check" + preferencesHelper.toggleFeature());

        if (!preferencesHelper.toggleFeature() || preferencesHelper.toggleFeature() == null) {
            switchToggle.setVisibility(View.GONE);
            setTopColor(R.color.colorPrimary);
        } else {
            switchToggle.setVisibility(View.VISIBLE);
            switchToggle.setOn(preferencesHelper.officeOnline());
            if (preferencesHelper.officeOnline()) {
                setTopColor(R.color.colorPrimary);
            } else {
                setTopColor(R.color.toggleOff);
            }
        }

        if (preferencesHelper.getInsights()) {
            getInsights();
        } else {
            mockSetup();
        }
    }



    private void setToggle(LabeledSwitch switchToggle) {
        switchToggle.setOnToggledListener((toggleableView, isOn) -> {
            toggleCount = +1;
            if (toggleCount < 2) {
                if (isOn) {
                    getCurrentLocation(true, isOn, null);

                } else {
                    showBDialog();
                }
            } else {
                toggleCount = 0;
            }
        });
    }

    private void getCurrentLocation(Boolean forceUpdate, Boolean onMode, String time) {
        TrackThat.getCurrentLocation(new TrackThatCallback() {

            @Override
            public void onSuccess(SuccessResponse successResponse) {
                TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                currentLocation = new GeoCoordinates();
                currentLocation.setLatitude(loc.getLatitude());
                currentLocation.setLongitude(loc.getLongitude());
                Double latitude = currentLocation.getLatitude();
                Double longitude = currentLocation.getLongitude();
                StatusCoordinates statusCoordinates = new StatusCoordinates(new ArrayList(), latitude, "", longitude);
                StatusLocation statusLocation = new StatusLocation(0.00, statusCoordinates, 0.00);
                StatusRequest statusRequest = new StatusRequest(forceUpdate, onMode, time, statusLocation);
                mMainViewModel.changeStatus(httpManager, statusRequest);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                currentLocation = null;
            }
        });
    }

    private void showBDialog() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bsheet_toggle_sdk);

        close = dialog.findViewById(R.id.iv_close);

        RadioGroup radioGroup = dialog.findViewById(R.id.rg_online);

        radioGroup.setOnCheckedChangeListener((radioGroup1, checkedId) -> {
            AppCompatRadioButton radioButton = dialog.findViewById(checkedId);
            StatusRequest statusRequest;
            CharSequence text = radioButton.getText();
            if (AppConstants.ONE_HOUR.equals(text)) {
                getCurrentLocation(true, false, "ONE_HOUR");

            } else if (AppConstants.TWO_HOUR.equals(text)) {
                getCurrentLocation(true, false, "TWO_HOUR");
            } else if (AppConstants.FOUR_HOUR.equals(text)) {
                getCurrentLocation(true, false, "FOUR_HOUR");
            } else if (AppConstants.SIX_HOUR.equals(text)) {
                getCurrentLocation(true, false, "SIX_HOUR");
            } else if (AppConstants.TOMORROW_HOUR.equals(text)) {
                getCurrentLocation(true, false, "TOMORROW_AT_SAME_TIME");
            } else if (AppConstants.STAY_OFFLINE_UNTIL_CHANGED.equals(text)) {
                getCurrentLocation(true, false, "KEEP_OFFLINE_UNTIL_CHANGED");
            }
            if (dialog.isShowing())
                dialog.dismiss();

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        close.setOnClickListener(view -> {
            switchToggle.setOn(!switchToggle.isOn());
            if (dialog.isShowing())
                dialog.dismiss();
        });


    }

    private void setTopColor(Integer colorId) {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(requireActivity(), colorId));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        } else {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }


        LinearLayout topBar = taskStageDashBoardBinding.toolLayoutDashboard.findViewById(R.id.ll_topbar);
        topBar.setBackgroundColor(getResources().getColor(colorId));
    }

    private void getInsights() {
        showLoading();
        mMainViewModel.getInsights(httpManager);
    }

    private void setUp() {
        View toolLayOut = getView().findViewById(R.id.toolLayoutDashboard);

        taskCategorySpinner = toolLayOut.findViewById(R.id.spinnerTaskCategory);
        cardSpinner = toolLayOut.findViewById(R.id.cardSpinner);
        ivScanQrCode = toolLayOut.findViewById(R.id.ivScanQrCode);
        ivFilter = toolLayOut.findViewById(R.id.ivFilter);

        ivCreateTask = getView().findViewById(R.id.ivCreateTask);
        ivCreateTask.setOnClickListener(this);
        tabLayout = getView().findViewById(R.id.tabLayout);
        cardViewTab = getView().findViewById(R.id.cardViewTab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tvFromDate = getView().findViewById(R.id.tvFromDate);
        tvName = getView().findViewById(R.id.textName);
        cardLocation = getView().findViewById(R.id.cardLocation);
        locationSpinner = getView().findViewById(R.id.spinnerLocation);
        Button btnSubmit = getView().findViewById(R.id.btnSubmit);
        ImageView ivRefresh = getView().findViewById(R.id.ivRefresh);
        gridRecyclerView = getView().findViewById(R.id.gridRecyclerView);
        tvProcessState = getView().findViewById(R.id.tvProcessState);
        //  gridAttendance = mainView.findViewById(R.id.gridAttendance);
        ivNavigationIcon = toolLayOut.findViewById(R.id.ivNavigationIcon);
        ivNavigationIcon.setOnClickListener(this);
        ivScanQrCode.setOnClickListener(this);
        ivFilter.setOnClickListener(this);


        ViewTreeObserver viewTreeObserver = taskStageDashBoardBinding.rlRecycler.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                taskStageDashBoardBinding.rlRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = taskStageDashBoardBinding.rlRecycler.getMeasuredWidth();
                int height = taskStageDashBoardBinding.rlRecycler.getMeasuredHeight();
                cellwidthWillbe = width / 3;

            }
        });
        LinearLayout llInsights = getView().findViewById(R.id.llInsights);
        ViewTreeObserver llInsightsTreeObserver = llInsights.getViewTreeObserver();
        llInsightsTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llInsights.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width = llInsights.getMeasuredWidth();
                int height = llInsights.getMeasuredHeight();
                InsightscellwidthWillbe = width / 3;

            }
        });
        taskStageDashBoardBinding.cardFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hitDashboardApi", "hitDashboardApi 1");
                hitDashboardApi();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hitDashboardApi", "hitDashboardApi 2");
                hitDashboardApi();
            }
        });
        //set adapter here

//        populateCategorySpinner();

        userDetail = preferencesHelper.getUserDetail();
        if (userDetail != null) {
            tvName.setText(userDetail.getName());
        }

        //setupMap();

        taskStageDashBoardBinding.newTaskCard.setOnClickListener(view -> {
            if (mAssignedtoMeAdapter.getList().size() > 0) {
                Task task = mAssignedtoMeAdapter.getList().get(0);
                Intent intent = new Intent(getActivity(), NewTaskDetailsActivity.class);
                intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId());
                intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.getAllowSubTask());
                intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.getSubCategoryIds());
                intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.getReferenceId());
                intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId);
                intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
                intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
                intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME);
                startActivity(intent);
            }
        });

        tabLayoutNew = getView().findViewById(R.id.tabLayoutNew);
        tabLayoutNew.addTab(tabLayoutNew.newTab().setText("Recents"));

        //tabLayoutNew.addTab(tabLayoutNew.newTab().setText("Pending Actions"));
        //tabLayoutNew.addTab(tabLayoutNew.newTab().setText("Closed"));

       /* tabLayoutNew.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:{
                        currentPage = PAGE_START;
                        getTasks(cat);
                    }
                    case 1:{
                        currentPage = PAGE_START;
                        getTasks(cat);
                    }
                    case 2:{
                        currentPage = PAGE_START;
                        getTasks(cat);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/


        Log.d("Bloacks", "Setup Complete");
    }

    TabLayout tabLayoutNew;
    String cat = "";

    private void getTasks(String str) {
        List<TaskStatus> statusList = new ArrayList<>();
        categoryMap = new Gson().fromJson(str, new TypeToken<HashMap<String, String>>() {
        }.getType());
        buddyRequest = new TaskRequest(statusList, BuddyInfo.LOCATION, categoryMap);
        PagingData pagingData = new PagingData();
        pagingData.setDatalimit(10);
        pagingData.setPageOffset(0);
        pagingData.setPageIndex(currentPage);
        buddyRequest.setPaginationData(pagingData);
        buddyRequest.setUserGeoReq(userGeoReq);
        buddyRequest.setFrom(fromDate);
        buddyRequest.setTo(toDate);
        if (LOADBY.equals("ASSIGNED_TO_MERCHANT")) {
            buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_TO_MERCHANT);
        } else if (LOADBY.equals("ASSIGNED_TO_ME")) {
            buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_TO_ME);
        } else if (LOADBY.equals("ASSIGNED_BY_ME")) {
            buddyRequest.setLoadBy(BuddyInfo.ASSIGNED_BY_ME);
        } else if (LOADBY.equals("LOCATION")) {
            buddyRequest.setLoadBy(BuddyInfo.LOCATION);
        }

        api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);
        if (api != null) {
            api.setAppendWithKey(LOADBY);
        }
        mAssignedtoMeAdapter.clearItems();
        Log.d("getTaskList", "getTaskList 2");
        taskStageDashBoardBinding.cardViewTabNew.setVisibility(View.GONE);
        mMainViewModel.getTaskList(httpManager, api, buddyRequest);
    }

/*    private LineChart chart;

    private void setupMap() {
        {
            chart = getView().findViewById(R.id.reportingChart);
            chart.setBackgroundColor(Color.WHITE);
            chart.getDescription().setEnabled(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setDrawGridLines(false);
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }
        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            // axis range
            yAxis.setAxisMaximum(60);
        }

        setData(7, 50);
    }

    private void setData(int count, float range) {
        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int val = (int) (Math.random() * range);
            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.red_button_bg)));
        }
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Days");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                set1.setFillColor(getResources().getColor(R.color.orange));
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }
    }*/

    private ChapterAdapter chapterAdapter;
    private ProjectsAdapter projectAdapter;

    private void mockSetup() {
        ArrayList<ProjectCategories> categories = preferencesHelper.getProjectCategoriesDataList();
        if (categories != null) {
            if (categories.size() > 0) {
                String selectedProject = categories.get(0).getProjectId();
                String selectedCategory = categories.get(0).getCategories().get(0).getCategoryId();

                if (categories.size() == 1) {
                    taskStageDashBoardBinding.rvProjects.setVisibility(View.GONE);
                } else {
                    taskStageDashBoardBinding.rvProjects.setVisibility(View.VISIBLE);
                }

                LinearLayoutManager HorizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                taskStageDashBoardBinding.rvProjects.setLayoutManager(HorizontalLayout);
                projectAdapter = new ProjectsAdapter(categories, selectedProject, itemData -> {
                    Log.d("item project ", itemData.getName());
                    projectAdapter.setProjectId(itemData.getProjectId());
                    chapterAdapter.setCatAndItems(itemData.getCategories().get(0).getCategoryId(), itemData.getCategories());
                    onMenuClick(itemData.getCategories().get(0));
                    TabLayout.Tab tab = tabLayoutNew.getTabAt(tabLayoutNew.getSelectedTabPosition());
                    tab.setText("Recent " + itemData.getCategories().get(0).getName());
                    taskStageDashBoardBinding.listTitle.setText("Recent " + itemData.getCategories().get(0).getName());
                    taskStageDashBoardBinding.categoryName.setText(itemData.getName() + " Categories");
                });
                taskStageDashBoardBinding.rvProjects.setAdapter(projectAdapter);

                if (categories.size() > 0) {
                    taskStageDashBoardBinding.categoriesCard.setVisibility(View.VISIBLE);
                } else {
                    taskStageDashBoardBinding.categoriesCard.setVisibility(View.GONE);
                }

                chapterAdapter = new ChapterAdapter(selectedCategory, categories.get(0).getCategories(), item -> {
                    Log.d("item category ", item.getName());
                    onMenuClick(item);
                    chapterAdapter.setCat(item.getCategoryId());
                    TabLayout.Tab tab = tabLayoutNew.getTabAt(tabLayoutNew.getSelectedTabPosition());
                    tab.setText("Recent " + item.getName());
                    taskStageDashBoardBinding.listTitle.setText("Recent " + item.getName());
                });
                taskStageDashBoardBinding.rvCategories.setAdapter(chapterAdapter);

                taskStageDashBoardBinding.categoryName.setText(categories.get(0).getName() + " Categories");
                onMenuClick(categories.get(0).getCategories().get(0));
            }
        }
    }

    private void onMenuClick(Category item) {
        currentPage = 1;
        categoryId = item.getCategoryId();
        categoryName = item.getName();
        userGeoReq = catGeoMap.get(categoryId);
        Log.d("selected categoryId", categoryId);
        Log.d("selected categoryName", categoryName);
        Log.d("selected userGeoReq", userGeoReq + "");
        if (preferencesHelper.getServicePref()) {
            String tvStatus = categoryName + " Request Within Your Service Location";
            taskStageDashBoardBinding.tvProcessState.setText(tvStatus);
        } else {
            //String tvStatus = categoryName + " request status";
            String tvStatus = categoryName + " Status";
            taskStageDashBoardBinding.tvProcessState.setText(tvStatus);
        }
        List<WorkFlowCategories> workflow = preferencesHelper.getWorkFlowCategoriesList();
        for (int i = 0; i < workflow.size(); i++) {
            Log.d("stageNameMap", workflow.get(i).getCategoryId() + " == " + item.getCategoryId());
            if (workflow.get(i).getCategoryId().equals(item.getCategoryId())) {
                stageNameMap = workflow.get(i).getStageNameMap();
                Log.d("selected stageNameMap", workflow.get(i).getStageNameMap() + "");
            }
        }
        Log.d("selected stageNameMap", stageNameMap + "");
        hubId = preferencesHelper.getSelectedLocation();
        changeTab(categoryId, userGeoReq);
        if (showMerchantTab) {
            LOADBY = "ASSIGNED_TO_MERCHANT";
        }
        // Log.d("hitDashboardApi","hitDashboardApi 3");
        // hitDashboardApi();
    }

    boolean added = false;

    private void changeTab(String categoryId, boolean geoReq) {
        boolean called = false;
        if (tabLayout.getTabCount() > 0) {
            tabLayout.removeAllTabs();
        }
        List<WorkFlowCategories> listCategory = preferencesHelper.getWorkFlowCategoriesList();

        if (categoryId != null) {
            WorkFlowCategories workFlowCategories = new WorkFlowCategories();
            workFlowCategories.setCategoryId(categoryId);
            workFlowCategories.setAllowGeography(geoReq);
            if (listCategory.contains(workFlowCategories)) {
                int position = listCategory.indexOf(workFlowCategories);
                if (position != -1) {
                    WorkFlowCategories myCatData = listCategory.get(position);
                    // userGeoReq=myCatData.getAllowGeography();
                    if (myCatData.getAllowGeography()) ivFilter.setVisibility(View.VISIBLE);
                    else {
                        ivFilter.setVisibility(View.GONE);
                    }
                    if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getMappingOn() == MappingOn.CREATION) {
                        isTagInventory = true;
                    } else {
                        isTagInventory = false;
                    }
                    if (myCatData.getInventoryConfig() != null && myCatData.getInventoryConfig().getInvAction() != null) {
                        invAction = myCatData.getInventoryConfig().getInvAction().name();
                    } else {
                        invAction = null;
                    }
                    if (myCatData.getShowMerchantTasks()) {
                        showMerchantTab = true;
                    } else {
                        showMerchantTab = false;
                    }
                }
            }

        }
        Map<String, ChannelConfig> channelConfigMap = preferencesHelper.getWorkFlowCategoriesListChannel();
        if (categoryId != null && channelConfigMap != null && channelConfigMap.containsKey(categoryId)) {
            ChannelConfig channelConfig = channelConfigMap.get(categoryId);
            if (channelConfig != null && channelConfig.getChannelSetting() != null) {
                channelSetting = channelConfig.getChannelSetting();
                if (showMerchantTab) {
                    Log.d("ASSIGNED","showMerchantTab");
                    String merchantTabLabel = "Request BY Merchant";
                    if (channelSetting.getMerchantTaskLabel() != null && !channelSetting.getMerchantTaskLabel().isEmpty())
                        merchantTabLabel = channelSetting.getMerchantTaskLabel();
                    String ASSIGNED_TO_MERCHANT = merchantTabLabel;
                    tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_MERCHANT").setText(ASSIGNED_TO_MERCHANT));
                    LOADBY = "ASSIGNED_TO_MERCHANT";
                    tabLayout.getTabAt(0).select();
                }
                if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                        && channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution())
                {
                    Log.d("ASSIGNED","one");
                    if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty()) {
                        String ASSIGNED_TO_ME = channelSetting.getExecutionTitle();
                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
//                            if (channelSetting.getLocType() != null && channelSetting.getLocType().equals("FIXED")) {
//                                performLocation();
//                            } else {
//                                cardLocation.setVisibility(View.GONE);
//                            }
                            tabLayout.addTab(tabLayout.newTab().setTag("LOCATION").setText(ASSIGNED_TO_ME));
                        } else {
                            tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_ME").setText(ASSIGNED_TO_ME));
                        }
                    }
                    if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty()) {
                        String ASSIGNED_BY_ME = channelSetting.getCreationTitle();
                        tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_BY_ME").setText(ASSIGNED_BY_ME));
                    }

                    tabLayout.setVisibility(View.VISIBLE);
                    cardViewTab.setVisibility(View.VISIBLE);
                    if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                        ivCreateTask.setVisibility(View.GONE);
                    } else {
                        ivCreateTask.setVisibility(View.GONE);
                    }
//                    if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
//                        LOADBY = "LOCATION";
//                    } else {
//                        LOADBY = "ASSIGNED_TO_ME";
//                    }
                    tabLayout.getTabAt(0).select();
                }
                else if (channelSetting.getTaskExecution() != null && channelSetting.getTaskExecution()
                        && (channelSetting.getAllowCreation() == null || !channelSetting.getAllowCreation()))
                {
                    Log.d("ASSIGNED","two");
                    //String ASSIGNED_TO_ME = "Assigned to Me";
                    if (channelSetting.getExecutionTitle() != null && !channelSetting.getExecutionTitle().isEmpty()) {
                        String ASSIGNED_TO_ME = channelSetting.getExecutionTitle();
                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
                            tabLayout.addTab(tabLayout.newTab().setTag("LOCATION").setText(ASSIGNED_TO_ME));
                            if (channelSetting.getLocType() != null && channelSetting.getLocType().equals("FIXED")) {
                                performLocation();
                            } else {
                                cardLocation.setVisibility(View.GONE);
                            }
                        } else {
                            tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_TO_ME").setText(ASSIGNED_TO_ME));
                        }
                        tabLayout.setVisibility(View.GONE);
                        cardViewTab.setVisibility(View.GONE);
                        ivCreateTask.setVisibility(View.GONE);
//                        if (channelSetting.getExeLocation() != null && channelSetting.getExeLocation()) {
//                            LOADBY = "LOCATION";
//                        } else {
//                            LOADBY = "ASSIGNED_TO_ME";
//                        }
                        tabLayout.getTabAt(0).select();
                    }
                }
                else if (channelSetting.getAllowCreation() != null && channelSetting.getAllowCreation()
                        && (channelSetting.getTaskExecution() == null || !channelSetting.getTaskExecution()))
                {
                    Log.d("ASSIGNED","three");
                    //String ASSIGNED_BY_ME = "I have Assigned";
                    if (channelSetting.getCreationTitle() != null && !channelSetting.getCreationTitle().isEmpty()) {
                        String ASSIGNED_BY_ME = channelSetting.getCreationTitle();
                        tabLayout.addTab(tabLayout.newTab().setTag("ASSIGNED_BY_ME").setText(ASSIGNED_BY_ME));
                        tabLayout.setVisibility(View.GONE);
                        cardViewTab.setVisibility(View.GONE);
                        if (channelSetting.getCreationMode() == CreationMode.DIRECT) {
                            ivCreateTask.setVisibility(View.VISIBLE);
                        } else {
                            ivCreateTask.setVisibility(View.GONE);
                        }
                        // LOADBY = "ASSIGNED_BY_ME";
                        tabLayout.getTabAt(0).select();
                    }
                }
                else
                {
                    ivCreateTask.setVisibility(View.GONE);
                }
            }
            if (tabLayout.getTabCount() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
                cardViewTab.setVisibility(View.VISIBLE);
            } else {
                if (!called) {
                    Log.d("performLocationSpinnerTask", "performLocationSpinnerTask 1");
                    performLocationSpinnerTask();
                    called = true;
                }
                //  hitDashboardApi();
                cardViewTab.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
            }
        }

        if (!added) {
            if (!called) {
                Log.d("performLocationSpinnerTask","performLocationSpinnerTask 4");
                performLocationSpinnerTask();
            }
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    added = true;
                    currentPage = 1;
                    LOADBY = tab.getTag().toString();
                    Log.d("performLocationSpinnerTask","performLocationSpinnerTask 2");
                    performLocationSpinnerTask();
                    CommonUtils.showLogMessage("e", "LOADBY", "=>" + LOADBY);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
//                    currentPage = 1;
//                    LOADBY = tab.getTag().toString();
//                    performLocationSpinnerTask();
//                    CommonUtils.showLogMessage("e", "LOADBY", "=>" + LOADBY);
                }
            });
        }
    }

    public void performLocationSpinnerTask() {
        if (LOADBY.equals("LOCATION")) {
            CommonUtils.showLogMessage("e", "performLocationSpinnerTask", "=>" + LOADBY);
            List<Hub> hubsList = preferencesHelper.getUserHubList();
            if (hubsList != null && hubsList.size() == 1) {
                hubId = hubsList.get(0).getHubId();
                preferencesHelper.setSelectedLocation(hubId);
            } else {
                hubId = preferencesHelper.getSelectedLocation();
            }
            performLocation();
        } else {
            Log.d("hitDashboardApi", "hitDashboardApi 4");
            hitDashboardApi();
            hubId = null;
            cardLocation.setVisibility(View.GONE);
        }
    }

    private void performLocation() {
        if (preferencesHelper.getUserHubList() != null && !preferencesHelper.getUserHubList().isEmpty()) {
            List<Hub> hubsList = preferencesHelper.getUserHubList();
            List<String> hubsName = new ArrayList<>();
            List<String> hubIds = new ArrayList<>();
            for (int i = 0; i < hubsList.size(); i++) {
                String address = "";
                if (hubsList.get(i).getHubLocation() != null && hubsList.get(i).getHubLocation().getLocation() != null && hubsList.get(i).getHubLocation().getLocation().getLatitude() != null && hubsList.get(i).getHubLocation().getLocation().getLongitude() != null) {
                    LatLng latLng = new LatLng(hubsList.get(i).getHubLocation().getLocation().getLatitude(), hubsList.get(i).getHubLocation().getLocation().getLongitude());
                    address = CommonUtils.getAddress(getActivity(), latLng);
                }
                if (!address.isEmpty()) {
                    hubsName.add(hubsList.get(i).getName() + " | " + address);
                } else {
                    hubsName.add(hubsList.get(i).getName());
                }
                hubIds.add(hubsList.get(i).getHubId());
            }
            if (hubsList.size() > 0) {
                ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, hubsName) {

                    @NotNull
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        return v;
                    }


                    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                        return v;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locationSpinner.setAdapter(adapter);
                if (preferencesHelper.getSelectedLocation() != null && !preferencesHelper.getSelectedLocation().isEmpty()) {
                    if (hubIds.contains(preferencesHelper.getSelectedLocation())) {
                        int index = hubIds.indexOf(preferencesHelper.getSelectedLocation());
                        if (index != -1) {
                            locationSpinner.setSelection(index);
                        }
                    }
                }
                locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        hubId = hubIds.get(position);
                        preferencesHelper.setSelectedLocation(hubId);
                        Log.d("hitDashboardApi", "hitDashboardApi 5");
                        hitDashboardApi();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                cardLocation.setVisibility(View.VISIBLE);
            } /*else {
                cardLocation.setVisibility(View.GONE);
                hubId = hubsList.get(0).getHubId();
                preferencesHelper.setSelectedLocation(hubId);
                hitDashboardApi();
            }*/

        } else {
            hubId = null;
            preferencesHelper.setSelectedLocation(hubId);
            Log.d("hitDashboardApi", "hitDashboardApi 6");
            hitDashboardApi();
            cardLocation.setVisibility(View.GONE);
        }

    }

    private void hitDashboardApi() {
        Log.d("item new + ", "hitDashboardApi request");
        if (!tvFromDate.getText().toString().trim().isEmpty()) {
            if (fromDate <= toDate) {
                showLoading();
                Map<String, SaveFilterData> filterMap = preferencesHelper.getFilterMap();
                if ((filterMap != null)) {
                    if (filterMap.containsKey(categoryId)) {
                        SaveFilterData saveFilterData = filterMap.get(categoryId);
                        if (saveFilterData != null) {
                            regionId = saveFilterData.getRegionId();
                            stateId = saveFilterData.getStateId();
                            cityId = saveFilterData.getCityId();
                            hubIdStr = saveFilterData.getHubId();

                            if (hubIdStr != null && !hubIdStr.isEmpty()) {
                                hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
                            }
                        }

                    }
                }
                for (int i = 0; i < catIds.size(); i++) {
                    if (Objects.equals(catIds.get(i), categoryId)) userGeoReq = catGeo.get(i);
                }
                ProfileInfo user = preferencesHelper.getUserDetail();
                if (preferencesHelper != null && user != null && user.getUserId() != null) {
                    Log.d("Bloacks", "Dashboard Request Called" + categoryId);
                    Log.d("TimeCheck", DateTimeUtil.getParsedDate(fromDate, "dd/MM/yyyy h:mm aa") + " -dashboardApi- " + DateTimeUtil.getParsedDate(toDate, "dd/MM/yyyy h:mm aa"));
                    mMainViewModel.dashboardApi(httpManager, categoryId, fromDate, toDate, user.getUserId(), LOADBY, hubIds, regionId, stateId, cityId, userGeoReq, hubId);
                }

            } else {
                if (getActivity() != null)
                    TrackiToast.Message.showShort(getActivity(), "From Date can't be greater than To Date");
            }
        } else {
            if (getActivity() != null)
                TrackiToast.Message.showShort(getActivity(), "Please Select To date");

        }
    }

    private void populateCategorySpinner() {
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
        fromDate = cal.getTime().getTime();
        toDate = toDateDateAdd24Hour().getTime();
        Log.d("TimeCheck", DateTimeUtil.getChatDateFormat(fromDate) + " -one- " + DateTimeUtil.getChatDateFormat(toDate));
        tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));
        List<String> categories = new ArrayList<>();
        List<String> categoriesId = new ArrayList<>();
        catGeo = new ArrayList<>();
        catGeoMap = new HashMap<>();
        catIds = new ArrayList<>();
        if (preferencesHelper.getWorkFlowCategoriesList() != null) {
            List<WorkFlowCategories> list = preferencesHelper.getWorkFlowCategoriesList();
            for (WorkFlowCategories data : list) {
                categories.add(data.getName());
                Log.d("time here", data.getName());
                categoriesId.add(data.getCategoryId());
                catIds.add(data.getCategoryId());
                catGeo.add(data.getAllowGeography());
                catGeoMap.put(data.getCategoryId(), data.getAllowGeography());
            }
            cardSpinner.setVisibility(View.VISIBLE);
            cardViewTab.setVisibility(View.VISIBLE);
        } else {
            cardSpinner.setVisibility(View.GONE);
            cardViewTab.setVisibility(View.GONE);
        }

    }

    private Date toDateDateAdd24Hour() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        //c.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange()==0?15:preferencesHelper.getMaxDateRange());
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
        if (getActivity() != null) {
            CommonUtils.openDatePicker(getActivity(), mYear, mMonth, mDay, minTime, 0, (view, year, monthOfYear, dayOfMonth) -> {


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                // fromDate=0;
                // tvFromDate.setText(DateTimeUtil.getParsedDate(calendar.getTimeInMillis()));
                // toDate = 0;
                CommonUtils.openDatePicker(getActivity(), mYear, mMonth, mDay, calendar.getTimeInMillis(), 0, (view_, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {

                    fromDate = calendar.getTimeInMillis();
                    Calendar calEnd = Calendar.getInstance();
                    calEnd.set(Calendar.YEAR, yearEnd);
                    calEnd.set(Calendar.MONTH, monthOfYearEnd);
                    calEnd.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
                    calEnd.set(Calendar.HOUR_OF_DAY, 23);
                    calEnd.set(Calendar.MINUTE, 59);
                    calEnd.set(Calendar.SECOND, 0);
                    toDate = calEnd.getTimeInMillis();
                    Log.d("TimeCheck", DateTimeUtil.getParsedDate(fromDate) + " -two- " + DateTimeUtil.getParsedDate(toDate));

                    tvFromDate.setText(DateTimeUtil.getParsedDate(fromDate) + " - " + DateTimeUtil.getParsedDate(toDate));


                });

            });
        }
    }

    @Override
    public void handleDashboardResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        if (getActivity() != null) {
            if (gridRecyclerView.getAdapter() != null)
                ((DashBoardStageCountAdapter) gridRecyclerView.getAdapter()).clearList();
            if (CommonUtils.handleResponse(callback, error, result, getActivity())) {
                taskStageDashBoardBinding.rlRecycler.setVisibility(View.VISIBLE);
                DashBoardMapResponse dashboardResponse = new Gson().fromJson(String.valueOf(result), DashBoardMapResponse.class);
                if (dashboardResponse.getTaskCountMap() != null) {
                    tvProcessState.setVisibility(View.VISIBLE);
                    ArrayList<DashBoardBoxItem> list = new ArrayList<>();
                    Iterator hmIterator = stageNameMap.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry) hmIterator.next();
                        DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                        dashBoardBoxItem.setCategoryId(categoryId);
                        if (dashboardResponse.getTaskCountMap().get(mapElement.getKey()) != null)
                            dashBoardBoxItem.setCount((int) dashboardResponse.getTaskCountMap().get(mapElement.getKey().toString()));
                        dashBoardBoxItem.setStageId(mapElement.getKey().toString());
                        dashBoardBoxItem.setStageName(mapElement.getValue().toString());
                        list.add(dashBoardBoxItem);
                    }
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
                    gridRecyclerView.setLayoutManager(gridLayoutManager);
                    DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(list);
                    adapter.setListener(this);
                    adapter.cellWidth(cellwidthWillbe);
                    gridRecyclerView.setAdapter(adapter);
                    if (list.size() > 0) {
                        cat = new Gson().toJson(list.get(0));
                    } else {
                        cat = new Gson().toJson(new ArrayList());
                    }
                    getTasks(cat);
                } else {
                    tvProcessState.setVisibility(View.GONE);
                    LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity());
                    gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    gridRecyclerView.setLayoutManager(gridLayoutManager);
                    DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(new ArrayList<>());
                    adapter.setListener(this);
                    adapter.cellWidth(cellwidthWillbe);
                    gridRecyclerView.setAdapter(adapter);
                }
            } else {
                tvProcessState.setVisibility(View.GONE);
                LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getActivity());
                gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                gridRecyclerView.setLayoutManager(gridLayoutManager);
                DashBoardStageCountAdapter adapter = new DashBoardStageCountAdapter(new ArrayList<>());
                adapter.setListener(this);
                adapter.cellWidth(cellwidthWillbe);
                gridRecyclerView.setAdapter(adapter);

            }
        }
        Log.d("Bloacks", "Dashboard Response");
    }

    @Override
    public void handleStatusResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        try {
            JSONConverter jsonConverter = new JSONConverter();
            BaseResponse response = (BaseResponse) jsonConverter.jsonToObject(result.toString(), BaseResponse.class);
            Log.e("mode", "" + response.getSuccessful());
            toggleCount = +1;
            if (response.getSuccessful()) {
                Boolean mode = response.getOnline();
                preferencesHelper.setOfficeOnline(mode);
                switchToggle.setOn(mode);
                if (mode == true) {
                    TrackiToast.Message.showShort(requireContext(), "Store set to online");
                    setTopColor(R.color.toggleOn);
                } else {
                    TrackiToast.Message.showShort(requireContext(), "Store set to offline");
                    setTopColor(R.color.toggleOff);
                }
            } else {
                Boolean mode = !switchToggle.isOn();
                Log.e("mode", "" + mode);
                preferencesHelper.setOfficeOnline(mode);
                switchToggle.setOn(mode);
            }
        } catch (Exception exception) {
            Log.e("Status Change Exception", "" + exception.getMessage());
        }
    }

    @Override
    public void handleInsightsResponse(ApiCallback apiCallback, Object result, APIError error) {
        hideLoading();
        if (CommonUtils.handleResponse(apiCallback, error, result, getActivity())) {
            JSONConverter jsonConverter = new JSONConverter();
            InsightResponse response = (InsightResponse) jsonConverter.jsonToObject(result.toString(), InsightResponse.class);
            if (response.getData() != null) {
                taskStageDashBoardBinding.cardInsights.setVisibility(View.VISIBLE);
                if (response.getData().getInsights() != null && !response.getData().getInsights().isEmpty()) {
                    InsightAdapter adapter = new InsightAdapter((ArrayList<ServiceDescr>) response.getData().getInsights());
                    adapter.cellWidth(InsightscellwidthWillbe);
                    taskStageDashBoardBinding.rvInsight.setAdapter(adapter);
                }


                if (response.getData().getHeading() != null && !response.getData().getHeading().isEmpty()) {
                    taskStageDashBoardBinding.tvInsightsHeading.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvInsightsHeading.setText(response.getData().getHeading());
                } else {
                    taskStageDashBoardBinding.tvInsightsHeading.setVisibility(View.GONE);
                }
                if (response.getData().getSubheading() != null && !response.getData().getSubheading().isEmpty()) {
                    taskStageDashBoardBinding.tvSubheading.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvSubheading.setText(response.getData().getSubheading());
                } else {
                    taskStageDashBoardBinding.tvSubheading.setVisibility(View.GONE);
                }
                if (response.getData().getFooter() == null && response.getData().getButton() == null) {
                    taskStageDashBoardBinding.llFooter.setVisibility(View.GONE);
                }
                if (response.getData().getFooter() != null && !response.getData().getFooter().isEmpty()) {
                    taskStageDashBoardBinding.tvFooter.setVisibility(View.VISIBLE);
                    taskStageDashBoardBinding.tvFooter.setText(response.getData().getFooter());
                } else {
                    taskStageDashBoardBinding.tvFooter.setVisibility(View.GONE);
//                    LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) taskStageDashBoardBinding.llFooter.getLayoutParams();
//                    ll.gravity = Gravity.CENTER;
//                    taskStageDashBoardBinding.llFooter.setLayoutParams(ll);
//                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            0.5f
//                    );
//                    taskStageDashBoardBinding.btnInsights.setLayoutParams(param);
                }
                if (response.getData().getImg() != null && !response.getData().getImg().isEmpty()) {
                    taskStageDashBoardBinding.ivInsights.setVisibility(View.VISIBLE);
                    GlideApp.with(this).load(response.getData().getImg()).error(R.drawable.ic_undraw_notify).into(taskStageDashBoardBinding.ivInsights);
                }

                if (response.getData().getButton() != null) {
                    if (response.getData().getButton().getTitle() != null && !response.getData().getButton().getTitle().isEmpty()) {
                        taskStageDashBoardBinding.btnInsights.setText(response.getData().getButton().getTitle());
                    }
                    taskStageDashBoardBinding.btnInsights.setVisibility(View.VISIBLE);
                    if (response.getData().getButton().getUrl() != null && response.getData().getButton().getUrl().equals("CREATE_TASK")) {
                        taskStageDashBoardBinding.btnInsights.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = NewCreateTaskActivity.Companion.newIntent(getActivity());
                                startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);

                            }
                        });
                    } else if (response.getData().getButton().getUrl() != null && response.getData().getButton().getUrl().equals("SHARE")) {
                        taskStageDashBoardBinding.btnInsights.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkStoragePermission();

                            }
                        });
                    }
                } else {
                    taskStageDashBoardBinding.btnInsights.setVisibility(View.GONE);
                }
            }
        } else {
            taskStageDashBoardBinding.cardInsights.setVisibility(View.GONE);
        }
        Log.d("Bloacks", "Insights Response");
        mockSetup();
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
        hideLoading();
        isLoading = false;
        if (CommonUtils.handleResponse(callback, error, result, getActivity())) {
            taskStageDashBoardBinding.cardViewTabNew.setVisibility(View.VISIBLE);
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    TaskListing taskListing = new Gson().fromJson(String.valueOf(result), TaskListing.class);
                    if (taskListing.getPaginationData() != null) {
                        List<Task> list = taskListing.getTasks();
                        List<Task> listNew = new ArrayList<>();
                       /* for (int i = 0; i < list.size(); i++) {
                            if (tabLayoutNew.getSelectedTabPosition()==0){
                                if (list.get(i).getCurrentStage().getInitial()==true){
                                    listNew.add(list.get(i));
                                }
                            }else  if(tabLayoutNew.getSelectedTabPosition()==2){
                                if (list.get(i).getCurrentStage().getTerminal()==true){
                                    listNew.add(list.get(i));
                                }
                            }else {
                                    listNew.add(list.get(i));
                            }
                        }
                        */
                        mMainViewModel.getTaskListLiveData().setValue(list);
                        setRecyclerView();
                        CommonUtils.showLogMessage("e", "adapter total_count =>", "" + mAssignedtoMeAdapter.getItemCount());
                        CommonUtils.showLogMessage("e", "fetch total_count =>", "" + taskListing.getPaginationData().getDataCount());
                        if (taskListing.getPaginationData().getDataCount() == mAssignedtoMeAdapter.getItemCount()) {
                            isLastPage = true;
                        }
                    }

//                    setFilterItems(list, item);
                });
            }
        }
        Log.d("Bloacks", "Tasks Response");
    }

    private void setRecyclerView() {

        if (rvAssignedToMe == null) {
            rvAssignedToMe = getView().findViewById(R.id.rvAssignedToMe);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvAssignedToMe.setLayoutManager(mLayoutManager);
            rvAssignedToMe.setItemAnimator(new DefaultItemAnimator());
            rvAssignedToMe.setAdapter(mAssignedtoMeAdapter);
            rvAssignedToMe.setNestedScrollingEnabled(false);
        }

        taskStageDashBoardBinding.mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mAssignedtoMeAdapter.getItemCount() > 0) {

                    View view = (View) taskStageDashBoardBinding.mScrollView.getChildAt(taskStageDashBoardBinding.mScrollView.getChildCount() - 1);

                    int diff = (view.getBottom() - (taskStageDashBoardBinding.mScrollView.getHeight() + taskStageDashBoardBinding.mScrollView.getScrollY()));

                    if (diff == 0) {
                        if (!isLoading) {
                            isLoading = true;
                            currentPage++;
                            showLoading();
                            api = TrackiSdkApplication.getApiMap().get(ApiType.TASKS);

                            if (api != null) {
                                if (buddyRequest.getLoadBy() != null) {
                                    api.setAppendWithKey(buddyRequest.getLoadBy().name());
                                } else {
                                    api.setAppendWithKey("ASSIGNED_TO_ME");
                                }
                            }
                            PagingData pagingData = new PagingData();
                            pagingData.setDatalimit(10);
                            pagingData.setPageOffset((currentPage - 1) * 10);
                            pagingData.setPageIndex(currentPage);
                            buddyRequest.setUserGeoReq(userGeoReq);
                            buddyRequest.setPaginationData(pagingData);
                            Log.d("getTaskList", "getTaskList 4");
                            mMainViewModel.getTaskList(httpManager, api, buddyRequest);
                        }
                    }
                }
            }
        });
        mMainViewModel.addItemsToList(mMainViewModel.getTaskListLiveData().getValue());
        mAssignedtoMeAdapter.addItems(mMainViewModel.getBuddyObservableArrayList());

        if (mAssignedtoMeAdapter.getList().size() > 0) {
            taskStageDashBoardBinding.cardViewTabNew.setVisibility(View.VISIBLE);
            if (mAssignedtoMeAdapter.getList().get(0).getCurrentStage().getTerminal() == true) {
                taskStageDashBoardBinding.newTaskCard.setVisibility(View.GONE);
            } else {
                taskStageDashBoardBinding.newTaskCard.setVisibility(View.VISIBLE);
            }
        } else {
            taskStageDashBoardBinding.cardViewTabNew.setVisibility(View.GONE);
            taskStageDashBoardBinding.newTaskCard.setVisibility(View.GONE);
        }
        hideLoading();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivScanQrCode) {
            getCameraPermission();
        } else if (id == R.id.ivFilter) {
            Intent intentFilter = TaskFilterActivity.Companion.newIntent(getActivity());
            if (regionId != null) intentFilter.putExtra("regionId", regionId);
            if (hubIdStr != null) intentFilter.putExtra("hubIdStr", hubIdStr);
            if (stateId != null) intentFilter.putExtra("stateId", stateId);
            if (cityId != null) intentFilter.putExtra("cityId", cityId);
            if (categoryId != null) intentFilter.putExtra("categoryId", categoryId);
            intentFilter.putExtra("from", AppConstants.TASK);
            startActivityForResult(intentFilter, AppConstants.REQUEST_CODE_FILTER_USER);
        } else if (id == R.id.ivNavigationIcon) {
            if (mListener != null) {
                mListener.taskNavigationClick();
            }
        } else if (id == R.id.ivCreateTask) {
            CommonUtils.preventTwoClick(v);
            //showLoading();
            //  mMainViewModel.checkInventoryData(httpManager, categoryId);
            if (preferencesHelper.getIsTrackingLiveTrip() && categoryId.equals(preferencesHelper.getActiveTaskCategoryId())) {
                TrackiToast.Message.showShort(getActivity(), AppConstants.MSG_ONGOING_TASK_SAME_CATGEORY);

            } else {
                if (isTagInventory) {
                    Intent intent = SelectOrderActivity.Companion.newIntent(getActivity());
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setFile", Context.MODE_PRIVATE);
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    sharedPreferences.edit().putString("setFile", categoryId).apply();
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, new Gson().toJson(dashBoardBoxItem));
                    if (invAction != null && !invAction.isEmpty())
                        intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET, invAction);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_TAG_INVENTORY);

                } else {

                    Intent intent = NewCreateTaskActivity.Companion.newIntent(getActivity());
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, new Gson().toJson(dashBoardBoxItem));
                    intent.putExtra(AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU, true);
                    // intent.putExtra("from", "taskListing");
                    intent.putExtra("from", "dashBoard");
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK);
                }
            }
//                InventoryRequest request=new InventoryRequest();
//                request.setCategoryId(categoryId);
//                mMainViewModel.checkInventoryData(httpManager, request);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE_DYNAMIC_FORM:
                if (resultCode == RESULT_OK) {
                    //if there is no data in the form
                    if (!TrackThat.isTracking()) {
                        preferencesHelper.remove(AppPreferencesHelper.PREF_KEY_TASK);
                        preferencesHelper.remove(AppPreferencesHelper.PREF_KEY_GEOFENCE_IDS);
                    } else {
                        // checkForGeofence();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    // checkForGeofence();
                }
                break;
            case AppConstants.REQUEST_CODE_CREATE_TASK:
                if (resultCode == RESULT_OK) {
                    if (tabLayout.getVisibility() == View.VISIBLE) {
                        if (tabLayout.getTabAt(0).getTag().equals("ASSIGNED_TO_MERCHANT")) {
                            tabLayout.getTabAt(0).select();
                        } else if (tabLayout.getTabAt(0).getTag().equals("ASSIGNED_BY_ME")) {
                            tabLayout.getTabAt(0).select();
                        } else if (tabLayout.getTabAt(1).getTag().equals("ASSIGNED_BY_ME")) {
                            tabLayout.getTabAt(1).select();
                        }

                    }
                    if (showMerchantTab) {
                        LOADBY = "ASSIGNED_TO_MERCHANT";
                    } else {
                        LOADBY = "ASSIGNED_BY_ME";
                    }
                    Log.d("hitDashboardApi", "hitDashboardApi 8");
                    hitDashboardApi();
                }
                break;
            //
            case AppConstants.REQUEST_CODE_TAG_INVENTORY:
                if (resultCode == RESULT_OK) {
                    DashBoardBoxItem dashBoardBoxItem = new DashBoardBoxItem();
                    dashBoardBoxItem.setCategoryId(categoryId);
                    if (stageNameMap != null && stageNameMap.size() > 0) {
                        Map.Entry<String, String> entry = stageNameMap.entrySet().iterator().next();
                        String key = entry.getKey();
                        String value = entry.getValue();
                        dashBoardBoxItem.setStageId(key);
                        dashBoardBoxItem.setStageName(value);
                    }
                    onItemClick(dashBoardBoxItem);
                }

                break;
            case AppConstants.REQUEST_CODE_TASK_UPDATE: {
                Log.d("performLocationSpinnerTask", "performLocationSpinnerTask 3");
                performLocationSpinnerTask();
                Log.d("hitDashboardApi", "hitDashboardApi 9");
                hitDashboardApi();
            }
            case AppConstants.REQUEST_CODE_FILTER_USER: {
                if (resultCode == RESULT_OK) {
                    userGeoReq = true;
//                    regionId = data.getStringExtra("regionId");
//                    hubIdStr = data.getStringExtra("hubId");
//                    if (hubIdStr != null && !hubIdStr.isEmpty()) {
//                        hubIds = Arrays.asList(hubIdStr.split("\\s*,\\s*"));
//                    }
//                    stateId = data.getStringExtra("stateId");
//                    cityId = data.getStringExtra("cityId");
                    Log.d("hitDashboardApi", "hitDashboardApi 11");
                    hitDashboardApi();

                } else if (resultCode == RESULT_CANCELED) {
                    regionId = null;
                    hubIds = null;
                    hubIdStr = null;
                    stateId = null;
                    cityId = null;
                    userGeoReq = false;
                    Log.d("hitDashboardApi", "hitDashboardApi 12");
                    hitDashboardApi();
                }
            }
            default:
                break;
        }
    }

    @Override
    public void onItemClick(@NotNull DashBoardBoxItem response) {
        Intent intent = TaskActivity.newIntent(getActivity());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORIES, new Gson().toJson(response));
        if (categoryName != null) intent.putExtra(AppConstants.Extra.TITLE, categoryName);
        intent.putExtra(AppConstants.Extra.EXTRA_STAGEID, response.getStageId());
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        intent.putExtra(AppConstants.Extra.LOAD_BY, LOADBY);
        intent.putExtra(AppConstants.Extra.GEO_FILTER, userGeoReq);
        startActivityForResult(intent, AppConstants.REQUEST_CODE_TASK_UPDATE);
    }

    public void getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openScanActivity();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        } else {
            openScanActivity();
        }
    }

    private void openScanActivity() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Scan", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Scan", "Task").apply();
        startActivity(ScanQrAndBarCodeActivity.Companion.newIntent(getActivity()));
    }

    @Override
    public void onItemClick(Task task, int position) {
        Intent intent = new Intent(getActivity(), NewTaskDetailsActivity.class);
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId());
        intent.putExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, task.getAllowSubTask());
        intent.putExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, task.getSubCategoryIds());
        intent.putExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID, task.getReferenceId());
        intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId);
        intent.putExtra(AppConstants.Extra.FROM_DATE, fromDate);
        intent.putExtra(AppConstants.Extra.FROM_TO, toDate);
        intent.putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_TO_ME);
        startActivity(intent);
    }

    @Override
    public void onCallClick(String mobile) {
        CommonUtils.openDialer(getActivity(), mobile);
    }

    @Override
    public void onDetailsTaskClick(Task task) {
        startActivityForResult(DynamicFormActivity.Companion.newIntent(getActivity()).putExtra(AppConstants.Extra.EXTRA_TCF_ID, task.getTcfId()).putExtra(AppConstants.Extra.EXTRA_FORM_ID, task.getTcfId()).putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId()).putExtra(AppConstants.Extra.HIDE_BUTTON, true).putExtra(AppConstants.Extra.EXTRA_IS_EDITABLE, false), AppConstants.REQUEST_CODE_DYNAMIC_FORM);
    }

    public interface NavigationClickFromTaskDashBoard {
        void taskNavigationClick();

    }

    private NavigationClickFromTaskDashBoard mListener;

    public void setListener(NavigationClickFromTaskDashBoard mListener) {
        this.mListener = mListener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanActivity();
                }
            }
        } else if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImageSending();
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(refreshTaskListReceiver, new IntentFilter(AppConstants.ACTION_REFRESH_TASK_LIST));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(refreshTaskListReceiver);
    }

    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                proceedToImageSending();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            }
        } else {
            proceedToImageSending();
        }
    }

    private void proceedToImageSending() {
        String appPackageName = getActivity().getPackageName();
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String text = "Help us spread the word. Join us to serve the community." + "\n" + "Download App:" + "\n" + link;
        // String text=getString(R.string.app_name)+" believes in Power of United Community and extending help by connecting those who need help with someone who can help. Download now: "+link;
        CommonUtils.shareImageAndTextOnSocialMedia(getActivity(), text);

    }


}
