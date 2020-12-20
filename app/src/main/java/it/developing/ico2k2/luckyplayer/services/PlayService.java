package it.developing.ico2k2.luckyplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.items.Song;
import it.developing.ico2k2.luckyplayer.tasks.MediaScanner;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.provider.MediaStore.Audio.AudioColumns.IS_ALARM;
import static android.provider.MediaStore.Audio.AudioColumns.IS_AUDIOBOOK;
import static android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC;
import static android.provider.MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
import static android.provider.MediaStore.Audio.AudioColumns.IS_PODCAST;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RINGTONE;
import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE;
import static android.support.v4.media.MediaBrowserCompat.EXTRA_PAGE_SIZE;
import static it.developing.ico2k2.luckyplayer.Utils.APP_PACKAGE;
import static it.developing.ico2k2.luckyplayer.Utils.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Utils.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Utils.KEY_REQUEST;
import static it.developing.ico2k2.luckyplayer.Utils.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Utils.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;
import static it.developing.ico2k2.luckyplayer.Utils.examineBundle;

public class PlayService extends MediaBrowserServiceCompat
{
    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    public static final String PACKAGE_AUTO = "com.google.android.projection.gearhead";

    public static final String EXTRA_COLUMNS = "columns";
    public static final String EXTRA_TYPES = "types";

    public static final int TYPE_INT =  0xA;
    public static final int TYPE_LONG =  0xB;
    public static final int TYPE_STRING =  0xC;

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

