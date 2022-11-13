package it.developing.ico2k2.luckyplayer.preference;

import android.content.Context;

public class Settings
{
    public static final String PREFERENCE_SETTINGS = "settings";

    public static final String KEY_STRING_MEDIA_UPDATE = "media version";
    public static final String KEY_LONG_MEDIA_COUNT = "media count";
    public static final String KEY_BOOLEAN_QUERY_MUSIC = "query music";
    public static final String KEY_BOOLEAN_QUERY_RINGTONE = "query ringtone";
    public static final String KEY_BOOLEAN_QUERY_NOTIFICATION = "query notification";
    public static final String KEY_BOOLEAN_QUERY_PODCAST = "query podcast";
    public static final String KEY_BOOLEAN_QUERY_ALARM = "query alarm";
    public static final String KEY_BOOLEAN_QUERY_AUDIOBOOK = "query audiobook";
    public static final String KEY_BOOLEAN_QUERY_RECORDING = "query recording";
    public static final String KEY_BOOLEAN_QUERY_OTHER = "query other";

    public synchronized static PreferenceManager getInstance(Context context)
    {
        return PreferenceManager.getInstance(context,PREFERENCE_SETTINGS);
    }
}
