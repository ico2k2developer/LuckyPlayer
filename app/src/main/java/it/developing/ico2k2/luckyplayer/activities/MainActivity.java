package it.developing.ico2k2.luckyplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import it.developing.ico2k2.luckyplayer.database.file.media.MediaService;

public class MainActivity extends Activity
{
    private static final String LOG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        startService(new Intent(MainActivity.this,MediaService.class));

        Log.d(LOG,LOG + " started, redirecting user to " + TabsActivity.class.getSimpleName());
        startActivity(new Intent(MainActivity.this, TabsActivity.class));
        finish();
    }
}
