package com.homeran.collectmeta.domain.model

import com.homeran.collectmeta.data.db.entities.GameStatus

/**
 * 游戏详情领域模型，包含游戏的详细信息
 */
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