package it.developing.ico2k2.luckyplayer.database.file;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class BaseFile
{
    private static final String LOG = BaseFile.class.getSimpleName();

    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_LAST_MODIFIED = "last modified";

    @ColumnInfo(name = COLUMN_URI)
    private final String uri;

    @ColumnInfo(name = COLUMN_SIZE)
    private final long size;

    @ColumnInfo(name = COLUMN_LAST_MODIFIED)
    private final long lastModified;

    public BaseFile(final String uri, final long size, final long lastModified)
    {
        this.uri = uri;
        this.size = size;
        this.lastModified = lastModified;
    }

    @Ignore
    public BaseFile(final java.io.File file) throws IOException
    {
        uri = file.getAbsolutePath();
        FileInputStream stream = new FileInputStream(file);
        size = stream.getChannel().size();
        lastModified = file.lastModified();
        stream.close();
    }

    public static final short CRC32_BYTES_AT_ONCE = 256;

    public static long calculateCRC32(FileInputStream stream) throws IOException
    {
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[CRC32_BYTES_AT_ONCE];
        short bytesRead;
        while((bytesRead = (short)stream.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        Log.d(LOG,"Calculated CRC32 value " + crc.getValue());
        return crc.getValue();
    }

    public static long calculateSize(FileInputStream stream) throws IOException {
        return stream.getChannel().size();
    }

    public static long calculateSize(java.io.File file) throws IOException
    {
        FileInputStream stream = new FileInputStream(file);
        long result = stream.getChannel().size();
        stream.close();
        return result;
    }

    public String getUri()
    {
        return uri;
    }

    public long getSize()
    {
        return size;
    }

    public long getLastModified()
    {
        return lastModified;
    }
}
