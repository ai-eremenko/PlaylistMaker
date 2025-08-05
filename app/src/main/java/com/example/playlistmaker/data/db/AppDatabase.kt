package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavouriteTrackDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Database(
    version = 8,
    entities = [
        FavouriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun favouriteTrackDao(): FavouriteTrackDao

    abstract fun playlistDao(): PlaylistDao

}