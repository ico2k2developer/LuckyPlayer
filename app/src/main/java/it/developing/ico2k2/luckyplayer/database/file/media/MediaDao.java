package it.developing.ico2k2.luckyplayer.database.file.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;

import androidx.paging.PagingSource;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import it.developing.ico2k2.luckyplayer.database.file.File;

public interface MediaDao
{
    @Query("SELECT * FROM " + DATABASE_SONGS)
    PagingSource<Integer,Media> getAll();

    @Query("SELECT * FROM " + DATABASE_SONGS + " WHERE id IN (:ids)")
    PagingSource<Integer,Media> loadAllByIds(String[] ids);

    /*@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Media media);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Media... medias);

    @Update
    void update(Media media);

    @Update
    void updateAll(Media... medias);

    @Delete
    void delete(Media media);

    @Query("DELETE FROM " + DATABASE_SONGS)
    void deleteAll();

    @Query("SELECT * FROM " + DATABASE_SONGS + " WHERE size > :minSize")
    PagingSource<Integer,File> getAllFilesBiggerThan(long minSize);

    @Query("SELECT * FROM " + DATABASE_SONGS + " WHERE size < :maxSize")
    PagingSource<Integer,File> getAllFilesSmallerThan(long maxSize);

    @Query("SELECT * FROM " + DATABASE_SONGS + " WHERE uri LIKE '%' || :name")
    PagingSource<Integer,File> getAllFilesByName(String name);

    @Query("SELECT COUNT(" + File.COLUMN_SIZE + ") FROM " + DATABASE_SONGS)
    long getCount();
}
