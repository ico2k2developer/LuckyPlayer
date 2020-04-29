package it.developing.ico2k2.luckyplayer.activities;

import android.app.Activity;
import android.content.Intent;

import static it.developing.ico2k2.luckyplayer.Utils.KEY_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Utils.PREFERENCE_MAIN;

public class MainActivity extends Activity
{
    @Override
    public void onResume()
    {
        super.onResume();
        if(getSharedPreferences(PREFERENCE_MAIN,MODE_PRIVATE).getBoolean(KEY_INITIALIZED,false))
        {
            startActivity(new Intent(this,TabsActivity.class));
        }
        else
        {
            startActivity(new Intent(this,InitializeActivity.class));
        }
        finish();
    }
}
