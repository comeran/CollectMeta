package com.homeran.collectmeta.domain.model

import java.util.Date

/**
 * 书籍领域模型
 */
data class Book(
    val id: String,
    val title: String,
    val chineseTitle: String? = null,
    val originalTitle: String? = null,
    val author: String,
    val publisher: String? = null,
    val coverUrl: String? = null,
    val category: String? = null,
    val date: String? = null,
    val description: String? = null,
    val pages: Int? = null,
    val personalRating: Float? = null,
    val doubanRating: Float? = null,
    val overallRating: Float? = null,
    val doubanUrl: String? = null,
    val fileAttachment: String? = null,
    val isbn: String? = null,
    val createdAt: String? = null,
    val recommendationReason: String? = null,
    val translator: String? = null,
    val series: String? = null,
    val binding: String? = null,
    val price: String? = null,
    val publishDate: String? = null,
    val lastModified: Long? = null,
    val isSaved: Boolean = false,
    val status: ReadingStatus = ReadingStatus.WANT_TO_READ,
    val source: String? = null
) {
    /**
     * Book模型的Builder模式实现
     */
    class Builder {
        var id: String = ""
        var title: String = ""
        var chineseTitle: String? = null
        var originalTitle: String? = null
        var author: String = ""
        var publisher: String? = null
        var coverUrl: String? = null
        var category: String? = null
        var date: String? = null
        var description: String? = null
        var pages: Int? = null
        var personalRating: Float? = null
        var doubanRating: Float? = null
        var overallRating: Float? = null
        var doubanUrl: String? = null
        var fileAttachment: String? = null
        var isbn: String? = null
        var createdAt: String? = null
        var recommendationReason: String? = null
        var translator: String? = null
        var series: String? = null
        var binding: String? = null
        var price: String? = null
        var publishDate: String? = null
        var lastModified: Long? = System.currentTimeMillis()
        var isSaved: Boolean = false
        var status: ReadingStatus = ReadingStatus.WANT_TO_READ
        var source: String? = null

        fun build(): Book {
            return Book(
                id = id,
                title = title,
                chineseTitle = chineseTitle,
                originalTitle = originalTitle,
                author = author,
                publisher = publisher,
                coverUrl = coverUrl,
                category = category,
                date = date,
                description = description,
                pages = pages,
                personalRating = personalRating,
                doubanRating = doubanRating,
                overallRating = overallRating,
                doubanUrl = doubanUrl,
                fileAttachment = fileAttachment,
                isbn = isbn,
                createdAt = createdAt,
                recommendationReason = recommendationReason,
                translator = translator,
                series = series,
                binding = binding,
                price = price,
                publishDate = publishDate,
                lastModified = lastModified,
                isSaved = isSaved,
                status = status,
                source = source
            )
        }
    }
}

/**
 * 书籍阅读状态枚举
 */
enum class ReadingStatus {
    WANT_TO_READ, // 想读（橙色）
    READING,      // 在读（蓝色）
    READ,         // 读过（绿色）
    ABANDONED     // 放弃（红色）
} 