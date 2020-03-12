package it.developing.ico2k2.luckyplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_MAIN;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_STATUS;

public class LuckyPlayer extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel;
            channel = new NotificationChannel(CHANNEL_ID_MAIN,getString(R.string.main_notification_channel),NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            channel = new NotificationChannel(CHANNEL_ID_INFO,getString(R.string.info_notification_channel),NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            channel = new NotificationChannel(CHANNEL_ID_STATUS,getString(R.string.status_notification_channel),NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
