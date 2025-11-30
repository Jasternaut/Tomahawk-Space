package com.example.tomahawkspace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tomahawkspace.data.local.dao.ApodDao
import com.example.tomahawkspace.data.local.entity.ApodEntity

@Database(entities = [ApodEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
}
