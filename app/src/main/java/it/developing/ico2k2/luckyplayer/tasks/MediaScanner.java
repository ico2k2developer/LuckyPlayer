package it.developing.ico2k2.luckyplayer.tasks;


import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import it.developing.ico2k2.luckyplayer.adapters.SongsAdapter;

public class MediaScanner
{
    protected ContentResolver resolver;
    protected boolean mediaFiles = false,scanning = false;
    protected OnMediaScannerResult callbacks;
    int index = 0;

    public interface OnMediaScannerResult
    {
        void onScanStart();
        void onScanResult(SongsAdapter.Song song);
        void onScanStop();
    }

    public MediaScanner(ContentResolver contentResolver,@NonNull OnMediaScannerResult onMediaScannerResult)
    {
        resolver = contentResolver;
        callbacks = onMediaScannerResult;
    }

    public void setIncludeMediaFiles(boolean include)
    {
        mediaFiles = include;
    }

    public boolean getIncludeMediaFiles()
    {
        return mediaFiles;
    }

    public void startScan()
    {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                if(!scanning)
                {
                    scanning = true;
                    callbacks.onScanStart();
                    try
                    {
                        Cursor cursor = resolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.TITLE + " ASC");
                        while(cursor.moveToNext()){
                            processFile(cursor);
                        }
                        cursor.close();
                        cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.TITLE + " ASC");
                        while(cursor.moveToNext()){
                            processFile(cursor);
                        }
                        cursor.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    scanning = false;
                    callbacks.onScanStop();
                }
            }
        });
        index = 0;
        thread.run();
    }

    public boolean isScanning()
    {
        return scanning;
    }

    protected void processFile(Cursor cursor)
    {
        boolean valid = true;
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        if(!mediaFiles)
        {
            valid = !path.startsWith("/system");
        }
        if(valid)
        {
            SongsAdapter.Song song = new SongsAdapter.Song(path,index,
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            song.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            song.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            song.setTrackN(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)));
            song.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            callbacks.onScanResult(song);
            index++;
        }
    }
}