package it.developing.ico2k2.luckyplayer.database.data.plays;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlayDao
{
    @Query("SELECT * FROM Play")
    List<Play> loadAll();

    @Query("SELECT * FROM Play WHERE id IN (:ids)")
    List<Play> loadAllById(String[] ids);

    @Query("SELECT * FROM Play WHERE title IN (:titles)")
    List<Play> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM Play WHERE album_artist IN (:albumArtists)")
    List<Play> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM Play WHERE length IN (:lengths)")
    List<Play> loadAllByLength(int[] lengths);

    @Query("SELECT * FROM Play WHERE id LIKE :id")
    Play loadById(String id);

    @Query("SELECT * FROM Play WHERE title LIKE :title")
    Play loadByTitle(String title);

    @Query("SELECT * FROM Play WHERE album_artist LIKE :albumArtist")
    Play loadByAlbumArtist(String albumArtist);

    @Query("SELECT * FROM Play WHERE id LIKE :length")
    Play loadByLength(int length);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Play... songs);

    @Delete
    void delete(Play user);

    @Query("DELETE FROM Play WHERE id LIKE :id")
    void delete(String id);

    @Query("DELETE FROM Play")
    void deleteAll();


    
    @Query("SELECT * FROM Play WHERE plays_count IN (:playsCounts)")
    List<Play> loadAllByPlaysCount(int[] playsCounts);

    @Query("SELECT * FROM Play WHERE last_play_day IN (:days)")
    List<Play> loadAllByLastPlayDay(byte[] days);

    @Query("SELECT * FROM Play WHERE last_play_month IN (:months)")
    List<Play> loadAllByLastPlayMonth(byte[] months);

    @Query("SELECT * FROM Play WHERE last_play_year IN (:years)")
    List<Play> loadAllByLastPlayYear(byte[] years);

    @Query("SELECT * FROM Play WHERE last_play_hour IN (:hours)")
    List<Play> loadAllByLastPlayHour(byte[] hours);

    @Query("SELECT * FROM Play WHERE last_play_minute IN (:minutes)")
    List<Play> loadAllByLastPlayMinute(byte[] minutes);

    @Query("SELECT * FROM Play WHERE year IN (:years)")
    List<Play> loadAllByYear(byte[] years);

    @Query("SELECT * FROM Play WHERE genre IN (:genres)")
    List<Play> loadAllByGenre(String[] genres);
}
