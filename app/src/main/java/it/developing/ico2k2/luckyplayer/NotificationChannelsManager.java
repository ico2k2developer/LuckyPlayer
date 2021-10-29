package it.developing.ico2k2.luckyplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

public class NotificationChannelsManager {

    public static final Channel CHANNEL_MAIN =
            new Channel("main", R.string.main_notification_channel,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT);
    public static final Channel CHANNEL_INFO =
            new Channel("main", R.string.info_notification_channel,
                    NotificationManagerCompat.IMPORTANCE_LOW);
    public static final Channel CHANNEL_STATUS =
            new Channel("main", R.string.status_notification_channel,
                    NotificationManagerCompat.IMPORTANCE_LOW);

    private static final Channel[] channels = {
            CHANNEL_MAIN,
            CHANNEL_INFO,
            CHANNEL_STATUS,
    };

    @RequiresApi(Build.VERSION_CODES.O)
    public static void checkChannel(Context context, Channel channel) {
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel.build(context));
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void checkAll(Context context)
    {
        for(Channel channel : channels)
            checkChannel(context,channel);
    }

    public static class Channel {
        private final String id;
        private final @StringRes
        int name;
        private final int priority;

        private Channel(String id, @StringRes int name, int priority) {
            this.id = id;
            this.name = name;
            this.priority = priority;
        }

        private Channel(String id, @StringRes int name) {
            this(id, name, NotificationManagerCompat.IMPORTANCE_DEFAULT);
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private NotificationChannel build(Context context) {
            return new NotificationChannel(id, context.getString(name), priority);
        }

        public String getId() {
            return id;
        }
    }
}