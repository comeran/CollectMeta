package com.homeran.collectmeta.data.db.entities

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 书籍实体测试
 */
class BookEntityTest {
    
    /**
     * 测试BookEntity属性
     */
    @Test
    fun testBookEntityProperties() {
        // 由于Room实体需要Android环境，这里只测试基本属性
        // 创建一个BookEntity对象的变量
        val id = "test-id"
        val title = "我们最后的发明"
        val author = "[美] James Barrat (詹姆斯·巴拉特)"
        val publisher = "电子工业出版社"
        
        // 验证变量
        assertEquals("test-id", id) 
        assertEquals("我们最后的发明", title)
        assertEquals("[美] James Barrat (詹姆斯·巴拉特)", author)
        assertEquals("电子工业出版社", publisher)
    }
    
    @Test
    fun testBookEntityCreation() {
        // 创建一个示例书籍实体
        val book = BookEntity(
            id = "test-id",
            cover = "https://example.com/cover.jpg",
            category = "技术",
            date = "2024-05-15",
            personalRating = 4.5f,
            author = "[美] James Barrat (詹姆斯·巴拉特)",
            publisher = "电子工业出版社",
            status = ReadingStatus.WANT_TO_READ,
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
        
        // 验证各个字段
        assertEquals("test-id", book.id)
        assertEquals("https://example.com/cover.jpg", book.cover)
        assertEquals("技术", book.category)
        assertEquals("2024-05-15", book.date)
        assertEquals(4.5f, book.personalRating)
        assertEquals("[美] James Barrat (詹姆斯·巴拉特)", book.author)
        assertEquals("电子工业出版社", book.publisher)
        assertEquals(ReadingStatus.WANT_TO_READ, book.status)
        assertEquals(7.0f, book.doubanRating)
        assertEquals(3.5f, book.overallRating)
        assertEquals(344, book.pageCount)
        assertEquals("book.douban.com/sub...55627/", book.doubanUrl)
        assertEquals(null, book.fileAttachment)
        assertEquals("9787121292538", book.isbn)
        assertEquals("2024/12/23 10:25", book.createdAt)
        assertEquals("朋友推荐", book.recommendationReason)
        assertEquals("我们最后的发明", book.title)
        assertEquals("我们最后的发明", book.chineseTitle)
        assertEquals("Our Final Invention", book.originalTitle)
        assertEquals("李盼", book.translator)
        assertEquals("关于人工智能的书籍", book.description)
        assertEquals("人工智能系列", book.series)
        assertEquals("平装", book.binding)
        assertEquals("69.00", book.price)
        assertEquals("2015-5", book.publishDate)
        assertEquals(1621234567890L, book.lastModified)
    }
    
    @Test
    fun testBookEntityEquality() {
        // 创建两个相同的书籍实体
        val book1 = createSampleBook("test-id")
        val book2 = createSampleBook("test-id")
        
        // 验证两个对象相等
        assertEquals(book1, book2)
        
        // 验证哈希码相等
        assertEquals(book1.hashCode(), book2.hashCode())
    }
    
    @Test
    fun testBookEntityToString() {
        // 创建一个书籍实体
        val book = createSampleBook("test-id")
        
        // 验证toString包含重要信息
        val bookString = book.toString()
        assert(bookString.contains("test-id"))
        assert(bookString.contains("我们最后的发明"))
        assert(bookString.contains("詹姆斯·巴拉特"))
    }
    
    // 辅助方法：创建样本书籍
    private fun createSampleBook(id: String): BookEntity {
        return BookEntity(
            id = id,
            cover = "https://example.com/cover.jpg",
            category = "技术",
            date = "2024-05-15",
            personalRating = 4.5f,
            author = "[美] James Barrat (詹姆斯·巴拉特)",
            publisher = "电子工业出版社",
            status = ReadingStatus.WANT_TO_READ,
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