package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;

public class PlayableItem extends MusicItem
{
    static class NotPlayableException extends Exception
    {
        NotPlayableException(String error)
        {
            super(error);
        }
    }

    public static final Parcelable.Creator<PlayableItem> CREATOR = new Parcelable.Creator<PlayableItem>()
    {
        public PlayableItem createFromParcel(Parcel in){
            return new PlayableItem(in,getClass().getClassLoader());
        }

        public PlayableItem[] newArray(int size)
        {
            return new PlayableItem[size];
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

    public PlayableItem(Parcel parcel,ClassLoader classLoader)
    {
        item = MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(parcel);
        item.getDescription().getExtras().setClassLoader(classLoader);
    }

    public PlayableItem(PlayableItem item)
    {
        super.item = createFromDescription(item.getDescription());
    }

    public PlayableItem(MediaDescriptionCompat description)
    {
        item = createFromDescription(description);
    }

    public PlayableItem(MediaBrowserCompat.MediaItem newItem) throws NotPlayableException
    {
        if(newItem.getFlags() == FLAG_PLAYABLE)
            item = createFromDescription(newItem.getDescription());
        else
            throw new NotPlayableException("This MediaItem is not playable");
    }

    public PlayableItem(String id)
    {
        item = new MediaBrowserCompat.MediaItem(
                new MediaDescriptionCompat.Builder()
                        .setMediaId(id)
                        .setExtras(new Bundle())
                        .build(),FLAG_PLAYABLE);
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
                        .setTitle(description.getTitle()).build(),FLAG_PLAYABLE);
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
