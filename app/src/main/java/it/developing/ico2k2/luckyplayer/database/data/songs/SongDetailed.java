package it.developing.ico2k2.luckyplayer.database.data.songs;

import android.content.ContentUris;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import it.developing.ico2k2.luckyplayer.database.Optimized;
import it.developing.ico2k2.luckyplayer.database.data.BaseSong;

@Entity
public class SongDetailed extends BaseSong
{
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM = "album";
    public static final String COLUMN_ALBUM_ARTIST = "album_artist";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_TRACK_N = "track_n";
    public static final String COLUMN_TRACK_TOTAL = "track_total";
    public static final String COLUMN_YEAR_RELEASE = "release_year";
    public static final String COLUMN_YEAR_ORIGINAL = "original_year";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_LYRICS = "lyrics";
    public static final String COLUMN_BPM = "bpm";
    public static final String COLUMN_KEY_INIT = "init_key";
    public static final String COLUMN_BITRATE = "bitrate";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_CHANNELS = "channels";
    public static final String COLUMN_VBR = "vbr";
    public static final String COLUMN_LOSSLESS = "lossless";

    public static final short TRACK_N_MIN = 1;
    public static final short TRACK_N_MAX = Optimized.byte256maxFromMin(TRACK_N_MIN);
    public static final short BPM_MIN = 0;
    public static final short BPM_MAX = Optimized.byte256maxFromMin(BPM_MIN);

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_URI)
    private final String uri;

    @ColumnInfo(name = COLUMN_TITLE)
    private final String title;

    @ColumnInfo(name = COLUMN_ALBUM)
    private final String album;

    @ColumnInfo(name = COLUMN_ALBUM_ARTIST)
    private final String albumArtist;

    @ColumnInfo(name = COLUMN_ARTIST)
    private final String artist;

    @ColumnInfo(name = COLUMN_LENGTH)
    private final short length;

    @ColumnInfo(name = COLUMN_TRACK_N)
    private final byte memTrackN;

    @ColumnInfo(name = COLUMN_TRACK_TOTAL)
    private final byte memTrackTotal;

    @ColumnInfo(name = COLUMN_YEAR_RELEASE)
    private final short releaseYear;

    @ColumnInfo(name = COLUMN_YEAR_ORIGINAL)
    private final short originalYear;

    @ColumnInfo(name = COLUMN_GENRE)
    private final String genre;

    @ColumnInfo(name = COLUMN_LYRICS)
    private final String lyrics;

    @ColumnInfo(name = COLUMN_BPM)
    private final byte memBpm;

    @ColumnInfo(name = COLUMN_KEY_INIT)
    private final String initKey;

    @ColumnInfo(name = COLUMN_BITRATE)
    private final short bitrate;

    @ColumnInfo(name = COLUMN_FORMAT)
    private final String format;

    @ColumnInfo(name = COLUMN_CHANNELS)
    private final byte channels;

    @ColumnInfo(name = COLUMN_VBR)
    private final boolean vbr;

    @ColumnInfo(name = COLUMN_LOSSLESS)
    private final boolean lossless;

    public SongDetailed(
            @NonNull String uri,String title,String album,String albumArtist,String artist,
            short length,byte memTrackN,byte memTrackTotal,short releaseYear,short originalYear,
            String genre,String lyrics,byte memBpm,String initKey,short bitrate,String format,
            byte channels,boolean vbr,boolean lossless)
    {
        this.uri = uri;
        this.title = title;
        this.album = album;
        this.albumArtist = albumArtist;
        this.artist = artist;
        this.length = length;
        this.memTrackN = memTrackN;
        this.memTrackTotal = memTrackTotal;
        this.releaseYear = releaseYear;
        this.originalYear = originalYear;
        this.genre = genre;
        this.lyrics = lyrics;
        this.memBpm = memBpm;
        this.initKey = initKey;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.vbr = vbr;
        this.lossless = lossless;
    }

    @Ignore
    public SongDetailed(@NonNull String uri,String title,String album,String albumArtist,String artist,
                        short length,short memTrackN,short memTrackTotal,short releaseYear,short originalYear,
                        String genre,String lyrics,byte memBpm,String initKey,short bitrate,String format,
                        byte channels,boolean vbr,boolean lossless)
    {
        this(uri,title,album,albumArtist,artist,length,
             Optimized.byte256((short) (memTrackN - TRACK_N_MIN)),
             Optimized.byte256((short) (memTrackTotal - TRACK_N_MIN)),releaseYear,originalYear,
             genre,lyrics,
             Optimized.byte256((short) (memBpm - BPM_MIN)),initKey,bitrate,format,channels,
             vbr,lossless);
    }

    public String getUri()
    {
        return uri;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAlbum()
    {
        return album;
    }

    public String getAlbumArtist()
    {
        return albumArtist;
    }

    public String getArtist()
    {
        return artist;
    }

    public byte getMemTrackN(){
        return memTrackN;
    }

    public short getTrackN(){
        return Optimized.shortFromByte256(memTrackN);
    }

    public byte getMemTrackTotal(){
        return memTrackTotal;
    }

    public short getTrackTotal(){
        return Optimized.shortFromByte256(memTrackTotal);
    }

    public short getReleaseYear(){
        return releaseYear;
    }

    public short getOriginalYear(){
        return originalYear;
    }

    public short getLength(){
        return length;
    }

    public String getGenre(){
        return genre;
    }

    public String getLyrics(){
        return lyrics;
    }

    public byte getMemBpm()
    {
        return memBpm;
    }

    public short getBpm()
    {
        return Optimized.shortFromByte256(memBpm);
    }

    public String getInitKey(){
        return initKey;
    }

    public short getBitrate()
    {
        return bitrate;
    }

    public String getFormat(){
        return format;
    }

    public byte getChannels(){
        return channels;
    }

    public boolean getVbr(){
        return vbr;
    }

    public boolean getLossless(){
        return lossless;
    }

    public static SongDetailed loadFromUri(String uri)
    {
        SongDetailed result;
        AudioFile file;
        try
        {
            file = AudioFileIO.readMagic(new java.io.File(uri));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            file = null;
        }
        if(file != null)
        {
            Tag tag = file.getTag();
            AudioHeader header = file.getAudioHeader();
            result = new SongDetailed(
                    uri,
                    retrieveField(tag, FieldKey.TITLE,null),
                    retrieveField(tag, FieldKey.ALBUM,null),
                    retrieveField(tag, FieldKey.ALBUM_ARTIST,null),
                    retrieveField(tag, FieldKey.ARTIST,null),
                    (short)header.getTrackLength(),
                    Short.parseShort(retrieveField(tag, FieldKey.TRACK,"0")),
                    Short.parseShort(retrieveField(tag, FieldKey.TRACK_TOTAL,"0")),
                    Short.parseShort(retrieveField(tag, FieldKey.YEAR,"0")),
                    Short.parseShort(retrieveField(tag, FieldKey.ORIGINAL_YEAR,"0")),
                    retrieveField(tag, FieldKey.GENRE,null),
                    retrieveField(tag, FieldKey.LYRICS,null),
                    Byte.parseByte(retrieveField(tag, FieldKey.BPM,"0")),
                    retrieveField(tag, FieldKey.KEY,null),
                    (short)header.getBitRateAsNumber(),
                    header.getFormat(),
                    Byte.parseByte(header.getChannels()),
                    header.isVariableBitRate(),
                    header.isLossless());
            /*result = new Song(
                    retrieveField(tag, FieldKey.TITLE,null),
                    retrieveField(tag,FieldKey.ALBUM,null),
                    retrieveField(tag,FieldKey.ALBUM_ARTIST,null),
                    Byte.parseByte(retrieveField(tag,FieldKey.TRACK,"0")),
                    file.getAudioHeader().getTrackLength(),
                    Short.parseShort(retrieveField(tag,FieldKey.ORIGINAL_YEAR,retrieveField(tag,FieldKey.YEAR,"0"))),
                    tag.getFirst(retrieveField(tag,FieldKey.GENRE,null)),
                    tag.getFirst(retrieveField(tag,FieldKey.LYRICS,null)));*/
        }
        else
            result = null;
        return result;
    }

    private static String retrieveField(Tag tag,FieldKey key,String defaultValue)
    {
        String result;
        try
        {
            result = tag.getFirst(key);
        }
        catch (Exception e)
        {
            result = defaultValue;
        }
        return result;
    }

    public String getLengthText()
    {
        short hours = (short)(length / 1000 / 60 / 60);
        byte minutes = (byte)(length / 1000 / 60 - hours * 60);
        byte seconds = (byte)(length / 1000 - hours * 60 * 60 - minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }

    @Override
    public @NotNull String toString()
    {
        return String.format(Locale.getDefault(),"Song with uri %s with title %s by %s," +
                        "duration: %s",getUri(),getTitle(),getAlbumArtist(),getTextualLength());
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof SongDetailed)
            {
                result = uri.equals(((SongDetailed)o).getUri());
            }
        }
        return result;
    }

}
