package it.developing.ico2k2.luckyplayer.database.data.songs;

import android.content.Context;

import androidx.room.Room;

import java.util.HashMap;
import java.util.Map;

public class SongsDatabaseClient
{
    private static final Map<String, SongsDatabaseClient> clients = new HashMap<>();
    private final SongsDatabase database;

    private SongsDatabaseClient(Context context, String name)
    {
        database = Room.databaseBuilder(context, SongsDatabase.class, name).build();
    }

    public static synchronized SongsDatabase getInstance(Context context, String name)
    {
        SongsDatabaseClient result;
        if (!clients.containsKey(name)) {
            clients.put(name,result = new SongsDatabaseClient(context,name));
        }
        else
            result = clients.get(name);
        return result.database;
    }
}