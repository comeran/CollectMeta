package com.homeran.collectmeta.data.db.dao

import androidx.room.*
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.TvShowDetailsEntity
import com.homeran.collectmeta.data.db.entities.TvShowWatchProgressEntity
import com.homeran.collectmeta.data.db.entities.TvSeasonEntity
import com.homeran.collectmeta.data.db.entities.TvEpisodeEntity
import kotlinx.coroutines.flow.Flow

/**
 * 电视剧数据访问对象
 */
@Dao
interface TvShowDao {
    @Query("SELECT * FROM media WHERE type = 'TV_SHOW'")
    fun getAllTvShows(): Flow<List<MediaEntity>>
    
    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun getTvShowById(id: String): MediaEntity?
    
    @Query("SELECT * FROM tv_show_details WHERE media_id = :id")
    suspend fun getTvShowDetailsById(id: String): TvShowDetailsEntity?
    
    @Query("SELECT * FROM tv_show_watch_progress WHERE media_id = :id")
    fun getTvShowWatchProgressById(id: String): Flow<TvShowWatchProgressEntity?>
    
    @Transaction
    suspend fun getTvShowWithRelations(id: String): TvShowWithDetails? {
        val mediaEntity = getTvShowById(id) ?: return null
        val details = getTvShowDetailsById(id)
        val seasons = emptyList<TvSeasonEntity>() // We'll implement this later when database is ready
        val episodes = emptyList<TvEpisodeEntity>() // We'll implement this later when database is ready
        
        return TvShowWithDetails(
            media = mediaEntity,
            details = details,
            seasons = seasons,
            episodes = episodes
        )
    }
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(tvShow: MediaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShowDetails(details: TvShowDetailsEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShowWatchProgress(progress: TvShowWatchProgressEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvSeason(season: TvSeasonEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvEpisode(episode: TvEpisodeEntity)
    
    @Delete
    suspend fun deleteTvShow(tvShow: MediaEntity)
    
    @Query("DELETE FROM tv_show_details WHERE media_id = :id")
    suspend fun deleteTvShowDetails(id: String)
    
    @Query("DELETE FROM tv_show_watch_progress WHERE media_id = :id")
    suspend fun deleteTvShowWatchProgress(id: String)
    
    @Transaction
    suspend fun deleteTvShowSeasons(id: String) {
        // We'll implement this later when database is ready
    }
    
    @Transaction
    suspend fun deleteTvShowEpisodes(id: String) {
        // We'll implement this later when database is ready
    }
    
    @Query("UPDATE tv_show_watch_progress SET watch_status = :status WHERE media_id = :id")
    suspend fun updateWatchStatus(id: String, status: String)
    
    @Query("UPDATE tv_show_watch_progress SET rating = :rating WHERE media_id = :id")
    suspend fun updateRating(id: String, rating: Float)
    
    @Query("UPDATE tv_show_watch_progress SET comment = :comment WHERE media_id = :id")
    suspend fun updateComment(id: String, comment: String)
    
    @Transaction
    suspend fun updateEpisodeWatchStatus(episodeId: String, status: String) {
        // We'll implement this later when database is ready
    }
    
    @Query("SELECT * FROM media WHERE type = 'TV_SHOW' AND (title LIKE '%' || :query || '%' OR original_title LIKE '%' || :query || '%')")
    suspend fun searchTvShows(query: String): List<MediaEntity>
    
    @Query("SELECT m.* FROM media m JOIN tv_show_watch_progress p ON m.id = p.media_id WHERE m.type = 'TV_SHOW' AND p.watch_status = :status")
    suspend fun getTvShowsByWatchStatus(status: String): List<MediaEntity>
}

/**
 * 电视剧详情类，包含电视剧及其相关信息
 */
data class TvShowWithDetails(
    val media: MediaEntity,
    val details: TvShowDetailsEntity?,
    val seasons: List<TvSeasonEntity> = emptyList(),
    val episodes: List<TvEpisodeEntity> = emptyList()
) 