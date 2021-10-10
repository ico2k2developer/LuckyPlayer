package it.developing.ico2k2.luckyplayer.database.data;

import org.jetbrains.annotations.NotNull;

public abstract class BaseSong
{
    private static final char SEPARATOR = '\f';

    protected static String getTextualLength(int length)
    {
        short hours = (short)(length / 1000 / 60 / 60);
        byte minutes = (byte)(length / 1000 / 60 - hours * 60);
        byte seconds = (byte)(length / 1000 - hours * 60 * 60 - minutes * 60);
        return hours + ":" + minutes + ":" + seconds;
    }

    @NotNull
    protected static String generateId(@NotNull String title, @NotNull String albumArtist, int length)
    {
        length /= 1000;
        short min = (short) (length / 60);
        return title + SEPARATOR + albumArtist + SEPARATOR + getTextualLength(length);
    }

    @NotNull
    public abstract String getId();

    public abstract String getTitle();

    public abstract String getAlbumArtist();

    public abstract short getLength();
}
