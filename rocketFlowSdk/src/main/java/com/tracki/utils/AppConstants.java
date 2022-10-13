package com.tracki.utils;

public final class AppConstants {

    //public static final String CORE_BASE_URL = "https://uat.rocketflyer.in/rfapi/";
    public static final String CORE_BASE_URL = "https://api.rocketflow.in/rfapi/";
    public static final String BASE_URL = CORE_BASE_URL+"secure/tracki/";

    public static final String UAT_CORE_BASE_URL = "https://uat.rocketflyer.in/rfapi/";
    public static final String UAT_BASE_URL = UAT_CORE_BASE_URL+"secure/tracki/";

    public static final String TRACKI_ENCRYPT_DECRYPT_IV = "tracki9990731159";
    public static final String TRACKI_ENCRYPT_DECRYPT_SECRET_KEY = "9958054435vikask";

    // Algorithm
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String KEY_SPEC_ALGORITHM = "AES";
    public static final String PROVIDER = "BC";

    public static final String SECRET_KEY = "SECRET_KEY";
    public static final int OUTPUT_KEY_LENGTH = 256;
    public static final String PREF_NAME = "retailuser_pref";
    public static final String APP_VERSION = "app_version";
    public static final String PLATFORM = "platform";
    public static final String ANDROID = "android";
    public static final String DELIVERY_CHARGE = "DELIVERY_CHARGE";
    public static final String DELIVERY_MODE = "DELIVERY_MODE";
    public static final int REQUEST_CODE_PROFILE = 1001;
    public static final int REQUEST_CODE_CHANGE_MOBILE = 1002;
    public static final int REQUEST_CODE_TASK_FILTER = 1003;
    public static final int REQUEST_CODE_BUDDY_LIST = 1004;
    public static final int REQUEST_CODE_ADD_BUDDY = 1005;
    public static final int REQUEST_CODE_FILTER_USER = 1006;
    public static final int REQUEST_CODE_CREATE_TASK = 1007;
    public static final int REQUEST_CODE_CREATE_TASK_DIRECT = 1026;
    public static final int REQUEST_CODE_SELECT_FLEET = 1008;
    public static final int REQUEST_CODE_FLEET_LIST = 1009;
    public static final int PERMISSIONS_REQUEST_CODE_READ_PHONE = 1010;
    public static final int PERMISSIONS_REQUEST_CODE_LOCATION = 1011;
    public static final int PERMISSIONS_REQUEST_CODE_MULTIPLE = 1012;
    public static final int REQUEST_CODE_ADD_FLEET = 1013;
    public static final int REQUEST_CODE_TASK_UPDATE = 1014;

