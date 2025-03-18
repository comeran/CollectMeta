package com.homeran.collectmeta.data.repository

import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.db.entities.MediaStatus
import com.homeran.collectmeta.data.mapper.toBookDetailsEntity
import com.homeran.collectmeta.data.mapper.toDomainModel
import com.homeran.collectmeta.data.mapper.toMediaEntity
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.repository.BookRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 书籍仓库实现类，实现书籍数据的访问逻辑
 */
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val notionClient: Any? = null, // 占位，实际应使用NotionClient
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BookRepository {
    
    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { bookEntities ->
            bookEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getBookById(id: String): Flow<Book?> {
        return bookDao.getBookById(id).map { it?.toDomainModel() }
    }
    
    override fun getBooksByStatus(status: MediaStatus): Flow<List<Book>> {
        return bookDao.getBooksByStatus(status.name).map { bookEntities ->
            bookEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun insertBook(book: Book) = withContext(ioDispatcher) {
        val mediaEntity = book.toMediaEntity()
        val bookDetailsEntity = book.toBookDetailsEntity()
        bookDao.insertBookWithDetails(mediaEntity, bookDetailsEntity)
        
        // 如果配置了Notion同步，则同步到Notion
        notionClient?.let {
            // 同步逻辑
        }
    }
    
    override suspend fun deleteBook(id: String) = withContext(ioDispatcher) {
        bookDao.deleteBook(id)
    }
    
    override suspend fun updateBookStatus(id: String, status: MediaStatus) = withContext(ioDispatcher) {
        bookDao.updateBookStatus(id, status.name)
    }
    
    override suspend fun updateBookRating(id: String, rating: Float) = withContext(ioDispatcher) {
        bookDao.updateBookRating(id, rating)
    }
    
    override fun searchBooks(query: String): Flow<List<Book>> {
        return bookDao.searchBooksByTitle(query).map { bookEntities ->
            bookEntities.map { it.toDomainModel() }
        }
    }
} 