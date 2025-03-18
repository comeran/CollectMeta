package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.BookDetailsEntity
import com.homeran.collectmeta.data.db.entities.BookWithDetails
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.domain.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 转换函数 - 将数据库实体转换为领域模型
 */
fun BookWithDetails.toDomainModel(): Book {
    val gson = Gson()
    return Book(
        id = this.media.id,
        title = this.media.title,
        originalTitle = this.media.originalTitle,
        year = this.media.year,
        cover = this.media.cover,
        description = this.media.description,
        rating = this.media.rating,
        overallRating = this.media.overallRating,
        genres = gson.fromJson(this.media.genres, object : TypeToken<List<String>>() {}.type),
        lastModified = this.media.lastModified,
        notionPageId = this.media.notionPageId,
        status = this.media.status,
        userRating = this.media.userRating,
        userComment = this.media.userComment,
        userTags = gson.fromJson(this.media.userTags, object : TypeToken<List<String>>() {}.type),
        doubanUrl = this.media.doubanUrl,
        createdAt = this.media.createdAt,
        author = this.bookDetails.author,
        isbn = this.bookDetails.isbn,
        pages = this.bookDetails.pages,
        publisher = this.bookDetails.publisher,
        recommendationSource = this.bookDetails.recommendationSource,
        filePath = this.bookDetails.filePath
    )
}

/**
 * 转换函数 - 将领域模型转换为媒体实体
 */
fun Book.toMediaEntity(): MediaEntity {
    val gson = Gson()
    return MediaEntity(
        id = this.id,
        type = MediaType.BOOK,
        title = this.title,
        originalTitle = this.originalTitle,
        year = this.year,
        cover = this.cover,
        description = this.description,
        rating = this.rating,
        overallRating = this.overallRating,
        genres = gson.toJson(this.genres),
        lastModified = this.lastModified,
        notionPageId = this.notionPageId,
        status = this.status,
        userRating = this.userRating,
        userComment = this.userComment,
        userTags = gson.toJson(this.userTags),
        doubanUrl = this.doubanUrl,
        createdAt = this.createdAt
    )
}

/**
 * 转换函数 - 将领域模型转换为书籍详情实体
 */
fun Book.toBookDetailsEntity(): BookDetailsEntity {
    return BookDetailsEntity(
        mediaId = this.id,
        author = this.author,
        isbn = this.isbn,
        pages = this.pages,
        publisher = this.publisher,
        recommendationSource = this.recommendationSource,
        filePath = this.filePath
    )
} 