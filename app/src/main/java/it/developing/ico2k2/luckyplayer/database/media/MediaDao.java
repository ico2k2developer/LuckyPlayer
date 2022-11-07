package it.developing.ico2k2.luckyplayer.database.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_FILES;
import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;

import androidx.paging.PagingSource;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import it.developing.ico2k2.luckyplayer.database.file.File;

public interface MediaDao
{
    @Query("SELECT * FROM " + DATABASE_SONGS)
    List<Media> getAll();

    @Query("SELECT * FROM " + DATABASE_SONGS + " WHERE id IN (:ids)")
    List<Media> loadAllByIds(String[] ids);

    /*@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);*/

    @Insert
    void insertAll(Media... medias);

    @Delete
    void delete(Media media);


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
