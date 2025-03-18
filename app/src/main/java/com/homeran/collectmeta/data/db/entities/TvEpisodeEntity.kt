package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.homeran.collectmeta.data.db.converters.Converters

/**
 * 电视剧集实体类
 */
@Entity(
    tableName = "tv_episodes",
    foreignKeys = [
        ForeignKey(
            entity = TvSeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["season_id"])
    ]
)
@TypeConverters(Converters::class)
data class TvEpisodeEntity(
    @PrimaryKey 
    val id: String,
    
    @ColumnInfo(name = "season_id") 
    val seasonId: String,
    
    @ColumnInfo(name = "episode_number") 
    val episodeNumber: Int,
    
    @ColumnInfo(name = "name") 
    val name: String,
    
    @ColumnInfo(name = "overview") 
    val overview: String,
    
    @ColumnInfo(name = "air_date") 
    val airDate: String,
    
    @ColumnInfo(name = "runtime") 
    val runtime: Int,
    
    @ColumnInfo(name = "still_path") 
    val stillPath: String?,
    
    @ColumnInfo(name = "vote_average") 
    val voteAverage: Float = 0.0f,
    
    @ColumnInfo(name = "vote_count") 
    val voteCount: Int = 0,
    
    @ColumnInfo(name = "watch_status", defaultValue = "'UNWATCHED'") 
    val watchStatus: String = "UNWATCHED"
) 