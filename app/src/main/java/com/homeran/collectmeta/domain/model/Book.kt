package com.homeran.collectmeta.domain.model

import java.util.Date

/**
 * 书籍领域模型
 */
data class Book(
    val id: String,
    val cover: String?,
    val category: String?,
    val date: String?,
    val personalRating: Float?,
    val author: String,
    val publisher: String?,
    val readingStatus: ReadingStatus,
    val doubanRating: Float?,
    val overallRating: Float?,
    val pageCount: Int?,
    val doubanUrl: String?,
    val fileAttachment: String?,
    val isbn: String?,
    val createdAt: String,
    val recommendationReason: String?,
    val title: String,
    val chineseTitle: String?,
    val originalTitle: String?,
    val translator: String?,
    val description: String?,
    val series: String?,
    val binding: String?,
    val price: String?,
    val publishDate: String?,
    val lastModified: Long
)

/**
 * 书籍阅读状态枚举
 */
enum class ReadingStatus {
    WANT_TO_READ, // 想读（橙色）
    READING,      // 在读（蓝色）
    READ,         // 读过（绿色）
    ABANDONED     // 放弃（红色）
} 