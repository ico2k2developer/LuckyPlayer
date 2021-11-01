package it.developing.ico2k2.luckyplayer.database.data.songs;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import it.developing.ico2k2.luckyplayer.database.Database;
import it.developing.ico2k2.luckyplayer.database.Optimized;
import it.developing.ico2k2.luckyplayer.database.data.BaseSong;

@Entity
public class SongDetailed extends BaseSong
{
    public static final String COLUMN_ID = "id";
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

    protected static final byte VALUE_YES = 1;
    protected static final byte VALUE_NO = -1;
    protected static final byte VALUE_UNKNOWN = -1;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    private final String id;

    @ColumnInfo(name = COLUMN_TITLE)
    private final String title;

    @ColumnInfo(name = COLUMN_ALBUM)
    private final String album;

    @ColumnInfo(name = COLUMN_ALBUM_ARTIST)
    private final String albumArtist;

    @ColumnInfo(name = COLUMN_ARTIST)
    private final String artist;

    @ColumnInfo(name = COLUMN_LENGTH)
    private final long length;

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
    private final short bpm;

    @ColumnInfo(name = COLUMN_KEY_INIT)
    private final String initKey;

    @ColumnInfo(name = COLUMN_BITRATE)
    private final int bitrate;

    @ColumnInfo(name = COLUMN_FORMAT)
    private final String format;

    @ColumnInfo(name = COLUMN_CHANNELS)
    private final byte channels;

    @ColumnInfo(name = COLUMN_VBR)
    private final byte vbrCoded;

    @ColumnInfo(name = COLUMN_LOSSLESS)
    private final byte losslessCoded;

    public SongDetailed(
            @NonNull String id, String title, String album, String albumArtist, String artist,
            long length, byte memTrackN, byte memTrackTotal, short releaseYear, short originalYear,
            String genre, String lyrics, short bpm, String initKey, int bitrate, String format,
            byte channels, byte vbrCoded, byte losslessCoded)
    {
        this.id = id;
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
        this.bpm = bpm;
        this.initKey = initKey;
        this.bitrate = bitrate;
        this.format = format;
        this.channels = channels;
        this.vbrCoded = vbrCoded;
        this.losslessCoded = losslessCoded;
    }

    public SongDetailed(int tableId,int itemId,String title,String album,String albumArtist,String artist,
                                      long length,short trackN,short trackTotal,short releaseYear,short originalYear,
                                      String genre,String lyrics,short bpm,String initKey,int bitrate,String format,
                                      byte channels,boolean vbr,boolean lossless)
    {
        this(Database.generateId(tableId,itemId),title,album,albumArtist,artist,length,
                Optimized.byte256((short) (trackN - TRACK_N_MIN)),
                Optimized.byte256((short) (trackTotal - TRACK_N_MIN)),releaseYear,originalYear,
                genre,lyrics,bpm,initKey,bitrate,format,channels,
                vbr ? VALUE_YES : VALUE_NO,lossless ? VALUE_YES : VALUE_NO);
    }

    public SongDetailed(int tableId,int itemId, String title, String album, String albumArtist, String artist,
                        long length, short trackN, short trackTotal, short releaseYear, short originalYear,
                        String genre, String lyrics, short bpm, String initKey, int bitrate, String format,
                        byte channels)
    {
        this(Database.generateId(tableId,itemId),title,album,albumArtist,artist,length,
                Optimized.byte256((short) (trackN - TRACK_N_MIN)),
                Optimized.byte256((short) (trackTotal - TRACK_N_MIN)),releaseYear,originalYear,
                genre,lyrics,bpm,initKey,bitrate,format,channels,VALUE_UNKNOWN,VALUE_UNKNOWN);
    }

    public SongDetailed(int tableId,int itemId, String title, String album, String albumArtist, String artist,
                        long length, short trackN, short releaseYear, String genre, int bitrate)
    {
        this(tableId,itemId,title,album,albumArtist,artist,length,trackN,(short)-1,releaseYear,(short)-1,genre,
                null, (short) -1,null,bitrate,null,(byte)-1);
    }

    @NonNull
    public String getId()
    {
        return id;
    }

    public int getTableId()
    {
        return Database.getTableId(getId());
    }

