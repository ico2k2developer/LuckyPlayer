package it.developing.ico2k2.luckyplayer.database.file.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;

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
    public static final String COLUMN_RELEASE_DATE = "release date";
    public static final String COLUMN_ORIGINAL_DATE = "original date";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_BITRATE = "bitrate";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_CHANNELS = "channels";
    public static final String COLUMN_LOSSLESS = "lossless";
    public static final String COLUMN_FAVOURITE = "like";

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

    @ColumnInfo(name = COLUMN_RELEASE_DATE)
    private final String releaseDate;

    @ColumnInfo(name = COLUMN_ORIGINAL_DATE)
    private final String originalDate;

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

    public Media(final String uri,final long size,final long lastModified,
                 final short volume,final long id,final String title,final String releaseArtist,
                 final long lengthMs,final String album,final String artist,final short trackN,
                 final short trackTotal,final byte trackDisc,final byte trackDiscTotal,
                 final String releaseDate,final String originalDate,final String genre,
                 final long bitrate,final String format,final byte channels,final boolean lossless,
                 final boolean like)
    {
        super(uri,size,lastModified,volume,id,title,releaseArtist,lengthMs);
        this.album = album;
        this.artist = artist;
        this.trackN = trackN;
        this.trackTotal = trackTotal;
        this.trackDisc = trackDisc;
        this.trackDiscTotal = trackDiscTotal;
        this.releaseDate = releaseDate;
        this.originalDate = originalDate;
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.lossless = lossless;
        this.like = like;
    }

    @Ignore
    public Media(final File file,final short volume,final long id,final String title,
                 final String releaseArtist,final long lengthMs,final String album,
                 final String artist,final short trackN,final short trackTotal,
                 final byte trackDisc,final byte trackDiscTotal,final String releaseDate,
                 final String originalDate,final String genre,final long bitrate,
                 final String format,final byte channels,final boolean lossless,final boolean like)
            throws IOException
    {
        super(file,volume,id,title,releaseArtist,lengthMs);
        this.album = album;
        this.artist = artist;
        this.trackN = trackN;
        this.trackTotal = trackTotal;
        this.trackDisc = trackDisc;
        this.trackDiscTotal = trackDiscTotal;
        this.releaseDate = releaseDate;
        this.originalDate = originalDate;
        this.genre = genre;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.lossless = lossless;
        this.like = like;
    }

    public static Media loadMedia(File file,boolean favourite,short volume,long id) throws Exception
    {
        AudioFile audioFile = AudioFileIO.read(file);
        AudioHeader header = audioFile.getAudioHeader();
        Tag tag = audioFile.getTag();

        String title,releaseArtist,artist,album,genre,date,origDate;
        short trackN,trackTotal;
        byte trackDisk,trackDiskTotal;
        releaseArtist = artist = album = genre = date = origDate = null;
        trackN = trackTotal = 0;
        trackDisk = trackDiskTotal = 0;

        if(tag.hasField(FieldKey.TITLE))
            title = tag.getFirstField(FieldKey.TITLE).toString();
        else
            title = audioFile.getFile().getName();
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

        if(hasValidField(tag,FieldKey.TRACK))
            trackN = parseShort(tag.getFirstField(FieldKey.TRACK).toString());
        if(hasValidField(tag,FieldKey.TRACK_TOTAL))
            trackTotal = parseShort(tag.getFirstField(FieldKey.TRACK_TOTAL).toString());
        if(hasValidField(tag,FieldKey.DISC_NO))
            trackDisk = parseByte(tag.getFirstField(FieldKey.DISC_NO).toString());
        if(hasValidField(tag,FieldKey.DISC_TOTAL))
            trackDiskTotal = parseByte(tag.getFirstField(FieldKey.DISC_TOTAL).toString());
        if(hasValidField(tag,FieldKey.YEAR))
            date = tag.getFirstField(FieldKey.YEAR).toString();
        if(hasValidField(tag,FieldKey.ORIGINAL_YEAR))
            origDate = tag.getFirstField(FieldKey.ORIGINAL_YEAR).toString();

        if(hasValidField(tag,FieldKey.GENRE))
            genre = tag.getFirstField(FieldKey.GENRE).toString();

        return new Media(file,volume,id,title,releaseArtist,
                header.getNoOfSamples() == null ? 0 :
                        (header.getNoOfSamples() * 1000L / ((long)header.getSampleRateAsNumber())),
                album,artist,trackN,trackTotal,trackDisk,trackDiskTotal,date,origDate,genre,
                header.getBitRateAsNumber(), header.getFormat(),
                parseChannel(header.getChannels()),header.isLossless(),favourite);

    }

    @Nullable
    private static String getLeadingDigits(@Nullable String value)
    {
        if(value == null)
            return null;
        int i;
        for(i = 0; i < value.length(); i++)
        {
            if(!Character.isDigit(value.charAt(i)))
                break;
        }
        if(i != value.length())
            value = value.substring(0,i);
        return value;
    }

    private static short parseShort(@Nullable String value)
    {
        value = getLeadingDigits(value);
        return TextUtils.isEmpty(value) ? 0 : Short.parseShort(value);
    }

    private static byte parseByte(@Nullable String value)
    {
        value = getLeadingDigits(value);
        return TextUtils.isEmpty(value) ? 0 : Byte.parseByte(value);
    }

    private static byte parseChannel(String channel)
    {
        byte result = 0;
        if(TextUtils.isEmpty(channel))
            return result;
        try
        {
            result = Byte.parseByte(channel);
        }
        catch(NumberFormatException e)
        {
            switch(channel.toLowerCase())
            {
                case "mono":
                {
                    result = 1;
                    break;
                }
                case "stereo":
                {
                    result = 2;
                    break;
                }
            }
        }
        return result;
    }

    private static boolean hasValidField(Tag tag,FieldKey field)
    {
        boolean result = false;
        if(tag.hasField(field))
            result = !TextUtils.isEmpty(tag.getFirstField(field).toString());
        return result;
    }

    public String getAlbum()
    {
        return album;
    }

    public String getArtist()
    {
        return album;
    }

    public short getTrackN()
    {
        return trackN;
    }

    public short getTrackTotal()
    {
        return trackTotal;
    }

    public byte getTrackDisc()
    {
        return trackDisc;
    }

    public byte getTrackDiscTotal()
    {
        return trackDiscTotal;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public String getOriginalDate()
    {
        return originalDate;
    }

    public String getGenre()
    {
        return genre;
    }

    public long getBitrate()
    {
        return bitrate;
    }

    public String getFormat()
    {
        return format;
    }

    public byte getChannels()
    {
        return channels;
    }

    public boolean getLossless()
    {
        return lossless;
    }

    public boolean getLike()
    {
        return like;
    }
}