    public static final String APP_ID = "app_id";
    public static final String DEVICE_ID = "device_id";
    public static final String ALERT_NO_CONNECTION = "Please check your connection and try again.";
    public static final String SLOW_INTERNET_CONNECTION_MESSAGE= "Your internet is too slow .Please change your network settings";
    public static final String CONNECTION_ERROR = "Connection Error";
    public static final String RETRY = "Retry";
    public static final String CHANGE_SEETINGS = "Settings";
    public static final String CLOSE = "Close";
    public static final String ALERT_REQUEST_TIME_OUT = "Request time out, Please try again";
    public static final String ALERT_TRY_AGAIN = "Try Again";
    public static final String UNABLE_TO_PROCESS_REQUEST = "Unable to process the request. please try after some time";
    public static final String ALERT_SERVER_UPGRADE_IN_PROGRESS = "We are upgrading our server, Please try again after some time";
    public static final String MSG_REQUEST_TIME_OUT_TYR_AGAIN = "Request timeout, Please try again";
    public static final String MOBILE = "mobile";
    public static final int REQUEST_CODE_BUDDY_PROFILE = 1014;
    public static final int REQUEST_CODE_CALL_BUDDY = 1015;
    public static final int REQUEST_CODE_FLEET_PROFILE = 1016;
    public static final int REQUEST_CODE_REJECT_TASK = 1017;
    public static final int REQUEST_CODE_ADD_EMERGENCY_CONTACT = 1018;
    public static final int REQUEST_CODE_PICK_CONTACT = 1019;
    public static final int REQUEST_CODE_EDIT_EMERGENCY_CONTACT = 1020;
    public static final int REQUEST_CODE_EDIT_TASK = 1021;
    public static final int REQUEST_CODE_CANCEL_BUDDY_REQUEST = 1022;
    public static final int REQUEST_CODE_RESTART_TRANSITION_SERVICE = 128320;
    public static final int REQUEST_CODE_RESTART_TIMER_TASK = 874378;
    public static final int REQUEST_CODE_DASHBOARD_BUDDIES = 1023;
    public static final int REQUEST_CODE_CHAT_FROM_AFTER_SELECT_BUDDY = 1024;
    public static final int REQUEST_CODE_FOR_PLACE = 8743;
    public static final String DASHBOARD = "DASHBOARD";
    public static final String PRODUCT_CATEGORY = "PRODUCT_CATEGORY";
    public static final String PRODUCTS = "PRODUCTS";
    public static final String PAYOUTS = "PAYOUTS";
    public static final String MY_BUDDY = "MY_BUDDY";
    public static final String MY_LOCATION = "USER_LOCATIONS";
    public static final String BROADCAST = "BROADCAST";
    public static final String FLEET = "FLEET";
    public static final String TASK = "TASK";
    public static final String SETTINGS = "SETTINGS";
    public static final String LOGOUT = "LOGOUT";
    public static final String PROFILE = "profile";
    public static final String ACCOUNTS_DATA = "accounts_data";
    public static final String CONVERSATION = "CONVERSATION";
    public static final String MY_EARNINGS = "MY_EARNINGS";
    public static final String ATTENDANCE = "ATTENDANCE";
    public static final String CUSTOMERS = "CUSTOMERS";
    public static final String EMPLOYEES = "EMPLOYEES";
    public static final String REQUESTED_USER_TYPES = "REQUESTED_USER_TYPES";
    public static final String OWNER = "OWNER";
    public static final String ATTENDANCE_Title = "Attendance";
    public static final String BASIC_INFO = "Basic Info";
    public static final String ADDRESS = "Address";
    public static final String LEAVE = "LEAVE";
    public static final String LEAVE_TEXT = "Leave";
    public static final String ATTENDNACE = "Attendance";
    public static final int REQUEST_CODE_DYNAMIC_FORM = 1024;
    public static final int REQUEST_CODE_TAG_INVENTORY = 1025;
    public static final int REQUEST_CODE_SCAN = 1032;
    public static final int REQUEST_CODE_UNIT_INFO = 1028;
    public static final int SERVICE_ID_1 = 10001;
    public static final int SERVICE_ID_2 = 10002;
    public static final int SERVICE_ID_3 = 10003;
    public static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 2930840;
    public static final String ADD_MORE = "add_more";
    public static final String ACTION_REFRESH_TASK_LIST = "refresh_task_list";

    public static final String ESTIMATE_EARNINGS = "Estimated Earnings:";
    public static final String INR = "INR";
    public static final String KM = "km";
    public static final String MIN = "Min.";
    public static final String RIDES = "Rides";
    public static final String RIDE = "Ride";
    public static final String CONTINUE = "Continue";
    public static final String CANCEL = "Cancel";
    public static final String TEXT_PLACE_YOUR_FINGER = "Place you finger on the device home button to verify your identity";
    public static final String UNLOCK = "Unlock";
    public static final String TOUCH_ID_FOR = "Touch ID for";
    public static final String FAILED = "Failed";

