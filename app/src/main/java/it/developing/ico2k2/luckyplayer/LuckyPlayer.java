package it.developing.ico2k2.luckyplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import it.developing.ico2k2.luckyplayer.services.OnServiceBoundListener;
import it.developing.ico2k2.luckyplayer.services.OnServiceMessageListener;
import it.developing.ico2k2.luckyplayer.services.PlayService;

import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_INFO;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_MAIN;
import static it.developing.ico2k2.luckyplayer.Keys.CHANNEL_ID_STATUS;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_BIND;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_DESTROY;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;

public class LuckyPlayer extends Application
{
    private DataManager dataManager;
    private ServiceConnection connection;
    private boolean bound = false;
    private Messenger myMessenger,itsMessenger;
    private ArrayList<OnServiceBoundListener> listeners;
    private ArrayList<OnServiceMessageListener> callbacks;

    @Override
    public void onCreate()
    {
        super.onCreate();
        dataManager = new DataManager(this);
        listeners = new ArrayList<>();
        callbacks = new ArrayList<>();
        myMessenger = new Messenger(new PlayHandler(this));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            String name = getString(R.string.main_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_MAIN,name,NotificationManager.IMPORTANCE_LOW));
            name = getString(R.string.info_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_INFO,name,NotificationManager.IMPORTANCE_LOW));
            name = getString(R.string.status_notification_channel);
            manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID_STATUS,name,NotificationManager.IMPORTANCE_LOW));
        }
        connection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name,IBinder service){
                bound = true;
                itsMessenger = new Messenger(service);
                Message message = Message.obtain();
                message.what = MESSAGE_BIND;
                message.replyTo = myMessenger;
                sendMessageToService(message,true);
                for(OnServiceBoundListener e : listeners)
                    e.onServiceBound();
                Log.d(TAG_LOGS,"Service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name){
                bound = false;
                for(OnServiceBoundListener e : listeners)
                    e.onServiceNotBound();
                Log.d(TAG_LOGS,"Service not connected");
            }
        };
    }

    protected static class PlayHandler extends Handler
    {
        WeakReference<LuckyPlayer> instance;

        PlayHandler(LuckyPlayer application)
        {
            instance = new WeakReference<>(application);
        }

        @Override
        public void handleMessage(Message message)
        {
            instance.get().elaborateRequest(message);
        }
    }

    protected void elaborateRequest(Message message)
    {
        Log.d(TAG_LOGS,"Request received in app: " + Integer.toHexString(message.what));
        Bundle data = message.peekData();
        if(data != null)
            data.setClassLoader(getClassLoader());
        switch(message.what)
        {
            case MESSAGE_DESTROY:
            {
                bound = false;
                unbindService(connection);
                for(OnServiceBoundListener e : listeners)
                    e.onServiceNotBound();
                break;
            }
        }
        for(OnServiceMessageListener e : callbacks)
            e.onMessageReceived(message.what,data);
    }

    public void sendMessageToService(int what)
    {
        Message message = Message.obtain();
        message.what = what;
        sendMessageToService(message,true);
    }

    public void sendMessageToService(Message message,boolean recycle)
    {
        Log.d(TAG_LOGS,"Trying to send message " + Integer.toHexString(message.what));
        if(itsMessenger != null)
        {
            try
            {
                itsMessenger.send(message);
                Log.d(TAG_LOGS,"Sent message " + Integer.toHexString(message.what));
                if(recycle)
                    message.recycle();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean isServiceBound()
    {
        return bound;
    }

    public void prepareService()
    {
        Log.d(TAG_LOGS,"Trying to prepare service");
        if(isServiceBound())
        {
            Log.d(TAG_LOGS,"Service already prepared");
            for(OnServiceBoundListener e : listeners)
                e.onServiceBound();
        }
        else
        {
            Log.d(TAG_LOGS,"Preparing service");
            Intent intent = new Intent(this,PlayService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent);
            else
                startService(intent);
            bindService(intent,connection,BIND_AUTO_CREATE);
        }
    }

    public void addOnServiceBoundListener(OnServiceBoundListener listener)
    {
        listeners.add(listener);
    }

    public void removeOnServiceBoundListener(OnServiceBoundListener listener)
    {
        listeners.remove(listener);
    }

    public void addOnServiceMessageListener(OnServiceMessageListener listener)
    {
        callbacks.add(listener);
    }

    public void removeOnServiceMessageListener(OnServiceMessageListener listener)
    {
        callbacks.remove(listener);
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }
}
