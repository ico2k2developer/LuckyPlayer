package it.developing.ico2k2.luckyplayer.database.file.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;

@Entity(tableName = DATABASE_SONGS)
public class Media extends BaseMedia
{
    public static final String COLUMN_ALBUM = "album";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_TRACK_N = "track n";
    public static final String COLUMN_TRACK_TOTAL = "track total";
    public static final String COLUMN_TRACK_DISC = "track disc";
    public static final String COLUMN_TRACK_DISC_TOTAL = "track disc total";
    public static final String COLUMN_RELEASE_YEAR = "release year";
    public static final String COLUMN_ORIGINAL_YEAR = "original year";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_BITRATE = "bitrate";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_CHANNELS = "channels";
    public static final String COLUMN_LOSSLESS = "lossless";
    public static final String COLUMN_FAVOURITE = "like";
    public static final String COLUMN_ID_MEDIASTORE = "mediastore id";

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

    @ColumnInfo(name = COLUMN_GENRE)
    private final String genre;

    @ColumnInfo(name = COLUMN_BITRATE)
    private final long bitrate;

    @ColumnInfo(name = COLUMN_FORMAT)
    private final String format;

    @ColumnInfo(name = COLUMN_CHANNELS)
    private final byte channels;

    @ColumnInfo(name = COLUMN_LOSSLESS)
    private final boolean lossless;

    @ColumnInfo(name = COLUMN_FAVOURITE)
    private final boolean like;

    @ColumnInfo(name = COLUMN_ID_MEDIASTORE)
    private final long idMediaStore;

    public Media(final String uri,final long crc32,final long size,final String id,
                 final String title,final String releaseArtist,final long lengthMs,
                 final String album,final String artist,final short trackN,final short trackTotal,
                 final byte trackDisc,final byte trackDiscTotal,final short releaseYear,
                 final short originalYear,final String genre,final long bitrate,
                 final String format,final byte channels,final boolean lossless,final boolean like,
                 final long idMediaStore)
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
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.lossless = lossless;
        this.like = like;
        this.idMediaStore = idMediaStore;
    }

    @Ignore
    public Media(final String uri,final String title,final String releaseArtist,final long lengthMs,
                 final String album,final String artist,final short trackN,final short trackTotal,
                 final byte trackDisc,final byte trackDiscTotal,final short releaseYear,
                 final short originalYear,final String genre,final long bitrate,
                 final String format,final byte channels,final boolean lossless,final boolean like,
                 final long idMediaStore)
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
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.lossless = lossless;
        this.like = like;
        this.idMediaStore = idMediaStore;
    }

    public static Media loadMedia(String uri,boolean favourite,long idMediaStore) throws Exception
    {
        AudioFile file = AudioFileIO.read(new File(uri));
        AudioHeader header = file.getAudioHeader();
        Tag tag = file.getTag();

        String title,releaseArtist = null,artist = null,album = null,genre = null;
        short trackN,trackTotal,year,origYear;
        byte trackDisk,trackDiskTotal;
        trackN = trackTotal = year = origYear = 0;
        trackDisk = trackDiskTotal = 0;

        if(tag.hasField(FieldKey.TITLE))
            title = tag.getFirstField(FieldKey.TITLE).toString();
        else
            title = file.getFile().getName();
        if(TextUtils.isEmpty(title))
            title = null;

        if(tag.hasField(FieldKey.ALBUM_ARTIST))
            releaseArtist = tag.getFirstField(FieldKey.ALBUM_ARTIST).toString();
        if(TextUtils.isEmpty(releaseArtist))
            releaseArtist = null;

        if(tag.hasField(FieldKey.ALBUM))
            album = tag.getFirstField(FieldKey.ALBUM).toString();
        if(TextUtils.isEmpty(album))
            album = null;

        if(tag.hasField(FieldKey.ARTIST))
            artist = tag.getFirstField(FieldKey.ARTIST).toString();
        if(TextUtils.isEmpty(artist))
        {
            if(TextUtils.isEmpty(releaseArtist))
                artist = null;
            else
                artist = releaseArtist;
        }

        if(tag.hasField(FieldKey.TRACK))
            trackN = Short.parseShort(tag.getFirstField(FieldKey.TRACK).toString());
        if(tag.hasField(FieldKey.TRACK_TOTAL))
            trackTotal = Short.parseShort(tag.getFirstField(FieldKey.TRACK_TOTAL).toString());
        if(tag.hasField(FieldKey.DISC_NO))
            trackDisk = Byte.parseByte(tag.getFirstField(FieldKey.DISC_NO).toString());
        if(tag.hasField(FieldKey.DISC_TOTAL))
            trackDiskTotal = Byte.parseByte(tag.getFirstField(FieldKey.DISC_TOTAL).toString());
        if(tag.hasField(FieldKey.YEAR))
            year = Short.parseShort(tag.getFirstField(FieldKey.YEAR).toString());
        if(tag.hasField(FieldKey.ORIGINAL_YEAR))
            origYear = Short.parseShort(tag.getFirstField(FieldKey.ORIGINAL_YEAR).toString());

        if(tag.hasField(FieldKey.GENRE))
            genre = tag.getFirstField(FieldKey.GENRE).toString();
        if(TextUtils.isEmpty(genre))
            genre = null;

        return new Media(uri,title,releaseArtist,
                (header.getNoOfSamples() * 1000L / ((long)header.getSampleRateAsNumber())),
                album,artist,trackN,trackTotal,trackDisk,trackDiskTotal,year,origYear,genre,
                header.getBitRateAsNumber(), header.getFormat(),
                Byte.parseByte(header.getChannels()),header.isLossless(),favourite,idMediaStore);

    }

}
