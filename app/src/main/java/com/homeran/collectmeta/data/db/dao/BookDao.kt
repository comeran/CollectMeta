package com.homeran.collectmeta.data.db.dao

import androidx.room.*
import com.homeran.collectmeta.data.db.entities.BookEntity
import com.homeran.collectmeta.data.db.entities.ReadingStatus
import kotlinx.coroutines.flow.Flow

/**
 * 书籍数据访问对象接口
 */
@Dao
interface BookDao {
    /**
     * 获取所有书籍
     */
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    /**
     * 获取指定状态的书籍
     */
    @Query("SELECT * FROM books WHERE status = :status")
    fun getBooksByStatus(status: ReadingStatus): Flow<List<BookEntity>>

    /**
     * 通过ID获取书籍
     */
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?

    /**
     * 通过ISBN获取书籍
     */
    @Query("SELECT * FROM books WHERE isbn = :isbn")
    suspend fun getBookByIsbn(isbn: String): BookEntity?

    /**
     * 根据作者搜索书籍
     */
    @Query("SELECT * FROM books WHERE author LIKE '%' || :author || '%'")
    fun searchBooksByAuthor(author: String): Flow<List<BookEntity>>

    /**
     * 根据标题搜索书籍
     */
    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' OR chinese_title LIKE '%' || :title || '%' OR original_title LIKE '%' || :title || '%'")
    fun searchBooksByTitle(title: String): Flow<List<BookEntity>>

    /**
     * 根据出版社搜索书籍
     */
    @Query("SELECT * FROM books WHERE publisher LIKE '%' || :publisher || '%'")
    fun searchBooksByPublisher(publisher: String): Flow<List<BookEntity>>

    /**
     * 保存/更新书籍
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBook(book: BookEntity)

    /**
     * 批量插入书籍
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    /**
     * 更新书籍阅读状态
     */
    @Query("UPDATE books SET status = :status WHERE id = :id")
    suspend fun updateBookStatus(id: String, status: ReadingStatus)

    /**
     * 更新个人评分
     */
    @Query("UPDATE books SET personal_rating = :rating WHERE id = :id")
    suspend fun updatePersonalRating(id: String, rating: Float)

    /**
     * 更新书籍信息
     */
    @Update
    suspend fun updateBook(book: BookEntity)

    /**
     * 删除书籍
     */
    @Delete
    suspend fun deleteBook(book: BookEntity)

    /**
     * 根据ID删除书籍
     */
    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)

    /**
     * 获取想读的书籍数量
     */
    @Query("SELECT COUNT(id) FROM books WHERE status = 'WANT_TO_READ'")
    fun getWantToReadCount(): Flow<Int>

    /**
     * 获取在读的书籍数量
     */
    @Query("SELECT COUNT(id) FROM books WHERE status = 'READING'")
    fun getReadingCount(): Flow<Int>

    /**
     * 获取已读的书籍数量
     */
    @Query("SELECT COUNT(id) FROM books WHERE status = 'READ'")
    fun getReadCount(): Flow<Int>
} 