    public static final String SUBMIT = "Submit";
    public static final String CAPTURED_KEY = "Selfie";
    public static final String PUNCH_IN_OUT = "My Attendance";
    public static final String ATTENDANCE_I = "My Summary";
    public static final String TEAM_ATTENDANCE = "Team Attendance";
    public static final String TASK_START_NOTI_TITLE = "Task Going To Start Soon";
    public static final String TASK_START_NOTI_BODY = "A trip assigned to you will start in 30 minutes!";
    public static final String ALL = "All";
    public static final String HISTORY = "History";
    public static final String APPROVAL = "Leave Approval";
    public static final String APPLY = "Apply Leave";
    public static final String SUMMARY = "Leave Summary";
    public static final String APPLIED = "Applied";
    public static final String PENDING = "Pending";
    public static final String APPROVED = "Approved";
    public static final String REJECTED = "Rejected";
    public static final String CANCELLED = "Cancelled";
    public static final String PRESENT = "Present";
    public static final String ABSENT = "Absent";
    public static final String DAY_OFF = "Day Off";
    public static final String HOLIDAY = "Holiday";
    public static final String ON_LEAVE = "On Leave";
    public static final String APPROVING_LEAVE = "Approving Leave";
    public static final String REJECTING_LEAVE = "Rejecting Leave";
    public static final String APPROVE_NOW = "Approve Now";
    public static final String REJECT_NOW = "Reject Now";
    public static final String TT_EMPTY = "Task type cannot be empty";
    public static final String TEXT_PREVIEW_FORM = "Form Preview";
    public static final String APPROVERS = "Approver(s):";
    public static final String START = "START";
    public static final String END = "END";
    public static final String MSG_ONGOING_TASK = "Ongoing task found Please complete that task";
    public static final String MSG_ONGOING_TASK_SAME_CATGEORY = "Ongoing task found on same catgeory,Please complete that task";
    public static final String LOCATION_TYPE = "locationType";
    public static final String GEOFENCE = "GEOFENCE";
    public static final String RADIUS = "radius";
    public static final String MSG_NO_LOCATION = "Unable to get location. Try Again";
    public static final String TIMED = "TIMED";
    public static final String TIME = "time";
    public static final String TIMEOUT = "timeout";
    public static final String MSG_TIME_PASSED = "Cannot perform this action now.";
    public static final String TASK_ID = "taskId";

    public static final String START_ALARM_INFO = "start_alarm_info";
    public static final String STOP_ALARM_INFO = "stop_alarm_info";

    public static final String RESTART = "restart";
    public static final String OLD_SHIFT_MAP = "old_shift_map";
    public static final String ALWAYS_NEW_SHIFT_MAP = "ALWAYS_NEW_SHIFT_MAP";
    public static final String CONFIG_CHANNEL_MAP = "CONFIG_CHANNEL_MAP";
    public static final String PREF_KEY_FLAVOUR_MAP = "PREF_KEY_FLAVOUR_MAP";
//    public static final String NEW_SHIFT_MAP = "new_shift_map";

    public static final String IS_HUB_CHANGED = "is_hub_changed";
    public static final String IS_MAP_CHANGED = "is_map_changed";

    public static final String OLD_HUB_LOCATION_LIST = "old_hub_location_list";
    public static final String NEW_HUB_LOCATION_LIST = "new_hub_location_list";
    public static final String HEARTBEAT = "heartbeat";
    public static final String TRACK = "track";
    public static final String DFID="dfId";
    public static final String DFDID="dfdId";
    public static final String DRAFT_ID="draftid";
    public static final String TITLE="title";
    public static final String MERCHNAT_ID="merchnat_id";
    public static final String NOTIFICATION_DATA="NOTIFICATION_DATA";

    public static final String START_REC = "Start Recording";
    public static final String STOP_REC = "Stop Recording.......";
    public static final String PLAY_AUDIO = "Play Audio";
    public static final String STOP_AUDIO = "Stop Audio";
    public static final String SINGNATURE_LIST = "Signature List";
    public static final String AUDIO_LIST = "Audio List";
    public static final String VIDEO_LIST = "Video List";
    public static final String CAMERA_LIST = "Camera List";
    public static final String FILE_LIST = "File List";
    public static final String TIME_ZONE = "Asia/Kolkata";
    public static int REQUEST_TIME_ZONE=1117;
    public static int TIME_DIFF=15*60 * 1000;
    public static int FROM_CAMERA=11;
    public static int FROM_NORMAL=12;


    private AppConstants() {
        // This utility class is not publicly instantiable
    }

    public final class Analytics {
        public static final String EXTRA_EVENT_NAME = "event_name";
        public static final String EXTRA_EVENT = "event";
        public static final String EXTRA_PAGE_NAME = "page_name";

