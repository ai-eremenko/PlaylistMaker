package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: PlaylistEntity): Long

    @Update
    fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists ORDER BY name")
    fun getAll(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    fun getTrackById(trackId: String): PlaylistTrackEntity?

    @Query("UPDATE playlists SET trackIds = :trackIds WHERE id = :playlistId")
    fun addTrackToPlaylist(playlistId: Long, trackIds: String):Int
}