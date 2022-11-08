package it.developing.ico2k2.luckyplayer.database.file.media;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Media.class}, version = 1)
public abstract class MediaDatabase extends RoomDatabase
{
    public abstract MediaDao mediaDao();
}
