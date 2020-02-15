package it.developing.ico2k2.luckyplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.tasks.MediaScanner;

import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_NOTIFICATION_TINT;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_REQUEST;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SONGLIST_LAST_SIZE;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SYSTEM_MEDIA;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Keys.PREFERENCE_MAIN;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.Song;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToAlbums;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToArtists;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToYears;

public class PlayService extends MediaBrowserServiceCompat
{
    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    private SharedPreferences prefs;
    private ArrayList<SongsAdapter.Song> songs;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder playNotif;
    private Notification notification;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;

    public static final String PACKAGE_AUTO = "com.google.android.projection.gearhead";

    public static final String ARG_AUTO = "auto";

    public static final String ID_ROOT = "$root&";

    public static final String ID_SONGS = "$songs&";
    public static final String ID_ALBUMS = "$albums&";
    public static final String ID_ARTISTS = "$artists&";
    public static final String ID_YEARS = "$years&";

    public static final String[] ID_TABS =
    {
        ID_SONGS,
        ID_ALBUMS,
        ID_ARTISTS,
        ID_YEARS,
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
        Log.d(TAG_LOGS,"Children request from " + parentId + ", songs size: " + songs.size());
        result.detach();
        ArrayList<MediaBrowserCompat.MediaItem> items;
        int page = 0,pageSize = 0;
        if(options.containsKey(MediaBrowserCompat.EXTRA_PAGE) && options.containsKey(MediaBrowserCompat.EXTRA_PAGE_SIZE))
        {
            page = options.getInt(MediaBrowserCompat.EXTRA_PAGE);
            pageSize = options.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE);
            items = new ArrayList<>(pageSize);
            Log.d(TAG_LOGS,"Using paging mode, page number: " + page + ", page size: " + pageSize);
        }
        else
            items = new ArrayList<>();
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
                if(parentId.substring(0,parentId.indexOf("$")).contains(ARG_AUTO))
                {
                    if(parentId.charAt(parentId.length() - 1) == ':')
                    {
                        Log.d(TAG_LOGS,"Songs mode 1");
                        char first = parentId.charAt(parentId.length() - 2);
                        for(Song song : songs)
                        {
                            if(song.getTitle().toString().toUpperCase().charAt(0) == first)
                                items.add(song.toMediaItem());
                                /*items.add(new MediaBrowserCompat.MediaItem(
                                        new MediaDescriptionCompat.Builder()
                                                .setMediaId(song.getMediaId())
                                                .setTitle(song.getTitle())
                                                .build(),MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));*/
                        }
                    }
                    else
                    {
                        Log.d(TAG_LOGS,"Songs mode 1");


                        ArrayList<Character> tmp = new ArrayList<>();
                        char last;
                        for(Song song : songs)
                        {
                            if(!tmp.contains(last = song.getTitle().toString().toUpperCase().charAt(0)))
                            {
                                tmp.add(last);
                                items.add(song.toMediaItem());
                            }
                        }
                        tmp.clear();
                        tmp.trimToSize();
                    }
                    Collections.sort(items,new Comparator<MediaBrowserCompat.MediaItem>()
                    {
                        @Override
                        public int compare(MediaBrowserCompat.MediaItem o1,MediaBrowserCompat.MediaItem o2){
                            return o1.getDescription().getTitle().charAt(0) - o2.getDescription().getTitle().charAt(0);
                        }
                    });
                }
                else
                {
                    Log.d(TAG_LOGS,"Songs mode 3");
                    int start,end;
                    if(pageSize != 0)
                    {
                        start = pageSize * page;
                        end = start + pageSize;
                        if(end > songs.size())
                            end = songs.size();
                    }
                    else
                    {
                        start = 0;
                        end = songs.size();
                    }
                    if(start < end)
                    {
                        for(Song song : songs.subList(start,end))
                            items.add(song.toMediaItem());
                    }
                    pageSize = 0;
                    Log.d(TAG_LOGS,"Packet (songs), from " + start + " to " + end);
                }
                break;
            }
            case ID_ALBUMS:
            {
                ArrayList<Integer> indexes = new ArrayList<>();
                trimToAlbums(songs,indexes);
                items.ensureCapacity(indexes.size());
                String album;
                for(int index : indexes)
                    items.add(new MediaBrowserCompat.MediaItem(
                            new MediaDescriptionCompat.Builder()
                            .setTitle(album = songs.get(index).getAlbum())
                            .setMediaId(parentId + album)
                            .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                break;
            }
            case ID_ARTISTS:
            {
                ArrayList<Integer> indexes = new ArrayList<>();
                trimToArtists(songs,indexes);
                items.ensureCapacity(indexes.size());
                String artist;
                for(int index : indexes)
                    items.add(new MediaBrowserCompat.MediaItem(
                            new MediaDescriptionCompat.Builder()
                                    .setTitle(artist = songs.get(index).getArtist())
                                    .setMediaId(parentId + artist)
                                    .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                break;
            }
            case ID_YEARS:
            {
                ArrayList<Integer> indexes = new ArrayList<>();
                trimToYears(songs,indexes);
                items.ensureCapacity(indexes.size());
                int year;
                for(int index : indexes)
                    items.add(new MediaBrowserCompat.MediaItem(
                            new MediaDescriptionCompat.Builder()
                                    .setTitle(Integer.toString(year = songs.get(index).getYear()))
                                    .setMediaId(parentId + year)
                                    .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                break;
            }
        }
        Log.d(TAG_LOGS,items.size() + " children found");
        if(pageSize == 0)
            result.sendResult(items);
        else
        {
            int from = page * pageSize;
            int to = page * pageSize + pageSize;
            if(to > items.size())
                to = items.size();
            if(from <= to)
                result.sendResult(items.subList(from,to));
            else
                result.sendResult(null);
            Log.d(TAG_LOGS,"Packet (items), from " + from + " to " + to);
        }
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

        private void updateMetadata(Song song)
        {
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,(long)player.getDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,song.getTitle().toString())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,song.getSongDescription())
                    .build());
        }

        @Override
        public void onPlay()
        {
            try
            {
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
        public void onPlayFromMediaId(String id,Bundle extras)
        {
            int mediaId = Integer.parseInt(id);
            if(player != null)
            {
                player.stop();
                player.release();
            }
            player = new MediaPlayer();
            try
            {
                player.setDataSource(mediaId);
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mp){
                        playNotif.setContentTitle(mediaId);
                        onPlay();
                        updateMetadata();
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp){
                        onPause();
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
        if(prefs.contains(KEY_SONGLIST_LAST_SIZE))
            songs = new ArrayList<>(prefs.getInt(KEY_SONGLIST_LAST_SIZE,100));
        else
            songs = new ArrayList<>();
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
        scan();
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
                scan();
                break;
            }
        }
    }

    public void scan()
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
            public void onScanResult(SongsAdapter.Song song){
                songs.add(song);
            }

            @Override
            public void onScanStop(){
                Collections.sort(songs,new Comparator<Song>(){
                    @Override
                    public int compare(Song o1,Song o2){
                        return o1.getTitle().toString().compareTo(o2.getTitle().toString());
                    }
                });
                manager.cancel(NOTIFICATION_SCAN);
                prefs.edit().putInt(KEY_SONGLIST_LAST_SIZE,songs.size()).apply();
                Log.d(TAG_LOGS,"Scan finished, items: " + songs.size());
            }
        });
        scanner.setIncludeMediaFiles(prefs.getBoolean(KEY_SYSTEM_MEDIA,false));
        scanner.startScan();
    }

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
