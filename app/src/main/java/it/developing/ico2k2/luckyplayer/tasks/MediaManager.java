package it.developing.ico2k2.luckyplayer.tasks;

import static android.provider.MediaStore.Audio.AudioColumns.IS_ALARM;
import static android.provider.MediaStore.Audio.AudioColumns.IS_AUDIOBOOK;
import static android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC;
import static android.provider.MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
import static android.provider.MediaStore.Audio.AudioColumns.IS_PODCAST;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RINGTONE;

import android.content.ContentResolver;
import android.content.ContentUris;
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
import java.util.concurrent.Callable;

import it.developing.ico2k2.luckyplayer.database.data.File;
import it.developing.ico2k2.luckyplayer.database.data.FileDao;
import it.developing.ico2k2.luckyplayer.database.data.FilesDatabase;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailedDao;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongsDetailedDatabase;

public class MediaManager
{
    private static final String TAG = MediaManager.class.getSimpleName();

    private static final String ID = MediaStore.MediaColumns._ID;

    private static final String PATH_OLD = MediaStore.MediaColumns.DATA;

    @RequiresApi(Build.VERSION_CODES.Q)
    private static final String PATH_NEW = MediaStore.MediaColumns.RELATIVE_PATH;

    public static class QuerySettings
    {
        private final boolean music,ringtone,notification,podcast,alarm,other;
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
                result = audiobook;
            if(result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                result = recording;
            return result;
        }

        private boolean areAllFalse()
        {
            boolean result = music || ringtone || notification || podcast || alarm || other;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                result = result || audiobook;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
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

        @RequiresApi(Build.VERSION_CODES.S)
        public boolean getRecording(){
            return recording;
        }

        public boolean getOther(){
            return other;
        }
    }

    private final Context context;
    private final SongsDetailedDatabase songsDetailed;
    private final FilesDatabase songsFiles;
    private String query;
    private boolean scanning;

    public MediaManager(Context context, FilesDatabase files, SongsDetailedDatabase songs)
    {
        this.context = context;
        songsFiles = files;
        songsDetailed = songs;
        scanning = false;
    }

    public void setQuerySettings(QuerySettings settings)
    {
        query = buildQuerySelectionString(settings);
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
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
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
        scanning = true;
        Cursor cursor = null;
        long count = 0;
        //dao.deleteAll();
        SongDetailedDao songDao = songsDetailed.dao();
        String[] keys = new String[2];
        keys[0] = ID;
        keys[1] = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? PATH_OLD : PATH_NEW;
        int[] columns = new int[2];
        FileDao fileDao = songsFiles.dao();
        ContentResolver resolver = context.getContentResolver();
        for(Uri uri : uris)
        {
            Log.d(TAG,"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,keys,query,null,null,null);
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
            int a;
            for(a = 0; a < keys.length; a++)
                columns[a] = cursor.getColumnIndex(keys[a]);
            count = 0;
            while(cursor.moveToNext() && count < 5)
            {
                final Uri fileUri = ContentUris.withAppendedId(uri,cursor.getLong(columns[0]));
                Log.d(TAG,"Uri is " + cursor.getString(columns[1]) + " id is " + cursor.getString(columns[0]) + " so file uri is " + fileUri);
                new AsyncWork().executeAsync(null,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            File oldFile = fileDao.loadByUri(fileUri.getPath());
                            File newFile = new File(fileUri,resolver);
                            boolean write = false;
                            if(oldFile != null)
                            {
                                write = oldFile.getCrc32() == newFile.getCrc32() && oldFile.getSize() == newFile.getSize();
                            }
                            if(write)
                            {
                                fileDao.insertAll(newFile);
                                songDao.insertAll(SongDetailed.loadFromUri(newFile.getUri()));
                            }
                            return null;
                        }
                    },null);
                count++;
            }
            Log.d(TAG,"Jumping to the next uri");
        }
        if(cursor != null)
            cursor.close();
        scanning = false;
        return count;
    }

    public void wipe()
    {
        songsFiles.dao().deleteAll();
        songsDetailed.dao().deleteAll();
    }

    public SongsDetailedDatabase getSongsDatabase()
    {
        return songsDetailed;
    }

    public FilesDatabase getFilesDatabase()
    {
        return songsFiles;
    }

    public static class QueryResult
    {
        private final Cursor cursor;

        private QueryResult(Cursor cursor)
        {
            this.cursor = cursor;
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
        }

        public Map<String,Integer> generateKeys(String[] columns)
        {
            Map<String,Integer> result = new HashMap<>(columns.length);
            for(String column : columns)
            {
                result.put(column,cursor.getColumnIndex(column));
            }
            return result;
        }

        public Map<String,Integer> generateKeys(List<String> columns)
        {
            Map<String,Integer> result = new HashMap<>(columns.size());
            for(String column : columns)
            {
                result.put(column,cursor.getColumnIndex(column));
            }
            return result;
        }

        private Map<String,String> getRow(Map<String,Integer> keys)
        {
            Map<String,String> row = new HashMap<>(keys.size());
            int i;
            for(String key : keys.keySet())
            {
                row.put(key,getCell(keys,key));
            }
            return row;
        }

        public Map<String,String> getRow(Map<String,Integer> keys,int row)
        {
            cursor.moveToPosition(row);
            return getRow(keys);
        }

        private List<Map<String,String>> getPage(Map<String,Integer> keys,int count)
        {
            ArrayList<Map<String,String>> result;
            if(count > 0)
            {
                result = new ArrayList<>(count);
                Map<String,String> row;
                do
                {
                    row = getRow(keys);
                    result.add(row);
                    count--;
                }
                while(cursor.moveToNext() && count > 0);
            }
            else
                result = null;
            return result;
        }

        public List<Map<String,String>> getPage(Map<String,Integer> keys,int start,int count)
        {
            cursor.moveToPosition(start);
            return getPage(keys,count);
        }

        private String getCell(Map<String,Integer> keys,String columnName)
        {
            return cursor.getString(keys.get(columnName));
        }

        public String getCell(Map<String,Integer> keys,String columnName,int rowN)
        {
            cursor.moveToPosition(rowN);
            return getCell(keys,columnName);
        }

        @Deprecated
        public List<Map<String,String>> getAll(Map<String,Integer> keys)
        {
            ArrayList<Map<String,String>> result = new ArrayList<>();
            cursor.moveToFirst();
            do
            {
                result.add(getRow(keys));
            }
            while(cursor.moveToNext());
            return result;
        }

        public void release()
        {
            cursor.close();
        }

        @Override
        public boolean equals(Object o)
        {
            boolean result = false;
            if(o != null)
            {
                if(o instanceof QueryResult)
                {
                    result = cursor.equals(((QueryResult)o).cursor);
                }
            }
            return result;
        }
    }

    public int getCount()
    {
        return songsFiles.dao().getCount();
    }

    public QueryResult query(@NonNull String selection, @Nullable String[] selectionArgs)
    {
        Log.d(TAG,"Query asked, selection: " + selection);
        return new QueryResult(songsDetailed.query(selection,selectionArgs));
    }
}