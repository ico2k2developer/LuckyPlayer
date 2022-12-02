package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.developing.ico2k2.luckyplayer.adapters.MediaAdapter;

public class MediaListFragment extends Fragment
{
    private static final String TAG = MediaListFragment.class.getSimpleName();

    private MediaAdapter adapter;
    private RecyclerView list;
    private MediaBrowserCompat browser;

    public static MediaListFragment create(String root,MediaBrowserCompat browser)
    {
        Log.d(TAG,"Creating new fragment, root: " + root);
        MediaListFragment fragment = new MediaListFragment();
        fragment.browser = browser;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new MediaAdapter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        list = new RecyclerView(getContext());
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(false);
        list.setAdapter(adapter);
        return list;
    }
}
