package com.tomahawk.space.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tomahawk.space.createDataStore
import com.tomahawk.space.data.model.ApodResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GalleryRepository(
    private val dataStore: DataStore<Preferences> = createDataStore("gallery_prefs")
) {
    private val likedApodsKey = stringPreferencesKey("liked_apods_json")
    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    val likedApods: Flow<List<ApodResponse>> = dataStore.data.map { prefs ->
        val jsonString = prefs[likedApodsKey] ?: return@map emptyList()
        try {
            jsonFormatter.decodeFromString<List<ApodResponse>>(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun toggleLike(apod: ApodResponse) {
        dataStore.edit { prefs ->
            val jsonString = prefs[likedApodsKey]
            val currentList: MutableList<ApodResponse> = try {
                if (jsonString != null) {
                    jsonFormatter.decodeFromString<List<ApodResponse>>(jsonString).toMutableList()
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
                currentList.add(0, apod)
            }

            prefs[likedApodsKey] = jsonFormatter.encodeToString(currentList)
        }
    }

    suspend fun updateApod(updatedApod: ApodResponse) {
        dataStore.edit { prefs ->
            val jsonString = prefs[likedApodsKey] ?: return@edit
            val currentList: MutableList<ApodResponse> = try {
                jsonFormatter.decodeFromString<List<ApodResponse>>(jsonString).toMutableList()
            } catch (_: Exception) {
                mutableListOf()
            }

            val index = currentList.indexOfFirst { it.date == updatedApod.date }
            if (index >= 0) {
                currentList[index] = updatedApod
                prefs[likedApodsKey] = jsonFormatter.encodeToString(currentList)
            }
        }
    }

    suspend fun removeLike(date: String) {
        dataStore.edit { prefs ->
            val jsonString = prefs[likedApodsKey] ?: return@edit
            val currentList: MutableList<ApodResponse> = try {
                jsonFormatter.decodeFromString<List<ApodResponse>>(jsonString).toMutableList()
            } catch (_: Exception) {
                mutableListOf()
            }

            val filteredList = currentList.filterNot { it.date == date }
            prefs[likedApodsKey] = jsonFormatter.encodeToString(filteredList)
        }
    }
}