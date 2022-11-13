package it.developing.ico2k2.luckyplayer.database.file.media;

import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

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

    @Ignore
    private final Timestamp timestamp;

    public static class Timestamp
    {
        private final short hours;
        private final byte minutes;
        private final byte seconds;

        private Timestamp(long lengthMs)
        {
            hours = (short)(lengthMs / (60 * 60 * 1000));
            minutes = (byte)((lengthMs - hours * (60 * 60 * 1000)) / (60 * 1000));
            seconds = (byte)((lengthMs - hours * (60 * 60 * 1000)
                    - minutes * (60 * 1000)) / 1000);
        }

        public short getHours() {
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
    }

    public BaseMedia(String uri,long size,long lastModified,short volume,long id,
                     String title,String releaseArtist,long lengthMs)
    {
        super(uri,size,lastModified);
        this.id = id;
        this.volume = volume;
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
        timestamp = new Timestamp(lengthMs);
    }

    @Ignore
    public BaseMedia(java.io.File file,short volume,long id, String title, String releaseArtist,
                     long lengthMs)
            throws IOException
    {
        super(file);
        this.id = id;
        this.volume = volume;
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
        timestamp = new Timestamp(lengthMs);
    }

    public short getVolume()
    {
        return volume;
    }
    public long getId()
    {
        return id;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public String getTitle()
    {
        return title;
    }

    public String getReleaseArtist()
    {
        return releaseArtist;
    }

    public long getLengthMs()
    {
        return lengthMs;
    }
}
