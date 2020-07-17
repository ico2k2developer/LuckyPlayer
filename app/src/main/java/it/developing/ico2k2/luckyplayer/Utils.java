package it.developing.ico2k2.luckyplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public final class Utils
{
    public static final String TAG_LOGS = "Player's logs";

    public static final String KEY_REQUEST = "request";

    public static final String EXTRA_URI = "intent uri";

    public static final String FILE_PROVIDER_AUTHORITY = "it.developing.ico2k2.luckyplayer.fileprovider";

    public static final String CHANNEL_ID_MAIN = "main";
    public static final String CHANNEL_ID_INFO = "info";
    public static final String CHANNEL_ID_STATUS = "status";

    public static final String MESSAGE_DESTROY = "destroy";
    public static final String MESSAGE_SONG_REQUEST = "14";
    public static final String MESSAGE_SONG_PACKET = "15";
    public static final String MESSAGE_SONG_START = "16";
    public static final String MESSAGE_SONG_END = "17";
    public static final String MESSAGE_SCAN_REQUESTED = "scan request";
    public static final String MESSAGE_SCAN_COMPLETED = "19";
    public static final String MESSAGE_PLAYER = "20";

    public static final String APP_PACKAGE = "it.developing.ico2k2.luckyplayer";

    public static final int REQUEST_CODE_PERMISSIONS = 0x10;

    public static int getThemeFromName(String name) throws NoSuchFieldException,IllegalAccessException
    {
        name = name.replace(" ","_");
        return R.style.class.getField(name).getInt(R.style.class);
    }

    public static ArrayList<Map<String,String>> adapterMapsFromAdapterList(ArrayList<String> formats,String listTitle)
    {
        ArrayList<Map<String,String>> result = new ArrayList<>(formats.size());
        for(String a : formats)
        {
            Map<String,String> map = new HashMap<>();
            map.put(listTitle,a);
            result.add(map);
        }
        return result;
    }

    public static void showException(Context context,Exception e)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        dialog.setTitle(android.R.string.dialog_alert_title);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        dialog.setMessage(stringWriter.toString());
        dialog.setCancelable(true);
        dialog.create().show();
    }

    public static void examineBundle(Bundle bundle)
    {
        String result = "Examining bundle: ";
        if(bundle == null)
            result += "bundle is null";
        else
        {
            if(bundle.isEmpty())
                result += "bundle is empty";
            else
            {
                Object obj;
                for(String key : bundle.keySet())
                {
                    obj = bundle.get(key);
                    result += "\n[" + obj.getClass().getSimpleName() + "] (" + key + "): " + obj.toString();
                }
            }
        }
        Log.d(TAG_LOGS,result);
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

    public static void permissionDialog(BaseActivity context,String permission,int requestCode,@Nullable String permissionReason,@Nullable String noMorePreferenceKey)
    {
        boolean shouldShow = true;
        if(noMorePreferenceKey != null)
            shouldShow = !context.getMainSharedPreferences().getBoolean(noMorePreferenceKey,false);
        if(shouldShow)
        {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context);
            dialog.setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog,int which){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(context,permission))
                    {
                        ActivityCompat.requestPermissions(context,new String[]{permission},requestCode);
                    }
                    else
                    {
                        goToAppSettingsPageForPermission(context,requestCode);
                    }
                }
            });
            dialog.setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog,int which){
                    if(noMorePreferenceKey != null)
                        context.getMainSharedPreferences().edit().putBoolean(noMorePreferenceKey,true).apply();
                }
            });
            if(permissionReason == null)
                dialog.setMessage(R.string.permission_reason);
            else
                dialog.setMessage(context.getString(R.string.permission_reason) + " " + permissionReason);
            dialog.setCancelable(false);
            dialog.create().show();
        }
    }

    public static void goToAppSettingsPageForPermission(Activity context,int requestCode)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent,requestCode);
    }
}
