package it.developing.ico2k2.luckyplayer.activities;

import android.app.Activity;
import android.content.Intent;

import it.developing.ico2k2.luckyplayer.DataManager;
import it.developing.ico2k2.luckyplayer.LuckyPlayer;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_INITIALIZED;

public class MainActivity extends Activity
{
    @Override
    public void onResume()
    {
        super.onResume();
        DataManager dataManager = ((LuckyPlayer)getApplication()).getDataManager();
        if(dataManager.getBoolean(KEY_INITIALIZED,false))
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
