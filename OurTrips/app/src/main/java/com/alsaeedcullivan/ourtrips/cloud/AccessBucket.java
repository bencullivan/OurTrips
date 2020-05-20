package com.alsaeedcullivan.ourtrips.cloud;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;

/**
 * Class of static methods to access the Firebase storage bucket
 */
public class AccessBucket {

    /**
     * uploadPicture()
     * uploads a picture to the storage bucket
     * @param path - the path where the photo will be stored in the storage bucket
     * @param is - the input stream that will be used to upload the photo to the bucket
     */
    public static void uploadPicture(String path, InputStream is) {
        FirebaseStorage.getInstance().getReference().child(path).putStream(is);
    }

    /**
     * uploadVideo()
     * uploads a video to the cloud storage bucket
     * @param path - the path where the video will be stored in the storage bucket
     * @param vidUri - the Uri of the video that will be uploaded
     */
    public static void uploadVideo(String path, Uri vidUri) {
        FirebaseStorage.getInstance().getReference().child(path).putFile(vidUri);
    }

    /**
     * deleteFromStorage()
     * deletes a file from the storage bucket
     * @param path - the path of the file that will be deleted
     */
    public static void deleteFromStorage(String path) {
        FirebaseStorage.getInstance().getReference().child(path).delete();
    }
}