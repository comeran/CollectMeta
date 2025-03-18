# 电影数据库设计文档

## 概述

本文档描述了CollectMeta应用中电影数据的本地存储方案，基于Room数据库实现。设计遵循Clean Architecture架构原则，采用Repository模式进行数据管理。数据结构参考了Notion模板的设计，确保本地数据库与Notion同步的兼容性。

## 数据模型设计

### 实体关系图

媒体基础实体（Media Entity）与电影详情实体（Movie Details Entity）之间为一对一关系：

```
┌─────────────────┐       ┌─────────────────┐
│  MediaEntity    │       │ MovieDetailsEntity│
├─────────────────┤       ├─────────────────┤
│ id (PK)         │──────>│ mediaId (PK)    │
│ type            │       │ director        │
│ title           │       │ duration        │
│ ...             │       │ ...             │
└─────────────────┘       └─────────────────┘
```

### 数据结构

1. **MediaType枚举**：定义媒体类型
2. **MediaStatus枚举**：定义媒体状态（想看/在看/看过）
3. **MediaEntity**：所有媒体类型的共享基础信息
4. **MovieDetailsEntity**：电影特有信息
5. **MovieWithDetails**：组合视图，表示完整的电影信息

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
    WANT_TO_CONSUME,  // 想看
    CONSUMING,        // 在看
    CONSUMED          // 看过
}
```

### 2. 实体定义

```kotlin
@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: MediaType = MediaType.MOVIE,
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
    @ColumnInfo(name = "created_at") val createdAt: Long, // 创建时间
    @ColumnInfo(name = "release_date") val releaseDate: Long?, // 上映日期
    @ColumnInfo(name = "release_status") val releaseStatus: String // 上映状态
)

@Entity(tableName = "movie_details")
data class MovieDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "director") val director: String,
    @ColumnInfo(name = "cast") val cast: String, // 存储为JSON
    @ColumnInfo(name = "duration") val duration: Int, // 时长（分钟）
    @ColumnInfo(name = "region") val region: String, // 制片地区
    @ColumnInfo(name = "episode_count") val episodeCount: Int, // 集数
    @ColumnInfo(name = "episode_duration") val episodeDuration: Int, // 单集时长
    @ColumnInfo(name = "tmdb_id") val tmdbId: String?,
    @ColumnInfo(name = "trakt_url") val traktUrl: String?
)

