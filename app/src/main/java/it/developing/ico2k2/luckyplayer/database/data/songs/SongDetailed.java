package it.developing.ico2k2.luckyplayer.database.data.songs;

import android.net.Uri;

import androidx.annotation.NonNull;
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

import java.io.File;
import java.util.Locale;

import it.developing.ico2k2.luckyplayer.database.Optimized;
import it.developing.ico2k2.luckyplayer.database.data.BaseSong;

@Entity
public class SongDetailed extends BaseSong
{
    public static final short TRACK_N_MIN = 1;
    public static final short TRACK_N_MAX = Optimized.byte256maxFromMin(TRACK_N_MIN);
    public static final short BPM_MIN = 0;
    public static final short BPM_MAX = Optimized.byte256maxFromMin(BPM_MIN);

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uri")
    private final String uri;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "album")
    private final String album;

    @ColumnInfo(name = "album_artist")
    private final String albumArtist;

    @ColumnInfo(name = "artist")
    private final String artist;

    @ColumnInfo(name = "length")
    private final short length;

    @ColumnInfo(name = "track_number")
    private final byte memTrackN;

    @ColumnInfo(name = "track_total")
    private final byte memTrackTotal;

    @ColumnInfo(name = "release_year")
    private final short releaseYear;

    @ColumnInfo(name = "orig_year")
    private final short originalYear;

    @ColumnInfo(name = "genre")
    private final String genre;

    @ColumnInfo(name = "lyrics")
    private final String lyrics;

    @ColumnInfo(name = "bpm")
    private final byte memBpm;

    @ColumnInfo(name = "init_key")
    private final String initKey;

    @ColumnInfo(name = "bitrate")
    private final short bitrate;

    @ColumnInfo(name = "format")
    private final String format;

    @ColumnInfo(name = "channels")
    private final byte channels;

    @ColumnInfo(name = "vbr")
    private final boolean vbr;

    @ColumnInfo(name = "lossless")
    private final boolean lossless;

    public SongDetailed(@NotNull String uri, String title, String album, String albumArtist, String artist,
                        short length, byte memTrackN, byte memTrackTotal, short releaseYear, short originalYear,
                        String genre, String lyrics, byte memBpm, String initKey, short bitrate, String format,
                        byte channels, boolean vbr, boolean lossless)
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
    public SongDetailed(@NotNull String uri, String title, String album, String albumArtist, String artist,
                        short length, short memTrackN, short memTrackTotal, short releaseYear, short originalYear,
                        String genre, String lyrics, byte memBpm, String initKey, short bitrate, String format,
                        byte channels, boolean vbr, boolean lossless)
    {
        this(uri,title,album,albumArtist,artist,length,
                Optimized.byte256((short) (memTrackN - TRACK_N_MIN)),
                Optimized.byte256((short) (memTrackTotal - TRACK_N_MIN)),releaseYear, originalYear,
                genre,lyrics,
                Optimized.byte256((short) (memBpm - BPM_MIN)),initKey,bitrate,format,channels,
                vbr,lossless);
    }

    @NotNull
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

    public static SongDetailed loadFromUri(Uri uri)
    {
        SongDetailed result;
        AudioFile file;
        try
        {
            file = AudioFileIO.read(new File(uri.getPath()));
        }
        catch (Exception e)
        {
            file = null;
        }
        if(file != null)
        {
            Tag tag = file.getTag();
            AudioHeader header = file.getAudioHeader();
            result = new SongDetailed(
                    uri.getPath(),
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
