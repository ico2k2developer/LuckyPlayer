package it.developing.ico2k2.luckyplayer.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import it.developing.ico2k2.luckyplayer.R;

public class TabsActivity extends BasePlayingActivity
{
    private static final String LOG = TabsActivity.class.getSimpleName();

    private ViewPager2 pager;
    private String[] tabs;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOG,"Activity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        setSupportActionBar(findViewById(R.id.toolbar));

        tabs = getResources().getStringArray(R.array.tabs);

        pager = findViewById(R.id.pager);
    }

}
