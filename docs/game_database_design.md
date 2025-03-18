# Game Database Design for CollectMeta

## Overview
This document outlines the database design for video games in the CollectMeta application. The implementation uses Room database and follows Clean Architecture principles.

## Data Model Design

### Entity Relationship Diagram
```
MediaEntity (1) -----> (1) GameDetailsEntity
                |
                |-----> (0..n) GamePlatformEntity
                |
                |-----> (0..n) GameDLCEntity
```

### Enumerations

```kotlin
enum class MediaType {
    TV_SHOW,
    MOVIE,
    BOOK,
    GAME
}

enum class GameStatus {
    RELEASED,
    EARLY_ACCESS,
    BETA,
    ANNOUNCED,
    CANCELLED
}

enum class PlayStatus {
    PLAN_TO_PLAY,
    PLAYING,
    COMPLETED,
    ABANDONED,
    ON_HOLD
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
    @ColumnInfo(name = "status") val playStatus: PlayStatus,
    @ColumnInfo(name = "user_rating") val userRating: Float?,
    @ColumnInfo(name = "user_comment") val userComment: String?,
    @ColumnInfo(name = "user_tags") val userTags: List<String>,
    @ColumnInfo(name = "custom_fields") val customFields: Map<String, String>
)

@Entity(
    tableName = "game_details",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "igdb_id") val igdbId: String,
    @ColumnInfo(name = "developer") val developer: String,
    @ColumnInfo(name = "publisher") val publisher: String,
    @ColumnInfo(name = "release_date") val releaseDate: String,
    @ColumnInfo(name = "game_status") val gameStatus: GameStatus,
    @ColumnInfo(name = "average_playtime") val averagePlaytime: Int?,
    @ColumnInfo(name = "esrb_rating") val esrbRating: String?,
    @ColumnInfo(name = "metacritic_score") val metacriticScore: Int?,
    @ColumnInfo(name = "website") val website: String?
)

@Entity(
    tableName = "game_platforms",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GamePlatformEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "game_id") val gameId: String,
    @ColumnInfo(name = "platform_name") val platformName: String,
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "owned") val owned: Boolean,
    @ColumnInfo(name = "digital") val digital: Boolean,
    @ColumnInfo(name = "store") val store: String?
)

@Entity(
    tableName = "game_dlc",
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameDLCEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "game_id") val gameId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "release_date") val releaseDate: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "owned") val owned: Boolean,
    @ColumnInfo(name = "completed") val completed: Boolean
)
```

### Type Converters

```kotlin
class GameTypeConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromCustomFields(value: Map<String, String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toCustomFields(value: String): Map<String, String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromPlayStatus(value: PlayStatus): String = value.name

    @TypeConverter
    fun toPlayStatus(value: String): PlayStatus = PlayStatus.valueOf(value)

    @TypeConverter
    fun fromGameStatus(value: GameStatus): String = value.name

    @TypeConverter
    fun toGameStatus(value: String): GameStatus = GameStatus.valueOf(value)
}
```

### DAO Implementation

```kotlin
@Dao
interface GameDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'GAME'")
    fun getAllGames(): Flow<List<MediaEntity>>

    @Transaction
    @Query("SELECT * FROM media WHERE id = :id AND type = 'GAME'")
    suspend fun getGameById(id: String): MediaEntity?

    @Transaction
    @Query("""
        SELECT * FROM media 
        WHERE type = 'GAME' 
        AND status = :status
    """)
    fun getGamesByStatus(status: PlayStatus): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(media: MediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetails(details: GameDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGamePlatform(platform: GamePlatformEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDLC(dlc: GameDLCEntity)

    @Transaction
    @Query("""
        SELECT * FROM game_platforms 
        WHERE game_id = :gameId
    """)
    fun getPlatformsForGame(gameId: String): Flow<List<GamePlatformEntity>>

    @Transaction
    @Query("""
        SELECT * FROM game_dlc 
        WHERE game_id = :gameId
    """)
    fun getDLCForGame(gameId: String): Flow<List<GameDLCEntity>>

    @Query("""
        SELECT * FROM media 
        WHERE type = 'GAME' 
        AND title LIKE '%' || :query || '%'
    """)
    suspend fun searchGames(query: String): List<MediaEntity>
    
    @Update
    suspend fun updateGameDetails(details: GameDetailsEntity)
    
    @Update
    suspend fun updateGamePlatform(platform: GamePlatformEntity)
    
    @Update
    suspend fun updateGameDLC(dlc: GameDLCEntity)

    @Delete
    suspend fun deleteGame(media: MediaEntity)
}
```

## Repository Implementation

