package it.developing.ico2k2.luckyplayer.database.data;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FileDao
{
    @Query("SELECT * FROM File")
    List<File> loadAll();

    @Query("SELECT * FROM File WHERE " + File.COLUMN_URI + " LIKE :uri")
    File loadByUri(String uri);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(File... songs);

    @Delete
    void delete(File File);

    @Query("DELETE FROM File WHERE " + File.COLUMN_URI + " LIKE :uri")
    void delete(String uri);

    @Query("DELETE FROM File")
    void deleteAll();
}