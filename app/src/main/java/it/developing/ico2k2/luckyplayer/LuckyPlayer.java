package it.developing.ico2k2.luckyplayer;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import static it.developing.ico2k2.luckyplayer.BuildConfig.VERSION_NAME;

public class LuckyPlayer extends Application
{
    private DataManager dataManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        dataManager = new DataManager(this);
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }
}
