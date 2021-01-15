package it.developing.ico2k2.luckyplayer.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContentResolverCompat;

import it.developing.ico2k2.luckyplayer.database.Song;
import it.developing.ico2k2.luckyplayer.database.SongDao;

import static android.provider.MediaStore.Audio.AudioColumns.IS_ALARM;
import static android.provider.MediaStore.Audio.AudioColumns.IS_AUDIOBOOK;
import static android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC;
import static android.provider.MediaStore.Audio.AudioColumns.IS_NOTIFICATION;
import static android.provider.MediaStore.Audio.AudioColumns.IS_PODCAST;
import static android.provider.MediaStore.Audio.AudioColumns.IS_RINGTONE;

public class MediaScanner
{

    public static class QuerySettings
    {
        private final boolean music,ringtone,notification,podcast,alarm,other;
        private final Boolean audiobook;

        @RequiresApi(29)
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
        }

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
        }

        public QuerySettings()
        {
            this(false,false,false,false,false,false);
        }

        private boolean areAllTrue()
        {
            return music && ringtone && notification && podcast && alarm && (audiobook == null ? true : audiobook) && other;
        }

        private boolean areAllFalse()
        {
            return !(music || ringtone || notification || podcast || alarm || (audiobook == null ? false : audiobook) || other);
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

        public boolean getPodcast(){
            return podcast;
        }

        public boolean getAlarm(){
            return alarm;
        }

        @RequiresApi(29)
        public boolean getAudiobook(){
            return audiobook == null ? false : audiobook;
        }

        public boolean getOther(){
            return other;
        }
    }

    private final ContentResolver resolver;
    private final SongDao dao;
    private final String query;

    public MediaScanner(QuerySettings settings,ContentResolver resolver,SongDao dao)
    {
        this.resolver = resolver;
        this.dao = dao;
        query = buildQuerySelectionString(settings);
    }

    private boolean negIf(boolean value,boolean negIf)
    {
        if(negIf)
            return !value;
        else
            return value;
    }

    @Nullable
    private String buildQuerySelectionString(QuerySettings settings)
    {
        String result = null;
        if(!settings.areAllFalse())
        {
            final StringBuilder builder = new StringBuilder();
            final String andOr,comparator;
            final boolean neg = settings.getOther();
            if(neg)
            {
                comparator = " == 0";
                andOr = " AND ";
            }
            else
            {
                comparator = " != 0";
                andOr = " OR ";
            }
            if(negIf(settings.getMusic(),neg))
                builder.append(IS_MUSIC).append(comparator).append(andOr);
            if(negIf(settings.getRingtone(),neg))
                builder.append(IS_RINGTONE).append(comparator).append(andOr);
            if(negIf(settings.getNotification(),neg))
                builder.append(IS_NOTIFICATION).append(comparator).append(andOr);
            if(negIf(settings.getPodcast(),neg))
                builder.append(IS_PODCAST).append(comparator).append(andOr);
            if(negIf(settings.getAlarm(),neg))
                builder.append(IS_ALARM).append(comparator);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                if(negIf(settings.getAudiobook(),neg))
                    builder.append(andOr).append(IS_AUDIOBOOK).append(comparator);
            }
            result = builder.toString();
        }
        return result;
    }

    public void scan(Uri[] uris)
    {
        Cursor cursor = null;
        for(Uri uri : uris)
        {
            Log.d(getClass().getSimpleName(),"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,Song.columns,query,null,null,null);
            Log.d(getClass().getSimpleName(),"Cursor contains " + cursor.getCount() + " elements");
            while(cursor.moveToNext())
            {
                dao.insertAll(new Song(cursor.get));
                result.data.add(processFile(i,id,cursor));
            }
            Log.d(getClass().getSimpleName(),"Jumping to the next uri");
        }
        if(cursor != null)
            cursor.close();
    }

    String[] processFile(int uri,int mediaColumn,Cursor cursor)
    {
        String[] results = new String[cursor.getColumnCount()];
        int i;
        for(i = 0; i < results.length; i++)
        {
            results[i] = (mediaColumn == i ? uri + ";" : "") + cursor.getString(i);
        }
        return results;
    }

    /*
    public static class MediaScanResult
    {
        protected Map<String,Integer> keys;
        protected List<String[]> data;

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
        Log.d(getClass().getSimpleName(),"Scan asked for range: " + from + " to " + to + ", selection: " + selection);
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
                Log.d(getClass().getSimpleName(),"Query, uri: " + uri.toString() + ", columns: " + Arrays.toString(columns) + ", selection: " + selection + ", selection args: " + selectionArgs);
                cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
                Log.d(getClass().getSimpleName(),"Cursor contains " + cursor.getCount() + " elements");
                if(from < (start + cursor.getCount()))
                {
                    Log.d(getClass().getSimpleName(),"Valid uri: " + uri.toString());
                    cursor.move(from - start);
                    while(cursor.moveToNext() && from < to)
                    {
                        result.data.add(processFile(i,id,cursor));
                        from++;
                    }
                }
                start += cursor.getCount();
                Log.d(getClass().getSimpleName(),"Jumping to the next uri");
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
        Log.d(getClass().getSimpleName(),"Total scan asked, selection: " + selection);
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
            Log.d(getClass().getSimpleName(),"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
            Log.d(getClass().getSimpleName(),"Cursor contains " + cursor.getCount() + " elements");
                while(cursor.moveToNext())
                {
                    result.data.add(processFile(i,id,cursor));
                }
            Log.d(getClass().getSimpleName(),"Jumping to the next uri");
            i++;
        }
        if(cursor != null)
            cursor.close();
        return result;
    }

    public MediaScanResult subscan(Uri[] uris,int count,String[] columns,String selection,String[] selectionArgs)
    {
        MediaScanResult result = new MediaScanResult();
        Log.d(getClass().getSimpleName(),"Scan asked for " + count + " items, selection: " + selection);
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
                Log.d(getClass().getSimpleName(),"Checking uri: " + uri.toString());
                cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
                Log.d(getClass().getSimpleName(),"Valid uri: " + uri.toString());
                while(cursor.moveToNext() && count != 0)
                {
                    Log.d(getClass().getSimpleName(),"Found new item, remaining: " + count);
                    result.data.add(processFile(i,id,cursor));
                    count--;
                }
                Log.d(getClass().getSimpleName(),"Jumping to the next uri");
                i++;
            }
        }
        return result;
    }*/
}