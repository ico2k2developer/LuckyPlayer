package it.developing.ico2k2.luckyplayer.database.data.plays;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Play.class}, version = 1)
public abstract class PlaysDatabase extends RoomDatabase
{
    public abstract PlayDao dao();
}
