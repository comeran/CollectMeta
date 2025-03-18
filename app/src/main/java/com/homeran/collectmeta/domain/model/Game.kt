package com.homeran.collectmeta.domain.model

import com.homeran.collectmeta.data.db.entities.GameStatus
import com.homeran.collectmeta.data.db.entities.PlayStatus
import java.time.LocalDate

/**
 * 游戏领域模型，代表业务逻辑层的游戏数据结构
 */
data class Game(
    val id: String,
    val igdbId: String,
    val title: String,
    val originalTitle: String? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val backdropImageUrl: String? = null,
    val releaseDate: LocalDate? = null,
    val genres: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val platforms: List<GamePlatform> = emptyList(),
    val dlcs: List<GameDLC> = emptyList(),
    val playStatus: PlayStatus = PlayStatus.PLAN_TO_PLAY,
    val userRating: Float? = null,
    val userComment: String? = null,
    val metacriticScore: Int? = null,
    val igdbRating: Float? = null,
    val playtime: Int? = null, // 游戏时间（分钟）
    val lastPlayedDate: LocalDate? = null
)

/**
 * 游戏平台信息
 */
data class GamePlatform(
    val id: String,
    val name: String,
    val owned: Boolean = false,
    val isDigital: Boolean = false,
    val store: String? = null
)

/**
 * 游戏DLC信息
 */
data class GameDLC(
    val id: String,
    val name: String,
    val description: String? = null,
    val releaseDate: LocalDate? = null,
    val owned: Boolean = false,
    val completed: Boolean = false
) 