package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.StyleRes;

import it.developing.ico2k2.luckyplayer.Prefs;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.Resources;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

public class MainActivity extends BaseActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_CODE_INIT = 0x10;

    private boolean go = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        go = !getMainSharedPreferences().getBoolean(getString(R.string.key_show_init_every_time),false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(go)
        {
            Log.d(TAG,"Launching " + TabsActivity.class.getSimpleName());
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
        Prefs prefs = Prefs.getInstance(this,Prefs.PREFS_SETTINGS);
        if(!prefs.getBoolean(getString(R.string.key_initialized),false))
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.key_include_music),true);
            edit.putBoolean(getString(R.string.key_include_podcast),true);
            edit.putInt(getString(R.string.key_theme), Resources.THEME_DEFAULT);
            edit.putInt(getString(R.string.key_notification_tint),getColorPrimary());
            edit.putInt(getString(R.string.key_songlist_packet_size),250);
            edit.putBoolean(getString(R.string.key_initialized),true);
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
