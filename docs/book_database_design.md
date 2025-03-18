# 书籍数据库设计文档

## 概述

本文档描述了CollectMeta应用中书籍数据的本地存储方案，基于Room数据库实现。设计遵循Clean Architecture架构原则，采用Repository模式进行数据管理。数据结构参考了Notion模板的设计，确保本地数据库与Notion同步的兼容性。

## 数据模型设计

### 实体关系图

媒体基础实体（Media Entity）与书籍详情实体（Book Details Entity）之间为一对一关系：

```
┌─────────────────┐       ┌─────────────────┐
│  MediaEntity    │       │ BookDetailsEntity│
├─────────────────┤       ├─────────────────┤
│ id (PK)         │──────>│ mediaId (PK)    │
│ type            │       │ author          │
│ title           │       │ isbn            │
│ ...             │       │ ...             │
└─────────────────┘       └─────────────────┘
```

### 数据结构

1. **MediaType枚举**：定义媒体类型
2. **MediaStatus枚举**：定义媒体状态（想读/在读/已读）
3. **MediaEntity**：所有媒体类型的共享基础信息
4. **BookDetailsEntity**：书籍特有信息
5. **BookWithDetails**：组合视图，表示完整的书籍信息

## Room数据库实现

### 1. 数据类型与枚举

```kotlin
// 媒体类型枚举
enum class MediaType {
    MOVIE,
    TV,
    GAME,
    BOOK
}

// 媒体状态枚举
enum class MediaStatus {
    WANT_TO_CONSUME,  // 想读/想看/想玩
    CONSUMING,        // 在读/在看/在玩
    CONSUMED          // 已读/已看/已玩
}
```

### 2. 实体定义

```kotlin
@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: MediaType = MediaType.BOOK,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "original_title") val originalTitle: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "rating") val rating: Float, // 豆瓣评分
    @ColumnInfo(name = "overall_rating") val overallRating: Float, // 综合评分
    @ColumnInfo(name = "genres") val genres: String, // 存储为JSON
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "notion_page_id") val notionPageId: String?,
    @ColumnInfo(name = "status") val status: MediaStatus,
    @ColumnInfo(name = "user_rating") val userRating: Float?, // 个人评分
    @ColumnInfo(name = "user_comment") val userComment: String?,
    @ColumnInfo(name = "user_tags") val userTags: String, // 存储为JSON
    @ColumnInfo(name = "douban_url") val doubanUrl: String?, // 豆瓣链接
    @ColumnInfo(name = "created_at") val createdAt: Long // 创建时间
)

@Entity(tableName = "book_details")
data class BookDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "isbn") val isbn: String,
    @ColumnInfo(name = "pages") val pages: Int,
    @ColumnInfo(name = "publisher") val publisher: String,
    @ColumnInfo(name = "recommendation_source") val recommendationSource: String?,
    @ColumnInfo(name = "file_path") val filePath: String?
)

// 关系实体
data class BookWithDetails(
    @Embedded val media: MediaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val bookDetails: BookDetailsEntity
)
```

### 3. 类型转换器

```kotlin
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun mediaTypeToString(mediaType: MediaType): String {
        return mediaType.name
    }
    
    @TypeConverter
    fun stringToMediaType(value: String): MediaType {
        return MediaType.valueOf(value)
    }
    
    @TypeConverter
    fun mediaStatusToString(status: MediaStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun stringToMediaStatus(value: String): MediaStatus {
        return MediaStatus.valueOf(value)
    }
}
```

### 4. DAO实现

```kotlin
@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK'")
    fun getAllBooks(): Flow<List<BookWithDetails>>

    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK' AND id = :bookId")
    fun getBookById(bookId: String): Flow<BookWithDetails>
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'BOOK' AND status = :status")
    fun getBooksByStatus(status: String): Flow<List<BookWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaEntity(mediaEntity: MediaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookDetails(bookDetails: BookDetailsEntity)
    
    @Transaction
    suspend fun insertBookWithDetails(mediaEntity: MediaEntity, bookDetails: BookDetailsEntity) {
        insertMediaEntity(mediaEntity)
        insertBookDetails(bookDetails)
    }
    
    @Query("DELETE FROM media WHERE id = :mediaId AND type = 'BOOK'")
    suspend fun deleteBook(mediaId: String)
    
    @Query("SELECT * FROM media WHERE type = 'BOOK' AND title LIKE '%' || :query || '%'")
    fun searchBooksByTitle(query: String): Flow<List<BookWithDetails>>
    
    @Query("UPDATE media SET status = :status WHERE id = :mediaId")
    suspend fun updateBookStatus(mediaId: String, status: String)
    
    @Query("UPDATE media SET user_rating = :rating WHERE id = :mediaId")
    suspend fun updateBookRating(mediaId: String, rating: Float)
}
```

### 5. 数据库定义

```kotlin
@Database(
    entities = [
        MediaEntity::class,
        BookDetailsEntity::class
        // 其他实体...
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    // 其他DAO...
    
    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null
        
        fun getDatabase(context: Context): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "media_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

## Repository实现

按照Clean Architecture原则，我们通过Repository模式抽象数据访问层：

```kotlin
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

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val notionClient: NotionClient?,
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
```

## 领域模型与映射扩展

```kotlin
// 领域模型
data class Book(
    val id: String,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val cover: String,
    val description: String,
    val rating: Float,
    val overallRating: Float,
    val genres: List<String>,
    val lastModified: Long,
    val notionPageId: String?,
    val status: MediaStatus,
    val userRating: Float?,
    val userComment: String?,
    val userTags: List<String>,
    val doubanUrl: String?,
    val createdAt: Long,
    // 书籍特有属性
    val author: String,
    val isbn: String,
    val pages: Int,
    val publisher: String,
    val recommendationSource: String?,
    val filePath: String?
)

