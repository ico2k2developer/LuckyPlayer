package it.developing.ico2k2.luckyplayer.database.file.media;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.File;
import java.io.IOException;

import it.developing.ico2k2.luckyplayer.database.file.BaseFile;

@Entity(primaryKeys = {BaseMedia.COLUMN_VOLUME,BaseMedia.COLUMN_ID})
public class BaseMedia extends BaseFile
{
    public static final String COLUMN_ID = "mediastore id";
    public static final String COLUMN_VOLUME = "mediastore volume";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_ARTIST = "release artist";
    public static final String COLUMN_LENGTH = "length";

    public static final Uri[] VOLUMES = new Uri[]
    {
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    };

    @ColumnInfo(name = COLUMN_ID)
    private final long id;

    @ColumnInfo(name = COLUMN_VOLUME)
    private final short volume;

    @ColumnInfo(name = COLUMN_TITLE)
    private final String title;

    @ColumnInfo(name = COLUMN_RELEASE_ARTIST)
    private final String releaseArtist;

    @ColumnInfo(name = COLUMN_LENGTH)
    private final long lengthMs;

    public BaseMedia(String uri,long size,long lastModified,short volume,long id,
                     String title,String releaseArtist,long lengthMs)
    {
        super(uri,size,lastModified);
        this.id = id;
        this.volume = volume;
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
    }

    @Ignore
    public BaseMedia(File file, short volume, long id, String title, String releaseArtist,
                     long lengthMs)
            throws IOException
    {
        super(file);
        this.id = id;
        this.volume = volume;
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
    }

    public static class UniqueId implements Parcelable
    {
        private final long id;
        private final short volume;

        public UniqueId(short volume, long id)
        {
            this.volume = volume;
            this.id = id;
        }

        public short getVolume()
        {
            return volume;
        }

        public long getId()
        {
            return id;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags)
        {
            dest.writeInt(getVolume());
            dest.writeLong(getId());
        }

        public static final Creator<UniqueId> CREATOR = new Creator<UniqueId>()
        {
            @Override
            public UniqueId createFromParcel(Parcel in)
            {
                return new UniqueId((short)in.readInt(),in.readLong());
            }

            @Override
            public UniqueId[] newArray(int size)
            {
                return new UniqueId[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @NonNull
        @Override
        @SuppressLint("DefaultLocale")
        public String toString()
        {
            return String.format("Volume %d, id: %d",getVolume(),getId());
        }
    }

    public short getVolume()
    {
        return volume;
    }

    public long getId()
    {
        return id;
    }

    public UniqueId getUniqueId()
    {
        return new UniqueId(getVolume(),getId());
    }

    public String getTitle()
    {
        return title;
    }

    public String getReleaseArtist()
    {
        return releaseArtist;
    }

    public long getLength()
    {
        return lengthMs;
    }
}
