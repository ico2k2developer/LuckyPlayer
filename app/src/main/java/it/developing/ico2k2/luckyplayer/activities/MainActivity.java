package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOG,LOG + " started, redirecting user to " + TabsActivity.class.getSimpleName());
        startActivity(new Intent(MainActivity.this, TabsActivity.class));
        finish();
    }
}
