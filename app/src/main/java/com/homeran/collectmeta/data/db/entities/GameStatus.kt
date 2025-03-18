package com.homeran.collectmeta.data.db.entities

/**
 * 游戏状态枚举，用于定义游戏的发布状态
 */
enum class GameStatus {
    RELEASED,       // 已发行
    EARLY_ACCESS,   // 抢先体验
    BETA,           // 测试版
    ANNOUNCED,      // 已公布
    CANCELLED       // 已取消
} 