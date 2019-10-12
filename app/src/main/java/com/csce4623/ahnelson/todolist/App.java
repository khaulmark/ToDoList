package com.csce4623.ahnelson.todolist;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

        //This intent filter starts the broadcast receiver to monitor network connectivity
        IntentFilter intentFilter = new IntentFilter();
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //This is a thing for newer versions of Android API
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void createNotificationChannels() {
        //Notification channels don't work before Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Channel 1 for alarms
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Alarm",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is an alarm for ToDoList.");

            NotificationManager alarmManager = getSystemService(NotificationManager.class);
            alarmManager.createNotificationChannel(channel1);

            //Channel 2 for network connectivity
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Network connectivity",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This is a message for network connectivity.");

            NotificationManager networkManager = getSystemService(NotificationManager.class);
            networkManager.createNotificationChannel(channel2);
        }
    }
}
