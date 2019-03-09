package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BasePlayingActivity;
import it.developing.ico2k2.luckyplayer.fragments.SongListFragment;

public class TabsActivity extends BasePlayingActivity
{
    private ViewPager pager;

    public class PagerAdapter extends FragmentPagerAdapter
    {
        private String[] tabs;

        public PagerAdapter(FragmentManager fm)
        {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(final int i)
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
        setSupportActionBar((Toolbar)findViewById(R.id.tabs_toolbar));

        TabLayout tabLayout = findViewById(R.id.tabs_tab_layout);
        pager = findViewById(R.id.tabs_pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(pager);

        requestPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
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
            case R.id.menuExit:
            {
                finish();
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
