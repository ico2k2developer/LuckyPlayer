package it.developing.ico2k2.luckyplayer.database;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Song.class}, version = 1)
public abstract class Database extends RoomDatabase
{
    public abstract SongDao dao();
}
