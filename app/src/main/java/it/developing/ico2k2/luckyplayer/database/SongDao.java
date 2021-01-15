package it.developing.ico2k2.luckyplayer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDao
{
    @Query("SELECT * FROM song")
    List<Song> getAll();

    @Query("SELECT * FROM song WHERE id IN (:ids)")
    List<Song> loadAllById(String[] ids);

    @Query("SELECT * FROM song WHERE plays_count IN (:playsCounts)")
    List<Song> loadAllByPlaysCount(int[] playsCounts);

    @Query("SELECT * FROM song WHERE last_play_day IN (:days)")
    List<Song> loadAllByLastPlayDay(byte[] days);

    @Query("SELECT * FROM song WHERE last_play_month IN (:months)")
    List<Song> loadAllByLastPlayMonth(byte[] months);

    @Query("SELECT * FROM song WHERE last_play_year IN (:years)")
    List<Song> loadAllByLastPlayYear(short[] years);

    @Query("SELECT * FROM song WHERE last_play_hour IN (:hours)")
    List<Song> loadAllByLastPlayHour(byte[] hours);

    @Query("SELECT * FROM song WHERE last_play_minute IN (:minutes)")
    List<Song> loadAllByLastPlayMinute(byte[] minutes);

    @Query("SELECT * FROM song WHERE year IN (:years)")
    List<Song> loadAllByYear(short[] years);

    @Query("SELECT * FROM song WHERE length IN (:lengths)")
    List<Song> loadAllByLength(int[] lengths);

    @Query("SELECT * FROM song WHERE genre IN (:genres)")
    List<Song> loadAllByGenre(String[] genres);

    @Query("SELECT * FROM song WHERE lyrics IN (:lyrics)")
    List<Song> loadAllByLyrics(String[] lyrics);

    @Query("SELECT * FROM song WHERE id LIKE :id")
    Song findById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Song... songs);

    @Delete
    void delete(Song user);

    @Query("DELETE FROM song WHERE id LIKE :id")
    void delete(String id);

    @Query("DELETE FROM song")
    void deleteAll();
}