// 关系实体
data class MovieWithDetails(
    @Embedded val media: MediaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val movieDetails: MovieDetailsEntity
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
interface MovieDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE'")
    fun getAllMovies(): Flow<List<MovieWithDetails>>

    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND id = :movieId")
    fun getMovieById(movieId: String): Flow<MovieWithDetails>
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND status = :status")
    fun getMoviesByStatus(status: String): Flow<List<MovieWithDetails>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaEntity(mediaEntity: MediaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)
    
    @Transaction
    suspend fun insertMovieWithDetails(mediaEntity: MediaEntity, movieDetails: MovieDetailsEntity) {
        insertMediaEntity(mediaEntity)
        insertMovieDetails(movieDetails)
    }
    
    @Query("DELETE FROM media WHERE id = :mediaId AND type = 'MOVIE'")
    suspend fun deleteMovie(mediaId: String)
    
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND title LIKE '%' || :query || '%'")
    fun searchMoviesByTitle(query: String): Flow<List<MovieWithDetails>>
    
    @Query("UPDATE media SET status = :status WHERE id = :mediaId")
    suspend fun updateMovieStatus(mediaId: String, status: String)
    
    @Query("UPDATE media SET user_rating = :rating WHERE id = :mediaId")
    suspend fun updateMovieRating(mediaId: String, rating: Float)
    
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'MOVIE' AND genres LIKE '%' || :genre || '%'")
    fun getMoviesByGenre(genre: String): Flow<List<MovieWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM media m JOIN movie_details md ON m.id = md.mediaId WHERE m.type = 'MOVIE' AND md.region = :region")
    fun getMoviesByRegion(region: String): Flow<List<MovieWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM media m JOIN movie_details md ON m.id = md.mediaId WHERE m.type = 'MOVIE' AND md.director LIKE '%' || :director || '%'")
    fun getMoviesByDirector(director: String): Flow<List<MovieWithDetails>>
}
```

### 5. 数据库定义

```kotlin
@Database(
    entities = [
        MediaEntity::class,
        MovieDetailsEntity::class,
        BookDetailsEntity::class
        // 其他实体...
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
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
interface MovieRepository {
    fun getAllMovies(): Flow<List<Movie>>
    fun getMovieById(id: String): Flow<Movie?>
    fun getMoviesByStatus(status: MediaStatus): Flow<List<Movie>>
    suspend fun insertMovie(movie: Movie)
    suspend fun deleteMovie(id: String)
    suspend fun updateMovieStatus(id: String, status: MediaStatus)
    suspend fun updateMovieRating(id: String, rating: Float)
    fun searchMovies(query: String): Flow<List<Movie>>
    fun getMoviesByGenre(genre: String): Flow<List<Movie>>
    fun getMoviesByRegion(region: String): Flow<List<Movie>>
    fun getMoviesByDirector(director: String): Flow<List<Movie>>
}

class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val notionClient: NotionClient?,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MovieRepository {
    
    override fun getAllMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMovieById(id: String): Flow<Movie?> {
        return movieDao.getMovieById(id).map { it?.toDomainModel() }
    }
    
    override fun getMoviesByStatus(status: MediaStatus): Flow<List<Movie>> {
        return movieDao.getMoviesByStatus(status.name).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun insertMovie(movie: Movie) = withContext(ioDispatcher) {
        val mediaEntity = movie.toMediaEntity()
        val movieDetailsEntity = movie.toMovieDetailsEntity()
        movieDao.insertMovieWithDetails(mediaEntity, movieDetailsEntity)
        
        // 如果配置了Notion同步，则同步到Notion
        notionClient?.let {
            // 同步逻辑
        }
    }
    
    override suspend fun deleteMovie(id: String) = withContext(ioDispatcher) {
        movieDao.deleteMovie(id)
    }
    
    override suspend fun updateMovieStatus(id: String, status: MediaStatus) = withContext(ioDispatcher) {
        movieDao.updateMovieStatus(id, status.name)
    }
    
    override suspend fun updateMovieRating(id: String, rating: Float) = withContext(ioDispatcher) {
        movieDao.updateMovieRating(id, rating)
    }
    
    override fun searchMovies(query: String): Flow<List<Movie>> {
        return movieDao.searchMoviesByTitle(query).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByGenre(genre: String): Flow<List<Movie>> {
        return movieDao.getMoviesByGenre(genre).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByRegion(region: String): Flow<List<Movie>> {
        return movieDao.getMoviesByRegion(region).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getMoviesByDirector(director: String): Flow<List<Movie>> {
        return movieDao.getMoviesByDirector(director).map { movieEntities ->
            movieEntities.map { it.toDomainModel() }
        }
    }
}
```

## 领域模型与映射扩展

```kotlin
// 领域模型
data class Movie(
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
    val releaseDate: Long?,
    val releaseStatus: String,
    // 电影特有属性
    val director: String,
    val cast: List<String>,
    val duration: Int,
    val region: String,
    val episodeCount: Int,
    val episodeDuration: Int,
    val tmdbId: String?,
    val traktUrl: String?
)

// 扩展函数 - 转换实体到领域模型
fun MovieWithDetails.toDomainModel(): Movie {
    return Movie(
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
        releaseDate = this.media.releaseDate,
        releaseStatus = this.media.releaseStatus,
        director = this.movieDetails.director,
        cast = Gson().fromJson(this.movieDetails.cast, object : TypeToken<List<String>>() {}.type),
        duration = this.movieDetails.duration,
        region = this.movieDetails.region,
        episodeCount = this.movieDetails.episodeCount,
        episodeDuration = this.movieDetails.episodeDuration,
        tmdbId = this.movieDetails.tmdbId,
        traktUrl = this.movieDetails.traktUrl
    )
}

// 扩展函数 - 转换领域模型到实体
fun Movie.toMediaEntity(): MediaEntity {
    return MediaEntity(
        id = this.id,
        type = MediaType.MOVIE,
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
        createdAt = this.createdAt,
        releaseDate = this.releaseDate,
        releaseStatus = this.releaseStatus
    )
}

fun Movie.toMovieDetailsEntity(): MovieDetailsEntity {
    return MovieDetailsEntity(
        mediaId = this.id,
        director = this.director,
        cast = Gson().toJson(this.cast),
        duration = this.duration,
        region = this.region,
        episodeCount = this.episodeCount,
        episodeDuration = this.episodeDuration,
        tmdbId = this.tmdbId,
        traktUrl = this.traktUrl
    )
}
```

## 使用示例

### 添加新电影

```kotlin
class AddMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    fun addMovie(
        title: String,
        originalTitle: String,
        year: Int,
        cover: String,
        description: String,
        rating: Float,
        genres: List<String>,
        status: MediaStatus,
        releaseDate: Long?,
        releaseStatus: String,
        director: String,
        cast: List<String>,
        duration: Int,
        region: String,
        episodeCount: Int = 1,
        episodeDuration: Int = duration
    ) {
        viewModelScope.launch {
            val movie = Movie(
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
                releaseDate = releaseDate,
                releaseStatus = releaseStatus,
                director = director,
                cast = cast,
                duration = duration,
                region = region,
                episodeCount = episodeCount,
                episodeDuration = episodeDuration,
                tmdbId = null,
                traktUrl = null
            )
            
            movieRepository.insertMovie(movie)
        }
    }
}
```

### 获取并显示电影列表

```kotlin
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _movieStatus = MutableStateFlow(MediaStatus.WANT_TO_CONSUME)
    val movieStatus = _movieStatus.asStateFlow()
    
    val movies = _movieStatus.flatMapLatest { status ->
        movieRepository.getMoviesByStatus(status)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    private val _selectedGenre = MutableStateFlow<String?>(null)
    val filteredMovies = combine(movies, _selectedGenre) { allMovies, genre ->
        genre?.let {
            allMovies.filter { movie -> movie.genres.contains(genre) }
        } ?: allMovies
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun setMovieStatus(status: MediaStatus) {
        _movieStatus.value = status
    }
    
    fun setGenreFilter(genre: String?) {
        _selectedGenre.value = genre
    }
    
    fun updateMovieStatus(id: String, status: MediaStatus) {
        viewModelScope.launch {
            movieRepository.updateMovieStatus(id, status)
        }
    }
    
    fun updateMovieRating(id: String, rating: Float) {
        viewModelScope.launch {
            movieRepository.updateMovieRating(id, rating)
        }
    }
    
    fun deleteMovie(id: String) {
        viewModelScope.launch {
            movieRepository.deleteMovie(id)
        }
    }
    
    fun searchMovies(query: String): Flow<List<Movie>> {
        return movieRepository.searchMovies(query)
    }
    
    fun getMoviesByRegion(region: String): Flow<List<Movie>> {
        return movieRepository.getMoviesByRegion(region)
    }
    
    fun getMoviesByDirector(director: String): Flow<List<Movie>> {
        return movieRepository.getMoviesByDirector(director)
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
    fun provideMovieDao(database: MediaDatabase): MovieDao {
        return database.movieDao()
    }
    
    @Provides
    @Singleton
    fun provideMovieRepository(
        movieDao: MovieDao,
        notionClient: NotionClient?
    ): MovieRepository {
        return MovieRepositoryImpl(movieDao, notionClient)
    }
    
    // 其他Provides方法...
}
```

## 版本控制与迁移

Room数据库迁移策略：

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加新列，例如增加电影特效类型字段
        database.execSQL("ALTER TABLE movie_details ADD COLUMN special_effects TEXT")
    }
}

// 在数据库创建时应用迁移策略
Room.databaseBuilder(context, MediaDatabase::class.java, "media_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

## 与Notion同步

电影数据与Notion的同步实现：

```kotlin
class NotionMovieSynchronizer @Inject constructor(
    private val notionClient: NotionClient,
    private val movieRepository: MovieRepository
) {
    
    suspend fun syncMoviesToNotion() {
        val movies = movieRepository.getAllMovies().first()
        
        for (movie in movies) {
            if (movie.notionPageId == null) {
                // 创建新页面
                val pageId = createMoviePageInNotion(movie)
                // 更新本地电影记录，添加Notion页面ID
                updateMovieWithNotionPageId(movie.id, pageId)
            } else {
                // 更新现有页面
                updateMoviePageInNotion(movie)
            }
        }
    }
    
    suspend fun syncMoviesFromNotion() {
        val notionMovies = notionClient.getMoviesFromNotion()
        
        for (notionMovie in notionMovies) {
            val localMovie = movieRepository.getMovieByNotionId(notionMovie.id).firstOrNull()
            
            if (localMovie == null) {
                // 本地不存在，创建新记录
                val movie = convertNotionToMovie(notionMovie)
                movieRepository.insertMovie(movie)
            } else if (notionMovie.lastModified > localMovie.lastModified) {
                // Notion版本更新，更新本地记录
                val updatedMovie = mergeNotionWithLocalMovie(notionMovie, localMovie)
                movieRepository.insertMovie(updatedMovie)
            }
        }
    }
    
    private suspend fun createMoviePageInNotion(movie: Movie): String {
        // 创建Notion页面并返回页面ID
        return notionClient.createMoviePage(movie)
    }
    
    private suspend fun updateMoviePageInNotion(movie: Movie) {
        // 更新Notion页面
        notionClient.updateMoviePage(movie)
    }
    
    private suspend fun updateMovieWithNotionPageId(movieId: String, notionPageId: String) {
        // 更新本地电影记录的Notion页面ID
        movieRepository.updateMovieNotionPageId(movieId, notionPageId)
    }
    
    private fun convertNotionToMovie(notionMovie: NotionMovie): Movie {
        // 将Notion数据转换为Movie领域模型
        return Movie(
            id = UUID.randomUUID().toString(),
            title = notionMovie.title,
            // ... 其他字段映射
            notionPageId = notionMovie.id,
            lastModified = notionMovie.lastModified
        )
    }
    
    private fun mergeNotionWithLocalMovie(notionMovie: NotionMovie, localMovie: Movie): Movie {
        // 合并Notion和本地数据
        return localMovie.copy(
            title = notionMovie.title,
            // ... 其他字段更新
            lastModified = notionMovie.lastModified
        )
    }
}
```

## 电影数据导入功能

支持从豆瓣API或文件导入电影数据：

```kotlin
class MovieImporter @Inject constructor(
    private val movieRepository: MovieRepository,
    private val doubanApiClient: DoubanApiClient
) {
    
    suspend fun importFromDoubanByUrl(doubanUrl: String): Result<Movie> {
        return try {
            val movieId = extractDoubanMovieId(doubanUrl)
            val doubanMovie = doubanApiClient.getMovieDetails(movieId)
            
            val movie = convertDoubanToMovie(doubanMovie, doubanUrl)
            movieRepository.insertMovie(movie)
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun importFromJsonFile(jsonContent: String): Result<List<Movie>> {
        return try {
            val movieList = parseMoviesFromJson(jsonContent)
            
            movieList.forEach { movie ->
                movieRepository.insertMovie(movie)
            }
            
            Result.success(movieList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun extractDoubanMovieId(doubanUrl: String): String {
        // 从URL中提取电影ID
        val regex = "subject/(\\d+)".toRegex()
        return regex.find(doubanUrl)?.groupValues?.get(1)
            ?: throw IllegalArgumentException("Invalid Douban URL")
    }
    
    private fun convertDoubanToMovie(doubanMovie: DoubanMovie, doubanUrl: String): Movie {
        // 将豆瓣API返回的数据转换为Movie领域模型
        return Movie(
            id = UUID.randomUUID().toString(),
            title = doubanMovie.title,
            originalTitle = doubanMovie.originalTitle ?: doubanMovie.title,
            year = doubanMovie.year,
            cover = doubanMovie.cover,
            description = doubanMovie.summary,
            rating = doubanMovie.rating.average,
            overallRating = doubanMovie.rating.average,
            genres = doubanMovie.genres,
            lastModified = System.currentTimeMillis(),
            notionPageId = null,
            status = MediaStatus.WANT_TO_CONSUME,
            userRating = null,
            userComment = null,
            userTags = emptyList(),
            doubanUrl = doubanUrl,
            createdAt = System.currentTimeMillis(),
            releaseDate = doubanMovie.releaseDate?.let { parseReleaseDate(it) },
            releaseStatus = "已上映",
            director = doubanMovie.directors.joinToString(", ") { it.name },
            cast = doubanMovie.casts.map { it.name },
            duration = doubanMovie.duration,
            region = doubanMovie.countries.joinToString(", "),
            episodeCount = 1,
            episodeDuration = doubanMovie.duration,
            tmdbId = null,
            traktUrl = null
        )
    }
    
    private fun parseReleaseDate(dateString: String): Long {
        // 解析发布日期字符串为时间戳
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.parse(dateString)?.time ?: System.currentTimeMillis()
    }
    
    private fun parseMoviesFromJson(jsonContent: String): List<Movie> {
        // 从JSON解析电影列表
        val type = object : TypeToken<List<JsonMovie>>() {}.type
        val jsonMovies: List<JsonMovie> = Gson().fromJson(jsonContent, type)
        
        return jsonMovies.map { jsonMovie ->
            // 转换为Movie领域模型
            Movie(
                id = UUID.randomUUID().toString(),
                // ... 字段映射
                createdAt = System.currentTimeMillis()
            )
        }
    }
}
```

## 结论

本文档详细描述了CollectMeta应用中电影数据的本地存储设计与实现。通过Room数据库与Repository模式，我们实现了:

1. 基于Clean Architecture的数据管理
2. 支持数据双向同步（本地与Notion）
3. 强类型的领域模型
4. 响应式数据流（Flow）
5. 依赖注入的组件化架构
6. 电影特有字段的处理（导演、地区、时长等）
7. 多种筛选和查询功能

后续可以扩展的功能包括:

1. 高级搜索与过滤（多条件组合）
2. 统计分析（观影时间、类型偏好等）
3. 推荐算法（基于已看电影推荐相似电影）
4. TMDB/Trakt等更多API集成
5. 电影收藏分享功能 