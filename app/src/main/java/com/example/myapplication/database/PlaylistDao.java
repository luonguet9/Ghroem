package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.models.Playlist;
import com.example.myapplication.models.Song;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPlaylist(Playlist...playlists);

    @Update
    void updatePlaylist(Playlist playlist);

    @Delete
    void deletePlaylist(Playlist playlist);

    @Query("SELECT * FROM playlist")
    List<Playlist> getListPlaylist();

    @Query("SELECT * FROM playlist WHERE name = :name")
    Playlist getPlaylistByName(String name);

    @Query("SELECT * FROM playlist WHERE id = :id")
    Playlist getPlaylistById(int id);

    @Query("SELECT * FROM song WHERE playlistId = :playlistId")
    List<Song> getAllSongsInPlaylist(int playlistId);

    @Insert
    void insertSong(Song song);

    @Delete
    void deleteSong(Song song);

    @Query("SELECT * FROM song WHERE name LIKE '%' || :name || '%'")
    List<Song> searchSong(String name);
}
