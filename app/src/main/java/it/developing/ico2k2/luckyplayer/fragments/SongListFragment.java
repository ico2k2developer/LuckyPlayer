package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;

public class SongListFragment extends Fragment
{
    public static final String KEY_INDEX = "index";

    private RecyclerView list;
    private SongsAdapter adapter;

    public static SongListFragment create(int index)
    {
        SongListFragment fragment = new SongListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_INDEX,index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SongsAdapter();
        SongsAdapter.Song song = new SongsAdapter.Song("");
        song.setTitle("Hey Hey!");
        song.setArtist("Somebody in Caselette City");
        song.setAlbum("Flaming mountain");
        song.setTime(7 * 60 * 1000 + 12 * 1000);
        song.setIndex(2002);
        adapter.add(song);
        adapter.add(new SongsAdapter.Song(song).setTitle("Hey Jude!"));
        adapter.add(new SongsAdapter.Song(song).setTitle("Hey Bulldog!"));
        adapter.add(new SongsAdapter.Song(song).setTitle("Hey Baby!"));
        adapter.add(new SongsAdapter.Song(song).setTitle("Hey You!"));
        adapter.setOrder(SongsAdapter.Ordering.ALPHABETICAL);
        adapter.setShowIndexes(true);
        adapter.reorder();
        adapter.setOnSongClickListener(new SongsAdapter.OnSongClickListener(){
            @Override
            public void onSongClick(SongsAdapter.SongHandle songHandle,int position){
                Toast.makeText(getContext(),adapter.get(position).getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        list = new RecyclerView(getActivity());
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        //container.addView(list);
        return list;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(false);
        list.setAdapter(adapter);
    }
}
