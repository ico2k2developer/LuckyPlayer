package it.developing.ico2k2.luckyplayer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.developing.ico2k2.luckyplayer.MediaBrowserDependent;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.InfoActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;

public class SongListFragment extends BaseFragment
{
    private static final int ID_MENU_INFO = 0XAAAA;

    private RecyclerView list;
    private SongsAdapter adapter;
    private int contextClickPosition;
    private String root;

    public static SongListFragment create(String root)
    {
        SongListFragment fragment = new SongListFragment();
        fragment.root = root;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SongsAdapter(getContext());
        adapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
            @Override
            public void onItemClick(ViewHandle handle,int position){
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromMediaId(adapter.get(position).getPath(),null);
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
        list = new RecyclerView(getContext());
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(list);
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
        requestItems();
    }

    protected void requestItems()
    {
        Bundle options = new Bundle();
        options.putInt(MediaBrowserCompat.EXTRA_PAGE,0);
        options.putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE,2);
        MediaBrowserCompat mediaBrowser = ((MediaBrowserDependent)getActivity()).getMediaBrowser();
        mediaBrowser.subscribe(root,options,new MediaBrowserCompat.SubscriptionCallback(){

            @Override
            public void onChildrenLoaded(@NonNull String parentId,@NonNull List<MediaBrowserCompat.MediaItem> children,@NonNull Bundle options){
                Log.d(TAG_LOGS,"Loading children from fragment");
                int page = options.getInt(MediaBrowserCompat.EXTRA_PAGE);
                if(page == 0)
                    adapter.clear();
                if(children.isEmpty())
                {
                    Log.d(TAG_LOGS,"No children available");
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    for(MediaBrowserCompat.MediaItem child : children)
                        adapter.add(new SongsAdapter.Song(child));
                    options.putInt(MediaBrowserCompat.EXTRA_PAGE,page + 1);
                    mediaBrowser.subscribe(parentId,options,this);
                }
            }

            @Override
            public void onError(@NonNull String parentId,@NonNull Bundle options){
                Toast.makeText(getContext(),"Error occurred when trying to load children at id: " + parentId,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull String parentId){
                onError(parentId,new Bundle());
            }

            @Override
            public void onChildrenLoaded(@NonNull String parentId,@NonNull List<MediaBrowserCompat.MediaItem> children){
                onChildrenLoaded(parentId,children,new Bundle());
            }
        });
    }
}