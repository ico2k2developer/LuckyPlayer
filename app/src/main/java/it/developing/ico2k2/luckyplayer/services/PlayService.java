package it.developing.ico2k2.luckyplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.Song;
import it.developing.ico2k2.luckyplayer.tasks.MediaScanner;

import static it.developing.ico2k2.luckyplayer.Utils.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Utils.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Utils.KEY_NOTIFICATION_TINT;
import static it.developing.ico2k2.luckyplayer.Utils.KEY_REQUEST;
import static it.developing.ico2k2.luckyplayer.Utils.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Utils.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Utils.PREFERENCE_MAIN;
import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;

public class PlayService extends MediaBrowserServiceCompat
{
    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    private SharedPreferences prefs;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder playNotif;
    private Notification notification;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public static final String PACKAGE_AUTO = "com.google.android.projection.gearhead";

    public static final String EXTRA_COLUMNS = "columns";
    public static final String EXTRA_TYPES = "types";

    public static final int TYPE_INT =  0xA;
    public static final int TYPE_LONG =  0xB;
    public static final int TYPE_STRING =  0xC;

    public static final String ARG_AUTO = "auto";

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

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,int clientUid,Bundle rootHints)
    {
        Log.d(TAG_LOGS,"Root request from " + clientPackageName);
        String result = ID_ROOT;
        switch(clientPackageName)
        {
            case PACKAGE_AUTO:
            {
                result = ARG_AUTO + result;
                break;
            }
        }
        return new BrowserRoot(result,null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,@NonNull Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        onLoadChildren(parentId,result,new Bundle());
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,@NonNull Result<List<MediaBrowserCompat.MediaItem>> result,@NonNull Bundle options)
    {
        Log.d(TAG_LOGS,"Children request from " + parentId);
        result.detach();
        ArrayList<MediaBrowserCompat.MediaItem> items;
        int pageFrom = 0,pageTo = 0;
        if(options.containsKey(MediaBrowserCompat.EXTRA_PAGE) && options.containsKey(MediaBrowserCompat.EXTRA_PAGE_SIZE))
        {
            pageFrom = options.getInt(MediaBrowserCompat.EXTRA_PAGE);
            pageTo = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE);
            Log.d(TAG_LOGS,"Using paging mode, page: " + pageFrom + ", size: " + pageTo);
            pageFrom *= pageTo;
            pageTo += pageFrom;
            items = new ArrayList<>(pageTo - pageFrom);
        }
        else
            items = new ArrayList<>();
        MediaScanner scanner = new MediaScanner(getContentResolver());
        Log.d(TAG_LOGS,"MediaScanner created");
        switch(parentId.substring(parentId.indexOf("$"),parentId.indexOf("&") + 1))
        {
            case ID_ROOT:
            {
                String[] tabs = getResources().getStringArray(R.array.tabs);

                items.ensureCapacity(tabs.length);
                int a = 0;
                String arg = parentId.substring(0,parentId.indexOf("$"));
                for(String tab : tabs)
                {
                    items.add(new MediaBrowserCompat.MediaItem(
                            new MediaDescriptionCompat.Builder()
                            .setMediaId(arg + ID_TABS[a])
                            .setTitle(tab)
                            .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                    a++;
                }
                break;
            }
            case ID_SONGS:
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
                    columns.addAll(Arrays.asList(requestedColumns = options.getStringArray(EXTRA_COLUMNS)));
                    requestedTypes = options.getIntArray(EXTRA_TYPES);
                }
                Log.d(TAG_LOGS,"Added " + requestedColumns.length + " columns");
                MediaScanner.MediaScanResult results = scanner.subscan(SONGS_URI,
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
                            .setMediaId(row[results.getIndexFromColumnName(MediaStore.MediaColumns._ID)])
                            .setTitle(row[results.getIndexFromColumnName(MediaStore.MediaColumns.TITLE)])
                            .setSubtitle(Song.getSongDescription(
                                    row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ALBUM)],
                                    row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ARTIST)]))
                            .setExtras(extras).build(),
                            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                }
                results.release();
                break;
            }
            case ID_ALBUMS:
            {
                Log.d(TAG_LOGS,"Processing albums case");
                List<String> columns = new ArrayList<>(Arrays.asList(
                        MediaStore.Audio.AlbumColumns.ALBUM_ID,
                        MediaStore.MediaColumns.TITLE,
                        MediaStore.Audio.AlbumColumns.ARTIST));
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
                            .setMediaId(row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ALBUM_ID)])
                            .setTitle(row[results.getIndexFromColumnName(MediaStore.MediaColumns.TITLE)])
                            .setSubtitle(row[results.getIndexFromColumnName(MediaStore.Audio.AlbumColumns.ARTIST)])
                            .setExtras(extras).build(),
                            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                }
                results.release();
                break;
            }
            case ID_ARTISTS:
            {
                /*scanner.scan(new String[] {
                                MediaStore.Audio.AlbumColumns.ARTIST_ID},
                        null,null,MediaStore.Audio.AlbumColumns.ARTIST + " ASC");
                break;*/
            }
            case ID_GENRES:
            {
                /*scanner.scan(new String[] {
                                MediaStore.Audio.GenresColumns.NAME},
                        null,null,MediaStore.Audio.GenresColumns.NAME + " ASC");
                break;*/
            }
        }
        Log.d(TAG_LOGS,items.size() + " children found");
        Log.d(TAG_LOGS,"Packet (items), from " + pageFrom + " to " + pageTo);
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
            MediaScanner scanner = new MediaScanner(getContentResolver());
            MediaScanner.MediaScanResult result = scanner.subscan(SONGS_URI,1,new String[]{
                            MediaStore.MediaColumns.TITLE,
                            MediaStore.Audio.AlbumColumns.ALBUM,
                            MediaStore.Audio.AlbumColumns.ARTIST,
                            MediaStore.MediaColumns.DURATION,},
                    MediaStore.MediaColumns._ID + "=" + originalMediaId,null);
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            Long.parseLong(result.getCell(
                                    MediaStore.MediaColumns.DURATION,0)))
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                            result.getCell(MediaStore.MediaColumns.TITLE,0))
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                            Song.getSongDescription(
                                    result.getCell(MediaStore.Audio.AlbumColumns.ALBUM,0),
                                    result.getCell(MediaStore.Audio.AlbumColumns.ARTIST,0)))
                    .build());
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
                startForeground(NOTIFICATION_STATUS,playNotif.build());
                if(thread != null)
                    thread.interrupt();
                thread = new Thread(new Runnable(){
                    @Override
                    public void run(){
                        while(player.isPlaying())
                        {
                            try
                            {
                                Thread.sleep(250);
                                updateState();
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
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
            final String subId = mediaId.substring(index + 1);
            Log.d(TAG_LOGS,"Trying to play " + subId + " with index " + index);
            onStop();
            player = new MediaPlayer();
            try
            {
                index = Integer.parseInt(mediaId.substring(0,mediaId.indexOf(";")));
                ParcelFileDescriptor file = getBaseContext().getContentResolver().openFileDescriptor(Uri.withAppendedPath(
                        SONGS_URI[Integer.parseInt(mediaId.substring(0,index))],
                        subId),"r");
                player.setDataSource(file.getFileDescriptor());
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mp){
                        playNotif.setContentTitle(subId);
                        onPlay();
                        updateMetadata(subId);
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
                startForeground(NOTIFICATION_STATUS,playNotif.build());
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
                e.printStackTrace();
            }
            startForeground(NOTIFICATION_STATUS,notification);
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

    @Override
    public void onCreate()
    {
        super.onCreate();
        prefs = getSharedPreferences(PREFERENCE_MAIN,MODE_PRIVATE);
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
                .setColor(prefs.getInt(KEY_NOTIFICATION_TINT,0))
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
                .addAction(new NotificationCompat.Action(
                        android.R.drawable.ic_media_play, getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP)));
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
                stopForeground(true);
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
