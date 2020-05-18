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
    public static final String REGISTER_KEY = "RegisterActivity";
    public static final String VERIFY_KEY = "VerifyActivity";
    public static final String MAIN_KEY = "MainActivity";

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

    // User sub-collections
    public static final String USER_FRIENDS_COLLECTION = "user_friends";
    public static final String USER_TRIPS_COLLECTION = "user_trips";

    // Trip keys
    public static final String TRIP_ID_KEY = "trip_id";
    public static final String TRIP_TITLE_KEY = "title";
    public static final String TRIP_LOCATION_KEY = "locations";
    public static final String TRIP_DATES_KEY = "dates";
    public static final String TRIP_TRIPPERS_KEY = "trippers";
    public static final String TRIP_COMMENTS_KEY = "comments";
    public static final String TRIP_PHOTO_PATHS_KEY = "photos";

    // Cloud function names
    public static final String FUNC_MATCH_DATES = "matchDates";

    // Cloud Function Keys
    public static final String LIST_1_KEY = "list1";
    public static final String LIST_2_KEY = "list2";
    public static final String UID_KEY = "id";
    public static final String FRIENDS_LIST_KEY = "friends";

    // Cloud Storage paths
    public static final String PROFILE_PIC_PATH = "profile_pictures";
    public static final String TRIP_PIC_PATH = "trip_pictures";
    public static final String PROFILE_PIC_NAME = "profile.jpg";

}
