package com.homeran.collectmeta.data.repository

import android.util.Log
import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.mapper.BookMapper
import com.homeran.collectmeta.domain.exception.*
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import com.homeran.collectmeta.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍仓库实现类
 * 使用BookDao访问本地数据库，通过BookMapper进行数据转换
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BookRepository {

    companion object {
        private const val TAG = "BookRepositoryImpl"
    }

    override fun getAllBooks(): Flow<List<Book>> {
        Log.d(TAG, "Getting all books")
        return bookDao.getAllBooks().map { entities ->
            Log.d(TAG, "Retrieved ${entities.size} books from database")
            BookMapper.toDomainList(entities)
        }
    }

    override fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>> {
        Log.d(TAG, "Getting books with status: $status")
        // 转换领域层状态为数据库实体状态
        val dbStatus = when (status) {
            ReadingStatus.WANT_TO_READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.WANT_TO_READ
            ReadingStatus.READING -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READING
            ReadingStatus.READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READ
            ReadingStatus.ABANDONED -> com.homeran.collectmeta.data.db.entities.ReadingStatus.ABANDONED
        }
        
        return bookDao.getBooksByStatus(dbStatus).map { entities ->
            Log.d(TAG, "Retrieved ${entities.size} books with status $status")
            BookMapper.toDomainList(entities)
        }
    }

    override suspend fun getBookById(id: String): Book? {
        Log.d(TAG, "Getting book with id: $id")
        val bookEntity = bookDao.getBookById(id)
        return bookEntity?.let { 
            Log.d(TAG, "Found book with id: $id")
            BookMapper.toDomain(it) 
        }?.also {
            Log.d(TAG, "Book not found with id: $id")
        }
    }

    override suspend fun getBookByIsbn(isbn: String): Book? {
        Log.d(TAG, "Getting book with ISBN: $isbn")
        val bookEntity = bookDao.getBookByIsbn(isbn)
        return bookEntity?.let { 
            Log.d(TAG, "Found book with ISBN: $isbn")
            BookMapper.toDomain(it) 
        }?.also {
            Log.d(TAG, "Book not found with ISBN: $isbn")
        }
    }

    override fun searchBooksByAuthor(author: String): Flow<List<Book>> {
        Log.d(TAG, "Searching books by author: $author")
        return bookDao.searchBooksByAuthor(author).map { entities ->
            Log.d(TAG, "Found ${entities.size} books by author: $author")
            BookMapper.toDomainList(entities)
        }
    }

    override fun searchBooksByTitle(title: String): Flow<List<Book>> {
        Log.d(TAG, "Searching books by title: $title")
        return bookDao.searchBooksByTitle(title).map { entities ->
            Log.d(TAG, "Found ${entities.size} books by title: $title")
            BookMapper.toDomainList(entities)
        }
    }

    override fun searchBooksByPublisher(publisher: String): Flow<List<Book>> {
        Log.d(TAG, "Searching books by publisher: $publisher")
        return bookDao.searchBooksByPublisher(publisher).map { entities ->
            Log.d(TAG, "Found ${entities.size} books by publisher: $publisher")
            BookMapper.toDomainList(entities)
        }
    }

    override suspend fun saveBook(book: Book) {
        try {
            Log.d(TAG, "Saving book: ${book.id}")
            validateBook(book)
            val bookEntity = BookMapper.toEntity(book)
            bookDao.insertOrUpdateBook(bookEntity)
            Log.d(TAG, "Successfully saved book: ${book.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save book: ${book.id}", e)
            throw BookSaveException(e.message ?: "Unknown error")
        }
    }

    override suspend fun saveBooks(books: List<Book>) {
        try {
            Log.d(TAG, "Saving ${books.size} books")
            books.forEach { validateBook(it) }
            val bookEntities = BookMapper.toEntityList(books)
            bookDao.insertBooks(bookEntities)
            Log.d(TAG, "Successfully saved ${books.size} books")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save books", e)
            throw BookSaveException(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateBookStatus(id: String, status: ReadingStatus) {
        try {
            Log.d(TAG, "Updating book status: $id to $status")
            // 检查书籍是否存在
            if (bookDao.getBookById(id) == null) {
                throw BookNotFoundException(id)
            }
            
            // 转换领域层状态为数据库实体状态
            val dbStatus = when (status) {
                ReadingStatus.WANT_TO_READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.WANT_TO_READ
                ReadingStatus.READING -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READING
                ReadingStatus.READ -> com.homeran.collectmeta.data.db.entities.ReadingStatus.READ
                ReadingStatus.ABANDONED -> com.homeran.collectmeta.data.db.entities.ReadingStatus.ABANDONED
            }
            
            bookDao.updateBookStatus(id, dbStatus)
            Log.d(TAG, "Successfully updated book status: $id to $status")
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update book status: $id to $status", e)
            throw BookStatusUpdateException(e.message ?: "Unknown error")
        }
    }

    override suspend fun updatePersonalRating(id: String, rating: Float) {
        try {
            Log.d(TAG, "Updating book rating: $id to $rating")
            // 检查书籍是否存在
            if (bookDao.getBookById(id) == null) {
                throw BookNotFoundException(id)
            }
            
            // 验证评分范围
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Rating must be between 0 and 5")
            }
            
            bookDao.updatePersonalRating(id, rating)
            Log.d(TAG, "Successfully updated book rating: $id to $rating")
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update book rating: $id to $rating", e)
            throw BookRatingUpdateException(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteBook(book: Book) {
        try {
            Log.d(TAG, "Deleting book: ${book.id}")
            val bookEntity = BookMapper.toEntity(book)
            bookDao.deleteBook(bookEntity)
            Log.d(TAG, "Successfully deleted book: ${book.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete book: ${book.id}", e)
            throw BookDeleteException(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteBookById(id: String) {
        try {
            Log.d(TAG, "Deleting book by id: $id")
            // 检查书籍是否存在
            if (bookDao.getBookById(id) == null) {
                throw BookNotFoundException(id)
            }
            
            bookDao.deleteBookById(id)
            Log.d(TAG, "Successfully deleted book: $id")
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete book: $id", e)
            throw BookDeleteException(e.message ?: "Unknown error")
        }
    }

    override fun getWantToReadCount(): Flow<Int> {
        Log.d(TAG, "Getting want to read count")
        return bookDao.getWantToReadCount()
    }

    override fun getReadingCount(): Flow<Int> {
        Log.d(TAG, "Getting reading count")
        return bookDao.getReadingCount()
    }

    override fun getReadCount(): Flow<Int> {
        Log.d(TAG, "Getting read count")
        return bookDao.getReadCount()
    }

    /**
     * 验证书籍数据
     */
    private fun validateBook(book: Book) {
        // 验证必填字段
        if (book.id.isBlank()) {
            throw IllegalArgumentException("Book ID cannot be empty")
        }
        if (book.title.isBlank()) {
            throw IllegalArgumentException("Book title cannot be empty")
        }
        if (book.author.isBlank()) {
            throw IllegalArgumentException("Book author cannot be empty")
        }
        
        // 验证评分范围
        book.personalRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Personal rating must be between 0 and 5")
            }
        }
        
        // 验证豆瓣评分范围
        book.doubanRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Douban rating must be between 0 and 5")
            }
        }
        
        // 验证总评分范围
        book.overallRating?.let { rating ->
            if (rating < 0f || rating > 5f) {
                throw IllegalArgumentException("Overall rating must be between 0 and 5")
            }
        }
        
        // 验证页数
        book.pageCount?.let { count ->
            if (count < 0) {
                throw IllegalArgumentException("Page count cannot be negative")
            }
        }
    }
} 