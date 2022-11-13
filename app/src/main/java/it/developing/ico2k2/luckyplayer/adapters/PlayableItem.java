package it.developing.ico2k2.luckyplayer.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import it.developing.ico2k2.luckyplayer.database.file.media.BaseMedia;

public abstract class PlayableItem extends MediaItem implements CreatedByArtist
{
    public static class Timestamp implements Parcelable
    {
        private final int hours;
        private final byte minutes;
        private final byte seconds;

        public Timestamp(long lengthMs)
        {
            hours = (short)(lengthMs / (60 * 60 * 1000));
            minutes = (byte)((lengthMs - hours * (60 * 60 * 1000)) / (60 * 1000));
            seconds = (byte)((lengthMs - hours * (60 * 60 * 1000)
                    - minutes * (60 * 1000)) / 1000);
        }

        public Timestamp(BaseMedia media)
        {
            this(media.getLength());
        }

        private Timestamp(int hours,byte minutes,byte seconds)
        {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeInt(getHours());
            dest.writeByte(getMinutes());
            dest.writeByte(getSeconds());
        }

        public static final Creator<Timestamp> CREATOR = new Creator<Timestamp>()
        {
            @Override
            public Timestamp createFromParcel(Parcel in)
            {
                return new Timestamp(in.readInt(),in.readByte(),in.readByte());
            }

            @Override
            public Timestamp[] newArray(int size)
            {
                return new Timestamp[size];
            }
        };

        public int getHours() {
            return hours;
        }

        public byte getMinutes() {
            return minutes;
        }

        public byte getSeconds() {
            return seconds;
        }

        @NonNull
        @Override
        public String toString()
        {
            return hours + ":" + minutes + ":" + seconds;
        }

        @Override
        public int describeContents() {
            return 0;
        }
    }

    private final BaseMedia.UniqueId id;
    private final Timestamp timestamp;
    private final String artist;

    public PlayableItem(BaseMedia.UniqueId id,String title,String artist,Timestamp timestamp)
    {
        super(title);
        this.id = id;
        this.artist = artist;
        this.timestamp = timestamp;
    }

    public PlayableItem(BaseMedia.UniqueId id, String title, String artist, long length)
    {
        this(id,title,artist,new Timestamp(length));
    }

    public PlayableItem(short volume,long id,String title,String artist,long length)
    {
        this(new BaseMedia.UniqueId(volume,id),title,artist,length);
    }

    public PlayableItem(BaseMedia media)
    {
        this(media.getUniqueId(),media.getTitle(),media.getReleaseArtist(),new Timestamp(media));
    }

    public BaseMedia.UniqueId getUniqueId()
    {
        return id;
    }

    @Override
    public String getArtist()
    {
        return artist;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    @Override
    public boolean isBrowsable()
    {
        return false;
    }

    @Override
    public boolean isPlayable()
    {
        return true;
    }
}
