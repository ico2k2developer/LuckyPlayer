package it.developing.ico2k2.luckyplayer.database;

public class Optimized
{
    public static byte byte256(short valueFrom0To255)
    {
        return (byte)(valueFrom0To255 + Byte.MIN_VALUE);
    }

    public static short shortFromByte256(byte byte256)
    {
        return (short)(((short)byte256) - ((short)Byte.MIN_VALUE));
    }

    public static short short65536(int valueFrom0To65535)
    {
        return (short)(valueFrom0To65535 + Short.MIN_VALUE);
    }

    public static int intFromShort65536(short short65536)
    {
        return (((int)short65536) - ((int)Short.MIN_VALUE));
    }

    public static short byte256maxFromMin(short min)
    {
        return (short)((Byte.MAX_VALUE + 1) * 2 - 1 + min);
    }
}
