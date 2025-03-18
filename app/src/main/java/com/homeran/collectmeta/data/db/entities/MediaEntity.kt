package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.homeran.collectmeta.data.db.converters.Converters

/**
 * 媒体实体基类
 */
@Entity(tableName = "media")
@TypeConverters(Converters::class)
data class MediaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: MediaType = MediaType.MOVIE,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "original_title") val originalTitle: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "rating") val rating: Float, // 豆瓣评分
    @ColumnInfo(name = "overall_rating") val overallRating: Float, // 综合评分
    @ColumnInfo(name = "genres") val genres: String, // 存储为JSON
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "notion_page_id") val notionPageId: String?,
    @ColumnInfo(name = "status") val status: MediaStatus,
    @ColumnInfo(name = "user_rating") val userRating: Float?, // 个人评分
    @ColumnInfo(name = "user_comment") val userComment: String?,
    @ColumnInfo(name = "user_tags") val userTags: String, // 存储为JSON
    @ColumnInfo(name = "douban_url") val doubanUrl: String?, // 豆瓣链接
    @ColumnInfo(name = "created_at") val createdAt: Long, // 创建时间
    @ColumnInfo(name = "release_date") val releaseDate: Long?, // 上映日期
    @ColumnInfo(name = "release_status") val releaseStatus: String // 上映状态
) 