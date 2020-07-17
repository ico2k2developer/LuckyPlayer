package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;

public class Song extends PlayableItem
{
    public Song(Parcel parcel,ClassLoader classLoader)
    {
        super(parcel,classLoader);
    }

    public Song(Song song)
    {
        super(song.getDescription());
    }

    public Song(MediaDescriptionCompat description)
    {
        super(description);
    }

    public Song(MediaBrowserCompat.MediaItem newItem) throws NotPlayableException
    {
        super(newItem);
    }

    public Song(String id)
    {
        super(id);
    }

    @Override
    MediaBrowserCompat.MediaItem createFromDescription(MediaDescriptionCompat description)
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

    public String getTextDescription()
    {
        return item.getDescription().getSubtitle().toString();
    }

    public String getTimeDescription()
    {
        return getSongTimeDescription(getDescription().getExtras().getLong(MediaStore.Audio.AudioColumns.DURATION,0));
    }
}
