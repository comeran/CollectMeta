package com.homeran.collectmeta.data.repository

import android.util.Log
import com.homeran.collectmeta.data.remote.api.GoogleBooksApi
import com.homeran.collectmeta.data.remote.api.GoogleBooksVolumeResponse
import com.homeran.collectmeta.data.remote.api.OpenLibraryApi
import com.homeran.collectmeta.data.remote.api.OpenLibraryBookDetails
import com.homeran.collectmeta.data.remote.api.OpenLibraryDoc
import com.homeran.collectmeta.data.remote.api.OpenLibrarySearchResponse
import com.homeran.collectmeta.data.remote.api.OpenLibraryIsbnResponse
import com.homeran.collectmeta.data.db.dao.BookDao
import com.homeran.collectmeta.data.mapper.BookMapper
import com.homeran.collectmeta.di.GoogleBooksApiFactory
import com.homeran.collectmeta.di.OpenLibraryApiFactory
import com.homeran.collectmeta.domain.exception.*
import com.homeran.collectmeta.domain.model.Book
import com.homeran.collectmeta.domain.model.ReadingStatus
import com.homeran.collectmeta.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍仓库实现类
 * 使用BookDao访问本地数据库，通过BookMapper进行数据转换
 */
@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val googleBooksApiFactory: GoogleBooksApiFactory,
    private val openLibraryApiFactory: OpenLibraryApiFactory
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
        } ?: run {
            Log.d(TAG, "Book not found with id: $id")
            null
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

    override suspend fun saveBook(book: Book): Book {
        try {
            Log.d(TAG, "Saving book: ${book.id}")
            validateBook(book)
            val bookEntity = BookMapper.toEntity(book)
            bookDao.insertOrUpdateBook(bookEntity)
            Log.d(TAG, "Successfully saved book: ${book.id}")
            
            // 返回保存后的书籍（从数据库重新获取以确保返回最新数据）
            val savedBook = bookDao.getBookById(book.id)
            return savedBook?.let { BookMapper.toDomain(it) } ?: book
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

    override suspend fun updateBookSavedStatus(id: String, isSaved: Boolean) {
        try {
            Log.d(TAG, "Updating book saved status: $id to $isSaved")
            // 检查书籍是否存在
            if (bookDao.getBookById(id) == null) {
                throw BookNotFoundException(id)
            }
            
            bookDao.updateSavedStatus(id, isSaved)
            Log.d(TAG, "Successfully updated book saved status: $id to $isSaved")
        } catch (e: BookNotFoundException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update book saved status: $id to $isSaved", e)
            throw BookSaveException(e.message ?: "Unknown error")
        }
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
        book.pages?.let { count ->
            if (count < 0) {
                throw IllegalArgumentException("Page count cannot be negative")
            }
        }
    }

    override suspend fun searchGoogleBooks(
        query: String,
        apiKey: String,
        baseUrl: String,
        maxResults: Int,
        startIndex: Int
    ): Flow<List<Book>> {
        Log.d(TAG, "开始执行searchGoogleBooks方法")
        Log.d(TAG, "参数: query=$query, apiKey=${if (apiKey.isNotEmpty()) "已设置" else "未设置"}, baseUrl=$baseUrl")
        Log.d(TAG, "maxResults=$maxResults, startIndex=$startIndex")
        
        // 检查参数 - 仅检查baseUrl
        if (baseUrl.isEmpty()) {
            Log.e(TAG, "Base URL为空")
            throw IllegalArgumentException("Google Books API Base URL不能为空")
        }
        
        return flow {
            try {
                // 使用工厂创建API实例
                val api = googleBooksApiFactory.create(baseUrl)
                Log.d(TAG, "创建API实例，baseUrl: $baseUrl")
                
                // 调用API - apiKey可能为空，这是允许的
                val response = api.searchBooks(query, maxResults, startIndex, apiKey)
                Log.d(TAG, "API响应码: ${response.code()}")
                
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse != null) {
                        Log.d(TAG, "Google Books API返回了${searchResponse.totalItems}本书")
                        
                        val books = searchResponse.items?.map { volumeResponse ->
                            mapGoogleBookToDomain(volumeResponse)
                        } ?: emptyList()
                        
                        Log.d(TAG, "解析得到${books.size}本书")
                        emit(books)
                    } else {
                        Log.e(TAG, "API响应体为空")
                        emit(emptyList())
                    }
                } else {
                    Log.e(TAG, "API请求失败，响应码: ${response.code()}, 错误: ${response.errorBody()?.string()}")
                    emit(emptyList())
                }
            } catch (e: Exception) {
                // 区分正常取消和其他异常
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d(TAG, "Google Books搜索被正常取消: ${e.message}")
                    // 重新抛出CancellationException，让Flow正常取消
                    throw e
                } else {
                    Log.e(TAG, "Google Books搜索失败: ${e.message}", e)
                    emit(emptyList())
                }
            }
        }.catch { e ->
            // 使用Flow.catch操作符处理异常 - 这是Flow API推荐的方式
            if (e is kotlinx.coroutines.CancellationException) {
                // 不捕获CancellationException，让它向上传播以正确取消Flow
                Log.d(TAG, "Google Books搜索流被正常取消，允许传播")
                throw e
            } else if (e.message?.contains("Flow was aborted") == true) {
                Log.d(TAG, "Google Books搜索流被取消，这是正常行为")
                emit(emptyList())
            } else if (e is retrofit2.HttpException) {
                Log.e(TAG, "Google Books API HTTP错误: ${e.code()}, ${e.message()}", e)
                emit(emptyList())
            } else if (e is java.io.IOException) {
                Log.e(TAG, "Google Books API网络错误: ${e.message}", e)
                emit(emptyList())
            } else {
                Log.e(TAG, "Google Books搜索失败: ${e.message}", e)
                emit(emptyList())
            }
        }
    }
    
    override suspend fun getGoogleBookDetails(
        bookId: String,
        apiKey: String,
        baseUrl: String
    ): Book? {
        Log.d(TAG, "获取Google Book详情: $bookId")
        
        // 检查参数 - 仅检查必需参数
        if (baseUrl.isEmpty() || bookId.isEmpty()) {
            Log.e(TAG, "参数无效: baseUrl=${baseUrl.isNotEmpty()}, bookId=${bookId.isNotEmpty()}")
            return null
        }
        
        return try {
            // 使用工厂创建API实例
            val api = googleBooksApiFactory.create(baseUrl)
            
            // 调用API - apiKey可能为空，这是允许的
            val response = api.getBookDetails(bookId, apiKey)
            
            if (response.isSuccessful) {
                val volumeResponse = response.body()
                if (volumeResponse != null) {
                    mapGoogleBookToDomain(volumeResponse)
                } else {
                    Log.e(TAG, "获取书籍详情响应体为空")
                    null
                }
            } else {
                Log.e(TAG, "获取书籍详情请求失败，响应码: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取书籍详情异常: ${e.message}", e)
            null
        }
    }
    
    /**
     * 将Google Books API响应映射到领域模型
     */
    private fun mapGoogleBookToDomain(volumeResponse: GoogleBooksVolumeResponse): Book {
        val volumeInfo = volumeResponse.volumeInfo
        
        // 提取作者
        val author = volumeInfo.authors?.joinToString(", ") ?: "Unknown Author"
        
        // 提取ISBN
        val isbn = volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier 
            ?: volumeInfo.industryIdentifiers?.find { it.type == "ISBN_10" }?.identifier
        
        // 提取分类
        val category = volumeInfo.categories?.joinToString(", ")
        
        // 提取封面URL（转换为HTTPS）
        val coverUrl = volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:")
        
        return Book(
            id = volumeResponse.id,
            title = volumeInfo.title,
            author = author,
            description = volumeInfo.description,
            publisher = volumeInfo.publisher,
            publishDate = volumeInfo.publishedDate,
            pages = volumeInfo.pageCount,
            category = category,
            coverUrl = coverUrl,
            isbn = isbn,
            lastModified = System.currentTimeMillis(),
            isSaved = false,
            source = "google_books"
        )
    }
    
    override suspend fun searchOpenLibraryBooks(
        query: String,
        searchField: String,
        baseUrl: String,
        limit: Int,
        page: Int
    ): Flow<List<Book>> {
        Log.d(TAG, "搜索OpenLibrary: query=$query, searchField=$searchField, baseUrl=$baseUrl")
        Log.d(TAG, "参数: limit=$limit, page=$page")
        
        // 验证参数
        if (baseUrl.isEmpty()) {
            Log.e(TAG, "OpenLibrary baseUrl不能为空")
            throw IllegalArgumentException("OpenLibrary API Base URL不能为空")
        }
        
        if (query.isEmpty()) {
            Log.e(TAG, "搜索关键词不能为空")
            return flowOf(emptyList())
        }
        
        return flow {
            try {
                // 确保基础URL以斜杠结尾
                val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
                Log.d(TAG, "使用基础URL: $normalizedBaseUrl")
                
                // 使用工厂创建Retrofit服务
                val api = openLibraryApiFactory.create(normalizedBaseUrl)
                
                // 准备URL查询参数 - OpenLibrary API格式
                val queryMap = HashMap<String, String>().apply {
                    // 根据搜索类型设置不同的查询参数
                    when (searchField.lowercase()) {
                        "title" -> {
                            put("title", query)
                        }
                        "author" -> {
                            put("author", query)
                        }
                        "publisher" -> {
                            put("publisher", query)
                        }
                        "subject" -> {
                            put("subject", query)
                        }
                        "isbn" -> {
                            put("isbn", query)
                        }
                        else -> {
                            // 默认使用通用搜索
                            put("q", query)
                        }
                    }
                    
                    // 设置分页和限制参数
                    put("limit", limit.toString())
                    put("page", page.toString())
                    
                    // 使用更简单的字段列表，避免可能的语法错误
                    put("fields", "key,title,author_name,author_key,publisher,first_publish_year,isbn,subject,cover_i,number_of_pages_median")
                }
                
                Log.d(TAG, "OpenLibrary请求: URL=${normalizedBaseUrl}search.json, 参数=$queryMap")
                
                // 执行API调用
                val response = api.searchBooks(queryMap)
                
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    
                    if (searchResponse != null) {
                        Log.d(TAG, "OpenLibrary API返回了${searchResponse.numFound}本书")
                        
                        val books = searchResponse.docs?.mapNotNull { doc ->
                            try {
                                mapOpenLibraryDocToDomain(doc)
                            } catch (e: Exception) {
                                Log.e(TAG, "解析OpenLibrary书籍失败: ${e.message}", e)
                                null
                            }
                        } ?: emptyList()
                        
                        Log.d(TAG, "成功解析${books.size}本书")
                        emit(books)
                    } else {
                        Log.e(TAG, "OpenLibrary API响应体为空")
                        emit(emptyList())
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "无错误信息"
                    Log.e(TAG, "OpenLibrary API请求失败: ${response.code()}, URL=${normalizedBaseUrl}search.json")
                    Log.e(TAG, "错误详情: $errorBody")
                    emit(emptyList())
                }
            } catch (e: Exception) {
                // 区分正常取消和其他异常
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d(TAG, "OpenLibrary搜索被正常取消: ${e.message}")
                    // 重新抛出CancellationException，让Flow正常取消
                    throw e
                } else {
                    Log.e(TAG, "OpenLibrary搜索失败: ${e.message}", e)
                    emit(emptyList())
                }
            }
        }.catch { e ->
            // 使用Flow.catch操作符处理异常 - 这是Flow API推荐的方式
            if (e is kotlinx.coroutines.CancellationException) {
                // 不捕获CancellationException，让它向上传播以正确取消Flow
                Log.d(TAG, "OpenLibrary搜索流被正常取消，允许传播")
                throw e
            } else if (e.message?.contains("Flow was aborted") == true) {
                Log.d(TAG, "OpenLibrary搜索流被取消，这是正常行为")
                emit(emptyList())
            } else if (e is retrofit2.HttpException) {
                Log.e(TAG, "OpenLibrary API HTTP错误: ${e.code()}, ${e.message()}", e)
                emit(emptyList())
            } else if (e is java.io.IOException) {
                Log.e(TAG, "OpenLibrary API网络错误: ${e.message}", e)
                emit(emptyList())
            } else {
                Log.e(TAG, "OpenLibrary搜索失败: ${e.message}", e)
                emit(emptyList())
            }
        }
    }
    
    override suspend fun getOpenLibraryBookDetails(
        bookId: String,
        baseUrl: String
    ): Book? {
        Log.d(TAG, "获取OpenLibrary书籍详情: bookId=$bookId, baseUrl=$baseUrl")
        
        // 验证参数
        if (baseUrl.isEmpty() || bookId.isEmpty()) {
            Log.e(TAG, "参数无效: baseUrl=${baseUrl.isNotEmpty()}, bookId=${bookId.isNotEmpty()}")
            return null
        }
        
        return try {
            // 确保基础URL以斜杠结尾
            val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            Log.d(TAG, "使用基础URL: $normalizedBaseUrl")
            
            // 使用工厂创建Retrofit服务
            val api = openLibraryApiFactory.create(normalizedBaseUrl)
            
            // 确保bookId格式正确 (如果是完整路径，提取ID部分)
            val processedBookId = if (bookId.contains("/works/")) {
                bookId.substringAfter("/works/")
            } else {
                bookId
            }
            
            Log.d(TAG, "使用处理后的bookId: $processedBookId")
            
            // 执行API调用获取书籍详情
            val response = api.getBookDetails(processedBookId)
            
            if (response.isSuccessful) {
                val bookResponse = response.body()
                
                if (bookResponse != null) {
                    Log.d(TAG, "成功获取OpenLibrary书籍详情")
                    mapOpenLibraryBookDetailsToDomain(processedBookId, bookResponse)
                } else {
                    Log.e(TAG, "OpenLibrary书籍详情响应体为空")
                    null
                }
            } else {
                Log.e(TAG, "获取OpenLibrary书籍详情失败: ${response.code()}, 错误: ${response.errorBody()?.string() ?: "无错误信息"}")
                null
            }
        } catch (e: Exception) {
            // 处理异常
            if (e.message?.contains("Flow was aborted") == true) {
                Log.d(TAG, "OpenLibrary详情请求被取消，这是正常行为")
            } else if (e is retrofit2.HttpException) {
                Log.e(TAG, "OpenLibrary API详情HTTP错误: ${e.code()}, ${e.message()}", e)
            } else if (e is java.io.IOException) {
                Log.e(TAG, "OpenLibrary API详情网络错误: ${e.message}", e)
            } else {
                Log.e(TAG, "获取OpenLibrary书籍详情异常: ${e.message}", e)
            }
            null
        }
    }
    
    /**
     * 将OpenLibrary搜索结果文档映射到领域模型
     */
    private fun mapOpenLibraryDocToDomain(doc: OpenLibraryDoc): Book {
        try {
            // 提取作者
            val author = doc.author_name?.joinToString(", ")?.trim() ?: "Unknown Author"
            
            // 提取译者
            val translator = doc.translator?.joinToString(", ")?.trim()
            
            // 提取ISBN，确保非空字符串
            val isbn = doc.isbn?.firstOrNull()?.trim() ?: ""
            
            // 【改进】提取封面URL - 记录原始cover_i值
            val coverId = doc.cover_i
            Log.d(TAG, "OpenLibrary书籍封面ID: key=${doc.key}, cover_i=$coverId")
            
            // 构建封面URL - 仅使用coverId构建URL
            val coverUrl = if (coverId != null) {
                // 使用中等尺寸 (M)，大尺寸(L)可能加载失败
                val url = "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
                Log.d(TAG, "构建封面URL: $url")
                url
            } else {
                // 无法构建封面URL
                Log.d(TAG, "无法构建封面URL，缺少cover_i: key=${doc.key}")
                null
            }
            
            // 安全处理发布日期 - 确保是字符串
            val publishDate = doc.first_publish_year?.toString() ?: ""
            
            // 安全处理页数
            val pages = doc.number_of_pages_median?.takeIf { it > 0 }
            
            // 安全处理分类
            val category = doc.subject?.takeIf { it.isNotEmpty() }?.joinToString(", ")?.trim()
            
            // 安全处理出版商
            val publisher = doc.publisher?.firstOrNull()?.trim()
            
            // 确保ID有效
            val id = doc.key?.takeIf { it.isNotEmpty() }?.removePrefix("/works/") 
                ?: UUID.randomUUID().toString()
            
            // 确保标题有效
            val title = doc.title?.trim() ?: "Unknown Title"
            
            return Book(
                id = id,
                title = title,
                author = author,
                description = doc.description?.trim(),
                publisher = publisher,
                publishDate = publishDate,
                pages = pages,
                category = category,
                coverUrl = coverUrl,
                isbn = isbn,
                lastModified = System.currentTimeMillis(),
                isSaved = false,
                chineseTitle = doc.chineseTitle?.trim(),
                originalTitle = doc.originalTitle?.trim(),
                translator = translator,
                series = doc.series?.trim(),
                binding = doc.binding?.trim(),
                price = doc.price?.trim(),
                doubanRating = doc.doubanRating,
                doubanUrl = doc.doubanUrl?.trim(),
                fileAttachment = doc.fileAttachment?.trim(),
                status = ReadingStatus.WANT_TO_READ, // 默认状态
                source = "open_library"
            )
        } catch (e: Exception) {
            Log.e(TAG, "OpenLibrary书籍映射错误: ${e.message}", e)
            Log.d(TAG, "问题书籍数据: key=${doc.key}, title=${doc.title}, author=${doc.author_name}, cover_i=${doc.cover_i}")
            
            // 尝试创建最小化的有效书籍对象
            val fallbackId = doc.key?.removePrefix("/works/") ?: UUID.randomUUID().toString()
            val fallbackTitle = doc.title ?: "Unknown Book"
            val fallbackAuthor = doc.author_name?.firstOrNull() ?: "Unknown Author"
            
            return Book(
                id = fallbackId,
                title = fallbackTitle,
                author = fallbackAuthor,
                description = null,
                publisher = null,
                publishDate = null,
                pages = null,
                category = null,
                coverUrl = null,
                isbn = null,
                lastModified = System.currentTimeMillis(),
                isSaved = false,
                status = ReadingStatus.WANT_TO_READ,
                source = "open_library"
            )
        }
    }
    
    /**
     * 将OpenLibrary书籍详情映射到领域模型
     */
    private fun mapOpenLibraryBookDetailsToDomain(bookId: String, details: OpenLibraryBookDetails): Book {
        // 提取作者 - 从作者引用中获取名称
        val authorNames = details.authors?.mapNotNull { it.author?.name } ?: emptyList()
        val author = authorNames.joinToString(", ").ifEmpty { "Unknown Author" }
        
        // 提取译者
        val translatorNames = details.translators?.mapNotNull { it.name } ?: emptyList()
        val translator = if (translatorNames.isNotEmpty()) translatorNames.joinToString(", ") else null
        
        // 提取ISBN
        val isbn = details.identifiers?.isbn_13?.firstOrNull() 
            ?: details.identifiers?.isbn_10?.firstOrNull()
        
        // 改进封面URL获取
        val covers = details.covers
        Log.d(TAG, "OpenLibrary书籍详情封面IDs: bookId=$bookId, covers=$covers")
        
        // 构建封面URL，仅使用封面ID
        val coverUrl = if (covers != null && covers.isNotEmpty()) {
            val firstCoverId = covers.first()
            // 使用中等尺寸 (M)，大尺寸(L)可能加载失败
            val url = "https://covers.openlibrary.org/b/id/$firstCoverId-M.jpg"
            Log.d(TAG, "使用第一个封面ID构建URL: $url")
            url
        } else {
            Log.d(TAG, "无法找到任何封面: bookId=$bookId")
            null
        }
        
        // 提取系列信息
        val series = details.series?.name
        
        // 提取主题/分类 - 处理不同类型的subjects
        val category = details.subjects?.let { subjects ->
            try {
                when {
                    subjects.isEmpty() -> null
                    subjects[0] is String -> {
                        // subjects是字符串列表
                        @Suppress("UNCHECKED_CAST")
                        (subjects as List<String>).joinToString(", ")
                    }
                    subjects[0] is Map<*, *> -> {
                        // subjects是Map列表(JSON对象)
                        subjects.mapNotNull { subject ->
                            (subject as? Map<*, *>)?.get("name") as? String
                        }.joinToString(", ")
                    }
                    subjects[0] is com.homeran.collectmeta.data.remote.api.Subject -> {
                        // subjects是Subject对象列表
                        @Suppress("UNCHECKED_CAST")
                        (subjects as List<com.homeran.collectmeta.data.remote.api.Subject>)
                            .mapNotNull { it.name }
                            .joinToString(", ")
                    }
                    else -> null
                }
            } catch (e: Exception) {
                Log.e(TAG, "处理subjects时出错: ${e.message}", e)
                null
            }
        }
        
        return Book(
            id = bookId,
            title = details.title ?: "Unknown Title",
            author = author,
            description = details.description?.value ?: details.description?.toString(),
            publisher = details.publishers?.firstOrNull()?.name,
            publishDate = details.publish_date,
            pages = details.number_of_pages,
            category = category,
            coverUrl = coverUrl,
            isbn = isbn,
            lastModified = System.currentTimeMillis(),
            isSaved = false,
            chineseTitle = details.chinese_title,
            originalTitle = details.original_title,
            translator = translator,
            series = series,
            binding = details.physical_format,
            price = details.price,
            doubanRating = details.douban_rating,
            doubanUrl = details.douban_url,
            fileAttachment = details.file_attachment,
            status = ReadingStatus.WANT_TO_READ,
            source = "open_library"
        )
    }

    /**
     * 通过ISBN从OpenLibrary获取书籍详情
     */
    override suspend fun getOpenLibraryBookByIsbn(
        isbn: String,
        baseUrl: String
    ): Book? {
        Log.d(TAG, "通过ISBN获取OpenLibrary书籍详情: $isbn")
        
        // 验证参数
        if (isbn.isBlank() || baseUrl.isBlank()) {
            Log.e(TAG, "参数无效: isbn=${isbn.isNotBlank()}, baseUrl=${baseUrl.isNotBlank()}")
            return null
        }
        
        try {
            // 正规化ISBN，移除所有非数字字符
            val normalizedIsbn = isbn.filter { it.isDigit() }
            
            if (normalizedIsbn.isEmpty()) {
                Log.e(TAG, "ISBN格式无效: $isbn")
                return null
            }
            
            // 确保基础URL以斜杠结尾
            val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            Log.d(TAG, "使用基础URL: $normalizedBaseUrl")
            
            // 使用工厂创建Retrofit服务
            val api = openLibraryApiFactory.create(normalizedBaseUrl)
            
            // 构建bibkeys参数，格式为"ISBN:1234567890"
            val bibkeys = "ISBN:$normalizedIsbn"
            
            Log.d(TAG, "请求OpenLibrary ISBN API，bibkeys=$bibkeys")
            // 记录完整的请求URL
            val fullUrl = "${normalizedBaseUrl}api/books?bibkeys=$bibkeys&format=json&jscmd=data"
            Log.d(TAG, "完整请求URL: $fullUrl")
            
            val response = api.getBookByIsbn(bibkeys)
            
            // 直接输出原始响应数据
            val rawResponse = response.raw()
            Log.d(TAG, "==== 原始响应头信息 ====")
            Log.d(TAG, "响应状态: ${rawResponse.code} ${rawResponse.message}")
            rawResponse.headers.forEach { header ->
                Log.d(TAG, "响应头: ${header.first} = ${header.second}")
            }
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                
                // 输出原始JSON响应
                if (responseBody != null) {
                    Log.d(TAG, "==== 原始响应数据开始 ====")
                    val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                    val jsonString = gson.toJson(responseBody)
                    // 分段打印，避免超出日志长度限制
                    val chunkSize = 3000
                    jsonString.chunked(chunkSize).forEachIndexed { index, chunk ->
                        Log.d(TAG, "JSON响应(第${index + 1}部分): $chunk")
                    }
                    Log.d(TAG, "==== 原始响应数据结束 ====")
                } else {
                    Log.e(TAG, "响应体为空")
                }
                
                // 继续原有逻辑...
                if (responseBody != null && responseBody.isNotEmpty()) {
                    val bookData = responseBody[bibkeys]
                    
                    if (bookData != null) {
                        Log.d(TAG, "成功获取书籍: ${bookData.title}, 键: $bibkeys")
                        return mapOpenLibraryIsbnResponseToDomain(normalizedIsbn, bookData)
                    } else {
                        Log.e(TAG, "找不到ISBN对应的书籍: $isbn, 键: $bibkeys")
                        Log.d(TAG, "可用的键: ${responseBody.keys}")
                    }
                } else {
                    Log.e(TAG, "响应为空或无结果")
                    val rawResponseString = response.raw().toString()
                    Log.d(TAG, "原始响应摘要: $rawResponseString")
                }
            } else {
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "无错误信息"
                Log.e(TAG, "OpenLibrary ISBN API请求失败，响应码: $errorCode")
                Log.e(TAG, "错误详情: $errorBody")
                
                if (errorCode == 404) {
                    Log.d(TAG, "ISBN可能不存在: $normalizedIsbn")
                }
            }
            
            return null
        } catch (e: Exception) {
            Log.e(TAG, "获取OpenLibrary ISBN书籍异常: ${e.message}", e)
            return null
        }
    }
    
    /**
     * 将OpenLibrary ISBN API响应映射到领域模型
     */
    private fun mapOpenLibraryIsbnResponseToDomain(isbn: String, data: OpenLibraryIsbnResponse): Book {
        Log.d(TAG, "开始映射OpenLibrary数据: ISBN=$isbn")
        Log.d(TAG, "接收到的数据: 标题=${data.title}, 作者=${data.authors?.map { it.name }}")
        Log.d(TAG, "封面数据: ${data.cover}")
        Log.d(TAG, "标识符类型: ${data.identifiers?.javaClass?.name}")
        Log.d(TAG, "标识符内容: ${data.identifiers?.isbn_13}, ${data.identifiers?.isbn_10}")
        Log.d(TAG, "描述类型: ${data.description?.javaClass?.name}")
        
        // 提取作者，可能有多个
        val authorNames = data.authors?.mapNotNull { it.name } ?: emptyList()
        Log.d(TAG, "提取到的作者: $authorNames")
        val author = if (authorNames.isNotEmpty()) {
            authorNames.joinToString(", ")
        } else {
            "Unknown Author"
        }
        Log.d(TAG, "最终使用的作者: $author")
        
        // 尝试获取ISBN-13，如果没有则使用ISBN-10或传入的ISBN
        val isbn13 = data.identifiers?.isbn_13?.firstOrNull()
        val isbn10 = data.identifiers?.isbn_10?.firstOrNull()
        val finalIsbn = isbn13 ?: isbn10 ?: isbn
        Log.d(TAG, "选择的ISBN: $finalIsbn (isbn13=$isbn13, isbn10=$isbn10, 原始isbn=$isbn)")
        
        // 分类/主题，可能有多个
        val subjects = data.subjects?.mapNotNull { it.name }
        val category = if (!subjects.isNullOrEmpty()) {
            Log.d(TAG, "提取到的主题: $subjects")
            subjects.joinToString(", ")
        } else {
            Log.d(TAG, "未找到主题")
            ""
        }
        
        // 封面URL，优先使用较大尺寸
        val coverUrl = when {
            data.cover?.medium != null -> {
                Log.d(TAG, "使用中等尺寸封面: ${data.cover.medium}")
                data.cover.medium
            }
            data.cover?.large != null -> {
                Log.d(TAG, "使用大尺寸封面: ${data.cover.large}")
                data.cover.large
            }
            data.cover?.small != null -> {
                Log.d(TAG, "使用小尺寸封面: ${data.cover.small}")
                data.cover.small
            }
            else -> {
                Log.d(TAG, "未找到封面URL")
                ""
            }
        }
        
        // 出版商
        val publisher = data.publishers?.firstOrNull()?.name ?: ""
        Log.d(TAG, "提取的出版商: $publisher")
        
        // 描述
        val description = when {
            data.description is String -> {
                Log.d(TAG, "使用字符串描述")
                data.description
            }
            data.excerpt?.text != null -> {
                Log.d(TAG, "使用摘录文本作为描述")
                data.excerpt.text
            }
            else -> {
                Log.d(TAG, "没有找到有效描述")
                ""
            }
        }
        
        // 页数，确保非负
        val pages = if (data.number_of_pages != null && data.number_of_pages > 0) {
            Log.d(TAG, "提取到的页数: ${data.number_of_pages}")
            data.number_of_pages
        } else {
            Log.d(TAG, "未找到有效页数")
            0
        }
        
        // 出版日期
        val publishDate = data.publish_date ?: ""
        
        // 如果有副标题，拼接完整标题
        val fullTitle = if (!data.subtitle.isNullOrBlank()) {
            "${data.title ?: "未知标题"}: ${data.subtitle}"
        } else {
            data.title ?: "未知标题"
        }
        
        return Book(
            id = finalIsbn,
            title = fullTitle,
            author = author,
            description = description,
            publisher = publisher,
            publishDate = publishDate,
            pages = pages,
            category = category,
            coverUrl = coverUrl,
            isbn = finalIsbn,
            lastModified = System.currentTimeMillis(),
            isSaved = false,
            status = ReadingStatus.WANT_TO_READ,
            source = "open_library"
        ).also {
            Log.d(TAG, "生成的Book对象: id=${it.id}, title=${it.title}, author=${it.author}, coverUrl=${it.coverUrl}")
        }
    }
}

