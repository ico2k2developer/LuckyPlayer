package it.developing.ico2k2.luckyplayer.database.file;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

import it.developing.ico2k2.luckyplayer.activities.TabsActivity;

public class File
{
    private static final String LOG = File.class.getSimpleName();

    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CHECKSUM = "crc32";
    public static final String COLUMN_SIZE = "size";

    @ColumnInfo(name = COLUMN_URI)
    private final String uri;

    @ColumnInfo(name = COLUMN_CHECKSUM)
    private final long crc32;

    @ColumnInfo(name = COLUMN_SIZE)
    private final long size;

    public File(final String uri,final long crc32,final long size)
    {
        this.uri = uri;
        this.crc32 = crc32;
        this.size = size;
    }

    @Ignore
    public File(final String uri) throws IOException
    {
        this.uri = uri;
        FileInputStream stream = new FileInputStream(uri);
        crc32 = calculateCRC32(stream);
        size = stream.getChannel().size();
    }

    public static final short CRC32_BYTES_AT_ONCE = 256;

    public static long calculateCRC32(FileInputStream stream) throws IOException
    {
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[CRC32_BYTES_AT_ONCE];
        short bytesRead;
        Log.d(LOG,"Calculating CRC32 value with buffer size " + CRC32_BYTES_AT_ONCE + " bytes");
        while((bytesRead = (short)stream.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        Log.d(LOG,"Calculated CRC32 value " + crc.getValue());
        return crc.getValue();
    }

    public static long calculateSize(FileInputStream stream) throws IOException {
        return stream.getChannel().size();
    }

    public String getUri()
    {
        return uri;
    }

    public long getCRC32()
    {
        return crc32;
    }

    public long getSize()
    {
        return size;
    }
}
