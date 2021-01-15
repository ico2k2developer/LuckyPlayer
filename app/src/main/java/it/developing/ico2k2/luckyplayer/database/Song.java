package it.developing.ico2k2.luckyplayer.database;

import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Locale;

@Entity
public class Song
{
    public static final String[] columns =
    {
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.TITLE,
        MediaStore.MediaColumns.ALBUM,
        MediaStore.MediaColumns.ALBUM_ARTIST,
        MediaStore.MediaColumns.CD_TRACK_NUMBER,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.YEAR,
        MediaStore.MediaColumns.GENRE,
    };

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private final String id;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "album")
    private final String album;

    @ColumnInfo(name = "album_artist")
    private final String albumArtist;

    @ColumnInfo(name = "plays_count")
    private int playsCount;

    @ColumnInfo(name = "last_play_day")
    private byte lastPlayDay;

    @ColumnInfo(name = "last_play_month")
    private byte lastPlayMonth;

    @ColumnInfo(name = "last_play_year")
    private short lastPlayYear;

    @ColumnInfo(name = "last_play_hour")
    private byte lastPlayHour;

    @ColumnInfo(name = "last_play_minute")
    private byte lastPlayMinute;

    @ColumnInfo(name = "length")
    private final int length;

    @ColumnInfo(name = "track_number")
    private final byte trackN;

    @ColumnInfo(name = "genre")
    private String genre;

    @ColumnInfo(name = "year")
    private final short year;

    @ColumnInfo(name = "lyrics")
    private String lyrics;

    public static class Date
    {
        private final byte day;
        private final byte month;
        private final short year;
        private final byte hour;
        private final byte minute;

        public Date(byte day,byte month,short year,byte hour,byte minute)
        {
            this.day = day;
            this.month = month;
            this.year = year;
            this.hour = hour;
            this.minute = minute;
        }

        public Date()
        {
            java.util.Date date = Calendar.getInstance().getTime();
            day = (byte)date.getDay();
            month = (byte)date.getMonth();
            year = (short)date.getYear();
            hour = (byte)date.getHours();
            minute = (byte)date.getMinutes();
        }

        public java.util.Date toDate()
        {
            Calendar c = Calendar.getInstance();
            c.set(year,month,day,hour,minute);
            return c.getTime();
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
    }

    public Song(String id,String title,String album,String albumArtist,byte trackN,int playsCount,byte lastPlayDay,byte lastPlayMonth,short lastPlayYear,byte lastPlayHour,byte lastPlayMinute,int length,short year,String genre,String lyrics)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.albumArtist = albumArtist;
        this.trackN = trackN;
        this.playsCount = playsCount;
        this.lastPlayDay= lastPlayDay;
        this.lastPlayMonth = lastPlayMonth;
        this.lastPlayYear = lastPlayYear;
        this.lastPlayHour = lastPlayHour;
        this.lastPlayMinute = lastPlayMinute;
        this.length = length;
        this.year = year;
        this.genre = genre;
        this.lyrics = lyrics;
    }

    @Ignore
    public Song(String id,String title,String album,String albumArtist,byte trackN,int playsCount,@NonNull Date lastPlay,int length,short year,@Nullable String genre,@Nullable String lyrics)
    {
        this(id,title,album,albumArtist,trackN,playsCount,lastPlay.getDay(),lastPlay.getMonth(),lastPlay.getYear(),
                lastPlay.getHour(),lastPlay.getMinute(),length,year,genre,lyrics);
    }

    @Ignore
    public Song(String id,String title,String album,String albumArtist,byte trackN,int playsCount,@NonNull Date lastPlay,int length,short year)
    {
        this(id,title,album,albumArtist,trackN,playsCount,lastPlay,length,year,null,null);
    }

    @Ignore
    public Song(String id,String title,String album,String albumArtist,byte trackN,int length)
    {
        this(id,title,album,albumArtist,trackN,0,new Date(),length,(short)0);
    }

    @Ignore
    public Song(String id,String title,String album,String albumArtist,int length)
    {
        this(id,title,album,albumArtist,(byte)0,0,new Date(),length,(short)0);
    }

    @NotNull
    public String getId()
    {
        return id;
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

    public int getPlaysCount()
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
    }

    public byte getTrackN(){
        return trackN;
    }

    public short getYear(){
        return year;
    }

    public int getLength(){
        return length;
    }

    public String getTextualLength()
    {
        short hours = (short)(length / 1000 / 60 / 60);
        byte minutes = (byte)(length / 1000 / 60 - hours * 60);
        byte seconds = (byte)(length / 1000 - hours * 60 * 60 - minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }

    public String getGenre(){
        return genre;
    }

    public String getLyrics(){
        return lyrics;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public void setLyrics(String lyrics){
        this.lyrics = lyrics;
    }

    public Date getLastPlay(){
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
    }

    @Override
    public String toString()
    {
        return String.format(Locale.getDefault(),"%s from %s by %s, length: %s; played %d times, last time was %s",
                title,album,albumArtist,getTextualLength(),playsCount,getLastPlay().toString());
    }

    @Override
    public boolean equals(Object o)
    {
        return id.equals(o instanceof Song ? ((Song)o).id : null);
    }
}