package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.domain.model.Movie
import com.homeran.collectmeta.domain.model.SyncResult
import com.homeran.collectmeta.domain.model.TvShow
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    /**
     * 同步所有电影数据到Notion
     * @return 同步结果流
     */
    fun syncMoviesToNotion(): Flow<SyncResult>
    
    /**
     * 同步特定电影到Notion
     * @param movie 要同步的电影
     * @return 同步结果
     */
    suspend fun syncMovieToNotion(movie: Movie): SyncResult
    
    /**
     * 从Notion同步所有电影数据
     * @return 同步结果流
     */
    fun syncMoviesFromNotion(): Flow<SyncResult>
    
    /**
     * 同步所有电视剧数据到Notion
     * @return 同步结果流
     */
    fun syncTvShowsToNotion(): Flow<SyncResult>
    
    /**
     * 同步特定电视剧到Notion
     * @param tvShow 要同步的电视剧
     * @return 同步结果
     */
    suspend fun syncTvShowToNotion(tvShow: TvShow): SyncResult
    
    /**
     * 从Notion同步所有电视剧数据
     * @return 同步结果流
     */
    fun syncTvShowsFromNotion(): Flow<SyncResult>
    
    /**
     * 获取最后同步时间
     * @return 最后同步时间（毫秒）
     */
    suspend fun getLastSyncTime(): Long
    
    /**
     * 更新最后同步时间
     * @param time 同步时间（毫秒）
     */
    suspend fun updateLastSyncTime(time: Long)
    
    /**
     * 测试Notion连接
     * @param apiKey Notion API Key
     * @param databaseId Notion数据库ID
     * @return 连接是否成功
     */
    suspend fun testNotionConnection(apiKey: String, databaseId: String): Boolean
} 