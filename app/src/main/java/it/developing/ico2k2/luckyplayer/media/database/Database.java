package it.developing.ico2k2.luckyplayer.media.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;
import java.util.Map;

public class Database
{
    public static final String DATABASE_SONGS = "songs";
    public static final String DATABASE_SONGS_DETAILED = "songs_detailed";
    public static final String DATABASE_PLAYS = "plays";

    private static final Map<String, Database> clients = new HashMap<>();
    private final RoomDatabase database;

    private Database(Context context, Class<? extends RoomDatabase> databaseClass, String actualName)
    {
        database = Room.databaseBuilder(context, databaseClass, actualName).build();
    }

    public static synchronized <D extends RoomDatabase>
    D getInstance(Context context, Class<D> databaseClass, String name)
    {
        Database result;
        String actualName = actualNameFromName(databaseClass,name);
        if (!clients.containsKey(actualName)) {
            clients.put(actualName,result = new Database(context,databaseClass,actualName));
        }
        else
            result = clients.get(actualName);
        return (D) result.database;
    }

    private static <D extends RoomDatabase>
    String actualNameFromName(Class<D> databaseClass, String name)
    {
        return databaseClass.getSimpleName() + "_" + name;
    }

    private static final char SEPARATOR = ';';

    public static String generateId(int tableId,int itemId)
    {
        return Integer.toString(tableId) + SEPARATOR + itemId;
    }

    public static int getTableId(String id) throws StringIndexOutOfBoundsException
    {
        return Integer.parseInt(id.substring(0,id.indexOf(SEPARATOR)));
    }

    public static int getItemId(String id) throws StringIndexOutOfBoundsException
    {
        return Integer.parseInt(id.substring(id.indexOf(SEPARATOR) + 1));
    }
}