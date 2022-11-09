package it.developing.ico2k2.luckyplayer.database.file.media;

import static android.provider.MediaStore.Audio.AudioColumns.*;

import static it.developing.ico2k2.luckyplayer.preference.Settings.KEY_MEDIA_UPDATE;
import static it.developing.ico2k2.luckyplayer.preference.Settings.PREFERENCE_SETTINGS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.AsyncTask;
import it.developing.ico2k2.luckyplayer.preference.PreferenceManager;
import it.developing.ico2k2.luckyplayer.R;

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
            boolean result = music || ringtone || notification || podcast || alarm |  | other;
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
    private String query;

    public MediaScan(Context context,MediaDatabase database)
    {
        this.context = context;
        this.database = database;
    }

    public void setQuerySettings(QuerySettings query)
    {
        this.query = query.build();
    }

    public boolean isScanNeeded()
    {
        return !MediaStore.getVersion(context).equals(
                PreferenceManager.getInstance(context,PREFERENCE_SETTINGS)
                .getString(KEY_MEDIA_UPDATE,null));
    }

    public interface OnScanProgress
    {
        void onProgress(int completedOfTotal,int total);
    }

    private static List<String> getBaseColumns()
    {
        final List<String> result = new ArrayList<>();
        result.add(_ID);
        result.add(DATA);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
        {
            result.add(VOLUME_NAME);
            result.add(RELATIVE_PATH);
            result.add(DISPLAY_NAME);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                result.add(IS_FAVORITE);
        }
        return result;
    }

    private static Map<String,Integer> getColumnsIndexes(Cursor cursor,List<String> columns,
                                                  @Nullable Map<String,Integer> recycle)
    {
        if(recycle == null)
            recycle = new HashMap<>(columns.size());
        else
            recycle.clear();
        for(String column : columns)
            recycle.put(column,cursor.getColumnIndex(column));
        return recycle;
    }

    private static Map<String,Integer> getColumnsIndexes(Cursor cursor,List<String> columns)
    {
        return getColumnsIndexes(cursor,columns,null);
    }

    public void scan(Uri[] uris, @Nullable AsyncTask.OnStart start, @Nullable OnScanProgress progress, @Nullable AsyncTask.OnFinish<Long> finish)
    {
        final MediaDao dao = database.mediaDao();
        final ContentResolver resolver = context.getContentResolver();
        final List<String> columns = getBaseColumns();
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
                long progress,count = 0;
                Cursor cursor;
                int tableId;
                Map<String,Integer> columnsMap = null;
                for(tableId = 0; tableId < uris.length; tableId++)
                {
                    progress = 0;
                    Log.d(LOG,"Checking uri " + uris[tableId].toString());
                    cursor = ContentResolverCompat.query(resolver,uris[tableId],
                            columns.toArray(new String[0]), query,null,null,
                            null);
                    Log.d(LOG,cursor.getCount() + " elements in cursor");
                    columnsMap = getColumnsIndexes(cursor,columns,columnsMap);

                }

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
