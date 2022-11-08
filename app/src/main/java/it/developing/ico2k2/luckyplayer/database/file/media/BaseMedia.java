package it.developing.ico2k2.luckyplayer.database.file.media;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.IOException;

import it.developing.ico2k2.luckyplayer.database.file.File;

@Entity
public class BaseMedia extends File
{
    private final static String ID_SEP = "§";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_ARTIST = "release_artist";
    public static final String COLUMN_LENGTH = "length";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    private final String id;

    @ColumnInfo(name = COLUMN_TITLE)
    private final String title;

    @ColumnInfo(name = COLUMN_RELEASE_ARTIST)
    private final String releaseArtist;

    @ColumnInfo(name = COLUMN_LENGTH)
    private final long lengthMs;

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

    private static String generateId(String title,String releaseArtist,Timestamp timestamp)
    {
        return title + ID_SEP + releaseArtist + ID_SEP + timestamp;
    }

    public BaseMedia(String uri,long crc32,long size,String id,String title,String releaseArtist,long lengthMs)
    {
        super(uri,crc32,size);
        this.id = id;
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
        timestamp = new Timestamp(lengthMs);
    }

    @Ignore
    public BaseMedia(String uri,String title,String releaseArtist,long lengthMs) throws IOException
    {
        super(uri);
        timestamp = new Timestamp(lengthMs);
        id = generateId(title,releaseArtist,timestamp);
        this.title = title;
        this.releaseArtist = releaseArtist;
        this.lengthMs = lengthMs;
    }

    @NonNull
    public String getId()
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
