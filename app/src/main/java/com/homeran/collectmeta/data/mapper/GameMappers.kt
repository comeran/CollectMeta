package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.GameDLCEntity
import com.homeran.collectmeta.data.db.entities.GameDetailsEntity
import com.homeran.collectmeta.data.db.entities.GameEntity
import com.homeran.collectmeta.data.db.entities.GamePlatformEntity
import com.homeran.collectmeta.data.db.entities.GameWithDetails
import com.homeran.collectmeta.domain.model.Game
import com.homeran.collectmeta.domain.model.GameDLC
import com.homeran.collectmeta.domain.model.GamePlatform
import java.util.UUID

/**
 * 游戏实体到领域模型的映射扩展
 */
fun GameWithDetails.toDomainModel(): Game {
    return Game(
        id = game.id,
        igdbId = game.igdbId,
        title = game.title,
        originalTitle = game.originalTitle,
        description = game.description,
        coverImageUrl = game.coverImageUrl,
        backdropImageUrl = game.backdropImageUrl,
        releaseDate = game.releaseDate,
        genres = game.genres,
        developers = details?.developers ?: emptyList(),
        publishers = game.publishers,
        platforms = platforms.map { it.toDomainModel() },
        dlcs = dlcs.map { it.toDomainModel() },
        playStatus = game.playStatus,
        userRating = game.userRating,
        userComment = game.userComment,
        metacriticScore = game.metacriticScore,
        igdbRating = game.igdbRating,
        playtime = details?.playtime,
        lastPlayedDate = game.lastPlayedDate
    )
}

/**
 * 领域模型到游戏实体的映射扩展
 */
fun Game.toEntity(): Pair<GameEntity, GameDetailsEntity> {
    val gameEntity = GameEntity(
        id = id,
        igdbId = igdbId,
        title = title,
        originalTitle = originalTitle,
        description = description,
        coverImageUrl = coverImageUrl,
        backdropImageUrl = backdropImageUrl,
        releaseDate = releaseDate,
        genres = genres,
        publishers = publishers,
        playStatus = playStatus,
        userRating = userRating,
        userComment = userComment,
        metacriticScore = metacriticScore,
        igdbRating = igdbRating,
        lastPlayedDate = lastPlayedDate
    )
    
    val detailsEntity = GameDetailsEntity(
        gameId = id,
        developers = developers,
        playtime = playtime
    )
    
    return Pair(gameEntity, detailsEntity)
}

/**
 * 游戏平台实体到领域模型的映射扩展
 */
fun GamePlatformEntity.toDomainModel(): GamePlatform {
    return GamePlatform(
        id = id,
        name = name,
        owned = owned,
        isDigital = isDigital,
        store = store
    )
}

/**
 * 领域模型到游戏平台实体的映射扩展
 */
fun GamePlatform.toEntity(gameId: String): GamePlatformEntity {
    return GamePlatformEntity(
        id = id,
        gameId = gameId,
        name = name,
        owned = owned,
        isDigital = isDigital,
        store = store
    )
}

/**
 * 游戏DLC实体到领域模型的映射扩展
 */
fun GameDLCEntity.toDomainModel(): GameDLC {
    return GameDLC(
        id = id,
        name = name,
        description = description,
        releaseDate = releaseDate,
        owned = owned,
        completed = completed
    )
}

/**
 * 领域模型到游戏DLC实体的映射扩展
 */
fun GameDLC.toEntity(gameId: String): GameDLCEntity {
    return GameDLCEntity(
        id = id,
        gameId = gameId,
        name = name,
        description = description,
        releaseDate = releaseDate,
        owned = owned,
        completed = completed
    )
}

/**
 * 创建新游戏的帮助函数
 */
fun createNewGame(
    igdbId: String,
    title: String,
    description: String? = null,
    coverImageUrl: String? = null
): Game {
    val id = UUID.randomUUID().toString()
    return Game(
        id = id,
        igdbId = igdbId,
        title = title,
        description = description,
        coverImageUrl = coverImageUrl,
        playStatus = com.example.collectmeta.data.db.entities.PlayStatus.PLAN_TO_PLAY
    )
} 