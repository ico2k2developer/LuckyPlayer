package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BasePlayingActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.fragments.SongListFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_REQUEST_CODE;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SIZE;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SONGS;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_COMPLETED;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SONG_END;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SONG_PACKET;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SONG_START;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;

public class TabsActivity extends BasePlayingActivity
{
    private String requestCode;
    private ArrayList<SongsAdapter.Song> songs;
    private ViewPager pager;
    private PagerAdapter adapter;
    private long time = 0;

    public class PagerAdapter extends FragmentPagerAdapter
    {
        private String[] tabs;
        private ArrayList<String> tags;

        public PagerAdapter(FragmentManager fm)
        {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
            tags = new ArrayList<>(getCount());
        }

        @Override
        @NonNull
        public Fragment getItem( final int i)
        {
            return SongListFragment.create(i);
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container,int position) {
            Object object = super.instantiateItem(container, position);
            if(object instanceof Fragment)
            {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                if(position < tags.size())
                    tags.remove(position);
                tags.add(position,tag);
            }
            return object;
        }

        public Fragment getFragment(int position)
        {
            Fragment result = null;
            if(position < tags.size())
                result = getSupportFragmentManager().findFragmentByTag(tags.get(position));
            return result;
        }

        /*@Override
        public void destroyItem(ViewGroup container,int position,Object object) {
            Toast.makeText(MainActivity.this,"Ignored destroy request",Toast.LENGTH_SHORT).show();
        }*/
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
        //requestPlayer();
    }

    @Override
    public void onServiceBound()
    {
        super.onServiceBound();
        requestCode = getClass().getName();
        requestSongs(requestCode);
    }

    @Override
    public void onMessageReceived(int key,@Nullable Bundle packet)
    {
        Log.d(TAG_LOGS,"Received message in activity " + Integer.toHexString(key));
        boolean processed = true;
        String code = null;
        if(packet != null)
        {
            packet.setClassLoader(getClassLoader());
            code = packet.getString(KEY_REQUEST_CODE);
        }
        if(code == null || code.equals(requestCode))
        {
            switch(key)
            {
                case MESSAGE_SONG_PACKET:
                {
                    if(packet != null)
                    {
                        if(packet.containsKey(KEY_SONGS))
                        {
                            songs.addAll(packet.getParcelableArrayList(KEY_SONGS));
                        }
                    }
                    break;
                }
                case MESSAGE_SONG_START:
                {
                    if(packet != null)
                    {
                        if(packet.containsKey(KEY_SIZE))
                        {
                            int size = packet.getInt(KEY_SIZE);
                            if(songs == null)
                                songs = new ArrayList<>(size);
                            else
                                songs.ensureCapacity(size);
                        }


                    }
                    break;
                }
                case MESSAGE_SONG_END:
                {
                    Toast.makeText(this,songs.size() + " songs",Toast.LENGTH_SHORT).show();
                    SongListFragment fragment;
                    SongsAdapter songsAdapter;
                    int a;
                    for(a = 0; a < adapter.getCount(); a++)
                    {
                        Log.d(TAG_LOGS,"Working on fragment " + a);
                        fragment = (SongListFragment)adapter.getFragment(a);
                        if(fragment != null)
                        {
                            Log.d(TAG_LOGS,"Non null fragment: " + a);
                            songsAdapter = fragment.getAdapter();
                            songsAdapter.clear();
                            songsAdapter.ensureCapacity(songs.size());
                            songsAdapter.setOrderType(PlayService.OrderType.ALPHABETICAL);
                            songsAdapter.setViewType(SongsAdapter.ViewType.values()[a]);
                            songsAdapter.setShowIndexes(false);
                            songsAdapter.addAll(songs);
                            songsAdapter.reorder();
                            songsAdapter.notifyDataSetChanged();
                            Log.d(TAG_LOGS,"Adapter updated in fragment " + a);
                        }
                        else
                            Log.d(TAG_LOGS,"Null fragment: " + a);
                    }
                    songs.clear();
                    songs.trimToSize();
                    break;
                }
                default:
                {
                    processed = false;
                }
            }
        }
        else
            processed = false;
        if(!processed)
        {
            switch(key)
            {
                case MESSAGE_SCAN_COMPLETED:
                {
                    requestSongs(requestCode);
                    break;
                }
            }
        }
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