```kotlin
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val igdbClient: IGDBClient,
    private val notionSync: NotionSync
) {
    fun getAllGames(): Flow<List<Game>> =
        gameDao.getAllGames()
            .map { entities -> entities.map { it.toDomain() } }

    fun getGamesByStatus(status: PlayStatus): Flow<List<Game>> =
        gameDao.getGamesByStatus(status)
            .map { entities -> entities.map { it.toDomain() } }

    suspend fun addGame(igdbId: String) {
        val gameDetails = igdbClient.getGameDetails(igdbId)
        val mediaEntity = gameDetails.toMediaEntity()
        val gameEntity = gameDetails.toGameDetailsEntity()
        
        gameDao.insertGame(mediaEntity)
        gameDao.insertGameDetails(gameEntity)
        
        gameDetails.platforms.forEach { platform ->
            val platformEntity = platform.toPlatformEntity(mediaEntity.id)
            gameDao.insertGamePlatform(platformEntity)
        }
        
        gameDetails.dlc.forEach { dlc ->
            val dlcEntity = dlc.toDLCEntity(mediaEntity.id)
            gameDao.insertGameDLC(dlcEntity)
        }

        notionSync.syncGame(mediaEntity.id)
    }

    suspend fun updateGamePlayStatus(
        gameId: String,
        status: PlayStatus,
        userRating: Float? = null,
        userComment: String? = null
    ) {
        val game = gameDao.getGameById(gameId)
        game?.let {
            val updatedGame = it.copy(
                playStatus = status,
                userRating = userRating ?: it.userRating,
                userComment = userComment ?: it.userComment,
                lastModified = System.currentTimeMillis()
            )
            gameDao.insertGame(updatedGame)
            notionSync.syncGame(gameId)
        }
    }
    
    suspend fun updateGamePlatformOwnership(
        platformId: String,
        owned: Boolean,
        digital: Boolean = false,
        store: String? = null
    ) {
        val platform = gameDao.getPlatformById(platformId)
        platform?.let {
            val updatedPlatform = it.copy(
                owned = owned,
                digital = digital,
                store = store
            )
            gameDao.updateGamePlatform(updatedPlatform)
            notionSync.syncGamePlatform(platformId)
        }
    }
    
    suspend fun updateDLCStatus(
        dlcId: String,
        owned: Boolean,
        completed: Boolean = false
    ) {
        val dlc = gameDao.getDLCById(dlcId)
        dlc?.let {
            val updatedDLC = it.copy(
                owned = owned,
                completed = completed
            )
            gameDao.updateGameDLC(updatedDLC)
            notionSync.syncGameDLC(dlcId)
        }
    }
    
    suspend fun searchGames(query: String): List<Game> {
        return gameDao.searchGames(query).map { it.toDomain() }
    }
}
```

## Domain Models and Mapping

```kotlin
data class Game(
    val id: String,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val coverUrl: String,
    val description: String,
    val rating: Float,
    val genres: List<String>,
    val playStatus: PlayStatus,
    val userRating: Float?,
    val userComment: String?,
    val userTags: List<String>,
    val details: GameDetails,
    val platforms: List<GamePlatform>,
    val dlc: List<GameDLC>
)

data class GameDetails(
    val igdbId: String,
    val developer: String,
    val publisher: String,
    val releaseDate: String,
    val gameStatus: GameStatus,
    val averagePlaytime: Int?,
    val esrbRating: String?,
    val metacriticScore: Int?,
    val website: String?
)

data class GamePlatform(
    val id: String,
    val platformName: String,
    val releaseDate: String?,
    val owned: Boolean,
    val digital: Boolean,
    val store: String?
)

data class GameDLC(
    val id: String,
    val name: String,
    val releaseDate: String?,
    val description: String?,
    val owned: Boolean,
    val completed: Boolean
)

// Extension functions for mapping
fun MediaEntity.toDomain(): Game {
    // Mapping implementation
}

fun GameDetailsEntity.toDomain(): GameDetails {
    // Mapping implementation
}

fun GamePlatformEntity.toDomain(): GamePlatform {
    // Mapping implementation
}

fun GameDLCEntity.toDomain(): GameDLC {
    // Mapping implementation
}
```

## Usage Example

