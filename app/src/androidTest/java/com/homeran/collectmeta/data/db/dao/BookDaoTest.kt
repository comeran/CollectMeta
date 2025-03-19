package com.homeran.collectmeta.data.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.homeran.collectmeta.data.db.AppDatabase
import com.homeran.collectmeta.data.db.entities.BookEntity
import com.homeran.collectmeta.data.db.entities.ReadingStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class BookDaoTest {
    private lateinit var bookDao: BookDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 使用内存数据库进行测试，这样测试完成后数据会自动清除
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        bookDao = db.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetBook() = runBlocking {
        // 创建测试书籍
        val book = createSampleBook()
        
        // 插入书籍
        bookDao.insertOrUpdateBook(book)
        
        // 获取并验证
        val retrievedBook = bookDao.getBookById(book.id)
        assertNotNull(retrievedBook)
        assertEquals(book.id, retrievedBook?.id)
        assertEquals(book.title, retrievedBook?.title)
        assertEquals(book.author, retrievedBook?.author)
        assertEquals(book.readingStatus, retrievedBook?.status)
    }
    
    @Test
    @Throws(Exception::class)
    fun updateBookStatus() = runBlocking {
        // 创建并插入测试书籍
        val book = createSampleBook()
        bookDao.insertOrUpdateBook(book)
        
        // 更新状态
        bookDao.updateBookStatus(book.id, ReadingStatus.READING)
        
        // 获取并验证
        val updatedBook = bookDao.getBookById(book.id)
        assertEquals(ReadingStatus.READING, updatedBook?.status)
    }
    
    @Test
    @Throws(Exception::class)
    fun getBooksByStatus() = runBlocking {
        // 创建并插入多本不同状态的书
        val book1 = createSampleBook(status = ReadingStatus.WANT_TO_READ)
        val book2 = createSampleBook(status = ReadingStatus.READING)
        val book3 = createSampleBook(status = ReadingStatus.READING)
        
        bookDao.insertOrUpdateBook(book1)
        bookDao.insertOrUpdateBook(book2)
        bookDao.insertOrUpdateBook(book3)
        
        // 获取并验证特定状态的书籍数量
        val readingBooks = bookDao.getBooksByStatus(ReadingStatus.READING).first()
        assertEquals(2, readingBooks.size)
    }
    
    @Test
    @Throws(Exception::class)
    fun deleteBook() = runBlocking {
        // 创建并插入测试书籍
        val book = createSampleBook()
        bookDao.insertOrUpdateBook(book)
        
        // 删除
        bookDao.deleteBookById(book.id)
        
        // 验证已删除
        val deletedBook = bookDao.getBookById(book.id)
        assertNull(deletedBook)
    }
    
    @Test
    @Throws(Exception::class)
    fun searchBooksByTitle() = runBlocking {
        // 创建并插入测试书籍
        val book1 = createSampleBook(title = "人工智能导论")
        val book2 = createSampleBook(title = "深度学习实战")
        val book3 = createSampleBook(title = "人工智能与机器学习")
        
        bookDao.insertOrUpdateBook(book1)
        bookDao.insertOrUpdateBook(book2)
        bookDao.insertOrUpdateBook(book3)
        
        // 搜索并验证
        val searchResults = bookDao.searchBooksByTitle("人工智能").first()
        assertEquals(2, searchResults.size)
    }
    
    // 辅助方法：创建测试用书籍
    private fun createSampleBook(
        id: String = UUID.randomUUID().toString(),
        title: String = "我们最后的发明",
        author: String = "[美] James Barrat (詹姆斯·巴拉特)",
        status: ReadingStatus = ReadingStatus.WANT_TO_READ
    ): BookEntity {
        return BookEntity(
            id = id,
            cover = "https://example.com/cover.jpg",
            category = null,
            date = null,
            personalRating = null,
            author = author,
            publisher = "电子工业出版社",
            status = status,
            doubanRating = 7.0f,
            overallRating = 3.5f,
            pageCount = 344,
            doubanUrl = "book.douban.com/sub...55627/",
            fileAttachment = null,
            isbn = "9787121292538",
            createdAt = "2024/12/23 10:25",
            recommendationReason = null,
            title = title,
            chineseTitle = "我们最后的发明",
            originalTitle = "Our Final Invention",
            translator = "李盼",
            description = "关于人工智能的书籍",
            series = null,
            binding = "平装",
            price = "69.00",
            publishDate = "2015-5",
            lastModified = System.currentTimeMillis()
        )
    }
} 