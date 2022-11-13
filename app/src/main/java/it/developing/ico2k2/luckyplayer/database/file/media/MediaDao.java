package it.developing.ico2k2.luckyplayer.database.file.media;

import static it.developing.ico2k2.luckyplayer.database.Database.DATABASE_SONGS;
import static it.developing.ico2k2.luckyplayer.database.file.BaseFile.COLUMN_LAST_MODIFIED;
import static it.developing.ico2k2.luckyplayer.database.file.BaseFile.COLUMN_SIZE;
import static it.developing.ico2k2.luckyplayer.database.file.BaseFile.COLUMN_URI;
import static it.developing.ico2k2.luckyplayer.database.file.media.BaseMedia.COLUMN_ID;
import static it.developing.ico2k2.luckyplayer.database.file.media.BaseMedia.COLUMN_VOLUME;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MediaDao
{
    /*@Query("SELECT * FROM `" + DATABASE_SONGS + "`")
    PagingSource<Integer,Media> loadAll();*/

    @Query("SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_VOLUME + "` LIKE :volume")
    PagingSource<Integer,Media> loadAllByVolume(short volume);

    @Query("SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_VOLUME + "` LIKE :volume" +
            " AND `" + COLUMN_ID + "` LIKE :id")
    Media load(short volume,long id);

    @Query("SELECT `" + COLUMN_LAST_MODIFIED + "` FROM `" + DATABASE_SONGS + "` WHERE `"
            + COLUMN_VOLUME + "` LIKE :volume AND `" + COLUMN_ID + "` LIKE :id")
    long loadLastModified(short volume,long id);

    /*@Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void put(Media media);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putAll(Media... medias);

    @Update
    void update(Media media);

    @Update
    void updateAll(Media... medias);

    @Delete
    void delete(Media media);

    @Query("DELETE FROM `" + DATABASE_SONGS + "`")
    void deleteAll();

    @Query("SELECT EXISTS (SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_VOLUME +
            "` LIKE :volume AND `" + COLUMN_ID + "` LIKE :id)")
    boolean contains(short volume,long id);

    @Query("SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_SIZE + "` > :minSize")
    PagingSource<Integer,Media> getAllFilesBiggerThan(long minSize);

    @Query("SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_SIZE + "` < :maxSize")
    PagingSource<Integer,Media> getAllFilesSmallerThan(long maxSize);

    @Query("SELECT * FROM `" + DATABASE_SONGS + "` WHERE `" + COLUMN_URI + "` LIKE '%' || :name")
    PagingSource<Integer,Media> getAllFilesByName(String name);
}
