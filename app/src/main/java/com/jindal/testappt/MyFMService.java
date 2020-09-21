package com.jindal.testappt;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
         if (remoteMessage.getData().size() > 0 ){
             String dataMsg = remoteMessage.getData().toString();
             Log.d("dataMsg====>>>", dataMsg);
         }

        //getting the title and the body
        String msgTitle = remoteMessage.getNotification().getTitle();
        String msgBody = remoteMessage.getNotification().getBody();
    }
}
