package com.homeran.collectmeta.data.db.entities

/**
 * 游戏游玩状态枚举，用于定义用户对游戏的游玩状态
 */
enum class PlayStatus {
    PLAN_TO_PLAY,   // 计划玩
    PLAYING,        // 正在玩
    COMPLETED,      // 已完成
    ON_HOLD,        // 搁置
    DROPPED         // 放弃
} 