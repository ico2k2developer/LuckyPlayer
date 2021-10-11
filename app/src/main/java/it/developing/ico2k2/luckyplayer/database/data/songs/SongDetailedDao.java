package it.developing.ico2k2.luckyplayer.database.data.songs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SongDetailedDao
{
    @Query("SELECT * FROM SongDetailed")
    List<SongDetailed> loadAll();

    @Query("SELECT * FROM SongDetailed WHERE uri LIKE :uri")
    SongDetailed loadByUri(String uri);

    @Query("SELECT * FROM SongDetailed WHERE uri IN (:uris)")
    List<SongDetailed> loadAllByUris(String[] uris);

    @Query("SELECT * FROM SongDetailed WHERE title IN (:titles)")
    List<SongDetailed> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM SongDetailed WHERE album IN (:albums)")
    List<SongDetailed> loadAllByAlbum(String[] albums);

    @Query("SELECT * FROM SongDetailed WHERE album_artist IN (:albumArtists)")
    List<SongDetailed> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM SongDetailed WHERE artist IN (:artists)")
    List<SongDetailed> loadAllByArtist(String[] artists);

    @Query("SELECT * FROM SongDetailed WHERE length IN (:lengths)")
    List<SongDetailed> loadAllByLength(short[] lengths);

    @Query("SELECT * FROM SongDetailed WHERE track_number IN (:trackNs)")
    List<SongDetailed> loadAllByTrackN(byte[] trackNs);

    @Query("SELECT * FROM SongDetailed WHERE track_total IN (:trackTotals)")
    List<SongDetailed> loadAllByTrackTotal(byte[] trackTotals);

    @Query("SELECT * FROM SongDetailed WHERE release_year IN (:releaseYears)")
    List<SongDetailed> loadAllByReleaseYear(short[] releaseYears);

    @Query("SELECT * FROM SongDetailed WHERE orig_year IN (:originalYears)")
    List<SongDetailed> loadAllByOriginalYear(short[] originalYears);

    @Query("SELECT * FROM SongDetailed WHERE genre IN (:genres)")
    List<SongDetailed> loadAllByGenre(String[] genres);

    @Query("SELECT * FROM SongDetailed WHERE lyrics IN (:lyrics)")
    List<SongDetailed> loadAllByLyrics(String[] lyrics);

    @Query("SELECT * FROM SongDetailed WHERE bpm IN (:bpms)")
    List<SongDetailed> loadAllByBPM(byte[] bpms);

    @Query("SELECT * FROM SongDetailed WHERE init_key IN (:initKeys)")
    List<SongDetailed> loadAllByInitKey(String[] initKeys);

    @Query("SELECT * FROM SongDetailed WHERE bitrate IN (:bitrates)")
    List<SongDetailed> loadAllByBitrate(short[] bitrates);

    @Query("SELECT * FROM SongDetailed WHERE format IN (:formats)")
    List<SongDetailed> loadAllByFormat(String[] formats);

    @Query("SELECT * FROM SongDetailed WHERE channels IN (:channels)")
    List<SongDetailed> loadAllByChannels(byte[] channels);

    @Query("SELECT * FROM SongDetailed WHERE vbr = 1")
    List<SongDetailed> loadAllVBR();

    @Query("SELECT * FROM SongDetailed WHERE vbr = 0")
    List<SongDetailed> loadAllCBR();

    @Query("SELECT * FROM SongDetailed WHERE lossless = 1")
    List<SongDetailed> loadAllLossless();

    @Query("SELECT * FROM SongDetailed WHERE lossless = 0")
    List<SongDetailed> loadAllLossy();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SongDetailed... songs);

    @Delete
    void delete(SongDetailed song);

    @Query("DELETE FROM SongDetailed WHERE uri LIKE :id")
    void delete(String id);

    @Query("DELETE FROM SongDetailed")
    void deleteAll();
}
