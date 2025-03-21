package com.homeran.collectmeta.data.db.dao

import androidx.room.*
import com.homeran.collectmeta.data.db.entities.ApiConfigEntity
import kotlinx.coroutines.flow.Flow

/**
 * API配置数据访问对象
 * 用于操作api_configs表
 */
@Dao
interface ApiConfigDao {
    /**
     * 获取所有API配置
     */
    @Query("SELECT * FROM api_configs")
    fun getAllConfigs(): Flow<List<ApiConfigEntity>>
    
    /**
     * 获取特定API的配置
     * @param configId API配置ID
     */
    @Query("SELECT * FROM api_configs WHERE configId = :configId")
    suspend fun getConfigById(configId: String): ApiConfigEntity?
    
    /**
     * 获取特定API的配置（作为Flow）
     * @param configId API配置ID
     */
    @Query("SELECT * FROM api_configs WHERE configId = :configId")
    fun getConfigByIdAsFlow(configId: String): Flow<ApiConfigEntity?>
    
    /**
     * 插入或更新API配置
     * @param config API配置
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: ApiConfigEntity)
    
    /**
     * 删除API配置
     * @param config API配置
     */
    @Delete
    suspend fun deleteConfig(config: ApiConfigEntity)
    
    /**
     * 根据ID删除API配置
     * @param configId API配置ID
     */
    @Query("DELETE FROM api_configs WHERE configId = :configId")
    suspend fun deleteConfigById(configId: String)
    
    /**
     * 启用或禁用API配置
     * @param configId API配置ID
     * @param isEnabled 是否启用
     */
    @Query("UPDATE api_configs SET isEnabled = :isEnabled, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun setConfigEnabled(configId: String, isEnabled: Boolean, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新API密钥
     * @param configId API配置ID
     * @param apiKey 新的API密钥
     */
    @Query("UPDATE api_configs SET apiKey = :apiKey, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateApiKey(configId: String, apiKey: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新API基础URL
     * @param configId API配置ID
     * @param baseUrl 新的基础URL
     */
    @Query("UPDATE api_configs SET baseUrl = :baseUrl, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateBaseUrl(configId: String, baseUrl: String, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新自定义参数1
     * @param configId API配置ID
     * @param param1 新的参数值
     */
    @Query("UPDATE api_configs SET param1 = :param1, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateParam1(configId: String, param1: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新自定义参数2
     * @param configId API配置ID
     * @param param2 新的参数值
     */
    @Query("UPDATE api_configs SET param2 = :param2, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateParam2(configId: String, param2: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新自定义参数3
     * @param configId API配置ID
     * @param param3 新的参数值
     */
    @Query("UPDATE api_configs SET param3 = :param3, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateParam3(configId: String, param3: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新自定义参数4
     * @param configId API配置ID
     * @param param4 新的参数值
     */
    @Query("UPDATE api_configs SET param4 = :param4, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateParam4(configId: String, param4: String?, timestamp: Long = System.currentTimeMillis())
    
    /**
     * 更新自定义参数5
     * @param configId API配置ID
     * @param param5 新的参数值
     */
    @Query("UPDATE api_configs SET param5 = :param5, lastUpdated = :timestamp WHERE configId = :configId")
    suspend fun updateParam5(configId: String, param5: String?, timestamp: Long = System.currentTimeMillis())
} 