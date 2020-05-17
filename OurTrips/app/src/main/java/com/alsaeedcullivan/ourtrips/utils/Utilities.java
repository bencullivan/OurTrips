package com.alsaeedcullivan.ourtrips.utils;

import java.util.regex.Pattern;

/**
 * Class to contain utility methods
 */
public class Utilities {

    // checks email validity
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern p = Pattern.compile(regex);
        if (email == null) return false;
        return p.matcher(email).matches();
    }

    // checks password validity
    public static boolean isValidPassword(String password) {
        return password.length() > 5;
    }
}
