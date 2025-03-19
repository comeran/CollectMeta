package com.homeran.collectmeta.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.homeran.collectmeta.data.db.converters.Converters

/**
 * 书籍实体类，对应数据库中的书籍表
 */
@Entity(tableName = "books")
@TypeConverters(Converters::class)
data class BookEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "cover")
    val cover: String?,
    
    @ColumnInfo(name = "category")
    val category: String?,
    
    @ColumnInfo(name = "date")
    val date: String?,
    
    @ColumnInfo(name = "personal_rating")
    val personalRating: Float?,
    
    @ColumnInfo(name = "author")
    val author: String,
    
    @ColumnInfo(name = "publisher")
    val publisher: String?,
    
    @ColumnInfo(name = "status") 
    val status: ReadingStatus,
    
    @ColumnInfo(name = "douban_rating")
    val doubanRating: Float?,
    
    @ColumnInfo(name = "overall_rating")
    val overallRating: Float?,
    
    @ColumnInfo(name = "page_count")
    val pageCount: Int?,
    
    @ColumnInfo(name = "douban_url")
    val doubanUrl: String?,
    
    @ColumnInfo(name = "file_attachment")
    val fileAttachment: String?,
    
    @ColumnInfo(name = "isbn")
    val isbn: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    
    @ColumnInfo(name = "recommendation_reason")
    val recommendationReason: String?,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "chinese_title")
    val chineseTitle: String?,
    
    @ColumnInfo(name = "original_title")
    val originalTitle: String?,
    
    @ColumnInfo(name = "translator")
    val translator: String?,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "series")
    val series: String?,
    
    @ColumnInfo(name = "binding")
    val binding: String?,
    
    @ColumnInfo(name = "price")
    val price: String?,
    
    @ColumnInfo(name = "publish_date")
    val publishDate: String?,
    
    @ColumnInfo(name = "last_modified")
    val lastModified: Long
) 