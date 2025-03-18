package com.homeran.collectmeta.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 游戏与详情的关联类，将基础媒体信息和游戏详情以及平台和DLC信息合并为一个完整的数据模型
 */
data class GameWithDetails(
    @Embedded val media: MediaEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val gameDetails: GameDetailsEntity?,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val platforms: List<GamePlatformEntity> = emptyList(),
    
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val dlcs: List<GameDLCEntity> = emptyList()
) 