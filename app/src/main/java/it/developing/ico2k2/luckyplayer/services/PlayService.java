package it.developing.ico2k2.luckyplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
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

import it.developing.ico2k2.luckyplayer.DataManager;
import it.developing.ico2k2.luckyplayer.LuckyPlayer;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.tasks.MediaScanner;

import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Keys.EXTRA_REQUEST;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_NOTIFICATION_TINT;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_SONGLIST_LAST_SIZE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_OFFLINE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_ONLINE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.Song;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToAlbums;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToArtists;
import static it.developing.ico2k2.luckyplayer.adapters.SongsAdapter.trimToYears;

public class PlayService extends MediaBrowserServiceCompat
{
    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    //public static final int SIZE_SONGS_PACKET = 150;

    private LuckyPlayer app;
    private DataManager dataManager;
    private ArrayList<SongsAdapter.Song> songs;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder playNotif;
    private Notification notification;
    //private Messenger myMessenger,itsMessenger;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private boolean online = false,scanning = false;

    /*static class IncomingHandler extends Handler
    {
        WeakReference<PlayService> reference;

        public IncomingHandler(PlayService service)
        {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message message)
        {
            if(reference.get() != null)
                reference.get().elaborateRequest(message);
        }
    }

    public IBinder onBind(Intent intent)
    {
        return myMessenger.getBinder();
    }*/

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
        Log.d(TAG_LOGS,"Children request from " + parentId);
        ArrayList<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
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
                        char first = parentId.charAt(parentId.length() - 2);
                        for(Song song : songs)
                        {
                            if(song.getTitle().toString().toUpperCase().charAt(0) == first)
                                items.add(new MediaBrowserCompat.MediaItem(
                                        new MediaDescriptionCompat.Builder()
                                                .setMediaId(song.getPath())
                                                .setTitle(song.getTitle())
                                                .build(),MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                        }
                    }
                    else
                    {
                        ArrayList<Character> tmp = new ArrayList<>();
                        char last;
                        for(Song song : songs)
                        {
                            if(!tmp.contains(last = song.getTitle().toString().toUpperCase().charAt(0)))
                            {
                                tmp.add(last);
                                items.add(new MediaBrowserCompat.MediaItem(
                                        new MediaDescriptionCompat.Builder()
                                                .setMediaId(parentId + last + ":")
                                                .setTitle(String.valueOf(last))
                                                .build(),MediaBrowserCompat.MediaItem.FLAG_BROWSABLE));
                            }
                        }
                        tmp.clear();
                        tmp.trimToSize();
                    }
                }
                Collections.sort(items,new Comparator<MediaBrowserCompat.MediaItem>()
                {
                    @Override
                    public int compare(MediaBrowserCompat.MediaItem o1,MediaBrowserCompat.MediaItem o2){
                        return o1.getDescription().getTitle().charAt(0) - o2.getDescription().getTitle().charAt(0);
                    }
                });
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
        result.sendResult(items);
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback{

        MediaPlayer player;

        @Override
        public void onPlay()
        {
            try
            {
                player.start();
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
            if(player != null)
                player.release();
            player = new MediaPlayer();
            try
            {
                player.setDataSource(mediaId);
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mp){
                        mp.start();
                        playNotif.setContentTitle(mediaId);
                        startForeground(NOTIFICATION_STATUS,playNotif.build());
                    }
                });
                player.prepareAsync();
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
        public void onCustomAction(String action,Bundle extras){
        }

        @Override
        public void onPlayFromSearch(final String query,final Bundle extras){
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        app = (LuckyPlayer)getApplication();
        dataManager = app.getDataManager();
        //myMessenger = new Messenger(new IncomingHandler(this));
        manager = NotificationManagerCompat.from(this);
        if(dataManager.contains(KEY_SONGLIST_LAST_SIZE))
            songs = new ArrayList<>(dataManager.getInt(KEY_SONGLIST_LAST_SIZE));
        else
            songs = new ArrayList<>();
        mediaSession = new MediaSessionCompat(this,TAG_LOGS);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(new PlayService.MediaSessionCallback());
        setSessionToken(mediaSession.getSessionToken());
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(this,PlayService.class);
        intent.putExtra(EXTRA_REQUEST,MESSAGE_DESTROY);
        PendingIntent action = PendingIntent.getService(this,MESSAGE_DESTROY,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pending = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(this,CHANNEL_ID_INFO)
                .setColor(dataManager.getInt(KEY_NOTIFICATION_TINT))
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

    public void scan()
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_INFO)
                .setColor(dataManager.getInt(KEY_NOTIFICATION_TINT))
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
                scanning = true;
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

                manager.cancel(NOTIFICATION_SCAN);
                scanning = false;
                //sendMessageToApplication(MESSAGE_SCAN_COMPLETED);
                Log.d(TAG_LOGS,"Scan finished, items: " + songs.size());
            }
        });
        scanner.startScan();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.d(TAG_LOGS,"Start command received");
        MediaButtonReceiver.handleIntent(mediaSession,intent);
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            if(extras.containsKey(EXTRA_REQUEST))
                elaborateRequest(extras.getInt(EXTRA_REQUEST),extras);
        }
        return super.onStartCommand(intent,flags,startId);
    }

    /*public void elaborateRequest(Message message)
    {
        Bundle data = message.peekData();
        if(data != null)
            data.setClassLoader(getClassLoader());
        switch(message.what)
        {
            case MESSAGE_BIND:
            {
                Messenger newMessenger = message.replyTo;
                if(newMessenger != null)
                    itsMessenger = newMessenger;
                break;
            }
        }
        elaborateRequest(message.what,data);
    }*/

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

    public void elaborateRequest(int request,Bundle extras)
    {
        Log.d(TAG_LOGS,"Request received in service: " + Integer.toHexString(request));
        switch(request)
        {
            case MESSAGE_ONLINE:
            {
                online = true;
                break;
            }
            case MESSAGE_OFFLINE:
            {
                online = false;
                break;
            }
            case MESSAGE_SCAN_REQUESTED:
            {
                scan();
                break;
            }
            case MESSAGE_DESTROY:
            {
                Message message = Message.obtain();
                message.what = request;
                //sendMessageToApplication(message,true);
                stopForeground(false);
                stopSelf();
                break;
            }
            /*case MESSAGE_SONG_REQUEST:
            {
                if(!scanning)
                {
                    RequestType requestType = RequestType.SONGS;
                    OrderType orderType = OrderType.NONE;
                    Message message = Message.obtain();
                    Bundle data = new Bundle();
                    String requestCode = null;
                    if(extras != null)
                    {
                        if(extras.containsKey(KEY_REQUEST))
                            requestType = RequestType.values()[extras.getInt(KEY_REQUEST)];
                        if(extras.containsKey(KEY_ORDER))
                            orderType = OrderType.values()[extras.getInt(KEY_ORDER)];
                        if(extras.containsKey(KEY_REQUEST_CODE))
                            requestCode = extras.getString(KEY_REQUEST_CODE);
                        else
                            Log.w(TAG_LOGS,"Missing request code!");
                    }
                    message.what = MESSAGE_SONG_PACKET;
                    data.putString(KEY_REQUEST_CODE,requestCode);
                    switch(requestType)
                    {
                        case SONGS:
                        {
                            Message message2 = Message.obtain();
                            message2.what = MESSAGE_SONG_START;
                            data.putInt(KEY_SIZE,songs.size());
                            message2.setData(data);
                            sendMessageToApplication(message2,true);
                            if(orderType == OrderType.ALPHABETICAL)
                            {
                                ArrayList<Integer> indexes = new ArrayList<>(songs.size());
                                int a;
                                for(a = 0; a < songs.size(); a++)
                                    indexes.add(a);
                                Collections.sort(indexes,new Comparator<Integer>(){
                                    @Override
                                    public int compare(Integer o1,Integer o2){
                                        return songs.get(o1).getTitle().compareTo(songs.get(o2).getTitle());
                                    }
                                });
                                sendSongList(message,requestCode,songs,indexes);
                                indexes.clear();
                                indexes.trimToSize();
                            }
                            else
                            {
                                sendSongList(message,requestCode,songs);
                            }
                            break;
                        }
                        case ALBUMS:
                        {
                            ArrayList<Integer> indexes = new ArrayList<>(songs.size());
                            trimToAlbums(songs,indexes);
                            indexes.trimToSize();
                            Message message2 = Message.obtain();
                            message2.what = MESSAGE_SONG_START;
                            data.putInt(KEY_SIZE,indexes.size());
                            message2.setData(data);
                            sendMessageToApplication(message2,true);
                            if(orderType == OrderType.ALPHABETICAL)
                            {
                                Collections.sort(indexes,new Comparator<Integer>(){
                                    @Override
                                    public int compare(Integer o1,Integer o2){
                                        return songs.get(o1).getAlbum().compareTo(songs.get(o2).getAlbum());
                                    }
                                });
                            }
                            sendSongList(message,requestCode,songs,indexes);
                            indexes.clear();
                            indexes.trimToSize();
                        }
                        case ARTISTS:
                        {

                            ArrayList<Integer> indexes = new ArrayList<>(songs.size());
                            trimToArtists(songs,indexes);
                            indexes.trimToSize();
                            Message message2 = Message.obtain();
                            message2.what = MESSAGE_SONG_START;
                            data.putInt(KEY_SIZE,indexes.size());
                            message2.setData(data);
                            sendMessageToApplication(message2,true);
                            if(orderType == OrderType.ALPHABETICAL)
                            {
                                Collections.sort(indexes,new Comparator<Integer>(){
                                    @Override
                                    public int compare(Integer o1,Integer o2){
                                        return songs.get(o1).getArtist().compareTo(songs.get(o2).getArtist());
                                    }
                                });
                            }
                            sendSongList(message,requestCode,songs,indexes);
                            indexes.clear();
                            indexes.trimToSize();
                        }
                    }
                    message.what = MESSAGE_SONG_END;
                    message.setData(null);
                    sendMessageToApplication(message,true);
                }
                break;
            }*/
        }
    }

    /*public void sendSongList(Message message,String requestCode,ArrayList<SongsAdapter.Song> list)
    {
        Bundle packet = new Bundle();
        int a = 0;
        ArrayList<SongsAdapter.Song> tmp = new ArrayList<>(SIZE_SONGS_PACKET);
        for(SongsAdapter.Song song : list)
        {
            if(a % SIZE_SONGS_PACKET == 0 && a != 0)
            {
                packet.putParcelableArrayList(KEY_SONGS,tmp);
                packet.putString(KEY_REQUEST_CODE,requestCode);
                message.setData(packet);
                sendMessageToApplication(message,false);
                tmp.clear();
                packet.clear();
            }
            tmp.add(song);
            a++;
        }
        if(tmp.size() > 0)
        {
            packet.putParcelableArrayList(KEY_SONGS,tmp);
            message.setData(packet);
            sendMessageToApplication(message,false);
        }
        tmp.clear();
        tmp.trimToSize();
    }

    public void sendSongList(Message message,String requestCode,ArrayList<SongsAdapter.Song> list,List<Integer> indexes)
    {
        Bundle packet = new Bundle();
        int a = 0;
        ArrayList<SongsAdapter.Song> tmp = new ArrayList<>(SIZE_SONGS_PACKET);
        for(int index : indexes)
        {
            if(a % SIZE_SONGS_PACKET == 0 && a != 0)
            {
                packet.putParcelableArrayList(KEY_SONGS,tmp);
                packet.putString(KEY_REQUEST_CODE,requestCode);
                message.setData(packet);
                sendMessageToApplication(message,false);
                tmp.clear();
                packet.clear();
            }
            tmp.add(list.get(index));
            a++;
        }
        if(tmp.size() > 0)
        {
            packet.putParcelableArrayList(KEY_SONGS,tmp);
            message.setData(packet);
            sendMessageToApplication(message,false);
        }
        tmp.clear();
        tmp.trimToSize();
    }*/

    /*protected void sendMessageToApplication(int what)
    {
        Message message = Message.obtain();
        message.what = what;
        sendMessageToApplication(message,true);
    }

    protected void sendMessageToApplication(Message message,boolean recycle)
    {
        if(itsMessenger != null)
        {
            try
            {
                itsMessenger.send(message);
                if(recycle)
                    message.recycle();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }*/

    public void onDestroy()
    {
        super.onDestroy();
        manager.cancel(NOTIFICATION_STATUS);
        Log.d(TAG_LOGS,"Service destroying");
    }
}
