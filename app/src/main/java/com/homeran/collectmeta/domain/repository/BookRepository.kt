package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.domain.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * 书籍仓库接口，定义书籍数据的访问方法
 */
interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>
    fun getBookById(id: String): Flow<Book?>
    fun getBooksByStatus(status: MediaStatus): Flow<List<Book>>
    suspend fun insertBook(book: Book)
    suspend fun deleteBook(id: String)
    suspend fun updateBookStatus(id: String, status: MediaStatus)
    suspend fun updateBookRating(id: String, rating: Float)
    fun searchBooks(query: String): Flow<List<Book>>
} 