package it.developing.ico2k2.luckyplayer.tasks;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Audio.AudioColumns.IS_ALARM;
import static android.provider.MediaStore.Audio.AudioColumns.IS_AUDIOBOOK;
import static android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC;
import static android.provider.MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
import static android.provider.MediaStore.Audio.AudioColumns.IS_PODCAST;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RECORDING;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RINGTONE;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.RELATIVE_PATH;
import static android.provider.MediaStore.MediaColumns.VOLUME_NAME;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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

import it.developing.ico2k2.luckyplayer.Storage;
import it.developing.ico2k2.luckyplayer.database.data.File;
import it.developing.ico2k2.luckyplayer.database.data.FileDao;
import it.developing.ico2k2.luckyplayer.database.data.FilesDatabase;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailedDao;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongsDetailedDatabase;

public class MediaManager
{
    private static final String TAG = MediaManager.class.getSimpleName();

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
    private final long count;

    public MediaManager(Context context, FilesDatabase files, SongsDetailedDatabase songs)
    {
        this.context = context;
        songsFiles = files;
        songsDetailed = songs;
        count = 0;
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
            final StringBuilder builder = new StringBuilder(" ");
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
                builder.append(IS_MUSIC).append(comparator);
            if(settings.getRingtone() ^ settings.getOther())
                builder.append(andOr).append(IS_RINGTONE).append(comparator);
            if(settings.getNotification() ^ settings.getOther())
                builder.append(andOr).append(IS_NOTIFICATION).append(comparator);
            if(settings.getPodcast() ^ settings.getOther())
                builder.append(andOr).append(IS_PODCAST).append(comparator);
            if(settings.getAlarm() ^ settings.getOther())
                builder.append(andOr).append(IS_ALARM).append(comparator);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                if(settings.getAudiobook() ^ settings.getOther())
                    builder.append(andOr).append(IS_AUDIOBOOK).append(comparator);
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                if(settings.getRecording() ^ settings.getOther())
                    builder.append(andOr).append(IS_RECORDING).append(comparator);
            }
            result = builder.toString();
        }
        return result;
    }

    /*public static class ScanSettings
    {
        public static class ScanItem
        {
            private final Uri uri;
            private String path;

            public ScanItem(final Uri uri,final String path)
            {
                this.uri = uri;
                if(path.endsWith("/"))
                    this.path = path;
                else
                    this.path = path + "/";
            }

            public static ScanItem externalStorage(Context context)
            {
                return new ScanItem(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.R ?
                        getExternalStorageDirectory().getAbsolutePath() :
                        getExternalStorageDirectoryAPI30(context).getAbsolutePath());
            }

            public String getCompletePath(String relativePath,String filename)
            {
                return path + relativePath + filename;
            }
        }

        private final List<ScanItem> items;

        public ScanSettings(int initialSize)
        {
            items = new ArrayList<>(initialSize);
        }

        public ScanSettings()
        {
            items = new ArrayList<>();
        }

        public ScanSettings(Collection<? extends ScanItem> items)
        {
            this(items.size());
            addAll(items);
        }

        public ScanSettings(ScanItem ... items)
        {
            this(items.length);
            addAll(items);
        }

        public void add(ScanItem item)
        {
            items.add(item);
        }

        public void addAll(Collection<? extends ScanItem> items)
        {
            this.items.addAll(items);
        }

        public void addAll(ScanItem ... items)
        {
            addAll(Arrays.asList(items));
        }
    }*/

    public void scan(Uri[] uris,@Nullable AsyncTask.OnStart start,@Nullable AsyncTask.OnFinish<Long> finish)
    {
        final SongDetailedDao songDao = songsDetailed.dao();
        final FileDao fileDao = songsFiles.dao();
        final ContentResolver resolver = context.getContentResolver();
        final String[] keys;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            keys = new String[]
                    {
                            _ID,
                            DATA,
                    };
        }
        else
        {
            keys = new String[]
                    {
                            _ID,
                            DATA,
                            VOLUME_NAME,
                            RELATIVE_PATH,
                            DISPLAY_NAME,
                    };
        }
        new AsyncTask<Long>().executeAsync(new AsyncTask.OnStart() {
            @Override
            public void onStart() {
                if(start != null)
                    start.onStart();
            }
        }, new Callable<Long>() {
            @Override
            public Long call() throws Exception
            {
                long count = 0;
                Cursor cursor;
                Map<String,Integer> columns = new HashMap<>();
                for(Uri uri : uris)
                {
                    Log.d(TAG,"Checking uri: " + uri.toString());
                    cursor = ContentResolverCompat.query(resolver,uri,keys,query,null,null,null);
                    Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
                    columns.clear();
                    for(String key : keys)
                        columns.put(key,cursor.getColumnIndex(key));
                    while(cursor.moveToNext())
                    {
                        String alternativePath = cursor.getString(columns.get(DATA));
                        String path;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            path = Storage.getAbsolutePath(context,
                                    cursor.getString(columns.get(VOLUME_NAME)),
                                    cursor.getString(columns.get(RELATIVE_PATH)),
                                    cursor.getString(columns.get(DISPLAY_NAME)));
                        }
                        else
                            path = null;
                        Log.d(TAG,"Path is " + path + " alternative path is " + alternativePath);
                        File newFile = null;
                        if(path != null)
                        {
                            try
                            {
                                newFile = new File(path);
                                Log.d(TAG,"Using path " + path);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if(newFile == null && alternativePath != null)
                        {
                            try
                            {
                                newFile = new File(alternativePath);
                                Log.d(TAG,"Using alternative path " + alternativePath);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if(newFile != null)
                        {
                            try
                            {
                                File oldFile = fileDao.loadByUri(newFile.getUri());
                                boolean write = true;
                                if(oldFile != null)
                                {
                                    if(oldFile.getCrc32() == newFile.getCrc32() && oldFile.getSize() == newFile.getSize())
                                        write = false;
                                    Log.d(TAG,"Old file found");
                                }
                                else
                                    Log.d(TAG,"Old file not found");
                                if(write)
                                {
                                    SongDetailed song = SongDetailed.loadFromUri(newFile.getUri());
                                    if(song != null)
                                    {
                                        Log.d(TAG,"Saving song " + song.getUri() + " to databases");
                                        fileDao.insertAll(newFile);
                                        songDao.insertAll(SongDetailed.loadFromUri(newFile.getUri()));
                                        count++;
                                    }
                                    else
                                        Log.d(TAG,"Could not load song from file " + newFile.getUri());
                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    cursor.close();
                    Log.d(TAG,"Jumping to the next uri, " + count + " items already found");
                }
                return count;
            }
        }, new AsyncTask.OnFinish<Long>() {
            @Override
            public void onComplete(@Nullable Long result) {
                Log.d(TAG,"Jumping to the next uri");
                if(finish != null)
                    finish.onComplete(result);
            }
        });
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

    public long getCount()
    {
        return songsFiles.dao().getCount();
    }

    public long getScanCount()
    {
        return count;
    }

    public QueryResult query(@NonNull String selection, @Nullable String[] selectionArgs)
    {
        Log.d(TAG,"Query asked, selection: " + selection);
        return new QueryResult(songsDetailed.query(selection,selectionArgs));
    }
}