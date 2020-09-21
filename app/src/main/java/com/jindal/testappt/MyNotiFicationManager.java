package com.jindal.testappt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.jindal.testappt.Activity.MainActivity;

public class MyNotiFicationManager {

    private Context context;
    private static MyNotiFicationManager myNotiFicationManager;

    public MyNotiFicationManager(Context context) {
        this.context = context;
    }

    public static synchronized MyNotiFicationManager getInstance(Context context){
        if (myNotiFicationManager==null){
            myNotiFicationManager = new MyNotiFicationManager(context);
        }
        return myNotiFicationManager;
    }

    public void showNotification(String title, String body){
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.common_full_open_on_phone)
                .setContentTitle(title)
                .setContentText(body);

        Intent notiIntent = new Intent(context, MainActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null){
            notificationManager.notify(1, notiBuilder.build());
        }
    }
}
