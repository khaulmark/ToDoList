package com.csce4623.ahnelson.todolist;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import static com.csce4623.ahnelson.todolist.App.CHANNEL_2_ID;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        notificationManager = NotificationManagerCompat.from(context);

        if (intent.getExtras() != null) {
            //Checks for connectivity and type of connectivity
            final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            String display;
            if (ni != null && ni.isConnectedOrConnecting()) {
                if (ni.getTypeName().equals("WIFI")) {
                    display = "Connection to WIFI has been made.";
                }
                else {
                    display = "No connection to WIFI! Saving locally to mobile device.";
                }
                //If there is connectivity (either MOBILE or WIFI), create a notification
                Notification notification = new NotificationCompat.Builder(context,CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                        .setContentTitle("Connectivity Status")
                        .setContentText(display)
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                notificationManager.notify(1, notification);
            }
            //If there is no connectivity to anything (shouldn't happen), then create a notification
            else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                display = "All connectivity has been lost!";
                Notification notification = new NotificationCompat.Builder(context,CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                        .setContentTitle("Connectivity Status")
                        .setContentText(display)
                        .setPriority(NotificationCompat.PRIORITY_MIN)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                notificationManager.notify(1, notification);
            }
        }
    }
}
