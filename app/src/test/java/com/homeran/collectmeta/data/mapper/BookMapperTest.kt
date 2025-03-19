package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.BookEntity
import com.homeran.collectmeta.data.db.entities.ReadingStatus as DbReadingStatus
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class BookMapperTest {
    
    @Test
    fun testToDomain() {
        // 创建测试数据
        val entity = createSampleBookEntity()
        
        // 执行转换
        val domain = BookMapper.toDomain(entity)
        
        // 验证转换结果
        assertEquals(entity.id, domain.id)
        assertEquals(entity.cover, domain.cover)
        assertEquals(entity.category, domain.category)
        assertEquals(entity.date, domain.date)
        assertEquals(entity.personalRating, domain.personalRating)
        assertEquals(entity.author, domain.author)
        assertEquals(entity.publisher, domain.publisher)
        assertEquals(ReadingStatus.WANT_TO_READ, domain.status)
        assertEquals(entity.doubanRating, domain.doubanRating)
        assertEquals(entity.overallRating, domain.overallRating)
        assertEquals(entity.pageCount, domain.pageCount)
        assertEquals(entity.doubanUrl, domain.doubanUrl)
        assertEquals(entity.fileAttachment, domain.fileAttachment)
        assertEquals(entity.isbn, domain.isbn)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.recommendationReason, domain.recommendationReason)
        assertEquals(entity.title, domain.title)
        assertEquals(entity.chineseTitle, domain.chineseTitle)
        assertEquals(entity.originalTitle, domain.originalTitle)
        assertEquals(entity.translator, domain.translator)
        assertEquals(entity.description, domain.description)
        assertEquals(entity.series, domain.series)
        assertEquals(entity.binding, domain.binding)
        assertEquals(entity.price, domain.price)
        assertEquals(entity.publishDate, domain.publishDate)
        assertEquals(entity.lastModified, domain.lastModified)
    }
    
    @Test
    fun testToEntity() {
        // 创建测试数据
        val domain = createSampleBook()
        
        // 执行转换
        val entity = BookMapper.toEntity(domain)
        
        // 验证转换结果
        assertEquals(domain.id, entity.id)
        assertEquals(domain.cover, entity.cover)
        assertEquals(domain.category, entity.category)
        assertEquals(domain.date, entity.date)
        assertEquals(domain.personalRating, entity.personalRating)
        assertEquals(domain.author, entity.author)
        assertEquals(domain.publisher, entity.publisher)
        assertEquals(DbReadingStatus.WANT_TO_READ, entity.status)
        assertEquals(domain.doubanRating, entity.doubanRating)
        assertEquals(domain.overallRating, entity.overallRating)
        assertEquals(domain.pageCount, entity.pageCount)
        assertEquals(domain.doubanUrl, entity.doubanUrl)
        assertEquals(domain.fileAttachment, entity.fileAttachment)
        assertEquals(domain.isbn, entity.isbn)
        assertEquals(domain.createdAt, entity.createdAt)
        assertEquals(domain.recommendationReason, entity.recommendationReason)
        assertEquals(domain.title, entity.title)
        assertEquals(domain.chineseTitle, entity.chineseTitle)
        assertEquals(domain.originalTitle, entity.originalTitle)
        assertEquals(domain.translator, entity.translator)
        assertEquals(domain.description, entity.description)
        assertEquals(domain.series, entity.series)
        assertEquals(domain.binding, entity.binding)
        assertEquals(domain.price, entity.price)
        assertEquals(domain.publishDate, entity.publishDate)
        assertEquals(domain.lastModified, entity.lastModified)
    }
    
    @Test
    fun testToDomainList() {
        // 创建测试数据
        val entities = listOf(
            createSampleBookEntity(),
            createSampleBookEntity(status = DbReadingStatus.READING)
        )
        
        // 执行转换
        val domains = BookMapper.toDomainList(entities)
        
        // 验证转换结果
        assertEquals(2, domains.size)
        assertEquals(ReadingStatus.WANT_TO_READ, domains[0].status)
        assertEquals(ReadingStatus.READING, domains[1].status)
    }
    
    @Test
    fun testToEntityList() {
        // 创建测试数据
        val domains = listOf(
            createSampleBook(),
            createSampleBook(status = ReadingStatus.READING)
        )
        
        // 执行转换
        val entities = BookMapper.toEntityList(domains)
        
        // 验证转换结果
        assertEquals(2, entities.size)
        assertEquals(DbReadingStatus.WANT_TO_READ, entities[0].status)
        assertEquals(DbReadingStatus.READING, entities[1].status)
    }
    
    // 辅助方法：创建样本书籍实体
    private fun createSampleBookEntity(
        id: String = "test-id",
        status: DbReadingStatus = DbReadingStatus.WANT_TO_READ
    ): BookEntity {
        return BookEntity(
            id = id,
            cover = "https://example.com/cover.jpg",
            category = "技术",
            date = "2024-05-15",
            personalRating = 4.5f,
            author = "[美] James Barrat (詹姆斯·巴拉特)",
            publisher = "电子工业出版社",
            status = status,
            doubanRating = 7.0f,
            overallRating = 3.5f,
            pageCount = 344,
            doubanUrl = "book.douban.com/sub...55627/",
            fileAttachment = null,
            isbn = "9787121292538",
            createdAt = "2024/12/23 10:25",
            recommendationReason = "朋友推荐",
            title = "我们最后的发明",
            chineseTitle = "我们最后的发明",
            originalTitle = "Our Final Invention",
            translator = "李盼",
            description = "关于人工智能的书籍",
            series = "人工智能系列",
            binding = "平装",
            price = "69.00",
            publishDate = "2015-5",
            lastModified = 1621234567890L
        )
    }
    
    // 辅助方法：创建样本书籍领域模型
    private fun createSampleBook(
        id: String = "test-id",
        status: ReadingStatus = ReadingStatus.WANT_TO_READ
    ): Book {
        return Book(
            id = id,
            cover = "https://example.com/cover.jpg",
            category = "技术",
            date = "2024-05-15",
            personalRating = 4.5f,
            author = "[美] James Barrat (詹姆斯·巴拉特)",
            publisher = "电子工业出版社",
            status = status,
            doubanRating = 7.0f,
            overallRating = 3.5f,
            pageCount = 344,
            doubanUrl = "book.douban.com/sub...55627/",
            fileAttachment = null,
            isbn = "9787121292538",
            createdAt = "2024/12/23 10:25",
            recommendationReason = "朋友推荐",
            title = "我们最后的发明",
            chineseTitle = "我们最后的发明",
            originalTitle = "Our Final Invention",
            translator = "李盼",
            description = "关于人工智能的书籍",
            series = "人工智能系列",
            binding = "平装",
            price = "69.00",
            publishDate = "2015-5",
            lastModified = 1621234567890L
        )
    }
} 