        public final class Events {
            public static final String EVENT_INTRO_SKIP_CLICKED = "Intro Skip Clicked";
            public static final String EVENT_INTRO_NEXT_CLICKED = "Intro Next Clicked";
            public static final String EVENT_INTRO_START_CLICKED = "Intro Start Clicked";
            public static final String EVENT_LOGIN_CLICKED = "Login Clicked";
            public static final String EVENT_SEND_OTP_CLICKED = "Send Otp Clicked";
            public static final String EVENT_RESEND_OTP_CLICKED = "Resend Otp Clicked";
            public static final String EVENT_REGISTER_CLICKED = "Register Clicked";
            public static final String EVENT_HOME_DASHBOARD_CLICKED = "Dashboard Clicked";
            public static final String EVENT_HOME_NOTIFICATION_CLICKED = "Notification Clicked";
            public static final String EVENT_NOTIFICATION_CLEAR_CLICKED = "Notification Clear Clicked";
            public static final String EVENT_HOME_BUDDY_REQUEST_CLICKED = "Buddy Request Clicked";
            public static final String EVENT_ACCEPT_BUDDY_REQUEST_CLICKED = "Accept Buddy Request Clicked";
            public static final String EVENT_REJECT_BUDDY_REQUEST_CLICKED = "Reject Buddy Request Clicked";
            public static final String EVENT_HOME_BUDDY_FILTER_CLICKED = "Buddy Filter Clicked";
            public static final String EVENT_HOME_MENU_CLICKED = "Home Menu Clicked";
            public static final String EVENT_HOME_CREATE_TASK_CLICKED = "Home Create Task Clicked";
            public static final String EVENT_MENU_ITEM_MYBUDDY_CLICKED = "Menu Item MyBuddy Clicked";
            public static final String EVENT_MENU_ITEM_FLEET_CLICKED = "Menu Item Fleet Clicked";
            public static final String EVENT_MENU_ITEM_TASK_CLICKED = "Menu Item Task Clicked";
            public static final String EVENT_MENU_ITEM_SETTINGS_CLICKED = "Menu Item Settings Clicked";
            public static final String EVENT_MENU_ITEM_LOGOUT_CLICKED = "Menu Item Logout Clicked";
            public static final String EVENT_MENU_ITEM_OTHERS_CLICKED = "Menu Item Others Clicked";
            public static final String EVENT_MENU_SUB_ITEM_PRIVACY_POLICY_CLICKED = "Menu Sub Item Privacy Policy Clicked";
            public static final String EVENT_MENU_SUB_ITEM_TNC_CLICKED = "Menu Sub Item TNC Clicked";
            public static final String EVENT_MENU_MY_ACCOUNT_CLICKED = "Menu My Account Clicked";
            public static final String EVENT_MY_PROFILE_CLICKED = "My Profile Clicked";
            public static final String EVENT_TRACKING_BUDDY_CLICKED = "Tracking Buddy Clicked";
            public static final String EVENT_I_AM_TRACKING_CLICKED = "I Am Tracking Clicked";
            public static final String EVENT_TRACKING_ME_CLICKED = "Tracking Me Clicked";
            public static final String EVENT_CHANGE_PASSWORD_CLICKED = "Change Password Clicked";
            public static final String EVENT_CHANGE_PASSWORD_SUBMIT_CLICKED = "Change Password Submit Clicked";
            public static final String EVENT_ADD_BUDDY_CLICKED = "Add Buddy Clicked";
            public static final String EVENT_SEND_INVITATION_CLICKED = "Send Invitation Clicked";
            public static final String EVENT_ADD_FLEET_CLICKED = "Add Fleet Clicked";
            public static final String EVENT_ADD_FLEET_SUBMIT_CLICKED = "Add Fleet Submit Clicked";
            public static final String EVENT_TASK_ASSIGNED_TO_ME_CLICKED = "Task Assigned To Me Clicked";
            public static final String EVENT_TASK_I_HAVE_ASSIGNED_CLICKED = "Task I Have Assigned Clicked";
            public static final String EVENT_TASK_STATUS_ALL_CLICKED = "Task Status All Clicked";
            public static final String EVENT_TASK_STATUS_PENDING_CLICKED = "Task Status Pending Clicked";
            public static final String EVENT_TASK_STATUS_SCHEDULED_CLICKED = "Task Status Scheduled Clicked";
            public static final String EVENT_TASK_STATUS_LIVE_CLICKED = "Task Status Live Clicked";
            public static final String EVENT_TASK_STATUS_CANCELLED_CLICKED = "Task Status Cancelled Clicked";
            public static final String EVENT_TASK_STATUS_COMPLETED_CLICKED = "Task Status Completed Clicked";
            public static final String EVENT_TASK_START_NOW_CLICKED = "Task Start Now Clicked";
            public static final String EVENT_TASK_CANCEL_CLICKED = "Task Cancel Clicked";
            public static final String EVENT_TASK_ACCEPT_CLICKED = "Task Accept Clicked";
            public static final String EVENT_TASK_REJECT_CLICKED = "Task Reject Clicked";
            public static final String EVENT_TASK_END_CLICKED = "Task End Clicked";
            public static final String EVENT_TASK_EDIT_CLICKED = "Task Edit Clicked";
            public static final String EVENT_TASK_CREATE_CLICKED = "Task Create Clicked";
            public static final String EVENT_TASK_ASSIGN_NOW_CLICKED = "Task Assign Now Clicked";
            public static final String EVENT_TASK_DETAIL_SHARE_CLICKED = "Task Detail Share Clicked";
            public static final String EVENT_ALERT_NOTIFICATION_SWITCH_CLICKED = "Alert Notification Switch Clicked";
            public static final String EVENT_AUTO_START_TRIP_SWITCH_CLICKED = "Autostart Trip Switch Clicked";
            public static final String EVENT_EMERGENCY_CONTACT_CLICKED = "Emergency Contact Clicked";
            public static final String EVENT_ADD_EMERGENCY_CONTACT_CLICKED = "Add Emergency Contact Clicked";
            public static final String EVENT_CHOOSE_CONTACT_CLICKED = "Choose Contact Clicked";
            public static final String EVENT_EDIT_CONTACT_CLICKED = "Edit Contact Clicked";
            public static final String EVENT_DELETE_CONTACT_CLICKED = "Delete Contact Clicked";
            public static final String EVENT_TEXT_MESSAGE_CLICKED = "Text MessageResponse Clicked";
            public static final String EVENT_LOGOUT_CLICKED = "Logout Clicked";
            public static final String OLD_HUB_LOCATION_LIST = "old_hub_location_list";
            public static final String NEW_HUB_LOCATION_LIST = "new_hub_location_list";


        }

