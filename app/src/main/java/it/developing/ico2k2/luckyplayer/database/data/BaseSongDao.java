package it.developing.ico2k2.luckyplayer.database.data;

import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Entity
public interface BaseSongDao
{
    @Query("SELECT * FROM BaseSong")
    List<BaseSong> loadAll();

    @Query("SELECT * FROM BaseSong WHERE id IN (:ids)")
    List<BaseSong> loadAllById(String[] ids);

    @Query("SELECT * FROM BaseSong WHERE title IN (:titles)")
    List<BaseSong> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM BaseSong WHERE album_artist IN (:albumArtists)")
    List<BaseSong> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM BaseSong WHERE length IN (:lengths)")
    List<BaseSong> loadAllByLength(int[] lengths);

    @Query("SELECT * FROM BaseSong WHERE id LIKE :id")
    BaseSong loadById(String id);

    @Query("SELECT * FROM BaseSong WHERE title LIKE :title")
    BaseSong loadByTitle(String title);

    @Query("SELECT * FROM BaseSong WHERE album_artist LIKE :albumArtist")
    BaseSong loadByAlbumArtist(String albumArtist);

    @Query("SELECT * FROM BaseSong WHERE id LIKE :length")
    BaseSong loadByLength(int length);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(BaseSong... songs);

    @Delete
    void delete(BaseSong user);

    @Query("DELETE FROM BaseSong WHERE id LIKE :id")
    void delete(String id);

    @Query("DELETE FROM BaseSong")
    void deleteAll();
}
