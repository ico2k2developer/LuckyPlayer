package it.developing.ico2k2.luckyplayer.database.file.media;

import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.Audio.AudioColumns.*;

import static it.developing.ico2k2.luckyplayer.preference.Settings.KEY_LONG_MEDIA_COUNT;
import static it.developing.ico2k2.luckyplayer.preference.Settings.KEY_STRING_MEDIA_UPDATE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.AsyncTask;
import it.developing.ico2k2.luckyplayer.database.file.BaseFile;
import it.developing.ico2k2.luckyplayer.preference.PreferenceManager;
import it.developing.ico2k2.luckyplayer.preference.Settings;

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

        private boolean areAllTrue()
        {
            boolean result = music && ringtone && notification && podcast && alarm && other;
            if(result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                result = Boolean.TRUE.equals(audiobook);
                if(result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    result = Boolean.TRUE.equals(recording);
            }
            return result;
        }

        private boolean areAllFalse()
        {
            boolean result = music || ringtone || notification || podcast || alarm || other;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                result = result || Boolean.TRUE.equals(audiobook);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    result = result || Boolean.TRUE.equals(recording);
            }
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

        public boolean getOther()
        {
            return other;
        }

        @Nullable
        private String build()
        {
            String result = null;
            if(!areAllTrue() && !areAllFalse())
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

        @NonNull
        @Override
        public String toString()
        {
            return String.format("Music: %s, ringtone %s, notification: %s, podcast: %s, alarm %s, " +
                    "other: %s",
                    getMusic(),getRingtone(),getNotification(),getPodcast(),getAlarm(),getOther());
        }
    }

    private final Context context;
    private final MediaDao database;
    private final PreferenceManager prefs;
    private String query;

    public MediaScan(Context context,MediaDao database)
    {
        this.context = context;
        this.database = database;
        prefs = Settings.getInstance(context);
    }

    public void setQuerySettings(QuerySettings query)
    {
        this.query = query.build();
        Log.d(LOG,"New query: \"" + this.query  + "\" (null? " + (this.query == null) + ") from " + query.toString());
    }

    public boolean isScanNeeded()
    {
        return !getMediaStoreVersion().equals(getCachedVersion()) ||
                getCachedCount() == 0;
    }

    public long getCachedCount()
    {
        return prefs.getLong(KEY_LONG_MEDIA_COUNT,0);
    }

    @NonNull
    public String getMediaStoreVersion()
    {
        return MediaStore.getVersion(context);
    }

    @Nullable
    public String getCachedVersion()
    {
        return prefs.getString(KEY_STRING_MEDIA_UPDATE, null);
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

    public void scan(Uri[] uris, @Nullable AsyncTask.OnStart start, @Nullable OnScanProgress progress, @Nullable AsyncTask.OnFinish<Long> finish)
    {
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
                long count = 0,mediaId;
                Cursor cursor;
                short volumeId;
                int cursorCount,progress;
                Map<String,Integer> columnsMap = null;
                boolean fav;
                File file;
                for(volumeId = 0; volumeId < uris.length; volumeId++)
                {
                    progress = 0;
                    Log.d(LOG,"Checking uri " + (volumeId + 1) + " of " + uris.length + " " +
                            uris[volumeId].toString());
                    cursor = null;
                    try
                    {
                        cursor = ContentResolverCompat.query(resolver,uris[volumeId],
                                columns.toArray(new String[0]), query,null,null,
                                null);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    if(cursor == null)
                    {
                        Log.d(LOG,"Cursor is null, skipping uri");
                        continue;
                    }
                    cursorCount = cursor.getCount();
                    Log.d(LOG,cursorCount + " rows in cursor");
                    columnsMap = getColumnsIndexes(cursor,columns,columnsMap);
                    while(cursor.moveToNext())
                    {
                        mediaId = cursor.getLong(columnsMap.get(_ID));
                        file = new File(cursor.getString(columnsMap.get(DATA)));
                        if(!file.exists() && Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
                        {
                            String volume = cursor.getString(columnsMap.get(VOLUME_NAME));
                            String path = cursor.getString(columnsMap.get(RELATIVE_PATH));
                            StringBuilder result = new StringBuilder();
                            switch(volume)
                            {
                                case MediaStore.VOLUME_EXTERNAL_PRIMARY:
                                {
                                    result.append((Build.VERSION.SDK_INT < Build.VERSION_CODES.R ?
                                            getExternalStorageDirectory() : ((StorageManager)
                                            context.getSystemService(Context.STORAGE_SERVICE))
                                            .getPrimaryStorageVolume().getDirectory())
                                            .getAbsolutePath());
                                    break;
                                }
                                case MediaStore.VOLUME_INTERNAL:
                                {
                                    result.append("/system/media/audio/ui");
                                    break;
                                }
                                default:
                                {
                                    result.append("/storage/");
                                    result.append(volume.toUpperCase());
                                }
                            }
                            result.append('/');
                            if(path != null)
                                result.append(path);
                            result.append(cursor.getString(columnsMap.get(DISPLAY_NAME)));
                            file = new File(result.toString());
                        }
                        if(file.exists())
                        {
                            fav = false;
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                                fav = cursor.getInt(columnsMap.get(IS_FAVORITE)) == 1;
                            try
                            {
                                if(database.contains(volumeId,mediaId))
                                {
                                    if(database.loadLastModified(volumeId,mediaId) !=
                                            file.lastModified())
                                    {
                                        database.update(Media.loadMedia(file,fav,volumeId,mediaId));
                                        Log.d(LOG,"Updated " + file.getAbsolutePath());
                                    }
                                    else
                                        Log.d(LOG,"Skipped " + file.getAbsolutePath());
                                }
                                else
                                {
                                    database.put(Media.loadMedia(file,fav,volumeId,mediaId));
                                    Log.d(LOG,"Put " + file.getAbsolutePath());
                                }
                                count++;
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            callback.publishProgress(new int[]{++progress,cursorCount});
                        }
                    }
                    cursor.close();
                    Log.d(LOG, "Jumping to the next uri, " + count + " items found until now");
                }
                return count;
            }
        }, new AsyncTask.OnProgress<int[]>() {
            @Override
            public void onProgress(@NonNull int[] result)
            {
                progress.onProgress(result[0],result[1]);
            }
        }, new AsyncTask.OnFinish<Long>() {
            @Override
            public void onComplete(@Nullable Long result)
            {
                Log.d(LOG,"MediaStore version was " + prefs.getString(KEY_STRING_MEDIA_UPDATE) +
                        " now is " + MediaStore.getVersion(context));
                prefs.edit()
                        .putString(KEY_STRING_MEDIA_UPDATE,MediaStore.getVersion(context))
                        .putLong(KEY_LONG_MEDIA_COUNT,result == null ? 0 : result)
                        .apply();
                if (finish != null)
                    finish.onComplete(result);
            }
        });
    }

}
