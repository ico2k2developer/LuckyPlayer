package it.developing.ico2k2.luckyplayer.database.data;

import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingSource;
import androidx.room.RoomDatabase;

import kotlin.jvm.functions.Function0;

@androidx.room.Database(entities = {File.class}, version = 1)
public abstract class FileDatabase extends RoomDatabase
{
    public abstract FileDao dao();
}
