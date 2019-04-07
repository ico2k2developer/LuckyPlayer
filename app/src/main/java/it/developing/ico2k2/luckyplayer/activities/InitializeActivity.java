package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import it.developing.ico2k2.luckyplayer.DataManager;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_DATA_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_NOTIFICATION_TINT;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SYSTEM_MEDIA;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_THEME;

public class InitializeActivity extends BaseActivity
{
    private DataManager dataManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        setSupportActionBar((Toolbar)findViewById(R.id.initialize_toolbar));
        dataManager = getDataManager();
        findViewById(R.id.initialize_fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dataManager.putBoolean(KEY_INITIALIZED,true);
                startActivity(new Intent(InitializeActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        initializeValues();
    }

    public void initializeValues()
    {
        if(!dataManager.getBoolean(KEY_DATA_INITIALIZED,false))
        {
            dataManager.putBoolean(KEY_SYSTEM_MEDIA,false);
            dataManager.putInt(KEY_THEME,THEME_DEFAULT);
            dataManager.putInt(KEY_NOTIFICATION_TINT,getColorPrimary());
            dataManager.putBoolean(KEY_DATA_INITIALIZED,true);
        }
    }

    @Override
    public boolean onNoDataFound()
    {
        super.onNoDataFound();
        return false;
    }
}
