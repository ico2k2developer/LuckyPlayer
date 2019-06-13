package it.developing.ico2k2.luckyplayer;

public final class Keys
{

    public static final String TAG_LOGS = "Player's logs";

    public static final String KEY_INITIALIZED = "initialized";
    public static final String KEY_DATA_INITIALIZED = "data initialized";
    public static final String KEY_SYSTEM_MEDIA = "system media";
    public static final String KEY_THEME = "theme";
    public static final String KEY_NOTIFICATION_TINT = "notification tint";
    public static final String KEY_SONGLIST_LAST_SIZE = "songlist last size";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_ORDER = "order";
    public static final String KEY_SONGS = "songs";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TIME = "time";
    public static final String KEY_REQUEST_CODE = "request code";
    public static final String KEY_INDEX = "index";

    public static final String EXTRA_URI = "intent uri";
    public static final String EXTRA_REQUEST = "intent request";

    public static final String FILE_PROVIDER_AUTHORITY = "it.developing.ico2k2.luckyplayer.fileprovider";

    public static final String CHANNEL_ID_MAIN = "main";
    public static final String CHANNEL_ID_INFO = "info";
    public static final String CHANNEL_ID_STATUS = "status";

    public static final int MESSAGE_DESTROY = 0x10;
    public static final int MESSAGE_ONLINE = 0x11;
    public static final int MESSAGE_OFFLINE = 0x12;
    public static final int MESSAGE_BIND = 0x13;
    public static final int MESSAGE_SONG_REQUEST = 0x14;
    public static final int MESSAGE_SONG_PACKET = 0x15;
    public static final int MESSAGE_SONG_START = 0x16;
    public static final int MESSAGE_SONG_END = 0x17;
    public static final int MESSAGE_SCAN_REQUESTED = 0x18;
    public static final int MESSAGE_SCAN_COMPLETED = 0x19;
    public static final int MESSAGE_PLAYER = 0x1A;
}
