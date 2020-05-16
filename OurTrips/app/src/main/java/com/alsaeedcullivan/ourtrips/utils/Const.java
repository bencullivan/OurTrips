package com.alsaeedcullivan.ourtrips.utils;

/**
 * Class to contain the public constants used throughout the application
 */
public class Const {

    // tags
    public static final String TAG = "test_tag";

    // Cloud FireStore root collection names
    public static final String USERS_COLLECTION = "users";
    public static final String TRIPS_COLLECTION = "trips";

    // Cloud FireStore database paths
    public static final String USERS_PATH = "users/";
    public static final String TRIPS_PATH = "trips/";

    // User keys
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";
    public static final String USER_GENDER_KEY = "user_gender";
    public static final String USER_AFFILIATION_KEY = "user_affiliation";
    public static final String USER_AGE_KEY = "user_age";
    public static final String FRIEND_ID_KEY = "friend_user_id";

    // User sub-collections
    public static final String USER_FRIENDS_COLLECTION = "user_friends";
    public static final String USER_TRIPS_COLLECTION = "user_trips";

    // Trip keys
    public static final String TRIP_ID_KEY = "trip_id";
    public static final String TRIP_TITLE_KEY = "trip_title";
    public static final String TRIP_USERS_LIST_KEY = "users_list";
    public static final String TRIP_COMMENTS_LIST_KEY = "comments_list";

}
