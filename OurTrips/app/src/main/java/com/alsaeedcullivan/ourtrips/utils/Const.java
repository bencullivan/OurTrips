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
    public static final String USER_ID_KEY = "userId";
    public static final String USER_NAME_KEY = "userName";
    public static final String USER_GENDER_KEY = "userGender";
    public static final String USER_AFFILIATION_KEY = "userAffiliation";
    public static final String USER_AGE_KEY = "userAge";
    public static final String FRIEND_ID_KEY = "friendUserId";

    // User sub-collections
    public static final String USER_FRIENDS_COLLECTION = "userFriends";
    public static final String USER_TRIPS_COLLECTION = "userTrips";

    // Trip keys
    public static final String TRIP_ID_KEY = "tripId";
    public static final String TRIP_TITLE_KEY = "tripTitle";
    public static final String TRIP_USERS_LIST_KEY = "usersList";
    public static final String TRIP_COMMENTS_LIST_KEY = "commentsList";

    // Cloud function names
    public static final String FUNC_RECURSIVE_DELETE = "recursiveDelete";
    public static final String FUNC_MATCH_DATES = "matchDates";

    // Cloud Function Keys
    public static final String USER_1_KEY = "user1";
    public static final String USER_2_KEY = "user2";
}
