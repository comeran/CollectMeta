package com.homeran.collectmeta.domain.model

/**
 * 游戏平台领域模型，包含游戏平台的信息以及用户对该平台的拥有情况
 */
data class GamePlatform(
    val id: String,
    val platformName: String,
    val releaseDate: String?,
    val owned: Boolean,
    val digital: Boolean,
    val store: String?
) 