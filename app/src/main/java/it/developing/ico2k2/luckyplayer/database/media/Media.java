package it.developing.ico2k2.luckyplayer.database.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;

import android.media.AudioMetadata;

import androidx.room.BuiltInTypeConverters;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Entity(tableName = DATABASE_SONGS)
public class Media extends BaseMedia
{
    public static final String COLUMN_ALBUM = "album";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_TRACK_N = "track_n";
    public static final String COLUMN_TRACK_TOTAL = "track_total";
    public static final String COLUMN_TRACK_DISC = "track_disc";
    public static final String COLUMN_TRACK_DISC_TOTAL = "track_disc_total";
    public static final String COLUMN_RELEASE_YEAR = "release_year";
    public static final String COLUMN_ORIGINAL_YEAR = "original_year";
    public static final String COLUMN_ORIGINAL_MONTH = "original_month";
    public static final String COLUMN_ORIGINAL_DAY = "original_day";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_BITRATE = "bitrate";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_CHANNELS = "channels";

    @ColumnInfo(name = COLUMN_ALBUM)
    private final String album;

    @ColumnInfo(name = COLUMN_ARTIST)
    private final String artist;

    @ColumnInfo(name = COLUMN_TRACK_N)
    private final short trackN;

    @ColumnInfo(name = COLUMN_TRACK_TOTAL)
    private final short trackTotal;

    @ColumnInfo(name = COLUMN_TRACK_DISC)
    private final byte trackDisc;

    @ColumnInfo(name = COLUMN_TRACK_DISC_TOTAL)
    private final byte trackDiscTotal;

    @ColumnInfo(name = COLUMN_RELEASE_YEAR)
    private final short releaseYear;

    @ColumnInfo(name = COLUMN_ORIGINAL_YEAR)
    private final short originalYear;

    @ColumnInfo(name = COLUMN_ORIGINAL_MONTH)
    private final byte originalMonth;

    @ColumnInfo(name = COLUMN_ORIGINAL_DAY)
    private final byte originalDay;

    @ColumnInfo(name = COLUMN_GENRE)
    private final String genre;

    @ColumnInfo(name = COLUMN_BITRATE)
    private final int bitrate;

    @ColumnInfo(name = COLUMN_FORMAT)
    private final String format;

    @ColumnInfo(name = COLUMN_CHANNELS)
    private final byte channels;

    public Media(final String uri,final long crc32,final long size,final String id,
                 final String title,final String releaseArtist,final long lengthMs,
                 final String album,final String artist,final short trackN,final short trackTotal,
                 final byte trackDisc,final byte trackDiscTotal,final short releaseYear,
                 final short originalYear,final byte originalMonth,final byte originalDay,
                 final String genre,final int bitrate,final String format,final byte channels)
    {
        super(uri,crc32,size,id,title,releaseArtist,lengthMs);
        this.album = album;
        this.artist = artist;
        this.trackN = trackN;
        this.trackTotal = trackTotal;
        this.trackDisc = trackDisc;
        this.trackDiscTotal = trackDiscTotal;
        this.releaseYear = releaseYear;
        this.originalYear = originalYear;
        this.originalMonth = originalMonth;
        this.originalDay = originalDay;
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
    }

    @Ignore
    public Media(final String uri,final String title,final String releaseArtist,final long lengthMs,
                 final String album,final String artist,final short trackN,final short trackTotal,
                 final byte trackDisc,final byte trackDiscTotal,final short releaseYear,
                 final short originalYear,final byte originalMonth,final byte originalDay,
                 final String genre,final int bitrate,final String format,final byte channels)
            throws IOException
    {
        super(uri,title,releaseArtist,lengthMs);
        this.album = album;
        this.artist = artist;
        this.trackN = trackN;
        this.trackTotal = trackTotal;
        this.trackDisc = trackDisc;
        this.trackDiscTotal = trackDiscTotal;
        this.releaseYear = releaseYear;
        this.originalYear = originalYear;
        this.originalMonth = originalMonth;
        this.originalDay = originalDay;
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
    }

    public static Media loadMedia(String uri) throws IOException
    {
        FileInputStream stream = new FileInputStream(uri);
        long crc32 = calculateCRC32(stream);
        long size = calculateSize(stream);
    }

}
