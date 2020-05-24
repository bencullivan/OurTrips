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
    public static final String TRIP_ID_TAG = "trip_id_tag";
    public static final String SUMMARY_TAG = "SummaryFragment";
    public static final String PLAN_TAG = "PlanFragment";
    public static final String MEDIA_TAG = "MediaFragment";
    public static final String TRIPPERS_TAG = "TrippersFragment";
    public static final String SELECTED_DATE_TAG = "selected";
    public static final String SELECTED_FRIEND_TAG = "selected_friend";

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
    public static final String TRIP_LOCATIONS_KEY = "locations";
    public static final String TRIP_START_DATE_KEY = "start_date";
    public static final String TRIP_END_DATE_KEY = "end_date";
    public static final String TRIP_TRIPPERS_COLLECTION = "trippers";
    public static final String TRIP_TRIPPER_KEY = "tripper";
    public static final String TRIP_COMMENTS_COLLECTION = "comments";
    public static final String TRIP_COMMENT_KEY = "comment";
    public static final String TRIP_PHOTO_PATHS_COLLECTION = "photos";
    public static final String TRIP_PHOTO_KEY = "photo";
    public static final String TRIP_TOPIC_KEY = "topic";
    public static final String TRIP_COLL_UPDATE_TYPE_KEY = "type";
    public static final String TRIP_NOTIFY_KEY = "notification";
    public static final String TRIP_INFO_KEY = "info";

    // Cloud function names
    public static final String FUNC_MATCH_DATES = "matchDates";
    public static final String FUNC_SUBSCRIBE_TOPICS = "subscribeToTopics";
    public static final String FUNC_UNSUBSCRIBE_TOPICS = "unsubscribeFromTopics";
    public static final String FUNC_SUBSCRIBE_TOPIC = "subscribeToTopic";
    public static final String FUNC_UNSUBSCRIBE_TOPIC = "unsubscribeFromTopic";
    public static final String FUNC_COLLECTION_UPDATED = "collectionUpdated";
    public static final String FUNC_TRIP_INFO_UPDATED = "tripInfoUpdated";

    // Cloud Function Keys
    public static final String LIST_1_KEY = "list1";
    public static final String LIST_2_KEY = "list2";
    public static final String UID_KEY = "id";
    public static final String FRIENDS_LIST_KEY = "friends";

    // Cloud Storage paths
    public static final String PROFILE_PIC_PATH = "profile_pictures";
    public static final String TRIP_PIC_PATH = "trip_pictures";
    public static final String PROFILE_PIC_NAME = "profile";
    public static final String PIC_JPG = ".jpg";

}
