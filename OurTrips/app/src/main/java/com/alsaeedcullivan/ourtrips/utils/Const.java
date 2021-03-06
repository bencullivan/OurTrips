package com.alsaeedcullivan.ourtrips.utils;

/**
 * Class to contain the public constants used throughout the application
 */
public class Const {

    // tags
    public static final String TAG = "test_tag";
    public static final String SOURCE_TAG = "source";
    public static final String DIALOG_TAG = "dialog";

    // intent keys
    public static final String REGISTER_TAG = "RegisterActivity";
    public static final String VERIFY_TAG = "VerifyActivity";
    public static final String MAIN_TAG = "MainActivity";
    public static final String LOGIN_TAG = "LoginActivity";
    public static final String SETTINGS_TAG = "SettingsActivity";
    public static final String MATCH_TAG = "MatchActivity";
    public static final String MATCH_ARR_TAG = "matched";
    public static final String TRIP_ACTIVITY_TAG = "TripActivity";
    public static final String TRIP_ID_TAG = "trip_id_tag";
    public static final String SUMMARY_TAG = "SummaryFragment";
    public static final String PLAN_TAG = "PlanFragment";
    public static final String MEDIA_TAG = "MediaFragment";
    public static final String TRIPPERS_TAG = "TrippersFragment";
    public static final String TRIPPERS_SET_TAG = "trippers_set";
    public static final String SELECTED_DATE_TAG = "selected";
    public static final String SELECTED_FRIEND_TAG = "selected_friend";
    public static final String GALLERY_TAG = "gallery_pics";
    public static final String USER_NAME_TAG = "user_name";
    public static final String TRIP_TITLE_TAG = "title";
    public static final String TRIP_START_TAG = "start";
    public static final String TRIP_END_TAG = "end";
    public static final String TRIP_OVER_TAG = "overview";
    public static final String PIC_TAG = "pic";
    public static final String POSITION_TAG = "position";
    public static final String PLACE_LIST_TAG = "place_list";

    // codes
    public static final int GALLERY_PERMISSION_REQUEST_CODE = 0;

    // Cloud FireStore root collection names
    public static final String USERS_COLLECTION = "users";
    public static final String TRIPS_COLLECTION = "trips";

    // User keys
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "name";
    public static final String USER_EMAIL_KEY = "email";
    public static final String USER_GENDER_KEY = "gender";
    public static final String USER_AFFILIATION_KEY = "affiliation";
    public static final String USER_BIRTHDAY_KEY = "birthday";
    public static final String FRIEND_ID_KEY = "friend_user_id";
    public static final String DATE_LIST_KEY = "dates";
    public static final String USER_PASSWORD_KEY = "password";
    public static final String USER_PROFILE_PIC_KEY = "profile_pic_path";
    public static final String USER_TOKEN_KEY = "token";
    public static final String USER_BIO_KEY = "bio";
    public static final String USER_REQ_EMAIL = "user_email";
    public static final String FRIEND_REQ_EMAIL = "friend_email";

    // User sub-collections
    public static final String USER_FRIENDS_COLLECTION = "user_friends";
    public static final String USER_TRIPS_COLLECTION = "user_trips";
    public static final String USER_F_REQUESTS_COLLECTION = "friend_requests";

    // User properties
    public static final String REGISTER_PROPERTY = "registered";
    public static final String REGISTER_FALSE = "f";
    public static final String REGISTER_TRUE = "t";

    // Trip keys
    public static final String TRIP_ID_KEY = "trip_id";
    public static final String TRIP_TITLE_KEY = "title";
    public static final String TRIP_START_DATE_KEY = "start_date";
    public static final String TRIP_END_DATE_KEY = "end_date";
    public static final String TRIP_TRIPPERS_COLLECTION = "trippers";
    public static final String TRIP_COMMENTS_COLLECTION = "comments";
    public static final String TRIP_COMMENT_KEY = "comment";
    public static final String TRIP_PHOTO_PATHS_COLLECTION = "photos";
    public static final String TRIP_PHOTO_KEY = "photo";
    public static final String TRIP_TIMESTAMP_KEY = "timestamp";
    public static final String TRIP_LOCATIONS_COLLECTION = "locations";
    public static final String TRIP_LOCATION_KEY = "location";
    public static final String TRIP_LOCATION_NAME_KEY = "name";
    public static final String TRIP_OVERVIEW_KEY = "overview";

    // Cloud Storage paths
    public static final String PROFILE_PIC_PATH = "profile_pictures";
    public static final String TRIP_PIC_PATH = "trip_pictures";
    public static final String PROFILE_PIC_NAME = "profile";
    public static final String PIC_JPG = ".jpg";

}
