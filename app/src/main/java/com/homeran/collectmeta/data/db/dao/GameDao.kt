package com.homeran.collectmeta.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.homeran.collectmeta.data.db.entities.GameDLCEntity
import com.homeran.collectmeta.data.db.entities.GameDetailsEntity
import com.homeran.collectmeta.data.db.entities.GamePlatformEntity
import com.homeran.collectmeta.data.db.entities.GameWithDetails
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.data.db.entities.MediaStatus
import kotlinx.coroutines.flow.Flow

/**
 * 游戏数据访问对象接口，定义对游戏数据的各种数据库操作
 */
@Dao
interface GameDao {
    @Transaction
    @Query("SELECT * FROM media WHERE type = 'GAME'")
    fun getAllGames(): Flow<List<GameWithDetails>>

    @Transaction
    @Query("SELECT * FROM media WHERE id = :id AND type = 'GAME'")
    suspend fun getGameById(id: String): GameWithDetails?

    @Transaction
    @Query("""
        SELECT * FROM media 
        WHERE type = 'GAME' 
        AND status = :status
    """)
    fun getGamesByStatus(status: MediaStatus): Flow<List<GameWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(media: MediaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDetails(details: GameDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGamePlatform(platform: GamePlatformEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameDLC(dlc: GameDLCEntity)
    
    @Transaction
    suspend fun insertGameWithDetails(
        media: MediaEntity,
        details: GameDetailsEntity,
        platforms: List<GamePlatformEntity>,
        dlcs: List<GameDLCEntity>
    ) {
        insertGame(media)
        insertGameDetails(details)
        platforms.forEach { insertGamePlatform(it) }
        dlcs.forEach { insertGameDLC(it) }
    }

    @Transaction
    @Query("""
        SELECT * FROM game_platform 
        WHERE gameId = :gameId
    """)
    fun getPlatformsForGame(gameId: String): Flow<List<GamePlatformEntity>>
    
    @Query("SELECT * FROM game_platform WHERE id = :platformId AND gameId = :gameId")
    suspend fun getPlatformById(platformId: String, gameId: String): GamePlatformEntity?

    @Transaction
    @Query("""
        SELECT * FROM game_dlc 
        WHERE gameId = :gameId
    """)
    fun getDLCForGame(gameId: String): Flow<List<GameDLCEntity>>
    
    @Query("SELECT * FROM game_dlc WHERE id = :dlcId AND gameId = :gameId")
    suspend fun getDLCById(dlcId: String, gameId: String): GameDLCEntity?

    @Transaction
    @Query("""
        SELECT * FROM media 
        WHERE type = 'GAME' 
        AND title LIKE '%' || :query || '%'
    """)
    suspend fun searchGames(query: String): List<GameWithDetails>
    
    @Update
    suspend fun updateGameDetails(details: GameDetailsEntity)
    
    @Update
    suspend fun updateGamePlatform(platform: GamePlatformEntity)
    
    @Update
    suspend fun updateGameDLC(dlc: GameDLCEntity)
    
    @Query("UPDATE media SET status = :status WHERE id = :mediaId AND type = 'GAME'")
    suspend fun updateGameStatus(mediaId: String, status: MediaStatus)
    
    @Query("UPDATE media SET user_rating = :rating WHERE id = :mediaId AND type = 'GAME'")
    suspend fun updateGameRating(mediaId: String, rating: Float)
    
    @Query("UPDATE media SET user_comment = :comment WHERE id = :mediaId AND type = 'GAME'")
    suspend fun updateGameComment(mediaId: String, comment: String)

    @Delete
    suspend fun deleteGame(media: MediaEntity)
    
    @Query("DELETE FROM media WHERE id = :mediaId AND type = 'GAME'")
    suspend fun deleteGameById(mediaId: String)
} 