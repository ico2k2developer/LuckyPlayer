package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Bundle;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

public class Album extends BrowsableItem
{
    public Album(Parcel parcel,ClassLoader classLoader)
    {
        super(parcel,classLoader);
    }

    public Album(Album album)
    {
        super(album.getDescription());
    }

    public Album(MediaDescriptionCompat description)
    {
        super(description);
    }

    public Album(MediaBrowserCompat.MediaItem newItem) throws NotBrowsableException
    {
        super(newItem);
    }

    public Album(String id)
    {
        super(id);
    }

    public static String getAlbumDescription(String artist)
    {
        return artist;
    }

    public String getTextDescription()
    {
        Bundle extras = getDescription().getExtras();
        return getAlbumDescription(extras.getString(MediaStore.Audio.AudioColumns.ARTIST));
    }

    public String getTimeDescription()
    {
        return null;
    }
}
