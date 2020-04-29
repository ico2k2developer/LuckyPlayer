package it.developing.ico2k2.luckyplayer.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.ContentResolverCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;

public class MediaScanner
{
    protected ContentResolver resolver;

    public MediaScanner(ContentResolver contentResolver)
    {
        resolver = contentResolver;
    }

    public class MediaScanResult
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
        Log.d(TAG_LOGS,"Scan asked for range: " + from + " to " + to + ", selection: " + selection);
        MediaScanResult result = new MediaScanResult();
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
            Log.d(TAG_LOGS,"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
            if(from < (start + cursor.getCount()))
            {
                Log.d(TAG_LOGS,"Valid uri: " + uri.toString());
                cursor.move(from - start);
                while(cursor.moveToNext() && from < to)
                {
                    result.data.add(processFile(i,id,cursor));
                    Log.d(TAG_LOGS,"Processing index: " + from + " (" + cursor.getPosition() + ") " + Arrays.toString(result.getRow(result.data.size() - 1)));
                    from++;
                }
            }
            start += cursor.getCount();
            Log.d(TAG_LOGS,"Jumping to the next uri");
            i++;
        }
        if(cursor != null)
            cursor.close();
        return result;
    }

    public MediaScanResult subscan(Uri[] uris,int count,String[] columns,String selection,String[] selectionArgs)
    {
        Log.d(TAG_LOGS,"Scan asked for " + count + " items, selection: " + selection);
        MediaScanResult result = new MediaScanResult();
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
            Log.d(TAG_LOGS,"Checking uri: " + uri.toString());
            cursor = ContentResolverCompat.query(resolver,uri,columns,selection,selectionArgs,null,null);
            Log.d(TAG_LOGS,"Valid uri: " + uri.toString());
            while(cursor.moveToNext() && count != 0)
            {
                Log.d(TAG_LOGS,"Found new item, remaining: " + count);
                result.data.add(processFile(i,id,cursor));
                count--;
            }
            Log.d(TAG_LOGS,"Jumping to the next uri");
            i++;
        }
        return result;
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
}