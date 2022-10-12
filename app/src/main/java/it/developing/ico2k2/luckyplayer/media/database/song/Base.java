package it.developing.ico2k2.luckyplayer.media.database.song;

public abstract class Base
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

    public abstract String getId();

    public abstract String getTitle();

    public abstract String getAlbumArtist();

    public abstract long getLength();
}
