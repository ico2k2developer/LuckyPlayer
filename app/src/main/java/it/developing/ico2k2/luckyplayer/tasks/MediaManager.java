package it.developing.ico2k2.luckyplayer.tasks;

import static android.provider.MediaStore.Audio.AudioColumns.IS_ALARM;
import static android.provider.MediaStore.Audio.AudioColumns.IS_AUDIOBOOK;
import static android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC;
import static android.provider.MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
import static android.provider.MediaStore.Audio.AudioColumns.IS_PODCAST;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RINGTONE;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.database.data.File;
import it.developing.ico2k2.luckyplayer.database.data.FileDao;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailedDao;

public class MediaManager
{
    private static final String TAG = MediaManager.class.getSimpleName();

    private static final String COLUMN = MediaStore.MediaColumns._ID;

    public static class QuerySettings
    {
        private final boolean music,ringtone,notification,podcast,alarm,other;
        private final Boolean audiobook,recording;

        @RequiresApi(31)
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
                result = audiobook;
            if(result && Build.VERSION.SDK_INT >= 31)
                result = recording;
            return result;
        }

        private boolean areAllFalse()
        {
            boolean result = music || ringtone || notification || podcast || alarm || other;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                result = result || audiobook;
            if(Build.VERSION.SDK_INT >= 31)
                result = result || recording;
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
            return audiobook;
        }

        @RequiresApi(31)
        public boolean getRecording(){
            return recording;
        }

