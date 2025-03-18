# TV Show Database Design for CollectMeta

## Overview
This document outlines the database design for TV shows in the CollectMeta application. The implementation uses Room database and follows Clean Architecture principles.

## Data Model Design

### Entity Relationship Diagram
```
MediaEntity (1) -----> (1) TvShowDetailsEntity
                |
                |-----> (1..n) TvSeasonEntity
                            |
                            |-----> (1..n) TvEpisodeEntity
```

### Enumerations

```kotlin
enum class MediaType {
    TV_SHOW,
    MOVIE,
    BOOK,
    GAME
}

enum class TvShowStatus {
    AIRING,
    ENDED,
    CANCELLED,
    ANNOUNCED
}

enum class WatchStatus {
    PLAN_TO_WATCH,
    WATCHING,
    COMPLETED,
    ON_HOLD,
    DROPPED
}
```

### Entity Definitions

```kotlin
@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: MediaType,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "original_title") val originalTitle: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "cover_url") val coverUrl: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "rating") val rating: Float,
    @ColumnInfo(name = "genres") val genres: List<String>,
    @ColumnInfo(name = "last_modified") val lastModified: Long,
    @ColumnInfo(name = "notion_page_id") val notionPageId: String?,
    @ColumnInfo(name = "status") val watchStatus: WatchStatus,
    @ColumnInfo(name = "user_rating") val userRating: Float?,
    @ColumnInfo(name = "user_comment") val userComment: String?,
    @ColumnInfo(name = "user_tags") val userTags: List<String>,
    @ColumnInfo(name = "custom_fields") val customFields: Map<String, String>
)

@Entity(
    tableName = "tv_show_details",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TvShowDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "tmdb_id") val tmdbId: String,
    @ColumnInfo(name = "total_episodes") val totalEpisodes: Int,
    @ColumnInfo(name = "total_seasons") val totalSeasons: Int,
    @ColumnInfo(name = "show_status") val showStatus: TvShowStatus,
    @ColumnInfo(name = "first_air_date") val firstAirDate: String,
    @ColumnInfo(name = "last_air_date") val lastAirDate: String?,
    @ColumnInfo(name = "network") val network: String,
    @ColumnInfo(name = "episode_runtime") val episodeRuntime: Int
)

@Entity(
    tableName = "tv_seasons",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tv_show_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TvSeasonEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "tv_show_id") val tvShowId: String,
    @ColumnInfo(name = "season_number") val seasonNumber: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "air_date") val airDate: String,
    @ColumnInfo(name = "episode_count") val episodeCount: Int,
    @ColumnInfo(name = "poster_path") val posterPath: String?
)

@Entity(
    tableName = "tv_episodes",
    foreignKeys = [
        ForeignKey(
            entity = TvSeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TvEpisodeEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "season_id") val seasonId: String,
    @ColumnInfo(name = "episode_number") val episodeNumber: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "overview") val overview: String,
    @ColumnInfo(name = "air_date") val airDate: String,
    @ColumnInfo(name = "runtime") val runtime: Int,
    @ColumnInfo(name = "still_path") val stillPath: String?,
    @ColumnInfo(name = "watched") val watched: Boolean,
    @ColumnInfo(name = "watch_date") val watchDate: String?
)
```

### Type Converters

```kotlin
class TvShowTypeConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromCustomFields(value: Map<String, String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toCustomFields(value: String): Map<String, String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromWatchStatus(value: WatchStatus): String = value.name

    @TypeConverter
    fun toWatchStatus(value: String): WatchStatus = WatchStatus.valueOf(value)

    @TypeConverter
    fun fromTvShowStatus(value: TvShowStatus): String = value.name

    @TypeConverter
    fun toTvShowStatus(value: String): TvShowStatus = TvShowStatus.valueOf(value)
}
```

### DAO Implementation

```kotlin
@Dao
interface TvShowDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'TV_SHOW'")
    fun getAllTvShows(): Flow<List<MediaEntity>>

    @Transaction
    @Query("SELECT * FROM media WHERE id = :id AND type = 'TV_SHOW'")
    suspend fun getTvShowById(id: String): MediaEntity?

    @Transaction
    @Query("""
        SELECT * FROM media 
        WHERE type = 'TV_SHOW' 
        AND status = :status
    """)
    fun getTvShowsByStatus(status: WatchStatus): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(media: MediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShowDetails(details: TvShowDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: TvSeasonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: TvEpisodeEntity)

    @Transaction
    @Query("""
        SELECT * FROM tv_seasons 
        WHERE tv_show_id = :tvShowId 
        ORDER BY season_number
    """)
    fun getSeasonsForTvShow(tvShowId: String): Flow<List<TvSeasonEntity>>

    @Transaction
    @Query("""
        SELECT * FROM tv_episodes 
        WHERE season_id = :seasonId 
        ORDER BY episode_number
    """)
    fun getEpisodesForSeason(seasonId: String): Flow<List<TvEpisodeEntity>>

    @Update
    suspend fun updateEpisode(episode: TvEpisodeEntity)

    @Delete
    suspend fun deleteTvShow(media: MediaEntity)
}
```

