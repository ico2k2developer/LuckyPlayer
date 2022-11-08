package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import it.developing.ico2k2.luckyplayer.database.file.media.MusicAdapter;

public class SongListFragment extends Fragment
{
    private static final String TAG = SongListFragment.class.getSimpleName();

    private MusicAdapter adapter;

    public static SongListFragment create(String root)
    {
        Log.d(TAG,"Creating new fragment, root: " + root);
        SongListFragment fragment = new SongListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new MusicAdapter();
    }
}
