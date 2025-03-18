package com.homeran.collectmeta.domain.model

/**
 * 游戏DLC领域模型，包含游戏DLC的信息以及用户对该DLC的拥有和完成情况
 */
data class GameDLC(
    val id: String,
    val name: String,
    val releaseDate: String?,
    val description: String?,
    val owned: Boolean,
    val completed: Boolean
) 