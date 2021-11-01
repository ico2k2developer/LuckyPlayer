package it.developing.ico2k2.luckyplayer.database.data;

import static it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed.COLUMN_ID;

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

    @Query("SELECT * FROM File WHERE " + COLUMN_ID + " IN (:ids)")
    List<File> loadAllById(String ... ids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(File ... songs);

    @Delete
    void delete(File File);

    @Query("DELETE FROM File WHERE " + File.COLUMN_ID + " LIKE :id")
    void deleteById(String id);

    @Query("DELETE FROM File WHERE " + File.COLUMN_URI + " LIKE :uri")
    void deleteByUri(String uri);

    @Query("DELETE FROM File")
    void deleteAll();

    @Query("SELECT COUNT(" + File.COLUMN_ID + ") FROM File")
    long getCount();
}