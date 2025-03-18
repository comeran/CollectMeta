package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 电视剧季实体类
 */
@Entity(
    tableName = "tv_seasons",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["media_id"])
    ]
)
data class TvSeasonEntity(
    @PrimaryKey 
    val id: String,
    
    @ColumnInfo(name = "media_id") 
    val mediaId: String,
    
    @ColumnInfo(name = "season_number") 
    val seasonNumber: Int,
    
    @ColumnInfo(name = "name") 
    val name: String,
    
    @ColumnInfo(name = "overview") 
    val overview: String,
    
    @ColumnInfo(name = "air_date") 
    val airDate: String,
    
    @ColumnInfo(name = "episode_count") 
    val episodeCount: Int,
    
    @ColumnInfo(name = "poster_path") 
    val posterPath: String?
) 