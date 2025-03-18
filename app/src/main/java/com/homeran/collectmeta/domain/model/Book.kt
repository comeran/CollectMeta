package com.homeran.collectmeta.domain.model

import com.homeran.collectmeta.data.db.entities.MediaStatus

/**
 * 书籍领域模型，代表业务逻辑层的书籍数据结构
 */
data class Book(
    val id: String,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val cover: String,
    val description: String,
    val rating: Float,
    val overallRating: Float,
    val genres: List<String>,
    val lastModified: Long,
    val notionPageId: String?,
    val status: MediaStatus,
    val userRating: Float?,
    val userComment: String?,
    val userTags: List<String>,
    val doubanUrl: String?,
    val createdAt: Long,
    // 书籍特有属性
    val author: String,
    val isbn: String,
    val pages: Int,
    val publisher: String,
    val recommendationSource: String?,
    val filePath: String?
) 