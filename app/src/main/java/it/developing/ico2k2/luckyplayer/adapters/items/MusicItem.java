package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

public abstract class MusicItem implements Parcelable
{
    public static final String DESCRIPTION_FORMAT = "%s1, %s2";

    public static final String TIME_FORMAT_MS = "%s1 ms";
    public static final String TIME_FORMAT_SEC = "%s1 s";
    public static final String TIME_FORMAT_MIN = "%s1:%s2";
    public static final String TIME_FORMAT_HOUR = "%s1:%s2:%s3";

    MediaBrowserCompat.MediaItem item;

    public MediaDescriptionCompat getDescription()
    {
        return item.getDescription();
    }

    public MediaBrowserCompat.MediaItem toMediaItem()
    {
        return item;
    }

    abstract MediaBrowserCompat.MediaItem createFromDescription(MediaDescriptionCompat description);

    public boolean equals(Object item)
    {
        boolean result;
        if(item instanceof MusicItem)
            result = getDescription().getMediaId().equals(((MusicItem)item).getDescription().getMediaId());
        else
            result = super.equals(item);
        return result;
    }

    public abstract String getTextDescription();

    public abstract String getTimeDescription();

    public boolean isBrowsable()
    {
        return item.getFlags() == MediaBrowserCompat.MediaItem.FLAG_BROWSABLE;
    }

    public boolean isPlayable()
    {
        return item.getFlags() == MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;
    }
}
