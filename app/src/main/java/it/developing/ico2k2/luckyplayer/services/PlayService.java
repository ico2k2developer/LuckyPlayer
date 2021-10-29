package it.developing.ico2k2.luckyplayer.services;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE;
import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE_SIZE;
import static it.developing.ico2k2.luckyplayer.Resources.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Resources.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Resources.KEY_REQUEST;
import static it.developing.ico2k2.luckyplayer.Resources.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Resources.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Resources.PACKAGE_LUCKY;
import static it.developing.ico2k2.luckyplayer.Resources.examineBundle;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import it.developing.ico2k2.luckyplayer.NotificationChannelsManager;
import it.developing.ico2k2.luckyplayer.Prefs;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.items.Song;
import it.developing.ico2k2.luckyplayer.database.Client;
import it.developing.ico2k2.luckyplayer.database.data.FilesDatabase;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongsDetailedDatabase;
import it.developing.ico2k2.luckyplayer.tasks.AsyncTask;
import it.developing.ico2k2.luckyplayer.tasks.MediaManager;

public class PlayService extends MediaBrowserServiceCompat
{
    private static final String TAG = PlayService.class.getSimpleName();

    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    public static final String PACKAGE_AUTO = "com.google.android.projection.gearhead";

    public static final String EXTRA_COLUMNS = "columns";
    public static final String EXTRA_TYPES = "types";

    public static final int TYPE_INT =  0xA;
    public static final int TYPE_LONG =  0xB;
    public static final int TYPE_STRING =  0xC;
    public static final int TYPE_BYTE =  0xD;
    public static final int TYPE_SHORT =  0xE;

    public static final String ARG_AUTO = "auto";
    public static final String ARG_LUCKY = "ico2k2";

    public static final String ID_ROOT = "$root&";

    public static final String ID_SONGS = "$songs&";
    public static final String ID_ALBUMS = "$albums&";
    public static final String ID_ARTISTS = "$artists&";
    public static final String ID_GENRES = "$genres&";

    public static final String[] ID_TABS =
    {
        ID_SONGS,
        ID_ALBUMS,
        ID_ARTISTS,
        ID_GENRES,
    };


    public static final Uri[] SONGS_URI = new Uri[]{
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    };

    public static final Uri[] ALBUMS_URI = new Uri[]{
            MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
    };

    public static final Uri[] ARTISTS_URI = new Uri[]{
            MediaStore.Audio.Artists.INTERNAL_CONTENT_URI,
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
    };

    public static final Uri[] GENRES_URI = new Uri[]{
            MediaStore.Audio.Genres.INTERNAL_CONTENT_URI,
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
    };

