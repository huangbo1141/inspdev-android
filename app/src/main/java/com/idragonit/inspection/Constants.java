package com.idragonit.inspection;

/**
 * Created by CJH on 2015-06-26.
 */

public class Constants {

    public final static int CONNECTION_TIMEOUT = 60;

    public static int CREDITS = 100;
    public final static String APP_NAME = "Inspections";
    public final static String[] INSPECTIONS = {
            "", "Drainage Plane", "Lath",
    };

//    public final static String API__BASEPATH = "http://inspdev.e3bldg.com/api/";
//    public final static String API__BASEPATH = "https://inspections.e3bldg.com/api/";
    public final static String API__BASEPATH = "http://192.168.1.121:88/inspdev/api/";

    public final static String API__USER_REGISTER = "v1/user/register";
    public final static String API__USER_UPDATE = "v1/user/update";
    public final static String API__USER_LOGIN = "v1/user/login";
    public final static String API__USER_SIGN = "v1/user/sign";
    public final static String API__USER_FIELD_MANAGER = "v2/user/field_manager";

    public final static String[] API__KIND = {  "",     "drainage",     "lath",  "wci"      };

    public final static String API__CHECK_JOB = "v1/inspection/check/";
    public final static String API__UPLOAD_PICTURE = "upload/";
    public final static String API__INSPECTION = "v1/inspection/submit/";
    public final static String API__REQUESTED_INSPECTION = "v1/inspection/requested";
    public final static String API__EMAIL = "v1/send";

    public final static String API__CHECK_COMMUNITY = "v1/community/check";
    public final static String API__SYS_ENERGY_INSPECTION = "v1/sys/energy_inspection";

    public final static String API__SYNC_REGION = "v2/sync/region";
    public final static String API__SYNC_FIELD_MANAGER = "v2/sync/field_manager";

    public final static int ACTION_NEW = 1;
    public final static int ACTION_UPDATE = 2;
    public final static int ACTION_DELETE = 3;

    public final static int MODE_NEW = 0;
    public final static int MODE_SYNC = 1;

    public final static int STEP__NONE = 0;
    public final static int STEP__JOB_NUMBER = 0;
    public final static int STEP__SYNC = 0;

    public final static int STEP__INITIALS = 1;
    public final static int STEP__BASIC = 10;
    public final static int STEP__LOCATION = 20;
    public final static int STEP__CHECKLIST = 21;
    public final static int STEP__CHECKPOINT = 22;
    public final static int STEP__RESULT = 30;
    public final static int STEP__RESULTPOINT = 31;
    public final static int STEP__EMAIL = 40;
    public final static int STEP__DONE = 50;

    public final static int STEP__BASIC_WCI = 100;
    public final static int STEP__UNIT_WCI = 101;
    public final static int STEP__RESULT_WCI = 102;
    public final static int STEP__DONE_WCI = 103;
    public final static int STEP__PHOTO_WCI = 104;

    public final static int INSPECTION_NONE = 0;
    public final static int INSPECTION_DRAINAGE = 1;
    public final static int INSPECTION_LATH = 2;
    public final static int INSPECTION_WCI = 3;

    public final static int PICTURE_EMPTY = 0;
    public final static int PICTURE_LOCAL = 1;
    public final static int PICTURE_SERVER = 2;

    public final static int PICTURE_IMAGE = 0;
    public final static int PICTURE_SIGNATURE = 1;

    public final static int CHECKING_STATUS__NONE = 0;
    public final static int CHECKING_STATUS__PASS = 1;
    public final static int CHECKING_STATUS__FAIL = 2;
    public final static int CHECKING_STATUS__NOT_READY = 3;
    public final static int CHECKING_STATUS__NOT_APPLICABLE = 4;
    public final static int CHECKING_STATUS__CANNOT_VERIFY = 5;

    public final static String[] CHECKING_STATUS = {    "Nothing",     "Pass",     "Fail",     "Not Ready",    "Not Applicable",   "Cannot Verify"     };

    public final static int MAX_STEP = 5;

    public final static int ANIMATION_NONE = 0;
    public final static int ANIMATION_RIGHT_TO_LEFT = 1;
    public final static int ANIMATION_LEFT_TO_RIGHT = 2;
    public final static int ANIMATION_BOTTOM_TO_UP = 3;
    public final static int ANIMATION_UP_TO_BOTTOM = 4;

    public final static String MSG_LOADING = "Loading.....";
    public final static String MSG_SUCCESS = "Successfully loaded!";
    public final static String MSG_FAILED = "Failed!";

    public final static String MSG_CONNECTION = "Your Internet is not available!";

    public final static String MSG_UPDATE = "Updating.....";
    public final static String MSG_UPDATE_SUCCESS = "Successfully updated!";
    public final static String MSG_UPDATE_FAILED = "Failed to update!";

    public final static String MSG_LOGIN = "Login.....";
    public final static String MSG_LOGIN_FAILED = "Failed to login!";

    public final static String MSG_REGISTER = "Register.....";
    public final static String MSG_REGISTER_SUCCESS = "Successfully registered!";
    public final static String MSG_REGISTER_FAILED = "Failed to register user!";

    public final static String PREFERENCE_KEY = "inspection_form";
    public final static String RESOLUTION_KEY = "resolution";

    public final static int[] RESOLUTION_WIDTH = {0, 1600, 1536, 1200, 1152, 1152, 768, 768, 600, 640, 480, 480, 480, 320, 240, 240};
    public final static int[] RESOLUTION_HEIGHT = {0, 2560, 2048, 1920, 1920, 1536, 1280, 1024, 1024, 960, 854, 800, 640, 480, 432, 400};

    public final static String SYS_HOUSE_PRESSURE = "house_pressure";
    public final static String SYS_HOME_MESSAGE1 = "app_home_message1";
}
