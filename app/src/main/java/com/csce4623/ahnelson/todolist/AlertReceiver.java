package com.csce4623.ahnelson.todolist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


import static com.csce4623.ahnelson.todolist.App.CHANNEL_1_ID;
import static com.csce4623.ahnelson.todolist.HomeActivity.EXTRA_MESSAGE_ID;
import static com.csce4623.ahnelson.todolist.ToDoListItemActivity.EXTRA_MESSAGE_TITLE;

public class AlertReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);

        String title = intent.getStringExtra(EXTRA_MESSAGE_TITLE);
        String id = intent.getStringExtra(EXTRA_MESSAGE_ID);

        Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setContentTitle(title)
                .setContentText("GO DO THIS ITEM!")
                //Loud Alarm
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        //Uses the row's id from the activity as the id for the notification
        notificationManager.notify(Integer.parseInt(id), notification);
    }
}
