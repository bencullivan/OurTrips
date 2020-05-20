package com.alsaeedcullivan.ourtrips.cloud;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alsaeedcullivan.ourtrips.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {

    public MessageService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(Const.TAG, "onNewToken: " + s);

        final String token = s;

        // if there is a current user, add this as their token in the db
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // get the old token from the db and unsubscribe from all topics
            AccessDB.getUserToken(user.getUid())
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        // the old token
                        String tok = task.getResult();

                        // unsubscribe
                        CloudFunctions.unsubscribeFromTopics(user.getUid(), tok);

                        // add new token to db
                        AccessDB.addUserToken(user.getUid(), token);

                        // subscribe
                        CloudFunctions.subscribeToTopics(user.getUid(), token);
                    }
                }
            });
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        // TODO: determine how to respond based on the properties of remoteMessage
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        /// TODO: figure out what to do with this method

    }
}
