package it.developing.ico2k2.luckyplayer.database.data.songs;

import static it.developing.ico2k2.luckyplayer.database.data.songs.SongDetailed.*;

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

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_URI + " LIKE :uri")
    SongDetailed loadByUri(String uri);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_URI + " IN (:uris)")
    List<SongDetailed> loadAllByUris(String[] uris);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_TITLE + " IN (:titles)")
    List<SongDetailed> loadAllByTitle(String[] titles);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_ALBUM + " IN (:albums)")
    List<SongDetailed> loadAllByAlbum(String[] albums);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_ALBUM_ARTIST + " IN (:albumArtists)")
    List<SongDetailed> loadAllByAlbumArtist(String[] albumArtists);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_ARTIST + " IN (:artists)")
    List<SongDetailed> loadAllByArtist(String[] artists);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_LENGTH + " IN (:lengths)")
    List<SongDetailed> loadAllByLength(short[] lengths);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_TRACK_N + " IN (:trackNs)")
    List<SongDetailed> loadAllByTrackN(byte[] trackNs);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_TRACK_TOTAL + " IN (:trackTotals)")
    List<SongDetailed> loadAllByTrackTotal(byte[] trackTotals);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_YEAR_RELEASE + " IN (:releaseYears)")
    List<SongDetailed> loadAllByReleaseYear(short[] releaseYears);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_YEAR_ORIGINAL + " IN (:originalYears)")
    List<SongDetailed> loadAllByOriginalYear(short[] originalYears);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_GENRE + " IN (:genres)")
    List<SongDetailed> loadAllByGenre(String[] genres);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_LYRICS + " IN (:lyrics)")
    List<SongDetailed> loadAllByLyrics(String[] lyrics);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_BPM + " IN (:bpms)")
    List<SongDetailed> loadAllByBPM(byte[] bpms);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_KEY_INIT + " IN (:initKeys)")
    List<SongDetailed> loadAllByInitKey(String[] initKeys);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_BITRATE + " IN (:bitrates)")
    List<SongDetailed> loadAllByBitrate(short[] bitrates);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_FORMAT + " IN (:formats)")
    List<SongDetailed> loadAllByFormat(String[] formats);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_CHANNELS + " IN (:channels)")
    List<SongDetailed> loadAllByChannels(byte[] channels);

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_VBR + " = 1")
    List<SongDetailed> loadAllVBR();

    @Query("SELECT * FROM SongDetailed WHERE vbr = 0")
    List<SongDetailed> loadAllCBR();

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_LOSSLESS + " = 1")
    List<SongDetailed> loadAllLossless();

    @Query("SELECT * FROM SongDetailed WHERE " + COLUMN_LOSSLESS + " = 0")
    List<SongDetailed> loadAllLossy();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SongDetailed... songs);

    @Delete
    void delete(SongDetailed song);

    @Query("DELETE FROM SongDetailed WHERE " + COLUMN_URI + " LIKE :id")
    void delete(String id);

    @Query("DELETE FROM SongDetailed")
    void deleteAll();
}
