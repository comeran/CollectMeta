package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

/**
 * 书籍仓库接口
 * 定义了与书籍相关的数据操作
 */
interface BookRepository {
    /**
     * 获取所有书籍
     */
    fun getAllBooks(): Flow<List<Book>>

    /**
     * 根据阅读状态获取书籍
     */
    fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>>

    /**
     * 通过ID获取书籍
     */
    suspend fun getBookById(id: String): Book?

    /**
     * 通过ISBN获取书籍
     */
    suspend fun getBookByIsbn(isbn: String): Book?

    /**
     * 根据作者搜索书籍
     */
    fun searchBooksByAuthor(author: String): Flow<List<Book>>

    /**
     * 根据标题搜索书籍
     */
    fun searchBooksByTitle(title: String): Flow<List<Book>>

    /**
     * 根据出版社搜索书籍
     */
    fun searchBooksByPublisher(publisher: String): Flow<List<Book>>

    /**
     * 保存/更新书籍
     */
    suspend fun saveBook(book: Book)

    /**
     * 批量保存书籍
     */
    suspend fun saveBooks(books: List<Book>)

    /**
     * 更新书籍阅读状态
     */
    suspend fun updateBookStatus(id: String, status: ReadingStatus)

    /**
     * 更新个人评分
     */
    suspend fun updatePersonalRating(id: String, rating: Float)

    /**
     * 删除书籍
     */
    suspend fun deleteBook(book: Book)

    /**
     * 根据ID删除书籍
     */
    suspend fun deleteBookById(id: String)

    /**
     * 获取想读的书籍数量
     */
    fun getWantToReadCount(): Flow<Int>

    /**
     * 获取在读的书籍数量
     */
    fun getReadingCount(): Flow<Int>

    /**
     * 获取已读的书籍数量
     */
    fun getReadCount(): Flow<Int>

    /**
     * 更新书籍是否已保存到Notion
     */
    suspend fun updateBookSavedStatus(id: String, isSaved: Boolean)
} 