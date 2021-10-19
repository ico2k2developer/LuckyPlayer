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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.database.data.File;
import it.developing.ico2k2.luckyplayer.database.data.FileDao;
import it.developing.ico2k2.luckyplayer.database.data.FileDatabase;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailedDao;
import it.developing.ico2k2.luckyplayer.database.data.songs.SongsDetailedDatabase;

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
    private final SongsDetailedDatabase songsDetailed;
    private final FileDatabase songsFiles;
    private final String query;
    private boolean scanning;

    public MediaManager(QuerySettings settings, ContentResolver resolver, FileDatabase files, SongsDetailedDatabase songs)
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
        scanning = true;
        Cursor cursor = null;
        int columnIndex;
        long count = 0;
        //dao.deleteAll();
        File file,loaded;
        SongDetailedDao songDao = songsDetailed.dao();
        FileDao fileDao = songsFiles.dao();
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
                    loaded = fileDao.loadByUri(file.getUri());
                    if(loaded.getCrc32() != file.getCrc32() || loaded.getSize() != file.getSize())
                    {
                        fileDao.insertAll(file);
                        songDao.insertAll(SongDetailed.loadFromFile(file));
                    }
                }
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

    public static class QueryResult
    {
        private final Map<String,Integer> keys;
        private final Cursor cursor;

        private QueryResult(Map<String,Integer> keys,Cursor cursor)
        {
            this.keys = keys;
            this.cursor = cursor;
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
        }

        private QueryResult(String[] columns,Cursor cursor)
        {
            keys = new HashMap<>(columns.length);
            int i = 0;
            for(String column : columns)
            {
                keys.put(column,i);
                i++;
            }
            this.cursor = cursor;
            Log.d(TAG,"Cursor contains " + cursor.getCount() + " elements");
        }

        private String[] elaborateRow()
        {
            String[] row = new String[keys.size()];
            int i;
            for(i = 0; i < keys.size(); i++)
            {
                row[i] = cursor.getString(i);
            }
            return row;
        }

        private String[] elaborateRow(int row)
        {
            cursor.moveToPosition(row);
            return elaborateRow();
        }

        private List<String[]> getPage(int count)
        {
            ArrayList<String[]> result;
            if(count > 0)
            {
                result = new ArrayList<>(count);
                String[] row;
                do
                {
                    row = elaborateRow();
                    result.add(row);
                    count--;
                }
                while(cursor.moveToNext() && count > 0);
            }
            else
                result = null;
            return result;
        }

        public List<String[]> getPage(int start,int count)
        {
            cursor.moveToPosition(start);
            return getPage(count);
        }

        public String getCell(String columnName,int rowN)
        {
            return getRow(rowN)[getIndexFromColumnName(columnName)];
        }

        public String[] getRow(int rowN)
        {
            return elaborateRow(rowN);
        }

        public int getIndexFromColumnName(String columnName)
        {
            return keys.get(columnName);
        }

        @Deprecated
        public List<String[]> getAll()
        {
            ArrayList<String[]> result = new ArrayList<>();
            cursor.moveToFirst();
            do
            {
                result.add(elaborateRow());
            }
            while(cursor.moveToNext());
            return result;
        }

        public void release()
        {
            keys.clear();
            cursor.close();
        }
    }

    public QueryResult query(String[] columns,String selection,String[] selectionArgs)
    {
        QueryResult result;
        Log.d(TAG,"Total scan asked, selection: " + selection);
        return new QueryResult(columns,songsDetailed.query(selection,selectionArgs));
    }
}