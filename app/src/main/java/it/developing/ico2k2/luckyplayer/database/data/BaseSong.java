package it.developing.ico2k2.luckyplayer.database.data;

import org.jetbrains.annotations.NotNull;

public abstract class BaseSong
{
    protected static String getTextualLength(long length)
    {
        length /= 1000;
        short hours = (short)(length / 60 / 60);
        byte minutes = (byte)(length / 60 - hours * 60);
        byte seconds = (byte)(length - hours * 60 * 60 - minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }
    protected String getTextualLength()
    {
        return getTextualLength(getLength());
    }

    /*
    private static final char SEPARATOR = '\f';

    @NotNull
    protected static String generateId(@NotNull String title, @NotNull String albumArtist, int length)
    {
        return title + SEPARATOR + albumArtist + SEPARATOR + getTextualLength(length);
    }*/

    @NotNull
    public abstract String getId();

    public abstract String getTitle();

    public abstract String getAlbumArtist();

    public abstract long getLength();
}
