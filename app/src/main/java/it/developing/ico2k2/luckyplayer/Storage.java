package it.developing.ico2k2.luckyplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class Storage
{
    private static final String TAG = Storage.class.getSimpleName();

    @RequiresApi(Build.VERSION_CODES.R)
    public static java.io.File getExternalStorageDirectoryAPI30(Context context)
    {
        StorageManager manager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        return manager.getPrimaryStorageVolume().getDirectory();
    }

    @TargetApi(Build.VERSION_CODES.Q)
    public static java.io.File getExternalStorageDirectory()
    {
        return Environment.getExternalStorageDirectory();
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    public static String getAbsolutePath(Context context, String volume,@Nullable String relativePath, String displayName)
    {
        Log.d(TAG,"Retrieving absolute path with volume name " + volume + " relative path " +
                relativePath + " and display name " + displayName);
        String result;
        switch(volume)
        {
            case MediaStore.VOLUME_EXTERNAL_PRIMARY:
            {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                    result = getExternalStorageDirectory().getAbsolutePath();
                else
                    result = getExternalStorageDirectoryAPI30(context).getAbsolutePath();
                break;
            }
            case MediaStore.VOLUME_INTERNAL:
            {
                result = "/system/media/audio/ui";
                break;
            }
            default:
            {
                result = "/storage/" + volume.toUpperCase();
            }
        }
        if(relativePath == null)
            relativePath = "";
        result += "/" + relativePath + displayName;
        return result;
    }
}
