package it.developing.ico2k2.luckyplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions
{
    public static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x10;
    public static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x11;

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkStoragePermissionAPI19(Context context)
    {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void requestStoragePermissionAPI19(Activity activity)
    {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
    }

    @RequiresApi(Build.VERSION_CODES.R)
    public static boolean checkStoragePermissionAPI30(Context context)
    {
        AppOpsManager ops = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
        return ops.unsafeCheckOpNoThrow(
                "android:manage_external_storage",
                context.getApplicationInfo().uid,
                context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
    }

    @RequiresApi(Build.VERSION_CODES.R)
    public static void requestStoragePermissionAPI30(Activity activity)
    {
        activity.startActivityForResult(
                new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
    }
}
