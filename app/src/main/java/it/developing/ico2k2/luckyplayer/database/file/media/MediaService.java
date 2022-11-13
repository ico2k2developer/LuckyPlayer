package it.developing.ico2k2.luckyplayer.database.file.media;

import static it.developing.ico2k2.luckyplayer.Notification.NOTIFICATION_SCAN;
import static it.developing.ico2k2.luckyplayer.database.file.media.BaseMedia.VOLUMES;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;


import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import it.developing.ico2k2.luckyplayer.AsyncTask;
import it.developing.ico2k2.luckyplayer.NotificationChannelsManager;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;
import it.developing.ico2k2.luckyplayer.database.Database;
import it.developing.ico2k2.luckyplayer.preference.PreferenceManager;
import it.developing.ico2k2.luckyplayer.preference.Settings;

public class MediaService extends MediaBrowserServiceCompat
{
    private static final String LOG = MediaService.class.getSimpleName();

    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaScan scanner;
    private boolean scanning = false;
    private PreferenceManager prefs;
    private NotificationManagerCompat notificationManager;

    @Override
    public void onCreate()
    {
        super.onCreate();

        prefs = Settings.getInstance(MediaService.this);

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(this,LOG);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(new MediaSessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        notificationManager = NotificationManagerCompat.from(this);

        scanner = new MediaScan(MediaService.this,Database.getInstance(
                MediaService.this,MediaDatabase.class,Database.DATABASE_SONGS).mediaDao());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            scanner.setQuerySettings(new MediaScan.QuerySettings(
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_MUSIC,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_RINGTONE,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_NOTIFICATION,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_PODCAST,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_ALARM,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_AUDIOBOOK,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_RECORDING,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_OTHER,true)
            ));
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            scanner.setQuerySettings(new MediaScan.QuerySettings(
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_MUSIC,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_RINGTONE,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_NOTIFICATION,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_PODCAST,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_ALARM,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_AUDIOBOOK,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_OTHER,true)
            ));
        }
        else
        {
            scanner.setQuerySettings(new MediaScan.QuerySettings(
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_MUSIC,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_RINGTONE,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_NOTIFICATION,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_PODCAST,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_ALARM,true),
                    prefs.getBoolean(Settings.KEY_BOOLEAN_QUERY_OTHER,true)
            ));
        }

        Log.d(LOG,"Service created");
    }

    private static class Progress
    {
        private int progress,total;

        private Progress()
        {
            progress = 0;
            total = 0;
        }

        private boolean ready()
        {
            return (progress | total) != 0;
        }

        private int getProgress()
        {
            return progress;
        }

        private int getTotal()
        {
            return total;
        }

        private void setProgress(int progress)
        {
            this.progress = progress;
        }

        private void setTotal(int total)
        {
            this.total = total;
        }
    }

    private boolean scan(boolean forceScan)
    {
        Log.d(LOG,"Requested scan: forceScan " + forceScan);
        if(scanning)
            return false;
        if(!forceScan)
            forceScan = scanner.isScanNeeded();
        if(forceScan)
        {
            Log.d(LOG,"Proceeding to scan media");
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                    NotificationChannelsManager.CHANNEL_INFO.getId())
                    .setColor(getResources().getColor(R.color.notification_tint))
                    .setSmallIcon(R.drawable.ic_notification_scan)
                    .setContentTitle(getString(R.string.notification_scan_title))
                    .setOngoing(true)
                    .setProgress(1,0,true)
                    .setContentIntent(PendingIntent.getActivity(this,0,
                            new Intent(this, MainActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT));
            final Progress progress = new Progress();
            final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run()
                {
                    if(progress.ready())
                    {
                        builder.setProgress(progress.getTotal(),progress.getProgress(),false)
                                .setContentText(getString(R.string.notification_scan_text,
                                        progress.getProgress(),progress.getTotal()));
                        notificationManager.notify(NOTIFICATION_SCAN,builder.build());
                        Log.d(LOG,"Updated notification " + progress.getProgress() + " of " +
                                progress.getTotal());
                    }
                }
            },0,500, TimeUnit.MILLISECONDS);
            scanner.scan(VOLUMES, new AsyncTask.OnStart() {
                @Override
                public void onStart() {
                    scanning = true;
                    Log.d(LOG,"Scan started");
                }
            }, new MediaScan.OnScanProgress() {
                @Override
                public void onProgress(int completedOfTotal, int total) {
                    progress.setProgress(completedOfTotal);
                    progress.setTotal(total);

                }
            }, new AsyncTask.OnFinish<Long>() {
                @Override
                public void onComplete(@Nullable Long result) {
                    executor.shutdown();
                    try
                    {
                        executor.awaitTermination(1500,TimeUnit.MILLISECONDS);
                    }
                    catch (InterruptedException ignored) {}
                    scanning = false;
                    ServiceCompat.stopForeground(MediaService.this,
                            ServiceCompat.STOP_FOREGROUND_DETACH);
                    notificationManager.cancel(NOTIFICATION_SCAN);
                    Log.d(LOG,"Scan ended: found " + result + " items");
                }
            });
        }
        return forceScan;
    }

    public static final String ROOT = "root/";

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints)
    {
        return new BrowserRoot(ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result)
    {
        Log.d(LOG,"Children request has no options Bundle");
        onLoadChildren(parentMediaId,result,new Bundle());
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result,
                               @NonNull final Bundle options)
    {
        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root men:
        if(ROOT.equals(parentMediaId))
        {

        }
        result.sendResult(mediaItems);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        Log.d(LOG,"Start command received");
        scan(true);
        MediaButtonReceiver.handleIntent(mediaSession,intent);
        return START_NOT_STICKY;
    }

    private static class MediaSessionCallback extends MediaSessionCompat.Callback
    {

        /**
         * Override to handle requests to prepare playback. Override {@link #onPlay} to handle
         * requests for starting playback.
         */
        public void onPrepare() {
        }

        /**
         * Override to handle requests to prepare for playing a specific mediaId that was provided
         * by your app. Override {@link #onPlayFromMediaId} to handle requests for starting
         * playback.
         */
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
        }

        /**
         * Override to handle requests to prepare playback from a search query. An
         * empty query indicates that the app may prepare any music. The
         * implementation should attempt to make a smart choice about what to play.
         * Override {@link #onPlayFromSearch} to handle requests
         * for starting playback.
         */
        public void onPrepareFromSearch(String query, Bundle extras) {
        }

        /**
         * Override to handle requests to prepare a specific media item represented by a URI.
         * Override {@link #onPlayFromUri} to handle requests
         * for starting playback.
         */
        public void onPrepareFromUri(Uri uri, Bundle extras) {
        }

        /**
         * Override to handle requests to begin playback.
         */
        public void onPlay() {
        }

        /**
         * Override to handle requests to play a specific mediaId that was
         * provided by your app.
         */
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
        }

        /**
         * Override to handle requests to begin playback from a search query. An
         * empty query indicates that the app may play any music. The
         * implementation should attempt to make a smart choice about what to
         * play.
         */
        public void onPlayFromSearch(String query, Bundle extras) {
        }

        /**
         * Override to handle requests to play a specific media item represented by a URI.
         */
        public void onPlayFromUri(Uri uri, Bundle extras) {
        }

        /**
         * Override to handle requests to play an item with a given id from the
         * play queue.
         */
        public void onSkipToQueueItem(long id) {
        }

        /**
         * Override to handle requests to pause playback.
         */
        public void onPause() {
        }

        /**
         * Override to handle requests to skip to the next media item.
         */
        public void onSkipToNext() {
        }

        /**
         * Override to handle requests to skip to the previous media item.
         */
        public void onSkipToPrevious() {
        }

        /**
         * Override to handle requests to fast forward.
         */
        public void onFastForward() {
        }

        /**
         * Override to handle requests to rewind.
         */
        public void onRewind() {
        }

        /**
         * Override to handle requests to stop playback.
         */
        public void onStop() {
        }

        /**
         * Override to handle requests to seek to a specific position in ms.
         *
         * @param pos New position to move to, in milliseconds.
         */
        public void onSeekTo(long pos) {
        }

        /**
         * Override to handle the item being rated.
         *
         * @param rating The rating being set.
         */
        public void onSetRating(RatingCompat rating) {
        }

        /**
         * Override to handle the item being rated.
         *
         * @param rating The rating being set.
         * @param extras The extras can include information about the media item being rated.
         */
        public void onSetRating(RatingCompat rating, Bundle extras) {
        }
    }
}
