package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 电视剧详情实体类，包含电视剧特有信息
 */
@Entity(
    tableName = "tv_show_details",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TvShowDetailsEntity(
    @PrimaryKey
    @ColumnInfo(name = "media_id") 
    val mediaId: String,
    
    @ColumnInfo(name = "tmdb_id") 
    val tmdbId: String,
    
    @ColumnInfo(name = "total_episodes") 
    val totalEpisodes: Int,
    
    @ColumnInfo(name = "total_seasons") 
    val totalSeasons: Int,
    
    @ColumnInfo(name = "show_status") 
    val showStatus: TvShowStatus,
    
    @ColumnInfo(name = "first_air_date") 
    val firstAirDate: String,
    
    @ColumnInfo(name = "last_air_date") 
    val lastAirDate: String?,
    
    @ColumnInfo(name = "network") 
    val network: String,
    
    @ColumnInfo(name = "episode_runtime") 
    val episodeRuntime: Int
) 