package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.homeran.collectmeta.data.db.converters.Converters

/**
 * 电视剧观看进度实体类
 */
@Entity(
    tableName = "tv_show_watch_progress",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(Converters::class)
data class TvShowWatchProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "media_id") 
    val mediaId: String,
    
    @ColumnInfo(name = "watch_status") 
    val watchStatus: String,
    
    @ColumnInfo(name = "current_season") 
    val currentSeason: Int,
    
    @ColumnInfo(name = "current_episode") 
    val currentEpisode: Int,
    
    @ColumnInfo(name = "watched_episodes") 
    val watchedEpisodes: Set<String> = emptySet(),
    
    @ColumnInfo(name = "rating") 
    val rating: Float? = null,
    
    @ColumnInfo(name = "comment") 
    val comment: String? = null
) 