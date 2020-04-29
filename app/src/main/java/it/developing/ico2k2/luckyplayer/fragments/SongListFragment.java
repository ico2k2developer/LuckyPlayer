package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
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
import it.developing.ico2k2.luckyplayer.adapters.Song;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;
import static it.developing.ico2k2.luckyplayer.services.PlayService.TYPE_INT;
import static it.developing.ico2k2.luckyplayer.services.PlayService.TYPE_LONG;

public class SongListFragment extends BaseFragment
{
    private static final int ID_MENU_INFO = 0XAAAA;

    private static final String ARG_ROOT = "root";

    private RecyclerView list;
    private SongsAdapter adapter;
    //private int contextClickPosition;
    private String root;

    public static SongListFragment create(String root)
    {
        Log.d(TAG_LOGS,"Creating new fragment, root: " + root);
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROOT,root);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(getArguments() != null)
            root = getArguments().getString(ARG_ROOT);
        adapter = new SongsAdapter();
        adapter.setOrder(SongsAdapter.OrderType.ALPHABETICAL);
        adapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
            @Override
            public void onItemClick(ViewHandle handle,int position){
                Song song = adapter.get(position);
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromMediaId(song.getDescription().getMediaId(),null);
                Toast.makeText(getContext(),"Song for position " + position + " is " +
                        song.getDescription().getTitle() + " with id " +
                        song.getDescription().getMediaId(),Toast.LENGTH_LONG).show();
            }
        });
        adapter.setOnItemContextMenuListener(new ViewHandle.OnItemContextMenuListener(){
            @Override
            public void onContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo,int position){
                menu.setHeaderTitle(((TextView)v.findViewById(R.id.itemTitle)).getText());
                menu.add(Menu.NONE,ID_MENU_INFO,70,R.string.song_info);
                //contextClickPosition = position;
            }
        });
        Log.d(TAG_LOGS,"Fragment created, root: " + root);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean result = true;
        switch(item.getItemId())
        {
            /*case ID_MENU_INFO:
            {
                Intent intent = new Intent(getActivity(),InfoActivity.class);
                intent.setData(Uri.parse(Song.getPathFromMediaId(adapter.get(contextClickPosition).getMediaId())));
                startActivity(intent);
                break;
            }*/
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(false);
        list.setAdapter(adapter);
        //container.addView(list);
        Log.d(TAG_LOGS,"Creating fragment\'s views, root: " + root + ", is list null? " + (list == null));
        return list;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        requestItems();
        Log.d(TAG_LOGS,"Fragment\' views created, root: " + root);
    }

    protected void requestItems()
    {
        Bundle options = new Bundle();
        options.putInt(MediaBrowserCompat.EXTRA_PAGE,0);
        options.putInt(MediaBrowserCompat.EXTRA_PAGE_SIZE,500);
        options.putStringArray(PlayService.EXTRA_COLUMNS,new String[]{
                MediaStore.MediaColumns.DURATION,
                MediaStore.Audio.AudioColumns.TRACK,
        });
        options.putIntArray(PlayService.EXTRA_TYPES,new int[]{
                TYPE_LONG,
                TYPE_INT,
        });
        MediaBrowserCompat mediaBrowser = ((MediaBrowserDependent)getActivity()).getMediaBrowser();
        mediaBrowser.subscribe(root,options,new MediaBrowserCompat.SubscriptionCallback(){

            @Override
            public void onChildrenLoaded(@NonNull String parentId,@NonNull List<MediaBrowserCompat.MediaItem> children,@NonNull Bundle options){
                int page = options.getInt(MediaBrowserCompat.EXTRA_PAGE);
                Log.d(TAG_LOGS,"Loading children from fragment, page: " + page);
                if(page == 0)
                    adapter.clear();
                if(children.isEmpty())
                {
                    Log.d(TAG_LOGS,"No children available");
                    mediaBrowser.unsubscribe(parentId);
                    updateList();
                }
                else
                {
                    for(MediaBrowserCompat.MediaItem child : children)
                    {
                        try
                        {
                            adapter.add(new Song(child));
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    options.putInt(MediaBrowserCompat.EXTRA_PAGE,page + 1);
                    mediaBrowser.subscribe(parentId,options,this);
                }
            }

            @Override
            public void onError(@NonNull String parentId,@NonNull Bundle options){
                Log.d(TAG_LOGS,"Error occurred when trying to load children at id: " +
                        parentId + ", page " + options.getInt(MediaBrowserCompat.EXTRA_PAGE));
                mediaBrowser.unsubscribe(parentId);
                updateList();
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

    void updateList()
    {
        adapter.reorder();
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(),adapter.getItemCount() + " items",Toast.LENGTH_SHORT).show();
    }
}