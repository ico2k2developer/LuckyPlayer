package it.developing.ico2k2.luckyplayer.database.data.songs;

import androidx.room.RoomDatabase;

import it.developing.ico2k2.luckyplayer.database.data.BaseSong;

@androidx.room.Database(entities = {BaseSong.class}, version = 1)
public abstract class SongsDatabase extends RoomDatabase
{
    public abstract SongDao dao();
}