        public final class Pages {
            public static final String PAGE_MY_ACCOUNT = "My Account";
            public static final String PAGE_ADD_BUDDY = "Add Buddy";
            public static final String PAGE_ADD_CONTACT = "Add Contact";
            public static final String PAGE_ADD_FLEET = "Add Fleet";
            public static final String PAGE_BUDDY_LISTING = "Buddy Listing";
            public static final String PAGE_BUDDY_PROFILE = "Buddy Profile";
            public static final String PAGE_BUDDY_REQUEST = "Buddy Request";
            public static final String PAGE_CHANGE_MOBILE = "Change Mobile";
            public static final String PAGE_CHANGE_PASSWORD = "Change Password";
            public static final String PAGE_CREATE_TASK = "Create Task";
            public static final String PAGE_APP_BLOCK = "App Block";
            public static final String PAGE_DYNAMIC_FORM = "Dynamic Form";
            public static final String PAGE_EMERGENCY_MESSAGE = "Emergency MessageResponse";
            public static final String PAGE_EMERGENCY_PHONE = "Emergency Phone";
            public static final String PAGE_FLEET_LISTING = "Fleet Listing";
            public static final String PAGE_INTRO_SCREEN = "Intro Screen";
            public static final String PAGE_LOGIN = "Login";
            public static final String PAGE_HOME_PAGE = "Home Page";
            public static final String PAGE_BUDDY_FILTER = "Buddy Filter";
            public static final String PAGE_NOTIFICATION = "Notification";
            public static final String PAGE_OTP = "Otp";
            public static final String PAGE_MY_PROFILE = "My Profile";
            public static final String PAGE_REFER_EARN = "Refer Earn";
            public static final String PAGE_REGISTER = "Register";
            public static final String PAGE_REJECT_TASK = "Reject Task";
            public static final String PAGE_SETTING = "Setting";
            public static final String PAGE_SHARE_TRIP = "Share Trip";
            public static final String PAGE_SPLASH = "Open App";
            public static final String PAGE_TASK_DETAIL = "Task Detail";
            public static final String PAGE_ASSIGN_TO_ME = "Assign To Me";
            public static final String PAGE_I_HAVE_ASSIGNED = "I Have Assigned";
            public static final String PAGE_TASK_FILTER = "Task Filter";
            public static final String PAGE_BUDDY_DETAIL = "Buddy Detail";
            public static final String PAGE_I_AM_TRACKING = "I Am Tracking";
            public static final String PAGE_TRACKING_ME = "Tracking Me";
            public static final String PAGE_WEBVIEW = "Webview";
            public static final String PAGE_PAYMENT = "Payment";
            public static final String PAGE_OTHER = "OTHER";
        }
    }

