package it.developing.ico2k2.luckyplayer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.InfoActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;

public class SongListFragment extends Fragment
{
    public static final String KEY_INDEX = "index";

    private static final int ID_MENU_INFO = 0XAAAA;

    private RecyclerView list;
    private SongsAdapter adapter;
    private int contextClickPosition;

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
        SongsAdapter.Song song = new SongsAdapter.Song("abcdef.mp3");
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
        adapter.setOnContextMenuListener(new SongsAdapter.OnContextMenuListener(){
            @Override
            public void onContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo,int position){
                menu.setHeaderTitle(adapter.get(position).getTitle());
                menu.add(Menu.NONE,ID_MENU_INFO,70,R.string.song_info);
                contextClickPosition = position;
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean result = true;
        switch(item.getItemId())
        {
            case ID_MENU_INFO:
            {
                Intent intent = new Intent(getActivity(),InfoActivity.class);
                intent.setData(Uri.parse(adapter.get(contextClickPosition).getPath()));
                startActivity(intent);
                break;
            }
            default:
            {
                result = false;
            }
        }
        return result;
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