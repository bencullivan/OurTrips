package com.alsaeedcullivan.ourtrips.utils;

/**
 * Class to contain utility methods
 */
public class Utilities {

    // checks email validity (NEED STRONGER CHECK)
    public static boolean isValidEmail(String email) {
        return email.contains("@");
    }

    // checks password validity
    public static boolean isValidPassword(String password) {
        return password.length() > 5;
    }
}
