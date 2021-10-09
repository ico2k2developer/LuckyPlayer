package it.developing.ico2k2.luckyplayer.database.data.songs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.developing.ico2k2.luckyplayer.database.data.Song;

@Dao
public interface SongDao
{
    @Query("SELECT * FROM Song")
    List<Song> loadAll();

    @Query("SELECT * FROM Song WHERE id IN (:ids)")
    List<Song> loadAllById(String[] ids);

    @Query("SELECT * FROM Song WHERE title IN (:titles)")
    List<Song> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM Song WHERE album_artist IN (:albumArtists)")
    List<Song> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM Song WHERE length IN (:lengths)")
    List<Song> loadAllByLength(int[] lengths);

    @Query("SELECT * FROM Song WHERE id LIKE :id")
    Song loadById(String id);

    @Query("SELECT * FROM Song WHERE title LIKE :title")
    Song loadByTitle(String title);

    @Query("SELECT * FROM Song WHERE album_artist LIKE :albumArtist")
    Song loadByAlbumArtist(String albumArtist);

    @Query("SELECT * FROM Song WHERE id LIKE :length")
    Song loadByLength(int length);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Song... songs);

    @Delete
    void delete(Song user);

    @Query("DELETE FROM Song WHERE id LIKE :id")
    void delete(String id);

    @Query("DELETE FROM Song")
    void deleteAll();
    
    
    
    @Query("SELECT * FROM Song WHERE year IN (:years)")
    List<Song> loadAllByYear(byte[] years);

    @Query("SELECT * FROM Song WHERE genre IN (:genres)")
    List<Song> loadAllByGenre(String[] genres);

    @Query("SELECT * FROM Song WHERE lyrics IN (:lyrics)")
    List<Song> loadAllByLyrics(String[] lyrics);
}
