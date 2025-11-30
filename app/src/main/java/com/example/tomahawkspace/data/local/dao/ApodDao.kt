package com.example.tomahawkspace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tomahawkspace.data.local.entity.ApodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Query("SELECT * FROM apods")
    fun getAllApods(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apods WHERE date = :date")
    fun getApodFlowByDate(date: String): Flow<ApodEntity?>

    @Query("SELECT * FROM apods WHERE date = :date")
    suspend fun getApodByDate(date: String): ApodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApod(apod: ApodEntity)

    @Delete
    suspend fun deleteApod(apod: ApodEntity)
}
