package com.alsaeedcullivan.ourtrips.cloud;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

/**
 * Class of static methods to access the Firebase storage bucket
 */
public class AccessBucket {

    /**
     * uploadPicture()
     * uploads a picture to the storage bucket
     * @param path the path where the photo will be stored in the storage bucket
     * @param is the input stream that will be used to upload the photo to the bucket
     */
    public static UploadTask uploadPicture(String path, InputStream is) {
        return FirebaseStorage.getInstance().getReference().child(path).putStream(is);
    }

    /**
     * deleteFromStorage()
     * deletes a file from the storage bucket
     * @param path the path of the file that will be deleted
     */
    public static Task<Void> deleteFromStorage(String path) {
        return FirebaseStorage.getInstance().getReference().child(path).delete();
    }
}
