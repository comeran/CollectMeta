package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.data.db.entities.TvShowStatus
import com.homeran.collectmeta.data.db.entities.WatchStatus
import com.homeran.collectmeta.domain.model.TvEpisode
import com.homeran.collectmeta.domain.model.TvSeason
import com.homeran.collectmeta.domain.model.TvShow
import kotlinx.coroutines.flow.Flow

/**
 * 电视剧仓库接口，定义电视剧相关操作
 */
interface TvShowRepository {
    /**
     * 获取所有电视剧
     */
    fun getAllTvShows(): Flow<List<TvShow>>
    
    /**
     * 根据ID获取电视剧
     */
    fun getTvShowById(id: String): Flow<TvShow?>
    
    /**
     * 根据观看状态获取电视剧
     */
    fun getTvShowsByWatchStatus(status: WatchStatus): Flow<List<TvShow>>
    
    /**
     * 搜索电视剧
     */
    fun searchTvShows(query: String): Flow<List<TvShow>>
    
    /**
     * 添加电视剧
     */
    suspend fun addTvShow(tvShow: TvShow)
    
    /**
     * 删除电视剧
     */
    suspend fun deleteTvShow(tvShowId: String)
    
    /**
     * 更新观看状态
     */
    suspend fun updateWatchStatus(tvShowId: String, status: WatchStatus)
    
    /**
     * 更新观看进度
     */
    suspend fun updateWatchProgress(
        tvShowId: String,
        currentSeason: Int,
        currentEpisode: Int,
        watchedEpisodes: Set<String>
    )
    
    /**
     * 更新用户评分
     */
    suspend fun updateRating(tvShowId: String, rating: Float)
    
    /**
     * 更新用户评论
     */
    suspend fun updateComment(tvShowId: String, comment: String)
    
    /**
     * 更新电视剧用户评分
     */
    suspend fun updateTvShowUserRating(id: String, rating: Float?)
    
    /**
     * 更新电视剧用户评论
     */
    suspend fun updateTvShowUserComment(id: String, comment: String?)
    
    /**
     * 更新电视剧播出状态
     */
    suspend fun updateTvShowStatus(id: String, status: TvShowStatus)
    
    /**
     * 更新电视剧Notion页面ID
     */
    suspend fun updateTvShowNotionId(id: String, notionPageId: String)
    
    /**
     * 更新剧集观看状态
     */
    suspend fun updateEpisodeWatchStatus(
        tvShowId: String,
        seasonNumber: Int,
        episodeNumber: Int,
        watched: Boolean
    )
    
    /**
     * 获取剧集
     */
    fun getEpisode(
        tvShowId: String,
        seasonNumber: Int,
        episodeNumber: Int
    ): Flow<TvEpisode?>
    
    /**
     * 获取季
     */
    fun getSeason(
        tvShowId: String,
        seasonNumber: Int
    ): Flow<TvSeason?>
} 