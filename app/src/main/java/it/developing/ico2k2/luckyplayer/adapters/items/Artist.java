package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Parcel;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

public class Artist extends BrowsableItem
{

    public Artist(Parcel parcel,ClassLoader classLoader)
    {
        super(parcel,classLoader);
    }

    public Artist(Artist artist)
    {
        super(artist.getDescription());
    }

    public Artist(MediaDescriptionCompat description)
    {
        super(description);
    }

    public Artist(MediaBrowserCompat.MediaItem newItem) throws NotBrowsableException
    {
        super(newItem);
    }

    public Artist(String id)
    {
        super(id);
    }

    public String getTextDescription()
    {
        return null;
    }

    public String getTimeDescription()
    {
        return null;
    }
}
