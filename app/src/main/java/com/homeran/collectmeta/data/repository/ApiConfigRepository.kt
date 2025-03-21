package com.homeran.collectmeta.data.repository

import android.util.Log
import com.homeran.collectmeta.data.db.dao.ApiConfigDao
import com.homeran.collectmeta.data.db.entities.ApiConfigEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API配置仓库
 * 负责管理外部API的配置信息
 */
@Singleton
class ApiConfigRepository @Inject constructor(
    private val apiConfigDao: ApiConfigDao
) {
    companion object {
        private const val TAG = "ApiConfigRepository"
        
        // 书籍相关API
        const val NOTION_BOOK_CONFIG_ID = "notion_book"
        const val OPEN_LIBRARY_CONFIG_ID = "open_library"
        const val GOOGLE_BOOKS_CONFIG_ID = "google_books"
        
        // 电影相关API
        const val NOTION_MOVIE_CONFIG_ID = "notion_movie"
        const val TMDB_CONFIG_ID = "tmdb"
        
        // 电视剧相关API
        const val NOTION_TVSHOW_CONFIG_ID = "notion_tvshow"
        
        // 游戏相关API
        const val NOTION_GAME_CONFIG_ID = "notion_game"
        const val IGDB_CONFIG_ID = "igdb"
    }

    /**
     * 获取所有API配置
     */
    fun getAllConfigs(): Flow<List<ApiConfigEntity>> {
        Log.d(TAG, "Getting all API configurations")
        return apiConfigDao.getAllConfigs()
    }

    /**
     * 获取特定API配置
     */
    suspend fun getConfigById(configId: String): ApiConfigEntity? {
        Log.d(TAG, "Getting API configuration: $configId")
        return apiConfigDao.getConfigById(configId)
    }

    /**
     * 获取特定API配置（流）
     */
    fun getConfigByIdAsFlow(configId: String): Flow<ApiConfigEntity?> {
        Log.d(TAG, "Getting API configuration as flow: $configId")
        return apiConfigDao.getConfigByIdAsFlow(configId)
    }

    // ========== 书籍相关API配置 ==========
    
    /**
     * 获取Notion书籍API配置
     */
    suspend fun getNotionBookConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Notion Book API configuration")
        return apiConfigDao.getConfigById(NOTION_BOOK_CONFIG_ID)
    }

    /**
     * 保存Notion书籍API配置
     */
    suspend fun saveNotionBookConfig(
        token: String,
        baseUrl: String = "https://api.notion.com/v1/",
        databaseId: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Notion Book API configuration")
        val config = ApiConfigEntity(
            configId = NOTION_BOOK_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId, // 数据库ID
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Notion Book API configuration saved")
    }


    /**
     * 获取Open Library API配置
     */
    suspend fun getOpenLibraryConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Open Library API configuration")
        return apiConfigDao.getConfigById(OPEN_LIBRARY_CONFIG_ID)
    }

    /**
     * 保存Open Library API配置
     */
    suspend fun saveOpenLibraryConfig(
        baseUrl: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Open Library API configuration")
        val config = ApiConfigEntity(
            configId = OPEN_LIBRARY_CONFIG_ID,
            apiKey = "", // Open Library不需要API密钥
            baseUrl = baseUrl,
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Open Library API configuration saved")
    }

    /**
     * 获取Google Books API配置
     */
    suspend fun getGoogleBooksConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Google Books API configuration")
        return apiConfigDao.getConfigById(GOOGLE_BOOKS_CONFIG_ID)
    }

    /**
     * 保存Google Books API配置
     */
    suspend fun saveGoogleBooksConfig(
        apiKey: String,
        baseUrl: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Google Books API configuration")
        val config = ApiConfigEntity(
            configId = GOOGLE_BOOKS_CONFIG_ID,
            apiKey = apiKey,
            baseUrl = baseUrl,
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Google Books API configuration saved")
    }

    // ========== 电影相关API配置 ==========
    
    /**
     * 获取Notion电影API配置
     */
    suspend fun getNotionMovieConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Notion Movie API configuration")
        return apiConfigDao.getConfigById(NOTION_MOVIE_CONFIG_ID)
    }

    /**
     * 保存Notion电影API配置
     */
    suspend fun saveNotionMovieConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Notion Movie API configuration")
        val config = ApiConfigEntity(
            configId = NOTION_MOVIE_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId, // 数据库ID
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Notion Movie API configuration saved")
    }

    /**
     * 获取TMDB API配置
     */
    suspend fun getTmdbConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting TMDB API configuration")
        return apiConfigDao.getConfigById(TMDB_CONFIG_ID)
    }

    /**
     * 保存TMDB API配置
     */
    suspend fun saveTmdbConfig(
        token: String,
        baseUrl: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving TMDB API configuration")
        val config = ApiConfigEntity(
            configId = TMDB_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "TMDB API configuration saved")
    }

    // ========== 电视剧相关API配置 ==========
    
    /**
     * 获取Notion电视剧API配置
     */
    suspend fun getNotionTvShowConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Notion TV Show API configuration")
        return apiConfigDao.getConfigById(NOTION_TVSHOW_CONFIG_ID)
    }

    /**
     * 保存Notion电视剧API配置
     */
    suspend fun saveNotionTvShowConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Notion TV Show API configuration")
        val config = ApiConfigEntity(
            configId = NOTION_TVSHOW_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId, // 数据库ID
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Notion TV Show API configuration saved")
    }

    // ========== 游戏相关API配置 ==========
    
    /**
     * 获取Notion游戏API配置
     */
    suspend fun getNotionGameConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting Notion Game API configuration")
        return apiConfigDao.getConfigById(NOTION_GAME_CONFIG_ID)
    }

    /**
     * 保存Notion游戏API配置
     */
    suspend fun saveNotionGameConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving Notion Game API configuration")
        val config = ApiConfigEntity(
            configId = NOTION_GAME_CONFIG_ID,
            apiKey = token,
            baseUrl = baseUrl,
            param1 = databaseId, // 数据库ID
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "Notion Game API configuration saved")
    }

    /**
     * 获取IGDB API配置
     */
    suspend fun getIgdbConfig(): ApiConfigEntity? {
        Log.d(TAG, "Getting IGDB API configuration")
        return apiConfigDao.getConfigById(IGDB_CONFIG_ID)
    }

    /**
     * 保存IGDB API配置
     */
    suspend fun saveIgdbConfig(
        clientId: String,
        clientSecret: String,
        baseUrl: String,
        isEnabled: Boolean = true
    ) {
        Log.d(TAG, "Saving IGDB API configuration")
        val config = ApiConfigEntity(
            configId = IGDB_CONFIG_ID,
            apiKey = clientId,
            baseUrl = baseUrl,
            param1 = clientSecret, // 客户端密钥
            isEnabled = isEnabled
        )
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "IGDB API configuration saved")
    }

    /**
     * 保存API配置
     */
    suspend fun saveConfig(config: ApiConfigEntity) {
        Log.d(TAG, "Saving API configuration: ${config.configId}")
        apiConfigDao.insertOrUpdateConfig(config)
        Log.d(TAG, "API configuration saved: ${config.configId}")
    }

    /**
     * 删除API配置
     */
    suspend fun deleteConfig(configId: String) {
        Log.d(TAG, "Deleting API configuration: $configId")
        apiConfigDao.deleteConfigById(configId)
        Log.d(TAG, "API configuration deleted: $configId")
    }

    /**
     * 启用或禁用API配置
     */
    suspend fun setConfigEnabled(configId: String, isEnabled: Boolean) {
        Log.d(TAG, "Setting API configuration enabled: $configId, $isEnabled")
        apiConfigDao.setConfigEnabled(configId, isEnabled)
        Log.d(TAG, "API configuration enabled status updated: $configId, $isEnabled")
    }

    /**
     * 更新API密钥
     */
    suspend fun updateApiKey(configId: String, apiKey: String) {
        Log.d(TAG, "Updating API key: $configId")
        apiConfigDao.updateApiKey(configId, apiKey)
        Log.d(TAG, "API key updated: $configId")
    }
} 