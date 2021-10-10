package it.developing.ico2k2.luckyplayer.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;
import java.util.Map;

public class Client
{
    public static final String DATABASE_SONGS = "songs";
    public static final String DATABASE_PLAYS = "plays";

    private static final Map<String,Client> clients = new HashMap<>();
    private final RoomDatabase database;

    private Client(Context context,Class<? extends RoomDatabase> databaseClass,String actualName)
    {
        database = Room.databaseBuilder(context, databaseClass, actualName).build();
    }

    public static synchronized <D extends RoomDatabase> D getInstance(Context context, Class<D> databaseClass, String name)
    {
        Client result;
        String actualName = actualNameFromName(databaseClass,name);
        if (!clients.containsKey(actualName)) {
            clients.put(actualName,result = new Client(context,databaseClass,actualName));
        }
        else
            result = clients.get(actualName);
        return (D) result.database;
    }

    private static <D extends RoomDatabase> String actualNameFromName(Class<D> databaseClass, String name)
    {
        return databaseClass.getSimpleName() + "_" + name;
    }
}
