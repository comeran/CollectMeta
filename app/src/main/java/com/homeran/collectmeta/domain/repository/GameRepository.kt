package com.homeran.collectmeta.domain.repository

import com.homeran.collectmeta.data.db.entities.PlayStatus
import com.homeran.collectmeta.domain.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * 游戏仓库接口，定义游戏数据的访问方法
 */
interface GameRepository {
    fun getAllGames(): Flow<List<Game>>
    suspend fun getGameById(id: String): Game?
    fun getGamesByStatus(status: PlayStatus): Flow<List<Game>>
    suspend fun addGame(igdbId: String): Result<Game>
    suspend fun insertGame(game: Game)
    suspend fun deleteGame(id: String)
    suspend fun updateGamePlayStatus(
        gameId: String,
        status: PlayStatus,
        userRating: Float? = null,
        userComment: String? = null
    )
    suspend fun updateGamePlatformOwnership(
        platformId: String,
        owned: Boolean,
        digital: Boolean = false,
        store: String? = null
    )
    suspend fun updateDLCStatus(
        dlcId: String,
        owned: Boolean,
        completed: Boolean = false
    )
    suspend fun searchGames(query: String): List<Game>
} 