package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_DATA_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_NOTIFICATION_TINT;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SONGLIST_PACKET_SIZE;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SYSTEM_MEDIA;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_THEME;

public class InitializeActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        setSupportActionBar(findViewById(R.id.initialize_toolbar));
        findViewById(R.id.initialize_fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getMainSharedPreferences().edit().putBoolean(KEY_INITIALIZED,true).apply();
                startActivity(new Intent(InitializeActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setup();
    }

    public void setup()
    {
        SharedPreferences prefs = getMainSharedPreferences();
        if(!prefs.getBoolean(KEY_DATA_INITIALIZED,false))
        {
            prefs.edit().putBoolean(KEY_SYSTEM_MEDIA,false).apply();
            prefs.edit().putInt(KEY_THEME,THEME_DEFAULT).apply();
            prefs.edit().putInt(KEY_NOTIFICATION_TINT,getColorPrimary()).apply();
            prefs.edit().putBoolean(KEY_DATA_INITIALIZED,true).apply();
            prefs.edit().putInt(KEY_SONGLIST_PACKET_SIZE,150).apply();
        }
    }

    @Override
    public boolean onNoDataFound()
    {
        super.onNoDataFound();
        return false;
    }
}