    public final class Extra {

        public static final String GEO_FILTER = "GeoFilter";
        public static final String NUMBER_EXTRA = "number";
        public static final String IS_FROM_TASK_FILTER_EXTRA = "is_from_task_filter";
        public static final String IS_FROM_ADD_BUDDY_EXTRA = "is_from_add_buddy";
        public static final String EXTRA_IS_CALLING_FROM_BUDDY_PROFILE = "is_calling_from_buddy_profile";
        public static final String BUDDY_STATUS_OFFLINE_EXTRA = "status_offline";
        public static final String BUDDY_STATUS_LIVE_EXTRA = "status_live";
        public static final String BUDDY_STATUS_ALL_EXTRA = "status_all";
        public static final String BUDDY_STATUS_IDLE_EXTRA = "status_idle";
        public static final String SELECT_BUDDY_EXTRA = "select_buddy";
        public static final String SELECT_FLEET_EXTRA = "select_fleet";
        public static final String SELECTED_BUDDY_LIST_EXTRA = "selected_buddy_list";
        public static final String SELECTED_FLEET_LIST_EXTRA = "selected_fleet_list";
        public static final String FLEET_MAIN_LIST_EXTRA = "fleet_main_list";
        public static final String BUDDY_MAIN_LIST_EXTRA = "buddy_main_list";
        public static final String COUNTRY_CODE_EXTRA = "country_code";
        public static final String EXTRA_BUDDY_LIST_CALLING_FROM_NAVIGATION_MENU = "buddy_main_list_from_navigation_menu";
        public static final String EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU = "buddy_main_list_from_dashboard_menu";
        public static final String EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU = "fleet_main_list_from_navigation_menu";
        public static final String EXTRA_FLEET_LIST_CALLING_FROM_DASHBOARD_MENU = "fleet_main_list_from_dashboard_menu";

        public static final String EXTRA_BUDDY = "buddy";
        public static final String EXTRA_IS_EDIT_MODE = "isEditMode";
        public static final String EXTRA_PROFILE_INFO = "profile_info";
        public static final String EXTRA_WEB_INFO = "web_response";
        public static final String EXTRA_PAREN_TASK_ID= "parent_task_id";
        public static final String EXTRA_PARENT_REF_ID= "parent_reff_id";
        public static final String EXTRA_TASK = "task";
        public static final String EXTRA_ALLOW_SUB_TASK= "allow_sub_task";
        public static final String EXTRA_SUB_TASK_CATEGORY_ID= "sub_task_category_id";
        public static final String EXTRA_TASK_ID = "task_id";
        public static final String EXTRA_TASK_TAG_INV_TARGET = "EXTRA_TASK_TAG_INV_TARGET";
        public static final String EXTRA_TASK_TAG_INV_DYNAMIC_PRICING = "EXTRA_TASK_TAG_INV_DYNAMIC_PRICING";
        public static final String EXTRA_TASK_TAG_IN_FLAVOUR_ID = "EXTRA_TASK_TAG_IN_FLAVOUR_ID";
        public static final String FROM = "from";
        public static final String TASK_DETAILS = "TASK_DETAILS";
        public static final String FROM_DATE = "from_date";
        public static final String FROM_TO = "to_date";
        public static final String IS_MERCHANT_TAB = "IS_MERCHANT_TAB";
        public static final String LOAD_BY = "load_by";
        public static final String ASSIGNED_TO_ME = "ASSIGNED_TO_ME";
        public static final String ASSIGNED_BY_ME = "ASSIGNED_BY_ME";
        public static final String EXTRA_CTA_ID = "cta_id";
        public static final String HIDE_BUTTON = "hide_button";
        public static final String EXTRA_IS_EDITABLE = "is_editable";
        public static final String EXTRA_CATEGORY_ID = "categoryId";


