package com.homeran.collectmeta.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.homeran.collectmeta.domain.repository.SettingsRepository
import com.homeran.collectmeta.presentation.viewmodel.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val TMDB_API_KEY = stringPreferencesKey("tmdb_api_key")
        val IGDB_API_KEY = stringPreferencesKey("igdb_api_key")
        val DOUBAN_API_KEY = stringPreferencesKey("douban_api_key")
        val NOTION_API_KEY = stringPreferencesKey("notion_api_key")
        val NOTION_DATABASE_ID = stringPreferencesKey("notion_database_id")
    }

    override suspend fun getSettings(): Settings {
        val preferences = context.dataStore.data.first()
        return Settings(
            tmdbApiKey = preferences[PreferencesKeys.TMDB_API_KEY] ?: "",
            igdbApiKey = preferences[PreferencesKeys.IGDB_API_KEY] ?: "",
            doubanApiKey = preferences[PreferencesKeys.DOUBAN_API_KEY] ?: "",
            notionApiKey = preferences[PreferencesKeys.NOTION_API_KEY] ?: "",
            notionDatabaseId = preferences[PreferencesKeys.NOTION_DATABASE_ID] ?: ""
        )
    }

    override suspend fun saveSettings(settings: Settings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TMDB_API_KEY] = settings.tmdbApiKey
            preferences[PreferencesKeys.IGDB_API_KEY] = settings.igdbApiKey
            preferences[PreferencesKeys.DOUBAN_API_KEY] = settings.doubanApiKey
            preferences[PreferencesKeys.NOTION_API_KEY] = settings.notionApiKey
            preferences[PreferencesKeys.NOTION_DATABASE_ID] = settings.notionDatabaseId
        }
    }
} 