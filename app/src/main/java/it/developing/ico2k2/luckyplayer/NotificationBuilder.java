package it.developing.ico2k2.luckyplayer;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class NotificationBuilder extends NotificationCompat.Builder
{
    public NotificationBuilder(@NonNull Context context,@NonNull String channelId)
    {
        super(context,channelId);
    }

    @SuppressLint("RestrictedApi")
    public void removeActions()
    {
        mActions.clear();
    }
}