    private Prefs prefs;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder playNotif;
    private Notification notification;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private String mediaSelection;

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,int clientUid,Bundle rootHints)
    {
        Log.d(TAG,"Root request from " + clientPackageName);
        String result = "";
        switch(clientPackageName)
        {
            case PACKAGE_AUTO:
            {
                result = ARG_AUTO;
                break;
            }
            case PACKAGE_LUCKY:
            {
                result = ARG_LUCKY;
            }
        }
        result = result + ID_ROOT;
        return new BrowserRoot(result,null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,@NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        Log.w(getClass().getSimpleName(),"Children request does not contain Bundle options!");
        onLoadChildren(parentId,result,new Bundle());
    }

    private final Map<String,Map<String,Integer>> keysCache = new HashMap<>();
    //private Map<String[],MediaManager.QueryResult> queryCache = new HashMap<>();

    private void loadQuerySettings()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            scanner.setQuerySettings(new MediaManager.QuerySettings(
                    prefs.getBoolean(getString(R.string.key_include_music),false),
                    prefs.getBoolean(getString(R.string.key_include_ringtone),false),
                    prefs.getBoolean(getString(R.string.key_include_notification),false),
                    prefs.getBoolean(getString(R.string.key_include_podcast),false),
                    prefs.getBoolean(getString(R.string.key_include_alarm),false),
                    prefs.getBoolean(getString(R.string.key_include_audiobook),false),
                    prefs.getBoolean(getString(R.string.key_include_recording),false),
                    prefs.getBoolean(getString(R.string.key_include_other),false)));
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            scanner.setQuerySettings(new MediaManager.QuerySettings(
                    prefs.getBoolean(getString(R.string.key_include_music),false),
                    prefs.getBoolean(getString(R.string.key_include_ringtone),false),
                    prefs.getBoolean(getString(R.string.key_include_notification),false),
                    prefs.getBoolean(getString(R.string.key_include_podcast),false),
                    prefs.getBoolean(getString(R.string.key_include_alarm),false),
                    prefs.getBoolean(getString(R.string.key_include_audiobook),false),
                    prefs.getBoolean(getString(R.string.key_include_other),false)));
        }
        else
        {
            scanner.setQuerySettings(new MediaManager.QuerySettings(
                    prefs.getBoolean(getString(R.string.key_include_music),false),
                    prefs.getBoolean(getString(R.string.key_include_ringtone),false),
                    prefs.getBoolean(getString(R.string.key_include_notification),false),
                    prefs.getBoolean(getString(R.string.key_include_podcast),false),
                    prefs.getBoolean(getString(R.string.key_include_alarm),false),
                    prefs.getBoolean(getString(R.string.key_include_other),false)));
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,@NonNull Result<List<MediaBrowserCompat.MediaItem>> result,@NonNull Bundle options)
    {
        ArrayList<MediaBrowserCompat.MediaItem> items;
        if(ContextCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            String arg1,id,arg2;
            int index1 = parentId.indexOf(ID_ROOT.charAt(0)),index2 = parentId.indexOf(ID_ROOT.charAt(ID_ROOT.length() - 1));
            arg1 = parentId.substring(0,index1);
            arg2 = parentId.substring(index2 + 1);
            id = parentId.substring(index1,index2 + 1);
            Log.d(TAG,"Children request from client " + arg1 + " with path " + id + ", arguments: " + arg2);
            result.detach();
            int pageFrom = -1,pageTo = -1;
            switch(arg1)
            {
                case ARG_LUCKY:
                {
                    try
                    {
                        JSONObject json = new JSONObject(arg2);
                        options.putInt(EXTRA_PAGE,json.getInt(EXTRA_PAGE));
                        options.putInt(EXTRA_PAGE_SIZE,json.getInt(EXTRA_PAGE_SIZE));
                        JSONArray a = json.getJSONArray(EXTRA_COLUMNS);
                        String[] arr = new String[a.length()];
                        int i;
                        for(i = 0; i < arr.length; i++)
                        {
                            arr[i] = a.getString(i);
                        }
                        options.putStringArray(EXTRA_COLUMNS,arr);
                        a = json.getJSONArray(EXTRA_TYPES);
                        int[] arr2 = new int[a.length()];
                        for(i = 0; i < arr2.length; i++)
                        {
                            arr2[i] = a.getInt(i);
                        }
                        options.putIntArray(EXTRA_TYPES,arr2);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            examineBundle(options);
            if(options.containsKey(EXTRA_PAGE) && options.containsKey(MediaBrowserCompat.EXTRA_PAGE_SIZE))
            {
                pageFrom = options.getInt(EXTRA_PAGE);
                pageTo = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE);
                Log.d(TAG,"Using paging mode, page: " + pageFrom + ", size: " + pageTo);
                pageFrom *= pageTo;
                pageTo += pageFrom;
                items = new ArrayList<>(pageTo - pageFrom);
            }
            else
            {
                Log.w(getClass().getSimpleName(),"Running in non paging mode: if too much, songs may be lost during transition!");
                items = new ArrayList<>();
            }

            Log.d(TAG,"MediaScanner created");
            String[] extraColumns = options.getStringArray(EXTRA_COLUMNS);
            switch(id)
            {
                case ID_ROOT:
                {
                    String[] tabs = getResources().getStringArray(R.array.tabs);
                    items.ensureCapacity(tabs.length);
                    int a = 0;
                    for(String tab : tabs)
                    {
                        items.add(new MediaBrowserCompat.MediaItem(
                                new MediaDescriptionCompat.Builder()
                                .setMediaId(arg1 + ID_TABS[a])
                                .setTitle(tab)
                                .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                        a++;
                    }
                    break;
                }
                case ID_SONGS:
                {
                    if(mediaSelection != null)
                    {
                        Log.d(TAG,"Processing songs case");
                        MediaManager.QueryResult query = scanner.query(mediaSelection,null);
                        Map<String,Integer> keys;
                        if(keysCache.containsKey(id))
                        {
                            if(extraColumns == null)
                            {
                                keys = keysCache.get(id);
                            }
                            else
                            {
                                keys = new HashMap<>(keysCache.get(id));
                                keys.putAll(query.generateKeys(extraColumns));
                            }
                        }
                        else
                        {
                            List<String> columns = new ArrayList<>(Arrays.asList(
                                    SongDetailed.COLUMN_URI,
                                    SongDetailed.COLUMN_TITLE,
                                    SongDetailed.COLUMN_ALBUM,
                                    SongDetailed.COLUMN_ALBUM_ARTIST));
                            if(extraColumns == null)
                            {
                                columns.addAll(Arrays.asList(extraColumns));
                            }
                            keysCache.put(id,keys = query.generateKeys(columns));
                        }
                        Log.d(TAG,"Requested " + keys.size() + " columns");
                        List<Map<String,String>> results;
                        if(pageFrom >= 0 && pageTo > 0)
                            results = query.getPage(keys,pageFrom,pageTo - pageFrom);
                        else
                        {
                            results = query.getAll(keys);
                        }
                        for(Map<String,String> row : results)
                        {
                            Bundle extras = new Bundle();
                            for(String key : row.keySet())
                                extras.putString(key,row.get(key));
                            items.add(new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                                    .setMediaId(row.get(SongDetailed.COLUMN_URI))
                                    .setTitle(row.get(SongDetailed.COLUMN_TITLE))
                                    .setSubtitle(Song.getSongDescription(
                                            row.get(SongDetailed.COLUMN_ALBUM),
                                            row.get(SongDetailed.COLUMN_ALBUM_ARTIST)))
                                    .setExtras(extras).build(),
                                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                        }
                        query.release();
                    }
                    else
                        Log.d(TAG,"Skipped songs query because media selection string is null");
                    break;
                }/*
                case ID_ALBUMS:
                {
                    Log.d(TAG,"Processing albums case");
                    List<String> columns = new ArrayList<>(Arrays.asList(
                            MediaStore.Audio.Albums._ID,
                            MediaStore.Audio.Albums.ALBUM,
                            MediaStore.Audio.Albums.ARTIST));
                    String[] requestedColumns = new String[0];
                    int[] requestedTypes = new int[0];
                    if(options.containsKey(EXTRA_COLUMNS))
                    {
                        columns.addAll(Arrays.asList(requestedColumns = options.getStringArray(EXTRA_COLUMNS)));
                        requestedTypes = options.getIntArray(EXTRA_TYPES);
                    }
                    Log.d(TAG,"Added " + requestedColumns.length + " columns");
                    MediaManager.QueryResult results = scanner.subscan(ALBUMS_URI,
                                                                       pageFrom,pageTo,columns,null,null);
                    Log.d(TAG,"Scan ended");
                    for(String[] row : results.getAll())
                    {
                        Bundle extras = new Bundle();
                        if(options.containsKey(EXTRA_COLUMNS))
                        {
                            int a = requestedTypes.length == requestedColumns.length ? 0 : -1,b;
                            String c;
                            for(String column : requestedColumns)
                            {
                                c = row[results.getIndexFromColumnName(column)];
                                if(a > -1)
                                {
                                    b = requestedTypes[a];
                                }
                                else
                                    b = TYPE_STRING;
                                switch(b)
                                {
                                    case TYPE_INT:
                                    {
                                        extras.putInt(column,Integer.parseInt(c));
                                        break;
                                    }
                                    case TYPE_LONG:
                                    {
                                        extras.putLong(column,Long.parseLong(c));
                                        break;
                                    }
                                    default:
                                    {
                                        extras.putString(column,c);
                                        break;
                                    }
                                }
                            }
                        }
                        items.add(new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                                .setMediaId(row[results.getIndexFromColumnName(MediaStore.Audio.Albums._ID)])
                                .setTitle(row[results.getIndexFromColumnName(MediaStore.Audio.Albums.ALBUM)])
                                .setSubtitle(row[results.getIndexFromColumnName(MediaStore.Audio.Albums.ARTIST)])
                                .setExtras(extras).build(),
                                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                    }
                    results.release();
                    break;
                }
                case ID_ARTISTS:
                {
                    Log.d(TAG,"Processing artists case");
                    List<String> columns = new ArrayList<>(Arrays.asList(
                            MediaStore.Audio.Artists._ID,
                            MediaStore.Audio.ArtistColumns.ARTIST,
                            MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS));
                    String[] requestedColumns = new String[0];
                    int[] requestedTypes = new int[0];
                    if(options.containsKey(EXTRA_COLUMNS))
                    {
                        columns.addAll(Arrays.asList(requestedColumns = options.getStringArray(EXTRA_COLUMNS)));
                        requestedTypes = options.getIntArray(EXTRA_TYPES);
                    }
                    Log.d(TAG,"Added " + requestedColumns.length + " columns");
                    MediaManager.QueryResult results = scanner.subscan(ARTISTS_URI,
                                                                       pageFrom,pageTo,columns,null,null);
                    Log.d(TAG,"Scan ended");
                    for(String[] row : results.getAll())
                    {
                        Bundle extras = new Bundle();
                        if(options.containsKey(EXTRA_COLUMNS))
                        {
                            int a = requestedTypes.length == requestedColumns.length ? 0 : -1,b;
                            String c;
                            for(String column : requestedColumns)
                            {
                                c = row[results.getIndexFromColumnName(column)];
                                if(a > -1)
                                {
                                    b = requestedTypes[a];
                                }
                                else
                                    b = TYPE_STRING;
                                switch(b)
                                {
                                    case TYPE_INT:
                                    {
                                        extras.putInt(column,Integer.parseInt(c));
                                        break;
                                    }
                                    case TYPE_LONG:
                                    {
                                        extras.putLong(column,Long.parseLong(c));
                                        break;
                                    }
                                    default:
                                    {
                                        extras.putString(column,c);
                                        break;
                                    }
                                }
                            }
                        }
                        items.add(new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                                .setMediaId(row[results.getIndexFromColumnName(MediaStore.Audio.Artists._ID)])
                                .setTitle(row[results.getIndexFromColumnName(MediaStore.Audio.ArtistColumns.ARTIST)])
                                .setSubtitle(row[results.getIndexFromColumnName(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS)])
                                .setExtras(extras).build(),
                                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                    }
                    results.release();
                    break;
                }
                case ID_GENRES:
                {
                    Log.d(TAG,"Processing genres case");
                    List<String> columns = new ArrayList<>(Arrays.asList(
                            MediaStore.Audio.Genres._ID,
                            MediaStore.Audio.Genres.NAME));
                    String[] requestedColumns = new String[0];
                    int[] requestedTypes = new int[0];
                    if(options.containsKey(EXTRA_COLUMNS))
                    {
                        columns.addAll(Arrays.asList(requestedColumns = options.getStringArray(EXTRA_COLUMNS)));
                        requestedTypes = options.getIntArray(EXTRA_TYPES);
                    }
                    Log.d(TAG,"Added " + requestedColumns.length + " columns");
                    MediaScanner.MediaScanResult results = scanner.subscan(GENRES_URI,
                            pageFrom,pageTo,columns,null,null);
                    Log.d(TAG,"Scan ended");
                    for(String[] row : results.getAll())
                    {
                        Bundle extras = new Bundle();
                        if(options.containsKey(EXTRA_COLUMNS))
                        {
                            int a = requestedTypes.length == requestedColumns.length ? 0 : -1,b;
                            String c;
                            for(String column : requestedColumns)
                            {
                                c = row[results.getIndexFromColumnName(column)];
                                if(a > -1)
                                {
                                    b = requestedTypes[a];
                                }
                                else
                                    b = TYPE_STRING;
                                switch(b)
                                {
                                    case TYPE_INT:
                                    {
                                        extras.putInt(column,Integer.parseInt(c));
                                        break;
                                    }
                                    case TYPE_LONG:
                                    {
                                        extras.putLong(column,Long.parseLong(c));
                                        break;
                                    }
                                    default:
                                    {
                                        extras.putString(column,c);
                                        break;
                                    }
                                }
                            }
                        }
                        items.add(new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                                .setMediaId(row[results.getIndexFromColumnName(MediaStore.Audio.Genres._ID)])
                                .setTitle(row[results.getIndexFromColumnName(MediaStore.Audio.Genres.NAME)])
                                .setExtras(extras).build(),
                                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                    }
                    results.release();
                    break;
                }*/
            }
            Log.d(TAG,items.size() + " children found");
            Log.d(TAG,"Packet (items), from " + pageFrom + " to " + pageTo);
        }
        else
        {
            Log.w(getClass().getSimpleName(),"Permission " + READ_EXTERNAL_STORAGE + " not granted");
            items = new ArrayList<>();
        }
        result.sendResult(items);
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback{

        MediaPlayer player;
        Thread thread;

        private void updateState()
        {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(player.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                            player.getCurrentPosition(),1)
                    .build());
        }

        private void updateMetadata(String originalMediaId)
        {
            Log.d(TAG,"Updating metadata");
            List<SongDetailed> result = scanner.getSongsDatabase().dao().loadAllByUris(new String[]{originalMediaId});
            if(result.size() > 0)
            {
                String description;
                SongDetailed song = result.get(0);
                mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                ((long)song.getLength() * 1000L))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,song.getTitle())
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                                description = Song.getSongDescription(
                                        song.getAlbum(),
                                        song.getArtist()))
                        .build());
                playNotif.setContentTitle(song.getTitle());
                playNotif.setContentText(description);
            }
        }

        @Override
        public void onPlay()
        {
            try
            {
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp){
                        onPause();
                    }
                });
                player.start();
                playNotif.setSmallIcon(R.drawable.ic_play_notification);
                Notification n = playNotif.build();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    Notification.Action[] a = new Notification.Action[1];
                    a[0] = play;
                    n.actions = a;
                }
                manager.notify(NOTIFICATION_STATUS,n);
                if(thread != null)
                    thread.interrupt();
                thread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        while(player.isPlaying())
                        {
                            try
                            {
                                if(player.getCurrentPosition() > 1000)
                                    Thread.sleep(250);
                                else
                                    Thread.sleep(50);
                                updateState();
                            }
                            catch(Exception e)
                            {
                                Log.d(TAG,"Update state thread interrupted");
                            }
                        }
                    }
                });
                thread.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onSkipToQueueItem(long queueId){
        }

        @Override
        public void onSeekTo(long position)
        {
            try
            {
                player.seekTo((int)position);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId,Bundle extras)
        {
            int index = mediaId.indexOf(";");
            final String id1 = mediaId.substring(0,index),id2 = mediaId.substring(index + 1);
            Log.d(TAG,"Trying to play media id " + mediaId + " (id1: " + id1 + ", id2: " + id2 + ")");
            onStop();
            player = new MediaPlayer();
            try
            {
                index = Integer.parseInt(mediaId.substring(0,mediaId.indexOf(";")));
                ParcelFileDescriptor file = getBaseContext().getContentResolver().openFileDescriptor(Uri.withAppendedPath(
                        SONGS_URI[Integer.parseInt(id1)],
                        id2),"r");
                player.setDataSource(file.getFileDescriptor());
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mp){
                        playNotif.setContentTitle(id2);
                        updateMetadata(id2);
                        onPlay();
                    }
                });
                player.prepare();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onPause()
        {
            try
            {
                player.pause();
                playNotif.setSmallIcon(R.drawable.ic_pause_notification);
                Notification n = playNotif.build();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    Notification.Action[] a = new Notification.Action[1];
                    a[0] = pause;
                    n.actions = a;
                }
                manager.notify(NOTIFICATION_STATUS,n);
                thread.interrupt();
                updateState();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onStop(){
            try
            {
                player.stop();
                player.release();
            }
            catch(Exception e)
            {
                Log.d(TAG,"Could not stop MediaPlayer");
            }
            manager.notify(NOTIFICATION_STATUS,notification);
        }

        @Override
        public void onSkipToNext(){
        }

        @Override
        public void onSkipToPrevious(){
        }

        @Override
        public void onCustomAction(String action,Bundle extras)
        {
            elaborateRequest(action,extras);
        }

        @Override
        public void onPlayFromSearch(final String query,final Bundle extras){
        }
    }

    private Notification.Action play,pause;
    private MediaManager scanner;

    @Override
    public void onCreate()
    {
        super.onCreate();
        prefs = Prefs.getInstance(this,Prefs.PREFS_SETTINGS);
        //mediaSelection = buildQuerySelectionString();
        /*if(prefs.contains(KEY_SONGLIST_LAST_SIZE))
            songs = new ArrayList<>(prefs.getInt(KEY_SONGLIST_LAST_SIZE,100));
        else
            songs = new ArrayList<>();*/

        scanner = new MediaManager(this,
                Client.getInstance(this,
                        FilesDatabase.class,Client.DATABASE_SONGS),
                Client.getInstance(this,
                        SongsDetailedDatabase.class,Client.DATABASE_SONGS_DETAILED));
        loadQuerySettings();
        scanner.scan(SONGS_URI);
        mediaSession = new MediaSessionCompat(this,getClass().getSimpleName(),new ComponentName(this,Intent.ACTION_MEDIA_BUTTON),null);
        manager = NotificationManagerCompat.from(this);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new PlayService.MediaSessionCallback());
        setSessionToken(mediaSession.getSessionToken());
        Intent intent = new Intent(this,PlayService.class);
        intent.putExtra(KEY_REQUEST,MESSAGE_DESTROY);
        PendingIntent action = PendingIntent.getService(this,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pending = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(this,CHANNEL_ID_INFO)
                .setColor(prefs.getInt(getString(R.string.key_notification_tint),0))
                .setSmallIcon(R.drawable.ic_player_notification)
                .setContentTitle(getString(R.string.player_notification_title).replace("%s",getString(R.string.app_name)))
                .setContentText(getString(R.string.player_notification_text).replace("%s",getString(R.string.app_name)))
                .setOngoing(true)
                .setContentIntent(pending)
                .addAction(R.drawable.ic_exit_notification_action,getString(R.string.exit),action)
                .build();
        startForeground(NOTIFICATION_STATUS,notification);
        playNotif = new NotificationCompat.Builder(this,CHANNEL_ID_STATUS)
                .setContentIntent(mediaSession.getController().getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_STOP))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP)));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            play = new Notification.Action(
                    android.R.drawable.ic_media_pause, getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PLAY));
            pause = new Notification.Action(
                    android.R.drawable.ic_media_play, getString(R.string.play),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                            PlaybackStateCompat.ACTION_PAUSE));
        }
        //scan();
        Log.d(TAG,"Service created");
    }

    public void elaborateRequest(String what,Bundle args)
    {
        Log.d(TAG,"Elaborating request " + what + " in service");
        if(args != null)
            args.setClassLoader(getClassLoader());
        switch(what)
        {
            case MESSAGE_DESTROY:
            {
                stopForeground(false);
                stopSelf();
                break;
            }
            case MESSAGE_SCAN_REQUESTED:
            {
                scan();
                break;
            }
        }
    }

    public void scan()
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(this, NotificationChannelsManager.CHANNEL_INFO.getId())
                .setColor(prefs.getInt(getString(R.string.key_notification_tint)))
                .setSmallIcon(R.drawable.ic_scan_notification)
                .setContentTitle(getString(R.string.scan_notification_title))
                .setContentText(getString(R.string.scan_notification_text))
                .setOngoing(true)
                .setProgress(1,0,true)
                .setContentIntent(pendingIntent)
                .build();
        new AsyncTask<Long>().executeAsync(new AsyncTask.OnStart() {
            @Override
            public void onStart() {
                manager.notify(NOTIFICATION_SCAN, notification);
                Log.d(TAG, "Scan started");
            }
        }, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return scanner.scan(SONGS_URI);
            }
        }, new AsyncTask.OnFinish<Long>() {
            @Override
            public void onComplete(@Nullable Long result) {
                Log.d(TAG, "Scan ended: " + result + " items found");
                manager.cancel(NOTIFICATION_SCAN);
                prefs.edit().putLong(getString(R.string.key_scan_last_size),result).apply();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.d(TAG,"Start command received");
        elaborateRequest(intent.getStringExtra(KEY_REQUEST),intent.getExtras());
        MediaButtonReceiver.handleIntent(mediaSession,intent);
        return super.onStartCommand(intent,flags,startId);
    }

    public enum RequestType
    {
        SONGS,
        ALBUMS,
        ARTISTS
    }

    public enum OrderType
    {
        NONE,
        ALPHABETICAL,
        INDEX
    }

    public void onDestroy()
    {
        super.onDestroy();
        mediaSession.release();
        manager.cancel(NOTIFICATION_STATUS);
        Log.d(TAG,"Service destroying");
    }
}
