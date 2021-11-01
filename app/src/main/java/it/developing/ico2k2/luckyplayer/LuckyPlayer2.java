package it.developing.ico2k2.luckyplayer;

import android.os.Build;

import androidx.multidex.MultiDexApplication;

public class LuckyPlayer2 extends MultiDexApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationChannelsManager.checkAll(this);
    }
}