    public int getItemId()
    {
        return Database.getItemId(getId());
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

    public long getLength(){
        return length;
    }

    public String getGenre(){
        return genre;
    }

    public String getLyrics(){
        return lyrics;
    }

    public short getBpm()
    {
        return bpm;
    }

    public String getInitKey(){
        return initKey;
    }

    public int getBitrate()
    {
        return bitrate;
    }

    public String getFormat(){
        return format;
    }

    public byte getChannels(){
        return channels;
    }

    @Nullable
    public Boolean isVbr()
    {
        Boolean result;
        switch(vbrCoded)
        {
            case VALUE_YES:
            {
                result = true;
                break;
            }
            case VALUE_NO:
            {
                result = false;
                break;
            }
            default:
            {
                result = null;
            }
        }
        return result;
    }

    @Nullable
    public Boolean isLossless(){
        Boolean result;
        switch(losslessCoded)
        {
            case VALUE_YES:
            {
                result = true;
                break;
            }
            case VALUE_NO:
            {
                result = false;
                break;
            }
            default:
            {
                result = null;
            }
        }
        return result;
    }

    @Nullable
    public Boolean isLossy(){
        Boolean lossless = isLossless();
        return lossless == null ? null : !lossless;
    }

    @Nullable
    public Boolean isCbr(){
        Boolean vbr = isVbr();
        return vbr == null ? null : !vbr;
    }

    @Deprecated
    public boolean getVbr()
    {
        return vbrCoded == VALUE_YES;
    }

    @Deprecated
    public boolean getLossless()
    {
        return losslessCoded == VALUE_YES;
    }

    public byte getVbrCoded()
    {
        return vbrCoded;
    }

    public byte getLosslessCoded()
    {
        return losslessCoded;
    }

    @Nullable
    public static SongDetailed loadFromUri(int tableId,int itemId,@NonNull String uri)
    {
        SongDetailed result = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        AudioFile file = null;
        Tag tag = null;
        AudioHeader header = null;
        try
        {
            retriever.setDataSource(uri);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            retriever = null;
        }
        try
        {
            file = AudioFileIO.read(new java.io.File(uri));
            tag = file.getTag();
            header = file.getAudioHeader();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(file != null)
        {
            long tmp;
            if(header.getNoOfSamples() != null)
                tmp = header.getNoOfSamples() * 1000L / header.getSampleRateAsNumber();
            else
                tmp = -1L;
            result = new SongDetailed(
                    tableId,
                    itemId,
                    retrieveTitle(retrieveField(tag, FieldKey.TITLE,null),uri),
                    retrieveField(tag, FieldKey.ALBUM,null),
                    retrieveField(tag, FieldKey.ALBUM_ARTIST,null),
                    retrieveField(tag, FieldKey.ARTIST,null),
                    tmp,
                    Short.parseShort(retrieveField(tag, FieldKey.TRACK,Short.toString(TRACK_N_MIN))),
                    Short.parseShort(retrieveField(tag, FieldKey.TRACK_TOTAL,Short.toString(TRACK_N_MIN))),
                    retrieveYear(retrieveField(tag, FieldKey.YEAR,"-1")),
                    retrieveYear(retrieveField(tag, FieldKey.ORIGINAL_YEAR,"-1")),
                    retrieveField(tag, FieldKey.GENRE,null),
                    retrieveField(tag, FieldKey.LYRICS,null),
                    Short.parseShort(retrieveField(tag, FieldKey.BPM,"-1")),
                    retrieveField(tag, FieldKey.KEY,null),
                    (int) header.getBitRateAsNumber(),
                    header.getFormat(),
                    retrieveChannels(header.getChannels()),
                    header.isVariableBitRate(),
                    header.isLossless());
        }
        else if(retriever != null)
        {
            result = new SongDetailed(
                    tableId,
                    itemId,
                    retrieveTitle(retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_TITLE,null),uri),
                    retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_ALBUM,null),
                    retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST,null),
                    retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_ARTIST,null),
                    Long.parseLong(retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_DURATION,"-1")),
                    Short.parseShort(retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER,Short.toString(TRACK_N_MIN))),
                    retrieveYear(retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_YEAR,"-1")),
                    retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_GENRE,null),
                    Integer.parseInt(retrieveField(retriever,MediaMetadataRetriever.METADATA_KEY_BITRATE,"-1")));
            retriever.release();
        }
        return result;
    }

    private static String retrieveTitle(String title,String uri)
    {
        if(TextUtils.isEmpty(title))
        {
            if(uri.contains("/"))
                uri = uri.substring(uri.lastIndexOf("/") + 1);
            if(uri.contains("."))
                uri = uri.substring(0,uri.indexOf("."));
            uri = uri.replace("_"," ");
            return uri;
        }
        else
            return title;
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
        return TextUtils.isEmpty(result) ? defaultValue : result;
    }

    private static String retrieveField(MediaMetadataRetriever retriever,int key,String defaultValue)
    {
        String result;
        try
        {
            result = retriever.extractMetadata(key);
        }
        catch (Exception e)
        {
            result = defaultValue;
        }
        return TextUtils.isEmpty(result) ? defaultValue : result;
    }

    private static short retrieveYear(String year)
    {
        if(year.length() >= 4)
            year = year.substring(0,4);
        return Short.parseShort(year);
    }

    private static byte retrieveChannels(String channels)
    {
        channels = channels.toLowerCase();
        if(channels.contains("mono"))
            return 1;
        else if(channels.contains("stereo"))
            return 2;
        else
            return Byte.parseByte(channels);
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
                        "duration: %s", getId(),getTitle(),getAlbumArtist(),getTextualLength());
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof SongDetailed)
            {
                result = id.equals(((SongDetailed)o).getId());
            }
        }
        return result;
    }

}
