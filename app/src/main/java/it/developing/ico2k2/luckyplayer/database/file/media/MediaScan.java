package it.developing.ico2k2.luckyplayer.database.file.media;

import static android.provider.MediaStore.Audio.AudioColumns.*;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import it.developing.ico2k2.luckyplayer.AsyncTask;

public class MediaScan
{
    private static final String LOG = MediaScan.class.getSimpleName();

    public static class QuerySettings
    {
        private final boolean alarm,music,notification,podcast,ringtone,other;
        private final Boolean audiobook,recording;

        @RequiresApi(Build.VERSION_CODES.S)
        public QuerySettings(boolean music,boolean ringtone,boolean notification,boolean podcast,
                             boolean alarm,boolean audiobook,boolean recording,boolean other)
        {
            this.music = music;
            this.ringtone = ringtone;
            this.notification = notification;
            this.podcast = podcast;
            this.alarm = alarm;
            this.other = other;
            this.audiobook = audiobook;
            this.recording = recording;
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        public QuerySettings(boolean music,boolean ringtone,boolean notification,boolean podcast,
                             boolean alarm,boolean audiobook,boolean other)
        {
            this.music = music;
            this.ringtone = ringtone;
            this.notification = notification;
            this.podcast = podcast;
            this.alarm = alarm;
            this.other = other;
            this.audiobook = audiobook;
            this.recording = null;
        }

        @RequiresApi(Build.VERSION_CODES.FROYO)
        public QuerySettings(boolean music,boolean ringtone,boolean notification,boolean podcast,
                             boolean alarm,boolean other)
        {
            this.music = music;
            this.ringtone = ringtone;
            this.notification = notification;
            this.podcast = podcast;
            this.alarm = alarm;
            this.other = other;
            this.audiobook = null;
            this.recording = null;
        }

        public QuerySettings()
        {
            this(false,false,false,false,false,false);
        }

        private boolean areAllTrue()
        {
            boolean result = music && ringtone && notification && podcast && alarm && other;
            if(result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                result = Boolean.TRUE.equals(audiobook);
            if(result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                result = Boolean.TRUE.equals(recording);
            return result;
        }

        private boolean areAllFalse()
        {
            boolean result = music || ringtone || notification || podcast || alarm || other;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                result = result || Boolean.TRUE.equals(audiobook);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                result = result || Boolean.TRUE.equals(recording);
            return !result;
        }

        public boolean getMusic(){
            return music;
        }

        public boolean getRingtone(){
            return ringtone;
        }

        public boolean getNotification(){
            return notification;
        }

        @RequiresApi(Build.VERSION_CODES.FROYO)
        public boolean getPodcast(){
            return podcast;
        }

        public boolean getAlarm(){
            return alarm;
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        public boolean getAudiobook(){
            return Boolean.TRUE.equals(audiobook);
        }

        @RequiresApi(Build.VERSION_CODES.S)
        public boolean getRecording(){
            return Boolean.TRUE.equals(recording);
        }

        public boolean getOther(){
            return other;
        }

        @Nullable
        private String build()
        {
            String result = null;
            if(!areAllFalse())
            {
                final StringBuilder builder = new StringBuilder(" ");
                final String andOr,comparator;
                if(getOther())
                {
                    comparator = " == 0";
                    andOr = " AND ";
                }
                else
                {
                    comparator = " != 0";
                    andOr = " OR ";
                }
                if(getMusic() ^ getOther())
                    builder.append(IS_MUSIC).append(comparator);
                if(getRingtone() ^ getOther())
                    builder.append(andOr).append(IS_RINGTONE).append(comparator);
                if(getNotification() ^ getOther())
                    builder.append(andOr).append(IS_NOTIFICATION).append(comparator);
                if(getPodcast() ^ getOther())
                    builder.append(andOr).append(IS_PODCAST).append(comparator);
                if(getAlarm() ^ getOther())
                    builder.append(andOr).append(IS_ALARM).append(comparator);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    if(getAudiobook() ^ getOther())
                        builder.append(andOr).append(IS_AUDIOBOOK).append(comparator);
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    if(getRecording() ^ getOther())
                        builder.append(andOr).append(IS_RECORDING).append(comparator);
                }
                result = builder.toString();
            }
            return result;
        }
    }

    private final Context context;
    private final MediaDatabase database;
    private QuerySettings query;

    public MediaScan(Context context,MediaDatabase database)
    {
        this.context = context;
        this.database = database;
    }

    public void setQuerySettings(QuerySettings query)
    {
        this.query = query;
    }

    public interface OnScanProgress
    {
        void onProgress(int completedOfTotal,int total);
    }

    //TODO: Implement actual media scanning

    public void scan(Uri[] uris, @Nullable AsyncTask.OnStart start, @Nullable OnScanProgress progress, @Nullable AsyncTask.OnFinish<Long> finish)
    {
        final MediaDao dao = database.mediaDao();
        final ContentResolver resolver = context.getContentResolver();
        new AsyncTask<int[],Long>().executeProgressAsync(new AsyncTask.OnStart() {
            @Override
            public void onStart() {
                if (start != null)
                    start.onStart();
            }
        }, new AsyncTask.OnCall<int[], Long>() {
            @Override
            public Long call(@NonNull AsyncTask.PublishProgress<int[]> callback) throws Exception
            {
                long count = 0;
                return count;
            }
        }, new AsyncTask.OnProgress<int[]>() {
            @Override
            public void onProgress(@NonNull int[] result)
            {
                progress.onProgress(0,0);
            }
        }, new AsyncTask.OnFinish<Long>() {
            @Override
            public void onComplete(@Nullable Long result)
            {
                if (finish != null)
                    finish.onComplete(result);
            }
        });
    }

}