// 扩展函数 - 转换实体到领域模型
fun BookWithDetails.toDomainModel(): Book {
    return Book(
        id = this.media.id,
        title = this.media.title,
        originalTitle = this.media.originalTitle,
        year = this.media.year,
        cover = this.media.cover,
        description = this.media.description,
        rating = this.media.rating,
        overallRating = this.media.overallRating,
        genres = Gson().fromJson(this.media.genres, object : TypeToken<List<String>>() {}.type),
        lastModified = this.media.lastModified,
        notionPageId = this.media.notionPageId,
        status = this.media.status,
        userRating = this.media.userRating,
        userComment = this.media.userComment,
        userTags = Gson().fromJson(this.media.userTags, object : TypeToken<List<String>>() {}.type),
        doubanUrl = this.media.doubanUrl,
        createdAt = this.media.createdAt,
        author = this.bookDetails.author,
        isbn = this.bookDetails.isbn,
        pages = this.bookDetails.pages,
        publisher = this.bookDetails.publisher,
        recommendationSource = this.bookDetails.recommendationSource,
        filePath = this.bookDetails.filePath
    )
}

// 扩展函数 - 转换领域模型到实体
fun Book.toMediaEntity(): MediaEntity {
    return MediaEntity(
        id = this.id,
        type = MediaType.BOOK,
        title = this.title,
        originalTitle = this.originalTitle,
        year = this.year,
        cover = this.cover,
        description = this.description,
        rating = this.rating,
        overallRating = this.overallRating,
        genres = Gson().toJson(this.genres),
        lastModified = this.lastModified,
        notionPageId = this.notionPageId,
        status = this.status,
        userRating = this.userRating,
        userComment = this.userComment,
        userTags = Gson().toJson(this.userTags),
        doubanUrl = this.doubanUrl,
        createdAt = this.createdAt
    )
}

fun Book.toBookDetailsEntity(): BookDetailsEntity {
    return BookDetailsEntity(
        mediaId = this.id,
        author = this.author,
        isbn = this.isbn,
        pages = this.pages,
        publisher = this.publisher,
        recommendationSource = this.recommendationSource,
        filePath = this.filePath
    )
}
```

## 使用示例

### 添加新书籍

```kotlin
class AddBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    fun addBook(
        title: String,
        originalTitle: String,
        year: Int,
        cover: String,
        description: String,
        rating: Float,
        genres: List<String>,
        status: MediaStatus,
        author: String,
        isbn: String,
        pages: Int,
        publisher: String
    ) {
        viewModelScope.launch {
            val book = Book(
                id = UUID.randomUUID().toString(),
                title = title,
                originalTitle = originalTitle,
                year = year,
                cover = cover,
                description = description,
                rating = rating,
                overallRating = rating,
                genres = genres,
                lastModified = System.currentTimeMillis(),
                notionPageId = null,
                status = status,
                userRating = null,
                userComment = null,
                userTags = emptyList(),
                doubanUrl = null,
                createdAt = System.currentTimeMillis(),
                author = author,
                isbn = isbn,
                pages = pages,
                publisher = publisher,
                recommendationSource = null,
                filePath = null
            )
            
            bookRepository.insertBook(book)
        }
    }
}
```

### 获取并显示书籍列表

```kotlin
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    private val _bookStatus = MutableStateFlow(MediaStatus.WANT_TO_CONSUME)
    val bookStatus = _bookStatus.asStateFlow()
    
    val books = _bookStatus.flatMapLatest { status ->
        bookRepository.getBooksByStatus(status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun setBookStatus(status: MediaStatus) {
        _bookStatus.value = status
    }
    
    fun updateBookStatus(id: String, status: MediaStatus) {
        viewModelScope.launch {
            bookRepository.updateBookStatus(id, status)
        }
    }
    
    fun updateBookRating(id: String, rating: Float) {
        viewModelScope.launch {
            bookRepository.updateBookRating(id, rating)
        }
    }
    
    fun deleteBook(id: String) {
        viewModelScope.launch {
            bookRepository.deleteBook(id)
        }
    }
}
```

## 依赖注入

使用Hilt进行依赖注入：

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return MediaDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideBookDao(database: MediaDatabase): BookDao {
        return database.bookDao()
    }
    
    @Provides
    @Singleton
    fun provideBookRepository(
        bookDao: BookDao,
        notionClient: NotionClient?
    ): BookRepository {
        return BookRepositoryImpl(bookDao, notionClient)
    }
    
    @Provides
    @Singleton
    fun provideNotionClient(
        userPreferencesRepository: UserPreferencesRepository
    ): NotionClient? {
        val preferences = runBlocking { userPreferencesRepository.getUserPreferences().first() }
        return preferences.notionToken?.let { token ->
            NotionClient(token, preferences.notionDatabaseId)
        }
    }
    
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```

## 版本控制与迁移

Room数据库迁移策略：

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加新列或修改表结构
        database.execSQL("ALTER TABLE book_details ADD COLUMN recommendation_source TEXT")
    }
}

// 在数据库创建时应用迁移策略
Room.databaseBuilder(context, MediaDatabase::class.java, "media_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

## 结论

本文档详细描述了CollectMeta应用中书籍数据的本地存储设计与实现。通过Room数据库与Repository模式，我们实现了:

1. 基于Clean Architecture的数据管理
2. 支持数据双向同步（本地与Notion）
3. 强类型的领域模型
4. 响应式数据流（Flow）
5. 依赖注入的组件化架构

后续可以扩展的功能包括:

1. 全文搜索功能
2. 多语言支持 
3. 批量导入/导出
4. 数据备份还原策略
5. 与其他媒体类型的关联 