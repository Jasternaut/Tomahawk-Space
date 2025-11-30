package com.example.tomahawkspace.data.repository

import com.example.tomahawkspace.data.local.dao.ApodDao
import com.example.tomahawkspace.data.local.entity.ApodEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodRepository @Inject constructor(
    private val apodDao: ApodDao
) {
    val allApods: Flow<List<ApodEntity>> = apodDao.getAllApods()

    fun getApodFlowByDate(date: String): Flow<ApodEntity?> {
        return apodDao.getApodFlowByDate(date)
    }

    suspend fun getApodByDate(date: String): ApodEntity? {
        return apodDao.getApodByDate(date)
    }

    suspend fun insertApod(apod: ApodEntity) {
        apodDao.insertApod(apod)
    }

    suspend fun deleteApod(apod: ApodEntity) {
        apodDao.deleteApod(apod)
    }
}
