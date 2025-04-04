package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.BookEntity
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus

/**
 * 书籍数据映射器
 * 负责在数据库实体和领域模型之间进行转换
 */
object BookMapper {
    /**
     * 将数据库实体转换为领域模型
     */
    fun toDomain(entity: BookEntity): Book {
        return Book(
            id = entity.id,
            title = entity.title,
            chineseTitle = entity.chineseTitle,
            originalTitle = entity.originalTitle,
            author = entity.author,
            publisher = entity.publisher,
            coverUrl = entity.cover,
            category = entity.category,
            date = entity.date,
            personalRating = entity.personalRating,
            doubanRating = entity.doubanRating,
            overallRating = entity.overallRating,
            pages = entity.pageCount,
            doubanUrl = entity.doubanUrl,
            fileAttachment = entity.fileAttachment,
            isbn = entity.isbn,
            createdAt = entity.createdAt,
            recommendationReason = entity.recommendationReason,
            translator = entity.translator,
            description = entity.description,
            series = entity.series,
            binding = entity.binding,
            price = entity.price,
            publishDate = entity.publishDate,
            lastModified = entity.lastModified,
            isSaved = entity.isSaved,
            status = entity.status.toDomain()
        )
    }

    /**
     * 将领域模型转换为数据库实体
     */
    fun toEntity(domain: Book): BookEntity {
        return BookEntity(
            id = domain.id,
            title = domain.title,
            chineseTitle = domain.chineseTitle,
            originalTitle = domain.originalTitle,
            author = domain.author,
            publisher = domain.publisher,
            cover = domain.coverUrl,
            category = domain.category,
            date = domain.date,
            personalRating = domain.personalRating,
            doubanRating = domain.doubanRating,
            overallRating = domain.overallRating,
            pageCount = domain.pages,
            doubanUrl = domain.doubanUrl,
            fileAttachment = domain.fileAttachment,
            isbn = domain.isbn,
            createdAt = domain.createdAt ?: "",
            recommendationReason = domain.recommendationReason,
            translator = domain.translator,
            description = domain.description,
            series = domain.series,
            binding = domain.binding,
            price = domain.price,
            publishDate = domain.publishDate,
            lastModified = domain.lastModified ?: System.currentTimeMillis(),
            isSaved = domain.isSaved,
            status = domain.status.toEntity()
        )
    }

    /**
     * 将数据库实体列表转换为领域模型列表
     */
    fun toDomainList(entities: List<BookEntity>): List<Book> {
        return entities.map { toDomain(it) }
    }

    /**
     * 将领域模型列表转换为数据库实体列表
     */
    fun toEntityList(domains: List<Book>): List<BookEntity> {
        return domains.map { toEntity(it) }
    }
}

/**
 * 将数据库层的ReadingStatus转换为领域层的ReadingStatus
 */
private fun com.homeran.collectmeta.data.db.entities.ReadingStatus.toDomain(): ReadingStatus {
    return when (this) {
        com.homeran.collectmeta.data.db.entities.ReadingStatus.WANT_TO_READ -> ReadingStatus.WANT_TO_READ
        com.homeran.collectmeta.data.db.entities.ReadingStatus.READING -> ReadingStatus.READING
        com.homeran.collectmeta.data.db.entities.ReadingStatus.READ -> ReadingStatus.READ
        com.homeran.collectmeta.data.db.entities.ReadingStatus.ABANDONED -> ReadingStatus.ABANDONED
    }
}

/**
 * 将领域层的ReadingStatus转换为数据库层的ReadingStatus
 */
private fun ReadingStatus.toEntity(): com.homeran.collectmeta.data.db.entities.ReadingStatus {
    return when (this) {
        ReadingStatus.WANT_TO_READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.WANT_TO_READ
        ReadingStatus.READING -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READING
        ReadingStatus.READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READ
        ReadingStatus.ABANDONED -> com.homeran.collectmeta.data.db.entities.ReadingStatus.ABANDONED
    }
} 