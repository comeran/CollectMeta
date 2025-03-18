package com.homeran.collectmeta.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 组合视图类，将基础媒体信息和书籍详情信息合并为一个完整的数据模型
 */
data class BookWithDetails(
    @Embedded val media: MediaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "media_id"
    )
    val bookDetails: BookDetailsEntity
) 