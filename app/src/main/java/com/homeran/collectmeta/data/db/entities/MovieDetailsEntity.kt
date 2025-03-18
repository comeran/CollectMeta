package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 电影详情实体类，包含电影特有信息
 */
@Entity(tableName = "movie_details")
data class MovieDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "director") val director: String,
    @ColumnInfo(name = "cast") val cast: String, // 存储为JSON
    @ColumnInfo(name = "duration") val duration: Int, // 时长（分钟）
    @ColumnInfo(name = "region") val region: String, // 制片地区
    @ColumnInfo(name = "episode_count") val episodeCount: Int, // 集数
    @ColumnInfo(name = "episode_duration") val episodeDuration: Int, // 单集时长
    @ColumnInfo(name = "tmdb_id") val tmdbId: String?,
    @ColumnInfo(name = "trakt_url") val traktUrl: String?
) 