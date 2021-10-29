package it.developing.ico2k2.luckyplayer.fragments;

import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE;
import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE_SIZE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static it.developing.ico2k2.luckyplayer.Resources.REQUEST_CODE_PERMISSIONS;
import static it.developing.ico2k2.luckyplayer.services.PlayService.ARG_LUCKY;
import static it.developing.ico2k2.luckyplayer.services.PlayService.ID_ALBUMS;
import static it.developing.ico2k2.luckyplayer.services.PlayService.ID_ARTISTS;
import static it.developing.ico2k2.luckyplayer.services.PlayService.ID_GENRES;
import static it.developing.ico2k2.luckyplayer.services.PlayService.ID_SONGS;
import static it.developing.ico2k2.luckyplayer.services.PlayService.TYPE_BYTE;
import static it.developing.ico2k2.luckyplayer.services.PlayService.TYPE_SHORT;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.List;

import it.developing.ico2k2.luckyplayer.MediaBrowserDependent;
import it.developing.ico2k2.luckyplayer.Permissions;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.MusicItemsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.items.Album;
import it.developing.ico2k2.luckyplayer.adapters.items.Artist;
import it.developing.ico2k2.luckyplayer.adapters.items.Genre;
import it.developing.ico2k2.luckyplayer.adapters.items.MusicItem;
import it.developing.ico2k2.luckyplayer.adapters.items.Song;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

public class SongListFragment extends BaseFragment
{
    private static final String TAG = SongListFragment.class.getSimpleName();

    private static final int ID_MENU_INFO = 0x10;

    private static final int VIEW_LIST = 0x10;
    private static final int VIEW_LABEL = 0x11;
    private static final int VIEW_BUTTON = 0x12;

    private static final String ARG_ROOT = "root";

    private MusicItemsAdapter adapter;
    private int contextClickPosition;
    private String root;

