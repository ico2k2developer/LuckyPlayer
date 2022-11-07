package it.developing.ico2k2.luckyplayer.database.file;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_FILES;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FileDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(File file);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(File... files);

    @Update
    void update(File file);

    @Update
    void updateAll(File... files);

    @Delete
    void delete(File file);

    @Delete
    void deleteAll(File... files);

    @Query("DELETE FROM " + DATABASE_FILES)
    void deleteAll();

    @Query("SELECT * FROM " + DATABASE_FILES)
    PagingSource<Integer,File> getAll();

    @Query("SELECT * FROM " + DATABASE_FILES + " WHERE size > :minSize")
    PagingSource<Integer,File> getAllFilesBiggerThan(long minSize);

    @Query("SELECT * FROM " + DATABASE_FILES + " WHERE size < :maxSize")
    PagingSource<Integer,File> getAllFilesSmallerThan(long maxSize);

    @Query("SELECT * FROM " + DATABASE_FILES + " WHERE uri LIKE '%' || :name")
    PagingSource<Integer,File> getAllFilesByName(String name);

    @Query("SELECT COUNT(" + File.COLUMN_SIZE + ") FROM " + DATABASE_FILES)
    long getCount();
}
