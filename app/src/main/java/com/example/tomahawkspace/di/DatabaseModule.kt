package com.example.tomahawkspace.di

import android.content.Context
import androidx.room.Room
import com.example.tomahawkspace.data.local.AppDatabase
import com.example.tomahawkspace.data.local.dao.ApodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tomahawk_space_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideApodDao(appDatabase: AppDatabase): ApodDao {
        return appDatabase.apodDao()
    }
}