        public boolean getOther(){
            return other;
        }
    }

    private final ContentResolver resolver;
    private final SongDetailedDao songsDetailed;
    private final FileDao songsFiles;
    private final String query;
    private final boolean scanning;

    public MediaManager(QuerySettings settings, ContentResolver resolver, FileDao files, SongDetailedDao songs)
    {
        this.resolver = resolver;
        songsFiles = files;
        songsDetailed = songs;
        query = buildQuerySelectionString(settings);
        scanning = false;
    }

    @Nullable
    private String buildQuerySelectionString(QuerySettings settings)
    {
        String result = null;
        if(!settings.areAllFalse())
        {
            final StringBuilder builder = new StringBuilder();
            final String andOr,comparator;
            if(settings.getOther())
            {
                comparator = " == 0";
                andOr = " AND ";
            }
            else
            {
                comparator = " != 0";
                andOr = " OR ";
            }
            if(settings.getMusic() ^ settings.getOther())
                builder.append(IS_MUSIC).append(comparator).append(andOr);
            if(settings.getRingtone() ^ settings.getOther())
                builder.append(IS_RINGTONE).append(comparator).append(andOr);
            if(settings.getNotification() ^ settings.getOther())
                builder.append(IS_NOTIFICATION).append(comparator).append(andOr);
            if(settings.getPodcast() ^ settings.getOther())
                builder.append(IS_PODCAST).append(comparator).append(andOr);
            if(settings.getAlarm() ^ settings.getOther())
                builder.append(IS_ALARM).append(comparator);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                if(settings.getAudiobook() ^ settings.getOther())
                    builder.append(andOr).append(IS_AUDIOBOOK).append(comparator);
            }
            if(Build.VERSION.SDK_INT >= 31)
            {
                if(settings.getRecording() ^ settings.getOther())
                    builder.append(andOr).append("is_recording").append(comparator);
            }
            result = builder.toString();
        }
        return result;
    }

    public long scan(Uri[] uris)
    {
        Cursor cursor = null;
        int columnIndex;
        long count = 0;
        //dao.deleteAll();
        File file,loaded;
        for(Uri uri : uris)
        {
            Log.d(TAG,"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,new String[]{COLUMN},query,null,null,null);
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
            columnIndex = cursor.getColumnIndex(COLUMN);
            while(cursor.moveToNext())
            {
                try
                {
                    file = new File(ContentUris.withAppendedId(uri,cursor.getLong(columnIndex)));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    file = null;
                }
                if(file != null)
                {
                    loaded = songsFiles.loadByUri(file.getUri());
                    if(loaded.getCrc32() != file.getCrc32() || loaded.getSize() != file.getSize())
                    {
                        songsFiles.insertAll(file);
                        songsDetailed.insertAll(SongDetailed.loadFromFile(file));
                    }
                }
                count++;
            }
            Log.d(TAG,"Jumping to the next uri");
        }
        if(cursor != null)
            cursor.close();
        return count;
    }

    public void wipe()
    {
        songsFiles.deleteAll();
        songsDetailed.deleteAll();
    }

    /*

    private static String[] processFile(int uri,int mediaColumn,Cursor cursor)
    {
        String[] results = new String[cursor.getColumnCount()];
        int i;
        for(i = 0; i < results.length; i++)
        {
            results[i] = (mediaColumn == i ? uri + ";" : "") + cursor.getString(i);
        }
        return results;
    }*/

    public static class MediaScanResult
    {
        private final Map<String,Integer> keys;
        private final List<String[]> data;

        public String getCell(String columnName,int rowN)
        {
            return getRow(rowN)[getIndexFromColumnName(columnName)];
        }

        public String[] getRow(int rowN)
        {
            return getAll().get(rowN);
        }

        public int getIndexFromColumnName(String columnName)
        {
            return getColumnsIndexesMap().get(columnName);
        }

        public Map<String,Integer> getColumnsIndexesMap()
        {
            return keys;
        }

        public List<String[]> getAll()
        {
            return data;
        }

        public void release()
        {
            keys.clear();
            data.clear();
        }
    }

    public MediaScanResult subscan(Uri[] uris,int from,int to,List<String> columns,String selection,String[] selectionArgs)
    {
        return subscan(uris,from,to,columns.toArray(new String[0]),selection,selectionArgs);
    }

    public MediaScanResult subscan(Uri[] uris,int from,int to,String[] columns,String selection,String[] selectionArgs)
    {
        MediaScanResult result;
        Log.d(TAG,"Scan asked for range: " + from + " to " + to + ", selection: " + selection);
        if(from < 0 || to < 0)
        {
            result = totalScan(uris,columns,selection,selectionArgs);
        }
        else if(from != to)
        {
            result = new MediaScanResult();
            int start = 0,i = 0,id = -1;
            result.data = new ArrayList<>(to - from);
            result.keys = new HashMap<>(columns.length);
            for(String column : columns)
            {
                if(column.endsWith(MediaStore.Audio.Media._ID))
                    id = i;
                result.keys.put(column,i);
                i++;
            }
            Cursor cursor = null;
            i = 0;
            for(Uri uri : uris)
            {
                Log.d(TAG,"Query, uri: " + uri.toString() + ", columns: " + Arrays.toString(columns) + ", selection: " + selection + ", selection args: " + selectionArgs);
                cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
                Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
                if(from < (start + cursor.getCount()))
                {
                    Log.d(TAG,"Valid uri: " + uri.toString());
                    cursor.move(from - start);
                    while(cursor.moveToNext() && from < to)
                    {
                        result.data.add(processFile(i,id,cursor));
                        from++;
                    }
                }
                start += cursor.getCount();
                Log.d(TAG,"Jumping to the next uri");
                i++;
            }
            if(cursor != null)
                cursor.close();
        }
        else
            result = new MediaScanResult();
        return result;
    }

    public MediaScanResult totalScan(Uri[] uris,List<String> columns,String selection,String[] selectionArgs)
    {
        return totalScan(uris,columns.toArray(new String[0]),selection,selectionArgs);
    }

    public MediaScanResult totalScan(Uri[] uris,String[] columns,String selection,String[] selectionArgs)
    {
        MediaScanResult result = new MediaScanResult();
        Log.d(TAG,"Total scan asked, selection: " + selection);
        int start = 0,i = 0,id = -1;
        result.data = new ArrayList<>();
        result.keys = new HashMap<>(columns.length);
        for(String column : columns)
        {
            if(column.endsWith(MediaStore.Audio.Media._ID))
                id = i;
            result.keys.put(column,i);
            i++;
        }
        Cursor cursor = null;
        i = 0;
        for(Uri uri : uris)
        {
            Log.d(TAG,"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
                while(cursor.moveToNext())
                {
                    result.data.add(processFile(i,id,cursor));
                }
            Log.d(TAG,"Jumping to the next uri");
            i++;
        }
        if(cursor != null)
            cursor.close();
        return result;
    }

    public MediaScanResult subscan(Uri[] uris,int count,String[] columns,String selection,String[] selectionArgs)
    {
        MediaScanResult result = new MediaScanResult();
        Log.d(TAG,"Scan asked for " + count + " items, selection: " + selection);
        if(count > 0)
        {
            int i = 0,id = -1;
            result.data = new ArrayList<>(count);
            result.keys = new HashMap<>(columns.length);
            for(String column : columns)
            {
                if(column.endsWith(MediaStore.Audio.Media._ID))
                    id = i;
                result.keys.put(column,i);
                i++;
            }
            Cursor cursor;
            i = 0;
            for(Uri uri : uris)
            {
                Log.d(TAG,"Checking uri: " + uri.toString());
                cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
                Log.d(TAG,"Valid uri: " + uri.toString());
                while(cursor.moveToNext() && count != 0)
                {
                    Log.d(TAG,"Found new item, remaining: " + count);
                    result.data.add(processFile(i,id,cursor));
                    count--;
                }
                Log.d(TAG,"Jumping to the next uri");
                i++;
            }
        }
        return result;
    }*/
}