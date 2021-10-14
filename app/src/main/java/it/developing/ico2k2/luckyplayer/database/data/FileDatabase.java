package it.developing.ico2k2.luckyplayer.database.data;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {File.class}, version = 1)
public abstract class FileDatabase extends RoomDatabase
{
    public abstract FileDao dao();
}
