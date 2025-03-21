package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.domain.model.ApiConfig
import kotlinx.coroutines.flow.Flow

/**
 * API配置仓库接口
 */
interface ApiConfigRepository {
    /**
     * 获取所有API配置
     */
    fun getAllConfigs(): Flow<List<ApiConfig>>

    /**
     * 获取特定API配置
     */
    suspend fun getConfigById(configId: String): ApiConfig?

    /**
     * 获取特定API配置（流）
     */
    fun getConfigByIdAsFlow(configId: String): Flow<ApiConfig?>

    // ========== 书籍相关API配置 ==========
    
    /**
     * 获取Notion书籍API配置
     */
    suspend fun getNotionBookConfig(): ApiConfig?

    /**
     * 保存Notion书籍API配置
     */
    suspend fun saveNotionBookConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    )

    /**
     * 获取Open Library API配置
     */
    suspend fun getOpenLibraryConfig(): ApiConfig?

    /**
     * 保存Open Library API配置
     */
    suspend fun saveOpenLibraryConfig(
        baseUrl: String,
        isEnabled: Boolean
    )

    /**
     * 获取Google Books API配置
     */
    suspend fun getGoogleBooksConfig(): ApiConfig?

    /**
     * 保存Google Books API配置
     */
    suspend fun saveGoogleBooksConfig(
        apiKey: String,
        baseUrl: String,
        isEnabled: Boolean
    )

    // ========== 电影相关API配置 ==========
    
    /**
     * 获取Notion电影API配置
     */
    suspend fun getNotionMovieConfig(): ApiConfig?

    /**
     * 保存Notion电影API配置
     */
    suspend fun saveNotionMovieConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    )

    /**
     * 获取TMDB API配置
     */
    suspend fun getTmdbConfig(): ApiConfig?

    /**
     * 保存TMDB API配置
     */
    suspend fun saveTmdbConfig(
        token: String,
        baseUrl: String,
        isEnabled: Boolean
    )

    // ========== 电视剧相关API配置 ==========
    
    /**
     * 获取Notion电视剧API配置
     */
    suspend fun getNotionTvShowConfig(): ApiConfig?

    /**
     * 保存Notion电视剧API配置
     */
    suspend fun saveNotionTvShowConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    )

    // ========== 游戏相关API配置 ==========
    
    /**
     * 获取Notion游戏API配置
     */
    suspend fun getNotionGameConfig(): ApiConfig?

    /**
     * 保存Notion游戏API配置
     */
    suspend fun saveNotionGameConfig(
        token: String,
        baseUrl: String,
        databaseId: String,
        isEnabled: Boolean
    )

    /**
     * 获取IGDB API配置
     */
    suspend fun getIgdbConfig(): ApiConfig?

    /**
     * 保存IGDB API配置
     */
    suspend fun saveIgdbConfig(
        clientId: String,
        clientSecret: String,
        baseUrl: String,
        isEnabled: Boolean
    )

    /**
     * 保存API配置
     */
    suspend fun saveConfig(config: ApiConfig)

    /**
     * 删除API配置
     */
    suspend fun deleteConfig(configId: String)

    /**
     * 启用或禁用API配置
     */
    suspend fun setConfigEnabled(configId: String, isEnabled: Boolean)

    /**
     * 更新API密钥
     */
    suspend fun updateApiKey(configId: String, apiKey: String)
} 