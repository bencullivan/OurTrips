package com.alsaeedcullivan.ourtrips.cloud;

import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;

/**
 * class with methods to access the Firebase storage bucket
 */
public class AccessBucket {

    /**
     * uploadProfilePicture()
     * uploads a profile picture to the storage bucket at the given path using
     * the provided InputStream
     */
    public static void saveProfilePicture(String path, InputStream is) {
        FirebaseStorage.getInstance().getReference().child(path).putStream(is);
    }
}
