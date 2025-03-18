package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 书籍详情实体类，存储书籍特有的信息
 */
@Entity(
    tableName = "book_details",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookDetailsEntity(
    @PrimaryKey 
    @ColumnInfo(name = "media_id") val mediaId: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "isbn") val isbn: String,
    @ColumnInfo(name = "pages") val pages: Int,
    @ColumnInfo(name = "publisher") val publisher: String,
    @ColumnInfo(name = "recommendation_source") val recommendationSource: String?,
    @ColumnInfo(name = "file_path") val filePath: String?
) 