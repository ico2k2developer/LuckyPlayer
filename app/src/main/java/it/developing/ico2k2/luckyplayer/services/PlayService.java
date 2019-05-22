package it.developing.ico2k2.luckyplayer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import it.developing.ico2k2.luckyplayer.DataManager;
import it.developing.ico2k2.luckyplayer.LuckyPlayer;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;
import it.developing.ico2k2.luckyplayer.tasks.MediaScanner;

import static it.developing.ico2k2.luckyplayer.Keys.*;

public class PlayService extends Service
{
    public static final int NOTIFICATION_SCAN = 0x10;
    public static final int NOTIFICATION_STATUS = 0x11;

    protected LuckyPlayer app;
    protected DataManager dataManager;
    protected ArrayList<SongsAdapter.Song> songs;
    protected NotificationManagerCompat manager;
    protected Messenger replyMessenger,messenger = new Messenger(new IncomingHandler(this));

    static class IncomingHandler extends Handler
    {
        WeakReference<PlayService> reference;

        public IncomingHandler(PlayService service)
        {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message message)
        {
            PlayService service = reference.get();
            //service.elaborateRequest(message);
        }
    }

    public IBinder onBind(Intent intent)
    {
        return messenger.getBinder();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        app = (LuckyPlayer)getApplication();
        dataManager = app.getDataManager();
        manager = NotificationManagerCompat.from(this);
        if(dataManager.contains(KEY_SONGLIST_LAST_SIZE))
            songs = new ArrayList<>(dataManager.getInt(KEY_SONGLIST_LAST_SIZE));
        else
            songs = new ArrayList<>();
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_STATUS)
                .setColor(dataManager.getInt(KEY_NOTIFICATION_TINT))
                .setSmallIcon(R.drawable.ic_player_notification)
                .setContentTitle(getString(R.string.player_notification_title))
                .setContentText(getString(R.string.player_notification_text))
                .setOngoing(true)
                .build();
        manager.notify(NOTIFICATION_STATUS,notification);
        scan();
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
                songs.clear();
                manager.notify(NOTIFICATION_SCAN,notification);
            }

            @Override
            public void onScanResult(SongsAdapter.Song song){
                songs.add(song);
            }

            @Override
            public void onScanStop(){
                manager.cancel(NOTIFICATION_SCAN);
            }
        });
        scanner.startScan();
    }

    public void onDestroy()
    {
        super.onDestroy();
        manager.cancel(NOTIFICATION_STATUS);
    }
}
