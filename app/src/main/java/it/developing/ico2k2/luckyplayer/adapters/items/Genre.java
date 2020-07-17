package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Parcel;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

public class Genre extends BrowsableItem
{
    public Genre(Parcel parcel,ClassLoader classLoader)
    {
        super(parcel,classLoader);
    }

    public Genre(Genre genre)
    {
        super(genre.getDescription());
    }

    public Genre(MediaDescriptionCompat description)
    {
        super(description);
    }

    public Genre(MediaBrowserCompat.MediaItem newItem) throws NotBrowsableException
    {
        super(newItem);
    }

    public Genre(String id)
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
