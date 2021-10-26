package it.developing.ico2k2.luckyplayer.database.data.songs.plays;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import it.developing.ico2k2.luckyplayer.database.Optimized;
import it.developing.ico2k2.luckyplayer.database.data.BaseSong;
import it.developing.ico2k2.luckyplayer.database.date.AbsoluteDate1970To2225;

@Entity
public class Play extends BaseSong
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uri")
    private final String uri;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "albumartist")
    private final String albumArtist;

    @ColumnInfo(name = "length")
    private final short length;

    @ColumnInfo(name = "plays_count")
    private final int playsCount;

    @ColumnInfo(name = "last_play_day")
    private final byte lastPlayDay;

    @ColumnInfo(name = "last_play_month")
    private final byte lastPlayMonth;

    @ColumnInfo(name = "last_play_year")
    private final byte lastPlayMemYear;

    @ColumnInfo(name = "last_play_hour")
    private final byte lastPlayHour;

    @ColumnInfo(name = "last_play_minute")
    private final byte lastPlayMinute;

    public Play(@NotNull String uri, @NotNull String title, @NotNull String albumArtist, short length,
                int playsCount, byte lastPlayDay, byte lastPlayMonth, byte lastPlayMemYear,
                byte lastPlayHour, byte lastPlayMinute)
    {
        this.uri = uri;
        this.title = title;
        this.albumArtist = albumArtist;
        this.length = length;
        this.playsCount = playsCount;
        this.lastPlayDay = lastPlayDay;
        this.lastPlayMonth = lastPlayMonth;
        this.lastPlayMemYear = lastPlayMemYear;
        this.lastPlayHour = lastPlayHour;
        this.lastPlayMinute = lastPlayMinute;
    }

    @Ignore
    public Play(@NotNull String uri, @NotNull String title,@NotNull String albumArtist,short length,int playsCount,
                byte lastPlayDay, byte lastPlayMonth, short lastPlayYear,
                byte lastPlayHour, byte lastPlayMinute)
    {
        this(uri,title,albumArtist,length,playsCount,
                lastPlayDay,lastPlayMonth, Optimized.byte256(lastPlayYear),lastPlayHour,lastPlayMinute);
    }

    @Ignore
    public Play(@NotNull String uri, @NotNull String title, @NotNull String albumArtist, short length, int playsCount,
                AbsoluteDate1970To2225 lastPlay)
    {
        this(uri,title,albumArtist,length,playsCount,
                lastPlay.getDay(),lastPlay.getMonth(),lastPlay.getMemYear(),
                lastPlay.getHour(),lastPlay.getMinute());
    }

    @Ignore
    public Play(@NotNull String uri,@NotNull String title,@NotNull String albumArtist,short length,int playsCount)
    {
        this(uri,title,albumArtist,length,playsCount,new AbsoluteDate1970To2225());
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

    public String getAlbumArtist()
    {
        return albumArtist;
    }

    public short getLength()
    {
        return length;
    }

    public int getPlaysCount()
    {
        return playsCount;
    }

    public AbsoluteDate1970To2225 getLastPlay()
    {
        return new AbsoluteDate1970To2225(lastPlayDay,lastPlayMonth, lastPlayMemYear,lastPlayHour,lastPlayMinute);
    }

    public byte getLastPlayDay()
    {
        return lastPlayDay;
    }

    public byte getLastPlayMonth() {
        return lastPlayMonth;
    }

    public byte getLastPlayMemYear() {
        return lastPlayMemYear;
    }

    public short getLastPlayYear() {
        return AbsoluteDate1970To2225.memYearToActualYear(getLastPlayMemYear());
    }

    public byte getLastPlayHour() {
        return lastPlayHour;
    }

    public byte getLastPlayMinute() {
        return lastPlayMinute;
    }

    /*@Nullable
    public static BaseSong loadFromUri(Uri uri)
    {
        BaseSong result;
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
            result = new BaseSong(
                    retrieveField(tag, FieldKey.TITLE,null),
                    retrieveField(tag,FieldKey.ALBUM,null),
                    retrieveField(tag,FieldKey.ALBUM_ARTIST,null),
                    Byte.parseByte(retrieveField(tag,FieldKey.TRACK,"0")),
                    file.getAudioHeader().getTrackLength(),
                    Short.parseShort(retrieveField(tag,FieldKey.ORIGINAL_YEAR,retrieveField(tag,FieldKey.YEAR,"0"))),
                    tag.getFirst(retrieveField(tag,FieldKey.GENRE,null)),
                    tag.getFirst(retrieveField(tag,FieldKey.LYRICS,null)));
        }
        else
            result = null;
        return result;
    }*/

    /*private static String retrieveField(Tag tag,FieldKey key,String defaultValue)
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
    }*/

    /*public static class Date
    {
        private final byte day;
        private final byte month;
        private final byte memYear;
        private final byte hour;
        private final byte minute;

        public Date(byte day,byte month,byte memYear,byte hour,byte minute)
        {
            this.day = day;
            this.month = month;
            this.year = year;
            this.hour = hour;
            this.minute = minute;
        }

        public Date()
        {
            Calendar c = Calendar.getInstance();
            day = (byte)c.get(Calendar.DAY_OF_MONTH);
            month = (byte)c.get(Calendar.MONTH);
            year = (short)c.get(Calendar.YEAR);
            hour = (byte)c.get(Calendar.HOUR_OF_DAY);
            minute = (byte)c.get(Calendar.MINUTE);
        }

        public Calendar toDate()
        {
            Calendar c = Calendar.getInstance();
            c.set(year,month,day,hour,minute);
            return c;
        }

        public byte getDay(){
            return day;
        }

        public byte getMonth(){
            return month;
        }

        public short getYear(){
            return year;
        }

        public byte getHour(){
            return hour;
        }

        public byte getMinute(){
            return minute;
        }

        @Override
        public @NotNull String toString()
        {
            return day + "/" + (month + 1) + "/" + year + " " + hour + ":" + minute;
        }
    }*/

    /*public int getPlaysCount()
    {
        return playsCount;
    }

    public void setPlaysCount(int count)
    {
        playsCount = count;
    }

    public byte getLastPlayDay(){
        return lastPlayDay;
    }

    public byte getLastPlayMonth(){
        return lastPlayMonth;
    }

    public short getLastPlayYear(){
        return lastPlayYear;
    }

    public byte getLastPlayHour(){
        return lastPlayHour;
    }

    public byte getLastPlayMinute(){
        return lastPlayMinute;
    }*/

    /*public Date getLastPlay(){
        return new Date(lastPlayDay,lastPlayMonth,lastPlayYear,lastPlayHour,lastPlayMinute);
    }

    public void updateLastPlayDate()
    {
        setLastPlay(new Date());
    }

    private void setLastPlay(@NonNull Date lastPlay)
    {
        lastPlayDay = lastPlay.getDay();
        lastPlayMonth = lastPlay.getMonth();
        lastPlayYear = lastPlay.getYear();
        lastPlayHour = lastPlay.getHour();
        lastPlayMinute = lastPlay.getMinute();
    }*/

    @Override
    public @NotNull String toString()
    {
        return String.format(Locale.getDefault(),"Song with uri %s with title %s by %s," +
                        "duration: %s, played %d times, last time was: %s",
                             getId(),getTitle(),getAlbumArtist(),getTextualLength(),getPlaysCount(),getLastPlay());
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof Play)
            {
                result = uri.equals(((Play)o).getId());
            }
        }
        return result;
    }

}
