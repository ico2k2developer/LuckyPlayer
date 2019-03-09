package it.developing.ico2k2.luckyplayer.activities;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;

import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

public class InfoActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getDataString());
    }


    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                    title.toString());
            setTaskDescription(taskDescription);

        }
    }
}