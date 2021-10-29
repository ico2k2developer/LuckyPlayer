package it.developing.ico2k2.luckyplayer;

import android.app.Application;
import android.os.Build;

public class LuckyPlayer2 extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationChannelsManager.checkAll(this);
    }
}
