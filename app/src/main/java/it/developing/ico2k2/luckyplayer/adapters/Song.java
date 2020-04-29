package it.developing.ico2k2.luckyplayer.adapters;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;

public class Song implements Parcelable
{
    public static final String DESCRIPTION_FORMAT = "%s1, %s2";

    public static final String TIME_FORMAT_MS = "%s1 ms";
    public static final String TIME_FORMAT_SEC = "%s1 s";
    public static final String TIME_FORMAT_MIN = "%s1:%s2";
    public static final String TIME_FORMAT_HOUR = "%s1:%s2:%s3";

    static class NotASongException extends Exception
    {
        NotASongException(String error)
        {
            super(error);
        }
    }

    private MediaBrowserCompat.MediaItem item;

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>()
    {
        public Song createFromParcel(Parcel in){
            return new Song(in,getClass().getClassLoader());
        }

        public Song[] newArray(int size)
        {
            return new Song[size];
        }
    };

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel dest,int flags)
    {
        item.writeToParcel(dest,flags);
    }

    public Song(Parcel parcel,ClassLoader classLoader)
    {
        item = MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(parcel);
        item.getDescription().getExtras().setClassLoader(classLoader);
    }

    public Song(Song song)
    {
        item = createFromDescription(song.getDescription());
    }

    public Song(MediaDescriptionCompat description)
    {
        item = createFromDescription(description);
    }

    public Song(MediaBrowserCompat.MediaItem newItem) throws NotASongException
    {
        if(newItem.getFlags() == FLAG_PLAYABLE)
            item = createFromDescription(newItem.getDescription());
        else
            throw new NotASongException("This MediaItem is not a song (flag is not set to playable)");
    }

    public Song(String id)
    {
        item = new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder()
                        .setMediaId(id)
                        .setExtras(new Bundle())
                        .build(),FLAG_PLAYABLE);
    }

    private static MediaBrowserCompat.MediaItem createFromDescription(MediaDescriptionCompat description)
    {
        return new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder()
                        .setDescription(description.getDescription())
                        .setExtras(description.getExtras() == null ? new Bundle() : description.getExtras())
                        .setIconBitmap(description.getIconBitmap())
                        .setIconUri(description.getIconUri())
                        .setMediaId(description.getMediaId())
                        .setMediaUri(description.getMediaUri())
                        .setSubtitle(description.getSubtitle())
                        .setTitle(description.getTitle()).build(),FLAG_PLAYABLE);
    }

    public MediaDescriptionCompat getDescription()
    {
        return item.getDescription();
    }

    /*public String[] getSpecificColumns(ContentResolver resolver,String[] columns)
    {
        return new MediaScanner(resolver).scan(columns,"_ID=?",new String[] {getDescription().getMediaId()},null).getRow(0);
    }*/

    public MediaBrowserCompat.MediaItem toMediaItem()
    {
        return item;
    }

    @Override
    public boolean equals(Object song)
    {
        boolean result;
        if(song instanceof Song)
            result = getDescription().getMediaId().equals(((Song)song).getDescription().getMediaId());
        else
            result = super.equals(song);
        return result;
    }

    public static String getSongTimeDescription(long ms)
    {
        String result;
        if(ms < 1000)
            result = TIME_FORMAT_MS.replace("%s1",Long.toString(ms));
        else if(ms < 60000)
            result = TIME_FORMAT_SEC.replace("%s1",Long.toString(ms / 1000));
        else if(ms < 3600000)
        {
            int min = (int)(ms / 1000 / 60);
            int sec = (int)(ms / 1000 - min * 60);
            result = TIME_FORMAT_MIN
                    .replace("%s1",Integer.toString(min))
                    .replace("%s2",Integer.toString(sec));
        }
        else
        {
            int hour = (int)(ms / 1000 / 60 / 24);
            int min = (int)(ms / 1000 / 60 - hour * 60);
            int sec = (int)(ms / 1000 - hour * 60 * 60 - min * 60);
            result = TIME_FORMAT_HOUR
                    .replace("%s1",Integer.toString(hour))
                    .replace("%s2",Integer.toString(min))
                    .replace("%s3",Integer.toString(sec));
        }
        return result;
    }

    public static String getSongDescription(String album,String artist)
    {
        return DESCRIPTION_FORMAT.replace("%s1",album == null ? "" : album).replace("%s2",artist == null ? "" : artist);
    }
}
