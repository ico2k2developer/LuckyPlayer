package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;

public class BrowsableItem extends MusicItem
{
    static class NotBrowsableException extends Exception
    {
        NotBrowsableException(String error)
        {
            super(error);
        }
    }

    public static final Parcelable.Creator<BrowsableItem> CREATOR = new Parcelable.Creator<BrowsableItem>()
    {
        public BrowsableItem createFromParcel(Parcel in){
            return new BrowsableItem(in,getClass().getClassLoader());
        }

        public BrowsableItem[] newArray(int size)
        {
            return new BrowsableItem[size];
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

    public BrowsableItem(Parcel parcel,ClassLoader classLoader)
    {
        item = MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(parcel);
        item.getDescription().getExtras().setClassLoader(classLoader);
    }

    public BrowsableItem(Artist artist)
    {
        item = createFromDescription(artist.getDescription());
    }

    public BrowsableItem(MediaDescriptionCompat description)
    {
        item = createFromDescription(description);
    }

    public BrowsableItem(MediaBrowserCompat.MediaItem newItem) throws NotBrowsableException
    {
        if(newItem.getFlags() == FLAG_BROWSABLE)
            item = createFromDescription(newItem.getDescription());
        else
            throw new NotBrowsableException("This MediaItem is not browsable");
    }

    public BrowsableItem(String id)
    {
        item = new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder()
                        .setMediaId(id)
                        .setExtras(new Bundle())
                        .build(),FLAG_BROWSABLE);
    }

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
                        .setTitle(description.getTitle()).build(),FLAG_BROWSABLE);
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
