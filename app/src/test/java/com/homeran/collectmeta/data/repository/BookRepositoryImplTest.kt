package com.homeran.collectmeta.data.repository

import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.db.entities.BookEntity
import com.homeran.collectmeta.data.db.entities.ReadingStatus as DbReadingStatus
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BookRepositoryImplTest {
    
    private lateinit var bookDao: BookDao
    private lateinit var bookRepository: BookRepositoryImpl
    
    @Before
    fun setup() {
        // 初始化mock对象
        bookDao = mock()
        bookRepository = BookRepositoryImpl(bookDao)
    }
    
    @Test
    fun `test getAllBooks returns mapped domain books`() = runBlocking {
        // 准备测试数据
        val bookEntities = listOf(
            createBookEntity("1", "测试书籍1", DbReadingStatus.WANT_TO_READ),
            createBookEntity("2", "测试书籍2", DbReadingStatus.READING)
        )
        
        // 设置mock行为
        whenever(bookDao.getAllBooks()).thenReturn(flowOf(bookEntities))
        
        // 执行测试
        val result = bookRepository.getAllBooks().first()
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals("测试书籍1", result[0].title)
        assertEquals(ReadingStatus.WANT_TO_READ, result[0].status)
        assertEquals("测试书籍2", result[1].title)
        assertEquals(ReadingStatus.READING, result[1].status)
    }
    
    @Test
    fun `test getBooksByStatus returns mapped domain books with correct status`() = runBlocking {
        // 准备测试数据
        val bookEntities = listOf(
            createBookEntity("1", "测试书籍1", DbReadingStatus.WANT_TO_READ),
            createBookEntity("2", "测试书籍2", DbReadingStatus.WANT_TO_READ)
        )
        
        // 设置mock行为
        whenever(bookDao.getBooksByStatus(DbReadingStatus.WANT_TO_READ)).thenReturn(flowOf(bookEntities))
        
        // 执行测试
        val result = bookRepository.getBooksByStatus(ReadingStatus.WANT_TO_READ).first()
        
        // 验证结果
        assertEquals(2, result.size)
        assertEquals(ReadingStatus.WANT_TO_READ, result[0].status)
        assertEquals(ReadingStatus.WANT_TO_READ, result[1].status)
    }
    
    @Test
    fun `test getBookById returns mapped domain book when book exists`() = runBlocking {
        // 准备测试数据
        val bookEntity = createBookEntity("1", "测试书籍", DbReadingStatus.READING)
        
        // 设置mock行为
        whenever(bookDao.getBookById("1")).thenReturn(bookEntity)
        
        // 执行测试
        val result = bookRepository.getBookById("1")
        
        // 验证结果
        assertNotNull(result)
        assertEquals("1", result?.id)
        assertEquals("测试书籍", result?.title)
        assertEquals(ReadingStatus.READING, result?.status)
    }
    
    @Test
    fun `test getBookById returns null when book does not exist`() = runBlocking {
        // 设置mock行为
        whenever(bookDao.getBookById("999")).thenReturn(null)
        
        // 执行测试
        val result = bookRepository.getBookById("999")
        
        // 验证结果
        assertNull(result)
    }
    
    @Test
    fun `test saveBook converts domain book to entity and saves it`() = runBlocking {
        // 准备测试数据
        val book = createBook("1", "测试书籍", ReadingStatus.WANT_TO_READ)
        
        // 执行测试
        bookRepository.saveBook(book)
        
        // 验证正确的方法被调用
        verify(bookDao).insertOrUpdateBook(any())
    }
    
    @Test
    fun `test updateBookStatus converts status and updates it`() = runBlocking {
        // 执行测试
        bookRepository.updateBookStatus("1", ReadingStatus.READING)
        
        // 验证正确的方法被调用，并且状态被正确转换
        verify(bookDao).updateBookStatus("1", DbReadingStatus.READING)
    }
    
    // 辅助方法：创建测试用BookEntity
    private fun createBookEntity(
        id: String,
        title: String,
        status: DbReadingStatus
    ): BookEntity {
        return BookEntity(
            id = id,
            cover = null,
            category = null,
            date = null,
            personalRating = null,
            author = "测试作者",
            publisher = null,
            status = status,
            doubanRating = null,
            overallRating = null,
            pageCount = null,
            doubanUrl = null,
            fileAttachment = null,
            isbn = null,
            createdAt = "2023-01-01",
            recommendationReason = null,
            title = title,
            chineseTitle = null,
            originalTitle = null,
            translator = null,
            description = null,
            series = null,
            binding = null,
            price = null,
            publishDate = null,
            lastModified = System.currentTimeMillis()
        )
    }
    
    // 辅助方法：创建测试用Book
    private fun createBook(
        id: String,
        title: String,
        status: ReadingStatus
    ): Book {
        return Book(
            id = id,
            cover = null,
            category = null,
            date = null,
            personalRating = null,
            author = "测试作者",
            publisher = null,
            status = status,
            doubanRating = null,
            overallRating = null,
            pageCount = null,
            doubanUrl = null,
            fileAttachment = null,
            isbn = null,
            createdAt = "2023-01-01",
            recommendationReason = null,
            title = title,
            chineseTitle = null,
            originalTitle = null,
            translator = null,
            description = null,
            series = null,
            binding = null,
            price = null,
            publishDate = null,
            lastModified = System.currentTimeMillis()
        )
    }
} 