```kotlin
@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val igdbClient: IGDBClient
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<GameSearchResult>>(emptyList())
    val searchResults: StateFlow<List<GameSearchResult>> = _searchResults.asStateFlow()

    private val _uiState = MutableStateFlow<AddGameUiState>(AddGameUiState.Initial)
    val uiState: StateFlow<AddGameUiState> = _uiState.asStateFlow()

    fun searchGame(query: String) = viewModelScope.launch {
        _uiState.value = AddGameUiState.Loading
        try {
            val results = igdbClient.searchGames(query)
            _searchResults.value = results
            _uiState.value = if (results.isEmpty()) {
                AddGameUiState.Empty
            } else {
                AddGameUiState.Success
            }
        } catch (e: Exception) {
            _uiState.value = AddGameUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun addGame(igdbId: String) = viewModelScope.launch {
        _uiState.value = AddGameUiState.Loading
        try {
            gameRepository.addGame(igdbId)
            _uiState.value = AddGameUiState.Added
        } catch (e: Exception) {
            _uiState.value = AddGameUiState.Error(e.message ?: "Unknown error")
        }
    }
    
    sealed class AddGameUiState {
        object Initial : AddGameUiState()
        object Loading : AddGameUiState()
        object Success : AddGameUiState()
        object Empty : AddGameUiState()
        object Added : AddGameUiState()
        data class Error(val message: String) : AddGameUiState()
    }
}
```

## Migration Strategy

When updating the database schema, implement migrations using Room's migration system:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add game-specific tables
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `game_details` (
                `media_id` TEXT NOT NULL, 
                `igdb_id` TEXT NOT NULL, 
                `developer` TEXT NOT NULL,
                `publisher` TEXT NOT NULL,
                `release_date` TEXT NOT NULL,
                `game_status` TEXT NOT NULL,
                `average_playtime` INTEGER,
                `esrb_rating` TEXT,
                `metacritic_score` INTEGER,
                `website` TEXT,
                PRIMARY KEY(`media_id`),
                FOREIGN KEY(`media_id`) REFERENCES `media`(`id`) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `game_platforms` (
                `id` TEXT NOT NULL,
                `game_id` TEXT NOT NULL,
                `platform_name` TEXT NOT NULL,
                `release_date` TEXT,
                `owned` INTEGER NOT NULL,
                `digital` INTEGER NOT NULL,
                `store` TEXT,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`game_id`) REFERENCES `media`(`id`) ON DELETE CASCADE
            )
        """)
        
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `game_dlc` (
                `id` TEXT NOT NULL,
                `game_id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `release_date` TEXT,
                `description` TEXT,
                `owned` INTEGER NOT NULL,
                `completed` INTEGER NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`game_id`) REFERENCES `media`(`id`) ON DELETE CASCADE
            )
        """)
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
class GameDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        gameDao = database.gameDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndRetrieveGame() = runTest {
        // Create test data
        val gameId = "game_1"
        val mediaEntity = MediaEntity(
            id = gameId,
            type = MediaType.GAME,
            title = "God of War: Ragnarok",
            originalTitle = "God of War: Ragnarok",
            year = 2022,
            coverUrl = "https://example.com/cover.jpg",
            description = "Epic action-adventure game.",
            rating = 9.2f,
            genres = listOf("Action", "Adventure"),
            lastModified = System.currentTimeMillis(),
            notionPageId = null,
            playStatus = PlayStatus.PLAYING,
            userRating = 9.5f,
            userComment = "Amazing game!",
            userTags = listOf("Favorite"),
            customFields = emptyMap()
        )
        
        val gameDetailsEntity = GameDetailsEntity(
            mediaId = gameId,
            igdbId = "igdb_123",
            developer = "Santa Monica Studio",
            publisher = "Sony Interactive Entertainment",
            releaseDate = "2022-11-09",
            gameStatus = GameStatus.RELEASED,
            averagePlaytime = 40,
            esrbRating = "M",
            metacriticScore = 94,
            website = "https://godofwar.playstation.com/"
        )
        
        // Insert test data
        gameDao.insertGame(mediaEntity)
        gameDao.insertGameDetails(gameDetailsEntity)
        
        // Retrieve and verify
        val retrievedGame = gameDao.getGameById(gameId)
        assertNotNull(retrievedGame)
        assertEquals("God of War: Ragnarok", retrievedGame?.title)
        assertEquals(MediaType.GAME, retrievedGame?.type)
    }
}
```

## Integration with Notion

The game database design supports integration with Notion using the following approach:

1. Each game in the local database can be mapped to a page in a Notion database
2. The `notionPageId` field in `MediaEntity` stores the reference to the corresponding Notion page
3. The `NotionSync` class handles bidirectional synchronization between the local database and Notion

### Notion Database Template Properties:
- Title: Game title
- Cover: Game cover image
- Status: Current play status
- Developer: Game developer
- Publisher: Game publisher
- Release Date: When the game was released
- Platforms: Multiselect of platforms the game is available on
- Genre: Multiselect of game genres
- Rating: Your rating (1-10)
- ESRB Rating: Content rating
- Metacritic: Metacritic score
- Playtime: Average hours to complete
- Completed: Whether you've finished the game
- Notes: Your comments about the game
