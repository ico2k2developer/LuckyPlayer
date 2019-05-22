package it.developing.ico2k2.luckyplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import static it.developing.ico2k2.luckyplayer.BuildConfig.VERSION_NAME;
import static it.developing.ico2k2.luckyplayer.Keys.*;

public class LuckyPlayer extends Application
{
    private DataManager dataManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        dataManager = new DataManager(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            String name = getString(R.string.main_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_MAIN,name,NotificationManager.IMPORTANCE_LOW));
            name = getString(R.string.info_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_INFO,name,NotificationManager.IMPORTANCE_LOW));
            name = getString(R.string.status_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_STATUS,name,NotificationManager.IMPORTANCE_LOW));
        }
    }

    public boolean checkNotificationChannelExists(NotificationManagerCompat managerCompat,String channelId)
    {
        boolean result = false;
        try
        {
            result = managerCompat.getNotificationChannel(channelId) != null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }
}
