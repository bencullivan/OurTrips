package com.alsaeedcullivan.ourtrips.cloud;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MessageService extends FirebaseMessagingService {
    public MessageService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