    public static SongListFragment create(String root)
    {
        Log.d(TAG,"Creating new fragment, root: " + root);
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
        adapter = new MusicItemsAdapter();
        adapter.setOrder(MusicItemsAdapter.OrderType.ALPHABETICAL);
        adapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
            @Override
            public void onItemClick(ViewHandle handle,int position){
                MusicItem item = adapter.get(position);
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().playFromMediaId(item.getDescription().getMediaId(),null);
                Toast.makeText(getContext(),"Item for position " + position + " is " +
                        item.getDescription().getTitle() + " with id " +
                        item.getDescription().getMediaId(),Toast.LENGTH_LONG).show();
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
        Log.d(TAG,"Fragment created, root: " + root);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean result = true;
        switch(item.getItemId())
        {
            case ID_MENU_INFO:
            {
                /*Intent intent = new Intent(getActivity(),InfoActivity.class);
                intent.setData(Uri.parse(Song.getPathFromMediaId(adapter.get(contextClickPosition).getMediaId())));
                startActivity(intent);*/
                Toast.makeText(getContext(),adapter.get(contextClickPosition).toMediaItem().getMediaId(),Toast.LENGTH_SHORT).show();
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
        RecyclerView list = new RecyclerView(getContext());
        list.setTag(VIEW_LIST);
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(false);
        list.setAdapter(adapter);
        LinearLayoutCompat layout = new LinearLayoutCompat(getContext());
        layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayoutCompat.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.addView(list);
        Log.d(TAG,"Creating fragment's views, root: " + root + ", is list null? " + (list == null));
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        try
        {
            requestItems();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Log.d(TAG,"Fragment's views created, root: " + root);
    }

    public void requestItems() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(EXTRA_PAGE,0);
        json.put(EXTRA_PAGE_SIZE,PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getString(R.string.key_songlist_packet_size),250));
        JSONArray a = new JSONArray();
        JSONArray b = new JSONArray();
        Class c = null;
        switch(root)
        {
            case ID_SONGS:
            {
                a.put(SongDetailed.COLUMN_LENGTH);
                a.put(SongDetailed.COLUMN_TRACK_N);
                b.put(TYPE_SHORT);
                b.put(TYPE_BYTE);
                c = Song.class;
                break;
            }
            case ID_ALBUMS:
            {
                c = Album.class;
                break;
            }
            case ID_ARTISTS:
            {
                c = Artist.class;
                break;
            }
            case ID_GENRES:
            {
                c = Genre.class;
                break;
            }
        }
        final Constructor constructor;
        Constructor constructor1;
        try
        {
            constructor1 = c.getConstructor(MediaBrowserCompat.MediaItem.class);
        }
        catch(Exception e)
        {
            constructor1 = null;
            e.printStackTrace();
        }
        constructor = constructor1;
        json.put(PlayService.EXTRA_COLUMNS,a);
        json.put(PlayService.EXTRA_TYPES,b);
        String id = ARG_LUCKY + root + json.toString();
        Log.d(TAG,"Subscribing to " + id + ", page: " + json.getInt(EXTRA_PAGE));
        final MediaBrowserCompat mediaBrowser = ((MediaBrowserDependent)getActivity()).getMediaBrowser();
        adapter.clear();
        mediaBrowser.subscribe(id,new MediaBrowserCompat.SubscriptionCallback(){
            @Override
            public void onChildrenLoaded(@NonNull String parentId,@NonNull List<MediaBrowserCompat.MediaItem> children,@NonNull Bundle oldOptions){
                try
                {
                    Log.d(TAG,"Loading " + children.size() + " children from fragment, id: " + parentId);
                    JSONObject json = new JSONObject(parentId.substring((ARG_LUCKY + root).length()));
                    int page = json.getInt(EXTRA_PAGE);
                    int size = json.getInt(EXTRA_PAGE_SIZE);
                    //examineBundle(options);
                    if(page == 0)
                        adapter.clear();
                    for(MediaBrowserCompat.MediaItem item : children)
                    {
                        try
                        {
                            adapter.add((MusicItem) constructor.newInstance(item));
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if(children.size() == size)
                    {
                        json.put(EXTRA_PAGE,page + 1);
                        Log.d(TAG,"Subscribing again to " + parentId + ", page: " + json.getInt(EXTRA_PAGE));
                        mediaBrowser.subscribe(ARG_LUCKY + root + json.toString(),this);
                    }
                    else
                    {
                        Log.d(TAG,"Children load ended");
                        mediaBrowser.unsubscribe(parentId,this);
                        updateList();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull String parentId,@NonNull Bundle options){
                Log.d(TAG,"Error occurred when trying to load children at id: " +
                        parentId + ", page " + options.getInt(EXTRA_PAGE));
                mediaBrowser.unsubscribe(parentId,this);
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
        LinearLayoutCompat container = (LinearLayoutCompat)getView();
        if(adapter.getItemCount() == 0)
        {
            container.findViewWithTag(VIEW_LIST).setVisibility(View.GONE);
            TextView label = container.findViewWithTag(VIEW_LABEL);
            Button button = container.findViewWithTag(VIEW_BUTTON);
            if(label == null)
            {
                label = new TextView(getContext(),null,android.R.attr.textAppearanceMedium);
                label.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                label.setGravity(Gravity.CENTER_HORIZONTAL);
                label.setTag(VIEW_LABEL);
                container.addView(label);
            }
            else
                label.setVisibility(View.VISIBLE);
            if(button == null)
            {
                button = new Button(getContext(),null,android.R.attr.buttonBarButtonStyle);
                button.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                button.setTag(VIEW_BUTTON);
                container.addView(button);
            }
            else
                button.setVisibility(View.VISIBLE);
            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
            {
                label.setText(R.string.no_songs_found);
                button.setText(getString(R.string.no_songs_found_button));
            }
            else
            {
                label.setText(R.string.no_songs_permission);
                button.setText(R.string.no_songs_permission_button);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Permissions.goToAppSettingsPageForPermission(getActivity(),REQUEST_CODE_PERMISSIONS);
                    }
                });
            }
        }
        else
        {
            View view = container.findViewWithTag(VIEW_LABEL);
            if(view != null)
                view.setVisibility(View.GONE);
            view = container.findViewWithTag(VIEW_BUTTON);
            if(view != null)
                view.setVisibility(View.GONE);
            adapter.reorder();
            adapter.notifyDataSetChanged();
        }
        Toast.makeText(getContext(),adapter.getItemCount() + " items",Toast.LENGTH_SHORT).show();
    }
}