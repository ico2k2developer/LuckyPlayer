package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BasePlayingActivity;
import it.developing.ico2k2.luckyplayer.fragments.SongListFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;

public class TabsActivity extends BasePlayingActivity
{
    private ViewPager pager;
    private PagerAdapter adapter;

    public class PagerAdapter extends FragmentPagerAdapter
    {
        private String[] tabs;
        private String[] ids =
        {
            PlayService.ID_SONGS,
            PlayService.ID_ALBUMS,
            PlayService.ID_ARTISTS,
            PlayService.ID_YEARS,
        };
        private ArrayList<String> tags;

        public PagerAdapter(FragmentManager fm)
        {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            tabs = getResources().getStringArray(R.array.tabs);
            tags = new ArrayList<>(getCount());
        }

        @Override
        @NonNull
        public Fragment getItem(int i)
        {
            Log.d(TAG_LOGS,"Loading fragment " + i);
            return SongListFragment.create(ids[i]);
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        setSupportActionBar(findViewById(R.id.tabs_toolbar));

        TabLayout tabLayout = findViewById(R.id.tabs_tab_layout);
        pager = findViewById(R.id.tabs_pager);
        adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(pager);
        adapter.notifyDataSetChanged();
    }

    void buildTransportControls()
    {
        Log.d(TAG_LOGS,"Building controls");
        requestPlayer();
        // Grab the view for the play/pause button
        /*playPause = (ImageView) findViewById(R.id.play_pause);

        // Attach a listener to the button
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                int pbState = MediaControllerCompat.getMediaController(MediaPlayerActivity.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(MediaPlayerActivity.this).getTransportControls().pause();
                } else {
                    MediaControllerCompat.getMediaController(MediaPlayerActivity.this).getTransportControls().play();
                }
            });*/

            MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);

            // Display the initial state
            MediaMetadataCompat metadata = mediaController.getMetadata();
            PlaybackStateCompat pbState = mediaController.getPlaybackState();

            // Register a Callback to stay in sync
            mediaController.registerCallback(controllerCallback);
    }

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata)
                {
                    Log.d(TAG_LOGS,"MediaController metadata changed");
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state)
                {
                    Log.d(TAG_LOGS,"MediaController playback state changed");
                }
            };

    public void onBackPressed()
    {
        int pbState = MediaControllerCompat.getMediaController(this).getPlaybackState().getState();
        if (pbState == PlaybackStateCompat.STATE_PLAYING) {
            Log.d(TAG_LOGS,"Pausing");
            MediaControllerCompat.getMediaController(this).getTransportControls().pause();
        } else {
            Log.d(TAG_LOGS,"Playing");
            MediaControllerCompat.getMediaController(this).getTransportControls().play();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //browser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback);
        }
        //browser.disconnect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_tabs,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean result = true;

        switch(id)
        {
            case R.id.menuSettings:
            {
                startActivity(new Intent(this,SettingsActivity.class));
                break;
            }
            case R.id.menuRefresh:
            {
                sendMessageToService(MESSAGE_SCAN_REQUESTED);
                break;
            }
            case R.id.menuExit:
            {
                sendMessageToService(MESSAGE_DESTROY);
                break;
            }
            default:
            {
                result = super.onOptionsItemSelected(item);
            }
        }
        return result;
    }
}
