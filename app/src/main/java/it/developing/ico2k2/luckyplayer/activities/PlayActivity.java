package it.developing.ico2k2.luckyplayer.activities;

import android.os.Bundle;

import it.developing.ico2k2.luckyplayer.activities.base.BasePlayingActivity;

public class PlayActivity extends BasePlayingActivity
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getIntent().setClass(this,InfoActivity.class);
        startActivity(getIntent());
    }
}