## Repository Implementation

```kotlin
class TvShowRepository @Inject constructor(
    private val tvShowDao: TvShowDao,
    private val tmdbClient: TmdbClient,
    private val notionSync: NotionSync
) {
    fun getAllTvShows(): Flow<List<TvShow>> =
        tvShowDao.getAllTvShows()
            .map { entities -> entities.map { it.toDomain() } }

    fun getTvShowsByStatus(status: WatchStatus): Flow<List<TvShow>> =
        tvShowDao.getTvShowsByStatus(status)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun addTvShow(tmdbId: String) {
        val tvShowDetails = tmdbClient.getTvShowDetails(tmdbId)
        val mediaEntity = tvShowDetails.toMediaEntity()
        val tvShowEntity = tvShowDetails.toTvShowDetailsEntity()
        
        tvShowDao.insertTvShow(mediaEntity)
        tvShowDao.insertTvShowDetails(tvShowEntity)
        
        tvShowDetails.seasons.forEach { season ->
            val seasonEntity = season.toSeasonEntity(mediaEntity.id)
            tvShowDao.insertSeason(seasonEntity)
            
            season.episodes.forEach { episode ->
                val episodeEntity = episode.toEpisodeEntity(seasonEntity.id)
                tvShowDao.insertEpisode(episodeEntity)
            }
        }

        notionSync.syncTvShow(mediaEntity.id)
    }

    suspend fun updateEpisodeWatchStatus(
        episodeId: String,
        watched: Boolean,
        watchDate: String? = null
    ) {
        val episode = tvShowDao.getEpisodeById(episodeId)
        episode?.let {
            val updatedEpisode = it.copy(
                watched = watched,
                watchDate = watchDate
            )
            tvShowDao.updateEpisode(updatedEpisode)
            notionSync.syncEpisodeStatus(episodeId)
        }
    }
}
```

## Domain Models and Mapping

```kotlin
data class TvShow(
    val id: String,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val coverUrl: String,
    val description: String,
    val rating: Float,
    val genres: List<String>,
    val watchStatus: WatchStatus,
    val userRating: Float?,
    val details: TvShowDetails,
    val seasons: List<TvSeason>
)

data class TvShowDetails(
    val tmdbId: String,
    val totalEpisodes: Int,
    val totalSeasons: Int,
    val showStatus: TvShowStatus,
    val firstAirDate: String,
    val lastAirDate: String?,
    val network: String,
    val episodeRuntime: Int
)

data class TvSeason(
    val id: String,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val airDate: String,
    val episodeCount: Int,
    val posterPath: String?,
    val episodes: List<TvEpisode>
)

data class TvEpisode(
    val id: String,
    val episodeNumber: Int,
    val name: String,
    val overview: String,
    val airDate: String,
    val runtime: Int,
    val stillPath: String?,
    val watched: Boolean,
    val watchDate: String?
)

// Extension functions for mapping
fun MediaEntity.toDomain(): TvShow {
    // Mapping implementation
}

fun TvShowDetailsEntity.toDomain(): TvShowDetails {
    // Mapping implementation
}

fun TvSeasonEntity.toDomain(episodes: List<TvEpisodeEntity>): TvSeason {
    // Mapping implementation
}

fun TvEpisodeEntity.toDomain(): TvEpisode {
    // Mapping implementation
}
```

## Usage Example

```kotlin
@HiltViewModel
class AddTvShowViewModel @Inject constructor(
    private val tvShowRepository: TvShowRepository,
    private val tmdbClient: TmdbClient
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<TvShowSearchResult>>(emptyList())
    val searchResults: StateFlow<List<TvShowSearchResult>> = _searchResults.asStateFlow()

    fun searchTvShow(query: String) = viewModelScope.launch {
        val results = tmdbClient.searchTvShows(query)
        _searchResults.value = results
    }

    fun addTvShow(tmdbId: String) = viewModelScope.launch {
        try {
            tvShowRepository.addTvShow(tmdbId)
            // Handle success
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

## Migration Strategy

When updating the database schema, implement migrations using Room's migration system:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Migration implementation
    }
}

Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "collect_meta.db"
)
.addMigrations(MIGRATION_1_2)
.build()
```

## Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class TvShowDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var tvShowDao: TvShowDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        tvShowDao = database.tvShowDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndRetrieveTvShow() = runTest {
        // Test implementation
    }
}
```
