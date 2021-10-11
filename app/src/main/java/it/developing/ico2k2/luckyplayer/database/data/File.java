package it.developing.ico2k2.luckyplayer.database.data;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

@Entity
public class File
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uri")
    private final String uri;

    @ColumnInfo(name = "checksum_crc32")
    private final long crc32;

    @ColumnInfo(name = "size")
    private final long size;

    public File(@NonNull String uri, long crc32, long size)
    {
        this.uri = uri;
        this.crc32 = crc32;
        this.size = size;
    }

    @Ignore
    public File(@NonNull java.io.File file) throws IOException
    {
        this(file.getCanonicalPath(),calculateCRC32(file),file.length());
    }

    @Ignore
    public File(@NonNull Uri uri) throws IOException
    {
        this(new java.io.File(uri.getPath()));
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    public long getCrc32() {
        return crc32;
    }

    public long getSize() {
        return size;
    }

    public static final byte CRC32_BYTES_AT_ONCE = 16;

    public static long calculateCRC32(File file) throws IOException
    {
        return calculateCRC32(new java.io.File(file.getUri()));
    }

    public static long calculateCRC32(java.io.File file) throws IOException
    {
        CRC32 crc = new CRC32();
        InputStream stream = new FileInputStream(file);
        byte[] buffer = new byte[CRC32_BYTES_AT_ONCE];
        byte bytesRead;
        while((bytesRead = (byte)stream.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        return crc.getValue();
    }

    @Override
    public boolean equals(Object o)
    {
        boolean result = false;
        if(o != null)
        {
            if(o instanceof File)
            {
                result =
                        ((File)o).getCrc32() == getCrc32() &&
                        getUri().equals(((File)o).getUri()) &&
                        ((File)o).getSize() == getSize();
            }
        }
        return result;
    }
}
