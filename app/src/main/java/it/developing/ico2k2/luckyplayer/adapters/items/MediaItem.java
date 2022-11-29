package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class MediaItem implements Parcelable
{
    private final String title;

    public MediaItem(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public abstract String getSubtitle();

    public abstract boolean isBrowsable();

    public abstract boolean isPlayable();

    @Override
    public int describeContents()
    {
        return 0;
    }
}