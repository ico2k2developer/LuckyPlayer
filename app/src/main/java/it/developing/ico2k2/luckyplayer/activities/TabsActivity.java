package it.developing.ico2k2.luckyplayer.activities;

import android.Manifest;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.Utils;
import it.developing.ico2k2.luckyplayer.activities.base.BasePlayingActivity;
import it.developing.ico2k2.luckyplayer.fragments.SongListFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static it.developing.ico2k2.luckyplayer.Utils.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Utils.REQUEST_CODE_PERMISSIONS;
import static it.developing.ico2k2.luckyplayer.Utils.permissionDialog;

public class TabsActivity extends BasePlayingActivity
{

    private ViewPager pager;

    public class PagerAdapter extends FragmentStatePagerAdapter
    {
        private final String[] tabs;
        private final String[] ids =
        {
            PlayService.ID_SONGS,
            PlayService.ID_ALBUMS,
            PlayService.ID_ARTISTS,
            PlayService.ID_GENRES,
        };

        public PagerAdapter(FragmentManager fm)
        {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            tabs = getResources().getStringArray(R.array.tabs);
            //tabs = new String[1];
            //tabs[0] = getResources().getStringArray(R.array.tabs)[0];
        }

        @Override
        @NonNull
        public Fragment getItem(int i)
        {
            Log.d(getClass().getSimpleName(),"Loading fragment " + i);
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
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        PagerAdapter adapter;
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
        Log.d(getClass().getSimpleName(),"Building controls");
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
                    Log.d(getClass().getSimpleName(),"MediaController metadata changed");
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state)
                {
                    Log.d(getClass().getSimpleName(),"MediaController playback state changed");
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        Utils.askForPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE,REQUEST_CODE_PERMISSIONS);
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        if(grantResults.length > 0)
        {
            switch(requestCode)
            {
                case REQUEST_CODE_PERMISSIONS:
                {
                    if(grantResults[0] == PERMISSION_GRANTED)
                        refresh();
                    else
                        permissionDialog(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                REQUEST_CODE_PERMISSIONS,
                                getString(R.string.permission_reason_data),
                                getString(R.string.key_permission_data_no_more));

                    break;
                }
            }
        }
    }

    protected void refresh()
    {
        pager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback);
        }
        getMediaBrowser().disconnect();

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
                refresh();
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