        public static final String EXTRA_SHOW_TOOLBAR = "show_toolbar";
        public static final String EXTRA_FORM_ID = "form_id";
        public static final String EXTRA_TCF_ID= "tcf_id";
        public static final String EXTRA_FLEET = "fleet";
        public static final String EXTRA_LOGOUT = "logout";
        public static final String EXTRA_CHANGE_ACCESSID = "change_access_id";
        public static final String EXTRA_BUDDY_ID = "buddy_id";
        public static final String EXTRA_FLEET_ID = "fleet_id";
        public static final String EXTRA_BUDDY_NAME = "buddy_name";
        public static final String EXTRA_FILTER_BUDDY = "filter_buddy";
        public static final String EXTRA_CONTACT_NAME = "contact_name";
        public static final String EXTRA_CONTACT_NUMBER = "contact_number";
        public static final String EXTRA_CONTACT_ID = "contact_id";
        public static final String EXTRA_EDIT_TASK = "edit_task";
        public static final String EXTRA_INTRO_POSITION = "intro_position";
        public static final String EXTRA_SHOW_CANCEL_BUTTON = "show_cancel_button";
        public static final String EXTRA_CHANGE_MOBILE = "change_mobile";
        public static final String EXTRA_FROM_IHAVE = "from_i_have_assigned";
        public static final String EXTRA_BUDDY_LIST_CALLING_FROM_BOTTOM_SHEET_MENU = "buddy_main_list_from_bottom_menu";
        public static final String EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST = "EXTRA_BUDDY_LIST_CALLING_FROM_MESSAGE_LIST";
        //        public static final String EXTRA_ALL_BUDDY = "all_buddy";
        public static final String EXTRA_BUNDLE = "bundle";
        public static final String EXTRA_STOP_SERVICE = "stop_service";
        public static final String EXTRA_TITLE = "title";
        public static final String EXTRA_SELECTED_BUDDY = "selected_buddies";
        public static final String EXTRA_IS_YOU = "is_you";
        public static final String EXTRA_FORM_TYPE = "form_type";
        public static final String EXTRA_FORM_MAP = "form_map";
        public static final String EXTRA_SCANNER_FIELD_NAME = "EXTRA_SCANNER_FIELD_NAME";
        public static final String EXTRA_SCANNER_FIELD_VALUE = "EXTRA_SCANNER_FIELD_VALUE";
        public static final String EXTRA_IS_OFFLINE = "is_offline";
        public static final String EXTRA_EXPIRATION_URL = "expiration_deprecation_url";
        public static final String EXTRA_NEXT_SCREEN = "next_screen";
        public static final String EXTRA_DEPRICATION_SCREEN = "deprication_check";
        public static final String EXTRA_FORM_DETAILS = "form_details";
        public static final String EXTRA_ROOM_ID = "room_id";
        public static final String EXTRA_IS_CREATE_ROOM = "is_create_room";
        public static final String EXTRA_DATA = "extra_form_data";
        public static final String EXTRA_DATE = "date";
        public static final String EXTRA_CATEGORIES = "categories";
        public static final String EXTRA_STAGEID = "stageId";
        public static final String TITLE = "title";
        public static final String START = "start";
        public static final String STOP = "stop";
        //public static final String ACCESS_ID = "123456";
        //public static final String ACCESS_ID = "SSTy9XYiV7";
        //public static final String ACCESS_ID = "Szd2EFkOTa";
        //public static final String ACCESS_ID = "xZc4m0UwTB";
        //public static final String ACCESS_ID = "0Iru7xkxlD";
        //public static final String ACCESS_ID = "rnnan3YtJn";
        public static final String ACCESS_ID = "";
        //public static final String ACCESS_ID = "esbgvYsBOeI";
        public static final String APP_TYPE_ADMIN= "ADMIN";
        public static final String APP_TYPE_CLIENT= "CLIENT";
        public static final String APP_TYPE_DRIVER= "DRIVER";
        public static final String TASK= "TASK";
        public static final String ATTENDANCE= "ATTENDANCE";
        public static final String DOC_TYPE = "doc_type";
        public static final String EXTRA_PRODUCT_ID = "product_id";
        public static final String EXTRA_PRODUCT_NAME = "product_name";




    }
}
