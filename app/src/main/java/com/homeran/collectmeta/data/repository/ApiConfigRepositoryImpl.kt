package com.homeran.collectmeta.data.repository

import android.util.Log
import com.homeran.collectmeta.data.db.dao.ApiConfigDao
import com.homeran.collectmeta.data.db.entities.ApiConfigEntity
import com.homeran.collectmeta.data.mapper.ApiConfigMapper
import com.homeran.collectmeta.domain.model.ApiConfig
import com.homeran.collectmeta.domain.repository.ApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API配置仓库实现
 */
@Singleton
class ApiConfigRepositoryImpl @Inject constructor(
    private val apiConfigDao: ApiConfigDao
) : ApiConfigRepository {
    companion object {
        private const val TAG = "ApiConfigRepositoryImpl"
        
        // API配置ID常量
        const val NOTION_BOOK_CONFIG_ID = "notion_book"
        const val OPEN_LIBRARY_CONFIG_ID = "open_library"
        const val GOOGLE_BOOKS_CONFIG_ID = "google_books"
        const val NOTION_MOVIE_CONFIG_ID = "notion_movie"
        const val TMDB_CONFIG_ID = "tmdb"
        const val NOTION_TVSHOW_CONFIG_ID = "notion_tvshow"
        const val NOTION_GAME_CONFIG_ID = "notion_game"
        const val IGDB_CONFIG_ID = "igdb"
    }

    override fun getAllConfigs(): Flow<List<ApiConfig>> {
        Log.d(TAG, "Getting all API configurations")
        return apiConfigDao.getAllConfigs().map { entities ->
            ApiConfigMapper.toDomainList(entities)
        }
    }

    override suspend fun getConfigById(configId: String): ApiConfig? {
        Log.d(TAG, "Getting API configuration: $configId")
        return apiConfigDao.getConfigById(configId)?.let { entity ->
            ApiConfigMapper.toDomain(entity)
        }
    }

    override fun getConfigByIdAsFlow(configId: String): Flow<ApiConfig?> {
        Log.d(TAG, "Getting API configuration as flow: $configId")
        return apiConfigDao.getConfigByIdAsFlow(configId).map { entity ->
            entity?.let { ApiConfigMapper.toDomain(it) }
        }
    }

    // ========== 书籍相关API配置 ==========
    
    override suspend fun getNotionBookConfig(): ApiConfig? {
        Log.d(TAG, "Getting Notion Book API configuration")
        return getConfigById(NOTION_BOOK_CONFIG_ID)
    }

    override suspend fun saveNotionBookConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Notion Book API configuration")
        val config = ApiConfig(
            configId = NOTION_BOOK_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Notion Book API configuration saved")
    }

    override suspend fun getOpenLibraryConfig(): ApiConfig? {
        Log.d(TAG, "Getting Open Library API configuration")
        return getConfigById(OPEN_LIBRARY_CONFIG_ID)
    }

    override suspend fun saveOpenLibraryConfig(
        baseUrl: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Open Library API configuration")
        val config = ApiConfig(
            configId = OPEN_LIBRARY_CONFIG_ID,
            apiKey = "", // Open Library不需要API密钥
            baseUrl = baseUrl,
            param1 = null,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Open Library API configuration saved")
    }

    override suspend fun getGoogleBooksConfig(): ApiConfig? {
        Log.d(TAG, "Getting Google Books API configuration")
        return getConfigById(GOOGLE_BOOKS_CONFIG_ID)
    }

    override suspend fun saveGoogleBooksConfig(
        apiKey: String,
        baseUrl: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Google Books API configuration")
        val config = ApiConfig(
            configId = GOOGLE_BOOKS_CONFIG_ID,
            apiKey = apiKey,
            baseUrl = baseUrl,
            param1 = null,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Google Books API configuration saved")
    }

    // ========== 电影相关API配置 ==========
    
    override suspend fun getNotionMovieConfig(): ApiConfig? {
        Log.d(TAG, "Getting Notion Movie API configuration")
        return getConfigById(NOTION_MOVIE_CONFIG_ID)
    }

    override suspend fun saveNotionMovieConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Notion Movie API configuration")
        val config = ApiConfig(
            configId = NOTION_MOVIE_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Notion Movie API configuration saved")
    }

    override suspend fun getTmdbConfig(): ApiConfig? {
        Log.d(TAG, "Getting TMDB API configuration")
        return getConfigById(TMDB_CONFIG_ID)
    }

    override suspend fun saveTmdbConfig(
        token: String,
        baseUrl: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving TMDB API configuration")
        val config = ApiConfig(
            configId = TMDB_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = null,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "TMDB API configuration saved")
    }

    // ========== 电视剧相关API配置 ==========
    
    override suspend fun getNotionTvShowConfig(): ApiConfig? {
        Log.d(TAG, "Getting Notion TV Show API configuration")
        return getConfigById(NOTION_TVSHOW_CONFIG_ID)
    }

    override suspend fun saveNotionTvShowConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Notion TV Show API configuration")
        val config = ApiConfig(
            configId = NOTION_TVSHOW_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Notion TV Show API configuration saved")
    }

    // ========== 游戏相关API配置 ==========
    
    override suspend fun getNotionGameConfig(): ApiConfig? {
        Log.d(TAG, "Getting Notion Game API configuration")
        return getConfigById(NOTION_GAME_CONFIG_ID)
    }

    override suspend fun saveNotionGameConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving Notion Game API configuration")
        val config = ApiConfig(
            configId = NOTION_GAME_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "Notion Game API configuration saved")
    }

    override suspend fun getIgdbConfig(): ApiConfig? {
        Log.d(TAG, "Getting IGDB API configuration")
        return getConfigById(IGDB_CONFIG_ID)
    }

    override suspend fun saveIgdbConfig(
        clientId: String,
        clientSecret: String,
        baseUrl: String,
        isEnabled: Boolean
    ) {
        Log.d(TAG, "Saving IGDB API configuration")
        val config = ApiConfig(
            configId = IGDB_CONFIG_ID,
            apiKey = clientId,
            baseUrl = baseUrl,
            param1 = clientSecret,
            param2 = null,
            param3 = null,
            param4 = null,
            param5 = null,
            isEnabled = isEnabled,
            lastUpdated = System.currentTimeMillis()
        )
        saveConfig(config)
        Log.d(TAG, "IGDB API configuration saved")
    }

    override suspend fun saveConfig(config: ApiConfig) {
        Log.d(TAG, "Saving API configuration: ${config.configId}")
        apiConfigDao.insertOrUpdateConfig(ApiConfigMapper.toEntity(config))
        Log.d(TAG, "API configuration saved: ${config.configId}")
    }

    override suspend fun deleteConfig(configId: String) {
        Log.d(TAG, "Deleting API configuration: $configId")
        apiConfigDao.deleteConfigById(configId)
        Log.d(TAG, "API configuration deleted: $configId")
    }

    override suspend fun setConfigEnabled(configId: String, isEnabled: Boolean) {
        Log.d(TAG, "Setting API configuration enabled: $configId, $isEnabled")
        apiConfigDao.setConfigEnabled(configId, isEnabled)
        Log.d(TAG, "API configuration enabled status updated: $configId, $isEnabled")
    }

    override suspend fun updateApiKey(configId: String, apiKey: String) {
        Log.d(TAG, "Updating API key: $configId")
        apiConfigDao.updateApiKey(configId, apiKey)
        Log.d(TAG, "API key updated: $configId")
    }
} 