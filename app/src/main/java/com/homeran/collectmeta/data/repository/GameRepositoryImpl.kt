package com.homeran.collectmeta.data.repository

import com.homeran.collectmeta.data.db.dao.GameDao
import com.homeran.collectmeta.data.db.entities.PlayStatus
import com.homeran.collectmeta.data.mapper.toDomainModel
import com.homeran.collectmeta.data.mapper.toEntity
import com.homeran.collectmeta.data.mapper.toGameDetailsEntity
import com.homeran.collectmeta.data.mapper.toMediaEntity
import com.homeran.collectmeta.domain.model.Game
import com.homeran.collectmeta.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * 游戏仓库实现类，实现游戏数据的访问逻辑
 */
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val igdbClient: Any? = null, // 占位，实际应使用IGDBClient
    private val notionSync: Any? = null, // 占位，实际应使用NotionSync
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GameRepository {
    
    override fun getAllGames(): Flow<List<Game>> {
        return gameDao.getAllGames().map { gameEntities ->
            gameEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getGameById(id: String): Game? = withContext(ioDispatcher) {
        gameDao.getGameById(id)?.toDomainModel()
    }
    
    override fun getGamesByStatus(status: PlayStatus): Flow<List<Game>> {
        return gameDao.getGamesByStatus(status).map { gameEntities ->
            gameEntities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun addGame(igdbId: String): Result<Game> = withContext(ioDispatcher) {
        try {
            // 在实际实现中，这里应该调用IGDB API获取游戏信息
            // 这里只是一个示例实现
            val game = createDummyGame(igdbId)
            insertGame(game)
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun insertGame(game: Game) = withContext(ioDispatcher) {
        val mediaEntity = game.toMediaEntity()
        val gameDetailsEntity = game.toGameDetailsEntity()
        val platformEntities = game.platforms.map { it.toEntity(game.id) }
        val dlcEntities = game.dlcs.map { it.toEntity(game.id) }
        
        gameDao.insertGameWithDetails(
            mediaEntity,
            gameDetailsEntity,
            platformEntities,
            dlcEntities
        )
        
        // 如果配置了Notion同步，则同步到Notion
        notionSync?.let {
            // 同步逻辑
        }
    }
    
    override suspend fun deleteGame(id: String) = withContext(ioDispatcher) {
        gameDao.deleteGameById(id)
    }
    
    override suspend fun updateGamePlayStatus(
        gameId: String,
        status: PlayStatus,
        userRating: Float?,
        userComment: String?
    ) = withContext(ioDispatcher) {
        gameDao.updateGameStatus(gameId, status.name)
        
        userRating?.let {
            gameDao.updateGameRating(gameId, it)
        }
        
        userComment?.let {
            gameDao.updateGameComment(gameId, it)
        }
        
        // 同步到Notion
        notionSync?.let {
            // 同步逻辑
        }
    }
    
    override suspend fun updateGamePlatformOwnership(
        platformId: String,
        owned: Boolean,
        digital: Boolean,
        store: String?
    ) = withContext(ioDispatcher) {
        val platform = gameDao.getPlatformById(platformId)
        platform?.let {
            val updatedPlatform = it.copy(
                owned = owned,
                digital = digital,
                store = store
            )
            gameDao.updateGamePlatform(updatedPlatform)
            
            // 同步到Notion
            notionSync?.let {
                // 同步逻辑
            }
        }
    }
    
    override suspend fun updateDLCStatus(
        dlcId: String,
        owned: Boolean,
        completed: Boolean
    ) = withContext(ioDispatcher) {
        val dlc = gameDao.getDLCById(dlcId)
        dlc?.let {
            val updatedDLC = it.copy(
                owned = owned,
                completed = completed
            )
            gameDao.updateGameDLC(updatedDLC)
            
            // 同步到Notion
            notionSync?.let {
                // 同步逻辑
            }
        }
    }
    
    override suspend fun searchGames(query: String): List<Game> = withContext(ioDispatcher) {
        gameDao.searchGames(query).map { it.toDomainModel() }
    }
    
    // 辅助方法，创建一个虚拟的游戏对象用于示例
    private fun createDummyGame(igdbId: String): Game {
        // 实际实现应从IGDB获取数据
        // 这里只是示例
        return Game(
            id = UUID.randomUUID().toString(),
            title = "示例游戏",
            originalTitle = "Example Game",
            year = 2023,
            coverUrl = "https://example.com/cover.jpg",
            description = "这是一个示例游戏",
            rating = 8.5f,
            genres = listOf("动作", "冒险"),
            playStatus = PlayStatus.PLAN_TO_PLAY,
            userRating = null,
            userComment = null,
            userTags = emptyList(),
            customFields = emptyMap(),
            details = com.example.collectmeta.domain.model.GameDetails(
                igdbId = igdbId,
                developer = "示例开发商",
                publisher = "示例发行商",
                releaseDate = "2023-01-01",
                gameStatus = com.example.collectmeta.data.db.entities.GameStatus.RELEASED,
                averagePlaytime = 20,
                esrbRating = "T",
                metacriticScore = 85,
                website = "https://example.com"
            ),
            platforms = listOf(
                com.example.collectmeta.domain.model.GamePlatform(
                    id = UUID.randomUUID().toString(),
                    platformName = "PC",
                    releaseDate = "2023-01-01",
                    owned = false,
                    digital = true,
                    store = "Steam"
                )
            ),
            dlcs = emptyList()
        )
    }
} 