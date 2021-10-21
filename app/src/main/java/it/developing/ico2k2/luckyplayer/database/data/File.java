package it.developing.ico2k2.luckyplayer.database.data;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

@Entity
public class File
{
    private static final String TAG = File.class.getSimpleName();

    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_CHECKSUM = "crc32";
    public static final String COLUMN_SIZE = "size";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_URI)
    private final String uri;

    @ColumnInfo(name = COLUMN_CHECKSUM)
    private final long crc32;

    @ColumnInfo(name = COLUMN_SIZE)
    private final long size;

    public File(@NonNull String uri, long crc32, long size)
    {
        this.uri = uri;
        this.crc32 = crc32;
        this.size = size;
    }

    @Ignore
    public File(@NonNull Uri uri,FileInputStream stream) throws IOException
    {
        this(uri.getPath(),calculateCRC32(stream),calculateSize(stream));
    }

    @Ignore
    public File(@NonNull Uri uri,ContentResolver resolver) throws IOException
    {
        this(uri,getStream(uri,resolver));
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

    public static FileInputStream getStream(@NonNull Uri uri,ContentResolver resolver) throws IOException
    {
        return new FileInputStream(resolver.openFileDescriptor(uri,"r").getFileDescriptor());
    }

    public static long calculateCRC32(FileInputStream stream) throws IOException
    {
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[CRC32_BYTES_AT_ONCE];
        byte bytesRead;
        while((bytesRead = (byte)stream.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        return crc.getValue();
    }

    public static long calculateSize(FileInputStream stream) throws IOException
    {
        return stream.getChannel().size();
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
