package com.tomahawk.space.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.galleryDataStore by preferencesDataStore(name = "gallery_prefs")

class GalleryRepository(private val context: Context) {
    private val likedApodsKey = stringPreferencesKey("liked_apods_json")
    private val gson = Gson()

    val likedApods: Flow<List<ApodResponse>> = context.galleryDataStore.data.map { prefs ->
        val json = prefs[likedApodsKey] ?: return@map emptyList()
        try {
            val type = object : TypeToken<List<ApodResponse>>() {}.type
            gson.fromJson<List<ApodResponse>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun toggleLike(apod: ApodResponse) {
        context.galleryDataStore.edit { prefs ->
            val json = prefs[likedApodsKey]
            val type = object : TypeToken<List<ApodResponse>>() {}.type
            val currentList: MutableList<ApodResponse> = try {
                if (json != null) {
                    gson.fromJson(json, type) ?: mutableListOf()
                } else {
                    mutableListOf()
                }
            } catch (_: Exception) {
                mutableListOf()
            }

            val existingIndex = currentList.indexOfFirst { it.date == apod.date }
            if (existingIndex >= 0) {
                currentList.removeAt(existingIndex)
            } else {
                currentList.add(0, apod) // Add to front
            }

            prefs[likedApodsKey] = gson.toJson(currentList)
        }
    }

    suspend fun updateApod(updatedApod: ApodResponse) {
        context.galleryDataStore.edit { prefs ->
            val json = prefs[likedApodsKey] ?: return@edit
            val type = object : TypeToken<List<ApodResponse>>() {}.type
            val currentList: MutableList<ApodResponse> = try {
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (_: Exception) {
                mutableListOf()
            }

            val index = currentList.indexOfFirst { it.date == updatedApod.date }
            if (index >= 0) {
                currentList[index] = updatedApod
                prefs[likedApodsKey] = gson.toJson(currentList)
            }
        }
    }

    suspend fun removeLike(date: String) {
        context.galleryDataStore.edit { prefs ->
            val json = prefs[likedApodsKey] ?: return@edit
            val type = object : TypeToken<List<ApodResponse>>() {}.type
            val currentList: MutableList<ApodResponse> = try {
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (_: Exception) {
                mutableListOf()
            }

            val filteredList = currentList.filterNot { it.date == date }
            prefs[likedApodsKey] = gson.toJson(filteredList)
        }
    }
}
