package it.developing.ico2k2.luckyplayer.database.data.songs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.developing.ico2k2.luckyplayer.database.data.plays.Song;

@Dao
public interface SongDao
{
    @Query("SELECT * FROM Song")
    List<Song> loadAll();

    @Query("SELECT * FROM Song WHERE id LIKE :id")
    Song loadById(String id);

    @Query("SELECT * FROM Song WHERE id IN (:ids)")
    List<Song> loadAllById(String[] ids);

    @Query("SELECT * FROM Song WHERE title IN (:titles)")
    List<Song> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM Song WHERE album IN (:albums)")
    List<Song> loadAllByAlbum(String[] albums);

    @Query("SELECT * FROM Song WHERE album_artist IN (:albumArtists)")
    List<Song> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM Song WHERE artist IN (:artists)")
    List<Song> loadAllByArtist(String[] artists);

    @Query("SELECT * FROM Song WHERE length IN (:lengths)")
    List<Song> loadAllByLength(short[] lengths);

    @Query("SELECT * FROM Song WHERE track_number IN (:trackNs)")
    List<Song> loadAllByTrackN(byte[] trackNs);

    @Query("SELECT * FROM Song WHERE track_total IN (:trackTotals)")
    List<Song> loadAllByTrackTotal(byte[] trackTotals);

    @Query("SELECT * FROM Song WHERE release_year IN (:releaseYears)")
    List<Song> loadAllByReleaseYear(short[] releaseYears);

    @Query("SELECT * FROM Song WHERE orig_year IN (:originalYears)")
    List<Song> loadAllByOriginalYear(short[] originalYears);

    @Query("SELECT * FROM Song WHERE genre IN (:genres)")
    List<Song> loadAllByGenre(String[] genres);

    @Query("SELECT * FROM Song WHERE lyrics IN (:lyrics)")
    List<Song> loadAllByLyrics(String[] lyrics);

    @Query("SELECT * FROM Song WHERE bpm IN (:bpms)")
    List<Song> loadAllByBPM(byte[] bpms);

    @Query("SELECT * FROM Song WHERE init_key IN (:initKeys)")
    List<Song> loadAllByInitKey(String[] initKeys);

    @Query("SELECT * FROM Song WHERE bitrate IN (:bitrates)")
    List<Song> loadAllByBitrate(short[] bitrates);

    @Query("SELECT * FROM Song WHERE format IN (:formats)")
    List<Song> loadAllByFormat(String[] formats);

    @Query("SELECT * FROM Song WHERE channels IN (:channels)")
    List<Song> loadAllByChannels(byte[] channels);

    @Query("SELECT * FROM Song WHERE vbr = 1")
    List<Song> loadAllVBR();

    @Query("SELECT * FROM Song WHERE vbr = 0")
    List<Song> loadAllCBR();

    @Query("SELECT * FROM Song WHERE lossless = 1")
    List<Song> loadAllLossless();

    @Query("SELECT * FROM Song WHERE lossless = 0")
    List<Song> loadAllLossy();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Song... songs);

    @Delete
    void delete(Song song);

    @Query("DELETE FROM Song WHERE id LIKE :id")
    void delete(String id);

    @Query("DELETE FROM Song")
    void deleteAll();
}
