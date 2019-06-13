package it.developing.ico2k2.luckyplayer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.developing.ico2k2.luckyplayer.LuckyPlayer;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.InfoActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_INDEX;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SIZE;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SONGS;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_PLAYER;

public class SongListFragment extends BaseFragment
{
    private static final int ID_MENU_INFO = 0XAAAA;

    private RecyclerView list;
    private SongsAdapter adapter;
    private int contextClickPosition;

    public static SongListFragment create(Bundle arguments)
    {
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment create(int index)
    {
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_INDEX,index);
        return create(arguments);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SongsAdapter(getContext());
        adapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
            @Override
            public void onItemClick(ViewHandle handle,int position){
                Message message = Message.obtain();
                message.what = MESSAGE_PLAYER;
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SONGS,adapter.get(position).getPath());
                message.setData(bundle);
                ((LuckyPlayer)getActivity().getApplication()).sendMessageToService(message,true);
            }
        });
        adapter.setOnItemContextMenuListener(new ViewHandle.OnItemContextMenuListener(){
            @Override
            public void onContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo,int position){
                menu.setHeaderTitle(((TextView)v.findViewById(R.id.itemTitle)).getText());
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
        super.onViewCreated(view,savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(false);
        list.setAdapter(adapter);
    }

    public SongsAdapter getAdapter()
    {
        return adapter;
    }
}