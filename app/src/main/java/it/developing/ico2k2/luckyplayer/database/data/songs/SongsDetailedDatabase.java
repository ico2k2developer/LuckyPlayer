package it.developing.ico2k2.luckyplayer.database.data.songs;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {SongDetailed.class}, version = 1)
public abstract  class SongsDetailedDatabase extends RoomDatabase
{
    public abstract SongDetailedDao dao();
}
