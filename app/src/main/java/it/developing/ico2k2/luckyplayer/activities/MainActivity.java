package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StyleRes;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;

public class MainActivity extends BaseActivity
{
    public static final int REQUEST_CODE_INIT = 0x10;

    private boolean go = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        go = !getMainSharedPreferences().getBoolean(getString(R.string.settings_show_init_every_time_key),false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(go)
        {
            Log.d(TAG_LOGS,"Launching " + TabsActivity.class.getSimpleName());
            startActivity(new Intent(this,TabsActivity.class));
            finish();
        }
        else
        {
            startActivityForResult(new Intent(this,InitializeActivity.class),REQUEST_CODE_INIT);
        }
    }

    public void setup()
    {
        SharedPreferences prefs = getMainSharedPreferences();
        if(!prefs.getBoolean(getString(R.string.settings_initialized_key),false))
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.settings_include_music_key),true);
            edit.putBoolean(getString(R.string.settings_include_podcast_key),true);
            edit.putInt(getString(R.string.settings_theme_key),THEME_DEFAULT);
            edit.putInt(getString(R.string.settings_notification_tint_key),getColorPrimary());
            edit.putInt(getString(R.string.settings_songlist_packet_size_key),250);
            edit.putBoolean(getString(R.string.settings_initialized_key),true);
            edit.apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case REQUEST_CODE_INIT:
            {
                if(resultCode == RESULT_OK)
                {
                    setup();
                    go = true;
                }
                break;
            }
        }
    }

    @Override
    public boolean onNoDataFound()
    {
        return go = false;
    }

    @Override
    public boolean onThemeChanged(@StyleRes int oldTheme,@StyleRes int newTheme)
    {
        return false;
    }
}
