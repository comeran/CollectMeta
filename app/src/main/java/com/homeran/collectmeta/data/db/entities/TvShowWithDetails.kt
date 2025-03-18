package com.homeran.collectmeta.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 电视剧与详情的关联类
 */
data class TvShowWithDetails(
    @Embedded val media: MediaEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val details: TvShowDetailsEntity
) 