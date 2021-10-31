package it.developing.ico2k2.luckyplayer.database.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

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

    public File(String uri,long crc32,long size)
    {
        this.uri = uri;
        this.crc32 = crc32;
        this.size = size;
    }

    @Ignore
    public File(String uri) throws IOException
    {
        this(uri,new java.io.File(uri));
    }

    @Ignore
    private File(String uri,java.io.File file) throws IOException
    {
        this(uri,new FileInputStream(file));
    }

    @Ignore
    public File(java.io.File file) throws IOException
    {
        this(file.getPath(),file);
    }

    @Ignore
    public File(String uri,FileInputStream stream) throws IOException
    {
        this(uri,calculateCRC32(stream),calculateSize(stream));
    }

    public String getUri() {
        return uri;
    }

    public long getCrc32() {
        return crc32;
    }

    public long getSize() {
        return size;
    }

    public static final short CRC32_BYTES_AT_ONCE = 256;

    public static FileInputStream getStream(@NonNull Uri uri,ContentResolver resolver) throws IOException
    {
        return new FileInputStream(resolver.openFileDescriptor(uri,"r").getFileDescriptor());
    }

    public static long calculateCRC32(FileInputStream stream) throws IOException
    {
        CRC32 crc = new CRC32();
        byte[] buffer = new byte[CRC32_BYTES_AT_ONCE];
        short bytesRead;
        Log.d(TAG,"Calculating CRC32 value with buffer size " + CRC32_BYTES_AT_ONCE + " bytes");
        while((bytesRead = (short)stream.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        Log.d(TAG,"Calculated CRC32 value " + crc.getValue());
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
                        ((File)o).getCrc32() == getCrc32() && ((File)o).getUri().equals(getUri()) &&
                        ((File)o).getSize() == getSize();
            }
        }
        return result;
    }
}
