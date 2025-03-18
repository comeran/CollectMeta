package com.homeran.collectmeta.data.mapper

import com.homeran.collectmeta.data.db.entities.GameDLCEntity
import com.homeran.collectmeta.data.db.entities.GameDetailsEntity
import com.homeran.collectmeta.data.db.entities.GamePlatformEntity
import com.homeran.collectmeta.data.db.entities.GameWithDetails
import com.homeran.collectmeta.data.db.entities.MediaEntity
import com.homeran.collectmeta.data.db.entities.MediaType
import com.homeran.collectmeta.domain.model.Game
import com.homeran.collectmeta.domain.model.GameDLC
import com.homeran.collectmeta.domain.model.GameDetails
import com.homeran.collectmeta.domain.model.GamePlatform
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 转换函数 - 将数据库实体转换为领域模型
 */
fun GameWithDetails.toDomainModel(): Game {
    val gson = Gson()
    return Game(
        id = this.media.id,
        title = this.media.title,
        originalTitle = this.media.originalTitle,
        year = this.media.year,
        coverUrl = this.media.cover,
        description = this.media.description,
        rating = this.media.rating,
        genres = gson.fromJson(this.media.genres, object : TypeToken<List<String>>() {}.type),
        playStatus = this.media.status as com.example.collectmeta.data.db.entities.PlayStatus,
        userRating = this.media.userRating,
        userComment = this.media.userComment,
        userTags = gson.fromJson(this.media.userTags, object : TypeToken<List<String>>() {}.type),
        customFields = gson.fromJson(gson.toJson(mapOf<String, String>()), object : TypeToken<Map<String, String>>() {}.type),
        details = this.gameDetails.toDomainModel(),
        platforms = this.platforms.map { it.toDomainModel() },
        dlcs = this.dlcs.map { it.toDomainModel() }
    )
}

/**
 * 转换函数 - 将领域模型转换为媒体实体
 */
fun Game.toMediaEntity(): MediaEntity {
    val gson = Gson()
    return MediaEntity(
        id = this.id,
        type = MediaType.GAME,
        title = this.title,
        originalTitle = this.originalTitle,
        year = this.year,
        cover = this.coverUrl,
        description = this.description,
        rating = this.rating,
        overallRating = this.rating,
        genres = gson.toJson(this.genres),
        lastModified = System.currentTimeMillis(),
        notionPageId = null,
        status = this.playStatus,
        userRating = this.userRating,
        userComment = this.userComment,
        userTags = gson.toJson(this.userTags),
        doubanUrl = null,
        createdAt = System.currentTimeMillis()
    )
}

/**
 * 转换函数 - 将游戏详情领域模型转换为实体
 */
fun Game.toGameDetailsEntity(): GameDetailsEntity {
    return GameDetailsEntity(
        mediaId = this.id,
        igdbId = this.details.igdbId,
        developer = this.details.developer,
        publisher = this.details.publisher,
        releaseDate = this.details.releaseDate,
        gameStatus = this.details.gameStatus,
        averagePlaytime = this.details.averagePlaytime,
        esrbRating = this.details.esrbRating,
        metacriticScore = this.details.metacriticScore,
        website = this.details.website
    )
}

/**
 * 转换函数 - 将游戏平台领域模型转换为实体
 */
fun GamePlatform.toEntity(gameId: String): GamePlatformEntity {
    return GamePlatformEntity(
        id = this.id,
        gameId = gameId,
        platformName = this.platformName,
        releaseDate = this.releaseDate,
        owned = this.owned,
        digital = this.digital,
        store = this.store
    )
}

/**
 * 转换函数 - 将游戏DLC领域模型转换为实体
 */
fun GameDLC.toEntity(gameId: String): GameDLCEntity {
    return GameDLCEntity(
        id = this.id,
        gameId = gameId,
        name = this.name,
        releaseDate = this.releaseDate,
        description = this.description,
        owned = this.owned,
        completed = this.completed
    )
}

/**
 * 转换函数 - 将游戏详情实体转换为领域模型
 */
fun GameDetailsEntity.toDomainModel(): GameDetails {
    return GameDetails(
        igdbId = this.igdbId,
        developer = this.developer,
        publisher = this.publisher,
        releaseDate = this.releaseDate,
        gameStatus = this.gameStatus,
        averagePlaytime = this.averagePlaytime,
        esrbRating = this.esrbRating,
        metacriticScore = this.metacriticScore,
        website = this.website
    )
}

/**
 * 转换函数 - 将游戏平台实体转换为领域模型
 */
fun GamePlatformEntity.toDomainModel(): GamePlatform {
    return GamePlatform(
        id = this.id,
        platformName = this.platformName,
        releaseDate = this.releaseDate,
        owned = this.owned,
        digital = this.digital,
        store = this.store
    )
}

/**
 * 转换函数 - 将游戏DLC实体转换为领域模型
 */
fun GameDLCEntity.toDomainModel(): GameDLC {
    return GameDLC(
        id = this.id,
        name = this.name,
        releaseDate = this.releaseDate,
        description = this.description,
        owned = this.owned,
        completed = this.completed
    )
} 