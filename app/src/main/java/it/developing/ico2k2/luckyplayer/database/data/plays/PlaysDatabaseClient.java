package it.developing.ico2k2.luckyplayer.database.data.plays;

import android.content.Context;

import androidx.room.Room;

import java.util.HashMap;
import java.util.Map;

public class PlaysDatabaseClient
{
    private static final Map<String, PlaysDatabaseClient> clients = new HashMap<>();
    private final PlaysDatabase database;

    private PlaysDatabaseClient(Context context, String name)
    {
        database = Room.databaseBuilder(context, PlaysDatabase.class, name).build();
    }

    public static synchronized PlaysDatabase getInstance(Context context, String name)
    {
        PlaysDatabaseClient result;
        if (!clients.containsKey(name)) {
            clients.put(name,result = new PlaysDatabaseClient(context,name));
        }
        else
            result = clients.get(name);
        return result.database;
    }
}