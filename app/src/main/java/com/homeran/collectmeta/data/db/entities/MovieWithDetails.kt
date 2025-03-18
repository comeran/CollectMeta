package com.homeran.collectmeta.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 电影与详情的关联类，用于获取完整的电影信息
 */
data class MovieWithDetails(
    @Embedded val media: MediaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val movieDetails: MovieDetailsEntity
) 