    private SharedPreferences prefs;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder playNotif;
    private Notification notification;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private String mediaSelection;

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,int clientUid,Bundle rootHints)
    {
        Log.d(TAG_LOGS,"Root request from " + clientPackageName);
        String result = "";
        switch(clientPackageName)
        {
            case PACKAGE_AUTO:
            {
                result = ARG_AUTO;
                break;
            }
            case APP_PACKAGE:
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
        Log.w(TAG_LOGS,"Children request does not contain Bundle options!");
        onLoadChildren(parentId,result,new Bundle());
    }

    private boolean negIf(boolean value,boolean negIf)
    {
        if(negIf)
            return !value;
        else
            return value;
    }

    public @Nullable String buildQuerySelectionString()
    {
        StringBuilder selection = new StringBuilder();
        String andOr,comparator;
        boolean neg = prefs.getBoolean(getString(R.string.settings_include_other_key),false);
        if(neg)
        {
            comparator = " == 0";
            andOr = " AND ";
        }
        else
        {
            comparator = " != 0";
            andOr = " OR ";
        }
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_music_key),false),neg))
            selection.append(IS_MUSIC).append(comparator).append(andOr);
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_ringtone_key),false),neg))
            selection.append(IS_RINGTONE).append(comparator).append(andOr);
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_notification_key),false),neg))
            selection.append(IS_NOTIFICATION).append(comparator).append(andOr);
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_podcast_key),false),neg))
            selection.append(IS_PODCAST).append(comparator).append(andOr);
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_alarm_key),false),neg))
            selection.append(IS_ALARM).append(comparator).append(andOr);
        if(negIf(prefs.getBoolean(getString(R.string.settings_include_audiobook_key),false),neg) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            selection.append(IS_AUDIOBOOK).append(comparator).append(andOr);
        return selection.length() > 0 ? selection.substring(0,selection.length() - andOr.length()) : null;
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
            Log.d(TAG_LOGS,"Children request from client " + arg1 + " with path " + id + ", arguments: " + arg2);
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
                Log.d(TAG_LOGS,"Using paging mode, page: " + pageFrom + ", size: " + pageTo);
                pageFrom *= pageTo;
                pageTo += pageFrom;
                items = new ArrayList<>(pageTo - pageFrom);
            }
            else
            {
                Log.w(TAG_LOGS,"Running in non paging mode: if too much, songs may be lost during transition!");
                items = new ArrayList<>();
            }
            MediaScanner scanner = new MediaScanner(getContentResolver());
            Log.d(TAG_LOGS,"MediaScanner created");
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
                        Log.d(TAG_LOGS,"Processing songs case");
                        List<String> columns = new ArrayList<>(Arrays.asList(
                                MediaStore.MediaColumns._ID,
                                MediaStore.MediaColumns.TITLE,
                                MediaStore.Audio.AlbumColumns.ALBUM,
                                MediaStore.Audio.AlbumColumns.ARTIST));
                        String[] requestedColumns = new String[0];
                        int[] requestedTypes = new int[0];
                        if(options.containsKey(EXTRA_COLUMNS))
                        {
                            columns.addAll(Arrays.asList(options.getStringArray(EXTRA_COLUMNS)));
                            requestedColumns = options.getStringArray(EXTRA_COLUMNS);
                            requestedTypes = options.getIntArray(EXTRA_TYPES);
                        }
                        Log.d(TAG_LOGS,"Added " + requestedColumns.length + " columns");
                        MediaScanner.MediaScanResult results = scanner.subscan(SONGS_URI,
                                pageFrom,pageTo,columns,mediaSelection,null);
                        Log.d(TAG_LOGS,"Scan ended");
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
                                            int n;
                                            try
                                            {
                                                n = Integer.parseInt(c);
                                            }
                                            catch(Exception e)
                                            {
                                                n = 0;
                                            }
                                            extras.putInt(column,n);
                                            break;
                                        }
                                        case TYPE_LONG:
                                        {
                                            long n;
                                            try
                                            {
                                                n = Long.parseLong(c);
                                            }
                                            catch(Exception e)
                                            {
                                                n = 0;
                                            }
                                            extras.putLong(column,n);
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
                                    .setMediaId(row[results.getIndexFromColumnName(MediaStore.MediaColumns._ID)])
                                    .setTitle(row[results.getIndexFromColumnName(MediaStore.MediaColumns.TITLE)])
                                    .setSubtitle(Song.getSongDescription(
                                            row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ALBUM)],
                                            row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ARTIST)]))
                                    .setExtras(extras).build(),
                                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                        }
                        results.release();
                    }
                    else
                        Log.d(TAG_LOGS,"Skipped songs query because media selection string is null");
                    break;
                }
                case ID_ALBUMS:
                {
                    Log.d(TAG_LOGS,"Processing albums case");
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
                    Log.d(TAG_LOGS,"Added " + requestedColumns.length + " columns");
                    MediaScanner.MediaScanResult results = scanner.subscan(ALBUMS_URI,
                            pageFrom,pageTo,columns,null,null);
                    Log.d(TAG_LOGS,"Scan ended");
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
                    Log.d(TAG_LOGS,"Processing artists case");
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
                    Log.d(TAG_LOGS,"Added " + requestedColumns.length + " columns");
                    MediaScanner.MediaScanResult results = scanner.subscan(ARTISTS_URI,
                            pageFrom,pageTo,columns,null,null);
                    Log.d(TAG_LOGS,"Scan ended");
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
                    /*Log.d(TAG_LOGS,"Processing genres case");
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
                    Log.d(TAG_LOGS,"Added " + requestedColumns.length + " columns");
                    MediaScanner.MediaScanResult results = scanner.subscan(GENRES_URI,
                            pageFrom,pageTo,columns,null,null);
                    Log.d(TAG_LOGS,"Scan ended");
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
                    results.release();*/
                    break;
                }
            }
            Log.d(TAG_LOGS,items.size() + " children found");
            Log.d(TAG_LOGS,"Packet (items), from " + pageFrom + " to " + pageTo);
        }
        else
        {
            Log.w(TAG_LOGS,"Permission " + READ_EXTERNAL_STORAGE + " not granted");
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
            Log.d(TAG_LOGS,"Updating metadata");
            MediaScanner scanner = new MediaScanner(getContentResolver());
            String title,description;
            MediaScanner.MediaScanResult result = scanner.subscan(SONGS_URI,1,new String[]{
                            MediaStore.MediaColumns.TITLE,
                            MediaStore.Audio.AlbumColumns.ALBUM,
                            MediaStore.Audio.AlbumColumns.ARTIST,
                            MediaStore.Audio.AudioColumns.DURATION,},
                    MediaStore.MediaColumns._ID + "=" + originalMediaId,null);
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            Long.parseLong(result.getCell(
                                    MediaStore.Audio.AudioColumns.DURATION,0)))
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                            title = result.getCell(MediaStore.MediaColumns.TITLE,0))
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                            description = Song.getSongDescription(
                                    result.getCell(MediaStore.Audio.AlbumColumns.ALBUM,0),
                                    result.getCell(MediaStore.Audio.AlbumColumns.ARTIST,0)))
                    .build());
            playNotif.setContentTitle(title);
            playNotif.setContentText(description);
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
                //playNotif.addAction(pause);
                manager.notify(NOTIFICATION_STATUS,playNotif.build());
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
                                Log.d(TAG_LOGS,"Update state thread interrupted");
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
            Log.d(TAG_LOGS,"Trying to play media id " + mediaId + " (id1: " + id1 + ", id2: " + id2 + ")");
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
                //playNotif.addAction(play);
                manager.notify(NOTIFICATION_STATUS,playNotif.build());
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
                Log.d(TAG_LOGS,"Could not stop MediaPlayer");
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

    private NotificationCompat.Action play,pause;

    @Override
    public void onCreate()
    {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mediaSelection = buildQuerySelectionString();
        /*if(prefs.contains(KEY_SONGLIST_LAST_SIZE))
            songs = new ArrayList<>(prefs.getInt(KEY_SONGLIST_LAST_SIZE,100));
        else
            songs = new ArrayList<>();*/
        mediaSession = new MediaSessionCompat(this,TAG_LOGS);
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
                .setColor(prefs.getInt(getString(R.string.settings_notification_tint_key),0))
                .setSmallIcon(R.drawable.ic_player_notification)
                .setContentTitle(getString(R.string.player_notification_title).replace("%s",getString(R.string.app_name)))
                .setContentText(getString(R.string.player_notification_text).replace("%s",getString(R.string.app_name)))
                .setOngoing(true)
                .setContentIntent(pending)
                .addAction(R.drawable.ic_exit_notification_action,getString(R.string.exit),action)
                .build();
        startForeground(NOTIFICATION_STATUS,notification);
        playNotif = (NotificationCompat.Builder)new NotificationCompat.Builder(this,CHANNEL_ID_STATUS)
                .setContentIntent(mediaSession.getController().getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_STOP))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP)));
        play = new NotificationCompat.Action(
                android.R.drawable.ic_media_pause, getString(R.string.pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PLAY));
        pause = new NotificationCompat.Action(
                android.R.drawable.ic_media_play, getString(R.string.play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PAUSE));
        //scan();
        Log.d(TAG_LOGS,"Service created");
    }

    public void elaborateRequest(String what,Bundle args)
    {
        Log.d(TAG_LOGS,"Elaborating request " + what + " in service");
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
                //scan();
                break;
            }
        }
    }

    /*public void scan()
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_INFO)
                .setColor(prefs.getInt(KEY_NOTIFICATION_TINT,0))
                .setSmallIcon(R.drawable.ic_scan_notification)
                .setContentTitle(getString(R.string.scan_notification_title))
                .setContentText(getString(R.string.scan_notification_text))
                .setOngoing(true)
                .setProgress(1,0,true)
                .setContentIntent(pendingIntent)
                .build();
        MediaScanner scanner = new MediaScanner(getContentResolver(),new MediaScanner.OnMediaScannerResult(){
            @Override
            public void onScanStart(){
                songs.clear();
                manager.notify(NOTIFICATION_SCAN,notification);
                Log.d(TAG_LOGS,"Scan started");
            }

            @Override
            public void onScanResult(Song song){
                songs.add(song);
            }

            @Override
            public void onScanStop(){
                manager.cancel(NOTIFICATION_SCAN);
                prefs.edit().putInt(KEY_SONGLIST_LAST_SIZE,songs.size()).apply();
                Log.d(TAG_LOGS,"Scan finished, items: " + songs.size());
            }
        });
        scanner.startScan();
    }*/

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.d(TAG_LOGS,"Start command received");
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
        Log.d(TAG_LOGS,"Service destroying");
    }
}
