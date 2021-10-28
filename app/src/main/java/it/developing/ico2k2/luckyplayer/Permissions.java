package it.developing.ico2k2.luckyplayer;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

public class Permissions
{

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkStoragePermissionAPI19(Context context)
    {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void requestStoragePermissionAPI19(Activity activity,int requestCode)
    {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},requestCode
        );
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
    public static void requestStoragePermissionAPI30(Activity activity,int requestCode)
    {
        activity.startActivityForResult(
                new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                requestCode);
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkStoragePermission(Context context)
    {
        boolean result;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            result = checkStoragePermissionAPI19(context);
        else
            result = checkStoragePermissionAPI30(context);
        return result;
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void requestStoragePermission(Activity activity,int requestCode)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            requestStoragePermissionAPI19(activity,requestCode);
        else
            requestStoragePermissionAPI30(activity,requestCode);
    }

    public static boolean askForPermission(BaseActivity context,String permission,int requestCode)
    {
        boolean result = ContextCompat.checkSelfPermission(context,permission) == PERMISSION_GRANTED;
        if(!result)
        {
            ActivityCompat.requestPermissions(context,new String[]{permission},requestCode);
            //permissionDialog(context,permission,requestCode,permissionReason,noMorePreferenceKey);
        }
        return result;
    }

    public static void permissionDialog(final Activity activity,final DialogInterface.OnClickListener ok,final int requestCode,@Nullable String permissionReason,@Nullable final String noMorePreferenceKey)
    {
        boolean shouldShow = true;
        if(noMorePreferenceKey != null)
            shouldShow = !Prefs.getInstance(activity,Prefs.PREFS_SETTINGS).getBoolean(noMorePreferenceKey,false);
        if(shouldShow)
        {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
            dialog.setPositiveButton(android.R.string.yes,ok);
            dialog.setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog,int which){
                    if(noMorePreferenceKey != null)
                        Prefs.getInstance(activity,Prefs.PREFS_SETTINGS).edit().putBoolean(noMorePreferenceKey,true).apply();
                }
            });
            if(permissionReason == null)
                dialog.setMessage(R.string.permission_reason);
            else
                dialog.setMessage(activity.getString(R.string.permission_reason) + " " + permissionReason);
            dialog.setCancelable(false);
            dialog.create().show();
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public static void permissionDialog(final Activity activity,final String permission,final int requestCode,@Nullable String permissionReason,@Nullable final String noMorePreferenceKey)
    {
        permissionDialog(activity,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission))
                {
                    ActivityCompat.requestPermissions(activity,new String[]{permission},requestCode);
                }
                else
                {
                    goToAppSettingsPageForPermission(activity,requestCode);
                }
            }
        },requestCode,permissionReason,noMorePreferenceKey);
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void permissionStorageDialog(final Activity activity,final int requestCode,@Nullable String permissionReason,@Nullable final String noMorePreferenceKey)
    {
        permissionDialog(activity,new DialogInterface.OnClickListener(){
            @Override public void onClick(DialogInterface dialogInterface,int i)
            {
                requestStoragePermission(activity,requestCode);
            }
        },requestCode,permissionReason,noMorePreferenceKey);
    }

    public static void goToAppSettingsPageForPermission(Activity context,int requestCode)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(),null);
        intent.setData(uri);
        context.startActivityForResult(intent,requestCode);
    }
}
