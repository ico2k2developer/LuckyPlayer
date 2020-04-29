package it.developing.ico2k2.luckyplayer;

import android.app.AlertDialog;
import android.content.Context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Utils
{
    public static final String TAG_LOGS = "Player's logs";

    public static final String PREFERENCE_MAIN = "main";

    public static final String KEY_INITIALIZED = "initialized";
    public static final String KEY_DATA_INITIALIZED = "data initialized";
    public static final String KEY_SYSTEM_MEDIA = "system media";
    public static final String KEY_THEME = "theme";
    public static final String KEY_NOTIFICATION_TINT = "notification tint";
    public static final String KEY_SONGLIST_LAST_SIZE = "songlist last size";
    public static final String KEY_SONGLIST_PACKET_SIZE = "songlist packet size";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_ORDER = "order";
    public static final String KEY_SONGS = "songs";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TIME = "time";
    public static final String KEY_REQUEST_CODE = "request code";
    public static final String KEY_INDEX = "index";

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

    }
